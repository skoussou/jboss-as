/*
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2011, Red Hat, Inc., and individual contributors
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
package org.jboss.as.ejb3.deployment.processors;

import org.jboss.as.ejb3.component.EJBComponentDescription;
import org.jboss.as.ejb3.component.MethodIntf;
import org.jboss.as.ejb3.component.stateless.StatelessComponentDescription;
import org.jboss.as.ejb3.deployment.EjbDeploymentMarker;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.annotation.CompositeIndex;
import org.jboss.jandex.Indexer;
import org.jboss.msc.service.ServiceName;
import org.junit.Test;

import javax.ejb.Local;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.jboss.as.ejb3.TestHelper.index;
import static org.jboss.as.ejb3.TestHelper.mockDeploymentUnit;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class LocalEjbViewAnnotationProcessorTestCase {
    @Local
    private static class MyBean implements MyInterface {

    }

    private static interface MyInterface {

    }

    private static <T, V extends T> Set<T> asSet(V... values) {
        Set<T> set = new HashSet<T>();
        for (T value : values) {
            set.add(value);
        }
        return set;
    }

    @Test
    public void test1() throws Exception {
        DeploymentUnit deploymentUnit = mockDeploymentUnit();
        when(deploymentUnit.getServiceName()).thenReturn(ServiceName.parse("test"));
        // Mark the deployment unit as a EJB deployment
        EjbDeploymentMarker.mark(deploymentUnit);
        DeploymentPhaseContext phaseContext = null;
        Indexer indexer = new Indexer();
        index(indexer, MyBean.class);
        index(indexer, MyInterface.class);
        CompositeIndex index = new CompositeIndex(Arrays.asList(indexer.complete()));

        EJBComponentDescription componentDescription = new StatelessComponentDescription(MyBean.class.getSimpleName(), MyBean.class.getName(), "TestModule", "TestApp");
        LocalEjbViewAnnotationProcessor processor = new LocalEjbViewAnnotationProcessor();
        processor.processComponentConfig(deploymentUnit, phaseContext, index, componentDescription);

        assertEquals(asSet(MyInterface.class.getName()), componentDescription.getViewClassNames());
        assertEquals(MethodIntf.LOCAL, componentDescription.getMethodIntf(MyInterface.class.getName()));
    }
}
