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
package org.jboss.as.cli.parsing.test;

import java.util.Set;

import org.jboss.as.cli.operation.impl.DefaultOperationCallbackHandler;
import org.jboss.as.cli.operation.impl.DefaultOperationRequestParser;
import org.junit.Test;

import junit.framework.TestCase;

/**
 *
 * @author Alexey Loubyansky
 */
public class OperationOnlyParsingTestCase extends TestCase {

    private DefaultOperationRequestParser parser = new DefaultOperationRequestParser();

    @Test
    public void testOperationNameOnly() throws Exception {
        DefaultOperationCallbackHandler handler = new DefaultOperationCallbackHandler();

        parser.parse(":read-resource", handler);

        assertFalse(handler.hasAddress());
        assertTrue(handler.hasOperationName());
        assertFalse(handler.hasProperties());
        assertFalse(handler.endsOnAddressOperationNameSeparator());
        assertFalse(handler.endsOnArgumentListStart());
        assertFalse(handler.endsOnArgumentSeparator());
        assertFalse(handler.endsOnArgumentValueSeparator());
        assertFalse(handler.endsOnNodeSeparator());
        assertFalse(handler.endsOnNodeTypeNameSeparator());
        assertFalse(handler.isRequestComplete());

        assertEquals("read-resource", handler.getOperationName());
    }

    @Test
    public void testArgListStart() throws Exception {
        DefaultOperationCallbackHandler handler = new DefaultOperationCallbackHandler();

        parser.parse(":read-resource(", handler);

        assertFalse(handler.hasAddress());
        assertTrue(handler.hasOperationName());
        assertFalse(handler.hasProperties());
        assertFalse(handler.endsOnAddressOperationNameSeparator());
        assertTrue(handler.endsOnArgumentListStart());
        assertFalse(handler.endsOnArgumentSeparator());
        assertFalse(handler.endsOnArgumentValueSeparator());
        assertFalse(handler.endsOnNodeSeparator());
        assertFalse(handler.endsOnNodeTypeNameSeparator());
        assertFalse(handler.isRequestComplete());

        assertEquals("read-resource", handler.getOperationName());
        assertTrue(handler.getPropertyNames().isEmpty());
    }

    @Test
    public void testEmptyArgList() throws Exception {
        DefaultOperationCallbackHandler handler = new DefaultOperationCallbackHandler();

        parser.parse(":read-resource()", handler);

        assertFalse(handler.hasAddress());
        assertTrue(handler.hasOperationName());
        assertFalse(handler.hasProperties());
        assertFalse(handler.endsOnAddressOperationNameSeparator());
        assertFalse(handler.endsOnArgumentListStart());
        assertFalse(handler.endsOnArgumentSeparator());
        assertFalse(handler.endsOnArgumentValueSeparator());
        assertFalse(handler.endsOnNodeSeparator());
        assertFalse(handler.endsOnNodeTypeNameSeparator());
        assertTrue(handler.isRequestComplete());

        assertEquals("read-resource", handler.getOperationName());
    }

    @Test
    public void testArgNameOnly() throws Exception {
        DefaultOperationCallbackHandler handler = new DefaultOperationCallbackHandler();

        parser.parse(":read-resource(recursive", handler);

        assertFalse(handler.hasAddress());
        assertTrue(handler.hasOperationName());
        assertTrue(handler.hasProperties());
        assertFalse(handler.endsOnAddressOperationNameSeparator());
        assertFalse(handler.endsOnArgumentListStart());
        assertFalse(handler.endsOnArgumentSeparator());
        assertFalse(handler.endsOnArgumentValueSeparator());
        assertFalse(handler.endsOnNodeSeparator());
        assertFalse(handler.endsOnNodeTypeNameSeparator());
        assertFalse(handler.isRequestComplete());

        assertEquals("read-resource", handler.getOperationName());

        Set<String> argNames = handler.getPropertyNames();
        assertEquals(1, argNames.size());
        assertTrue(argNames.contains("recursive"));
    }

    @Test
    public void testNameAndValueSeparator() throws Exception {
        DefaultOperationCallbackHandler handler = new DefaultOperationCallbackHandler();

        parser.parse(":read-resource(recursive=", handler);

        assertFalse(handler.hasAddress());
        assertTrue(handler.hasOperationName());
        assertTrue(handler.hasProperties());
        assertFalse(handler.endsOnAddressOperationNameSeparator());
        assertFalse(handler.endsOnArgumentListStart());
        assertFalse(handler.endsOnArgumentSeparator());
        assertTrue(handler.endsOnArgumentValueSeparator());
        assertFalse(handler.endsOnNodeSeparator());
        assertFalse(handler.endsOnNodeTypeNameSeparator());
        assertFalse(handler.isRequestComplete());

        assertEquals("read-resource", handler.getOperationName());

        Set<String> argNames = handler.getPropertyNames();
        assertEquals(1, argNames.size());
        assertTrue(argNames.contains("recursive"));
    }

