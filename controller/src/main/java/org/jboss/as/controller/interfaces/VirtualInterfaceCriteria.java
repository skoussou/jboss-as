/**
 *
 */
package org.jboss.as.controller.interfaces;

import java.io.ObjectStreamException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;

/**
 * {@link InterfaceCriteria} that tests whether a given interface is
 * {@link NetworkInterface#isVirtual() virtual}.
 *
 * @author Brian Stansberry
 */
public class VirtualInterfaceCriteria implements InterfaceCriteria {

    private static final long serialVersionUID = -2714634628678015738L;

    public static final VirtualInterfaceCriteria INSTANCE = new VirtualInterfaceCriteria();

    private VirtualInterfaceCriteria() {}

    /**
     * {@inheritDoc}
     *
     * @return <code>true</code> if <code>networkInterface</code> is
     *         {@link NetworkInterface#isVirtual() virtual}
     */
    @Override
    public boolean isAcceptable(NetworkInterface networkInterface, InetAddress address) throws SocketException {

        return networkInterface.isVirtual();
    }

    private Object readResolve() throws ObjectStreamException {
        return INSTANCE;
    }

}
