/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.cli;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jboss.as.cli.handlers.PrefixHandler;
import org.jboss.as.cli.handlers.ConnectHandler;
import org.jboss.as.cli.handlers.HelpHandler;
import org.jboss.as.cli.handlers.OperationRequestHandler;
import org.jboss.as.cli.handlers.QuitHandler;
import org.jboss.as.cli.operation.OperationCandidatesProvider;
import org.jboss.as.cli.operation.OperationRequestCompleter;
import org.jboss.as.cli.operation.OperationRequestParser;
import org.jboss.as.cli.operation.OperationRequestAddress;
import org.jboss.as.cli.operation.PrefixFormatter;
import org.jboss.as.cli.operation.impl.DefaultOperationCandidatesProvider;
import org.jboss.as.cli.operation.impl.DefaultOperationRequestAddress;
import org.jboss.as.cli.operation.impl.DefaultOperationRequestParser;
import org.jboss.as.cli.operation.impl.DefaultPrefixFormatter;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.protocol.StreamUtils;

/**
 *
 * @author Alexey Loubyansky
 */
public class CommandLineMain {

    private static final Map<String, CommandHandler> handlers = new HashMap<String, CommandHandler>();
    static {
        registerHandler(new HelpHandler(), "help", "h");
        registerHandler(new QuitHandler(), "quit", "q");
        registerHandler(new ConnectHandler(), "connect");
        registerHandler(new PrefixHandler(), "cd", "cn");
    }

    private static void registerHandler(CommandHandler handler, String... names) {
        for(String name : names) {
            CommandHandler previous = handlers.put(name, handler);
            if(previous != null)
                throw new IllegalStateException("Duplicate command name '" + name + "'. Handlers: " + previous + ", " + handler);
        }
    }

    private static final CommandHandler operationHandler = new OperationRequestHandler();

    public static void main(String[] args) throws Exception {

        final jline.ConsoleReader console = new jline.ConsoleReader();
        console.setUseHistory(true);
        console.addCompletor(new CommandCompleter(handlers.keySet()));

        final CommandContextImpl cmdCtx = new CommandContextImpl(console);
        SecurityActions.addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                StreamUtils.safeClose(cmdCtx.client);
                cmdCtx.log("closed");
            }
        }));
        console.addCompletor(new OperationRequestCompleter(cmdCtx));

        cmdCtx.log("You are disconnected at the moment." +
                " Type 'connect' to connect to the server or" +
                " 'help' for the list of supported commands.");

        while (!cmdCtx.terminate) {
            String line = console.readLine("[" + cmdCtx.getPrefixFormatter().format(cmdCtx.getPrefix()) + "] ");

            if (line.isEmpty()) {
                // cmdCtx.log("Type /help for the list of supported commands.");
                continue;
            }

            if(isOperation(line)) {
                final String opReq;
                if(line.startsWith("./")) {
                    // TODO this has to be added to the operation request parser
                    opReq = line.substring(2);
                } else {
                    opReq = line;
                }
                cmdCtx.cmdArgs = opReq;
                operationHandler.handle(cmdCtx);

            } else {
                String cmd = line.toLowerCase();
                cmdCtx.cmdArgs = null;
                for (int i = 0; i < cmd.length(); ++i) {
                    if (Character.isWhitespace(cmd.charAt(i))) {
                        cmdCtx.cmdArgs = cmd.substring(i + 1).trim();
                        cmd = cmd.substring(0, i);
                    }
                }

                CommandHandler handler = handlers.get(cmd);
                if (handler != null) {
                    handler.handle(cmdCtx);
                } else {
                    cmdCtx.log("Unexpected command '"
                            + line
                            + "'. Type 'help' for the list of supported commands.");
                }
            }
        }
    }

    private static boolean isOperation(String line) {
        char firstChar = line.charAt(0);
        return firstChar == '.' || firstChar == ':' || firstChar == '/' || line.startsWith("..") || line.startsWith(".type");
    }

    private static class CommandContextImpl implements CommandContext {

        private jline.ConsoleReader console;
        /** whether the session should be terminated*/
        private boolean terminate;
        /** current command's arguments */
        private String cmdArgs;
        /** the controller client */
        private ModelControllerClient client;
        /** various key/value pairs */
        private Map<String, Object> map = new HashMap<String, Object>();
        /** operation request parser */
        private final OperationRequestParser parser = new DefaultOperationRequestParser();
        /** operation request address prefix */
        private final OperationRequestAddress prefix = new DefaultOperationRequestAddress();
        /** the prefix formatter */
        private final PrefixFormatter prefixFormatter = new DefaultPrefixFormatter();
        /** provider of operation request candidates for tab-completion */
        private final OperationCandidatesProvider operationCandidatesProvider;

        private CommandContextImpl(jline.ConsoleReader console) {
            this.console = console;
            operationCandidatesProvider = new DefaultOperationCandidatesProvider(this);
        }

        @Override
        public String getCommandArguments() {
            return cmdArgs;
        }

        @Override
        public void terminateSession() {
            terminate = true;
        }

        @Override
        public void log(String message) {
            try {
                console.printString(message);
                console.printNewline();
            } catch (IOException e) {
                System.err.println("Failed to print '" + message + "' to the console: " + e.getLocalizedMessage());
            }
        }

        @Override
        public void set(String key, Object value) {
            map.put(key, value);
        }

        @Override
        public Object get(String key) {
            return map.get(key);
        }

        @Override
        public ModelControllerClient getModelControllerClient() {
            return client;
        }

        @Override
        public void setModelControllerClient(ModelControllerClient client) {
            this.client = client;
        }

        @Override
        public OperationRequestParser getOperationRequestParser() {
            return parser;
        }

        @Override
        public OperationRequestAddress getPrefix() {
            return prefix;
        }

        @Override
        public PrefixFormatter getPrefixFormatter() {

            return prefixFormatter;
        }

        @Override
        public OperationCandidatesProvider getOperationCandidatesProvider() {
            return operationCandidatesProvider;
        }

    }
}