    @Test
    public void testNameValue() throws Exception {
        DefaultOperationCallbackHandler handler = new DefaultOperationCallbackHandler();

        parser.parse(":read-resource(recursive=true", handler);

        assertFalse(handler.hasAddress());
        assertTrue(handler.hasOperationName());
        assertTrue(handler.hasProperties());
        assertFalse(handler.endsOnAddressOperationNameSeparator());
        assertFalse(handler.endsOnArgumentListStart());
        assertFalse(handler.endsOnArgumentSeparator());
        assertFalse(handler.endsOnArgumentValueSeparator());
        assertFalse(handler.endsOnNodeSeparator());
        assertFalse(handler.endsOnNodeTypeNameSeparator());
        assertFalse(handler.isRequestComplete());

        assertEquals("read-resource", handler.getOperationName());

        Set<String> argNames = handler.getPropertyNames();
        assertEquals(1, argNames.size());
        assertTrue(argNames.contains("recursive"));
        assertEquals("true", handler.getPropertyValue("recursive"));
    }

    @Test
    public void testNameValueAndSeparator() throws Exception {
        DefaultOperationCallbackHandler handler = new DefaultOperationCallbackHandler();

        parser.parse(":read-resource(recursive=true,", handler);

        assertFalse(handler.hasAddress());
        assertTrue(handler.hasOperationName());
        assertTrue(handler.hasProperties());
        assertFalse(handler.endsOnAddressOperationNameSeparator());
        assertFalse(handler.endsOnArgumentListStart());
        assertTrue(handler.endsOnArgumentSeparator());
        assertFalse(handler.endsOnArgumentValueSeparator());
        assertFalse(handler.endsOnNodeSeparator());
        assertFalse(handler.endsOnNodeTypeNameSeparator());
        assertFalse(handler.isRequestComplete());

        assertEquals("read-resource", handler.getOperationName());

        Set<String> argNames = handler.getPropertyNames();
        assertEquals(1, argNames.size());
        assertTrue(argNames.contains("recursive"));
        assertEquals("true", handler.getPropertyValue("recursive"));
    }

    @Test
    public void testNameValueSeparatorAndName() throws Exception {
        DefaultOperationCallbackHandler handler = new DefaultOperationCallbackHandler();

        parser.parse(":read-resource(recursive=true,other", handler);

        assertFalse(handler.hasAddress());
        assertTrue(handler.hasOperationName());
        assertTrue(handler.hasProperties());
        assertFalse(handler.endsOnAddressOperationNameSeparator());
        assertFalse(handler.endsOnArgumentListStart());
        assertFalse(handler.endsOnArgumentSeparator());
        assertFalse(handler.endsOnArgumentValueSeparator());
        assertFalse(handler.endsOnNodeSeparator());
        assertFalse(handler.endsOnNodeTypeNameSeparator());
        assertFalse(handler.isRequestComplete());

        assertEquals("read-resource", handler.getOperationName());

        Set<String> argNames = handler.getPropertyNames();
        assertEquals(2, argNames.size());
        assertTrue(argNames.contains("recursive"));
        assertEquals("true", handler.getPropertyValue("recursive"));
        assertTrue(argNames.contains("other"));
    }

    @Test
    public void testNameValueSeparatorNameAndValueSeparator() throws Exception {
        DefaultOperationCallbackHandler handler = new DefaultOperationCallbackHandler();

        parser.parse(":read-resource(recursive=true,other=", handler);

        assertFalse(handler.hasAddress());
        assertTrue(handler.hasOperationName());
        assertTrue(handler.hasProperties());
        assertFalse(handler.endsOnAddressOperationNameSeparator());
        assertFalse(handler.endsOnArgumentListStart());
        assertFalse(handler.endsOnArgumentSeparator());
        assertTrue(handler.endsOnArgumentValueSeparator());
        assertFalse(handler.endsOnNodeSeparator());
        assertFalse(handler.endsOnNodeTypeNameSeparator());
        assertFalse(handler.isRequestComplete());

        assertEquals("read-resource", handler.getOperationName());

        Set<String> argNames = handler.getPropertyNames();
        assertEquals(2, argNames.size());
        assertTrue(argNames.contains("recursive"));
        assertEquals("true", handler.getPropertyValue("recursive"));
        assertTrue(argNames.contains("other"));
    }

    @Test
    public void testComplete() throws Exception {
        DefaultOperationCallbackHandler handler = new DefaultOperationCallbackHandler();

        parser.parse(":read-resource(recursive=true,other=done)", handler);

        assertFalse(handler.hasAddress());
        assertTrue(handler.hasOperationName());
        assertTrue(handler.hasProperties());
        assertFalse(handler.endsOnAddressOperationNameSeparator());
        assertFalse(handler.endsOnArgumentListStart());
        assertFalse(handler.endsOnArgumentSeparator());
        assertFalse(handler.endsOnArgumentValueSeparator());
        assertFalse(handler.endsOnNodeSeparator());
        assertFalse(handler.endsOnNodeTypeNameSeparator());
        assertTrue(handler.isRequestComplete());

        assertEquals("read-resource", handler.getOperationName());

        Set<String> argNames = handler.getPropertyNames();
        assertEquals(2, argNames.size());
        assertTrue(argNames.contains("recursive"));
        assertEquals("true", handler.getPropertyValue("recursive"));
        assertTrue(argNames.contains("other"));
        assertEquals("done", handler.getPropertyValue("other"));
    }

}
