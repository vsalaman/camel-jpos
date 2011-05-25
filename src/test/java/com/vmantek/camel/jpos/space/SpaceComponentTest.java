package com.vmantek.camel.jpos.space;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class SpaceComponentTest
{
    SpaceComponent component;

    @Before
    public void setUp()
    {
        CamelContext context = new DefaultCamelContext();
        component = new SpaceComponent(context);
    }

    @Test
    public void createEndpointStringShouldReturnASpaceEndpoint() throws Exception
    {
        Endpoint endpoint = component.createEndpoint("space:tspace:default?key=dummy&timeout=40000");
        SpaceEndpoint spaceEndpoint = (SpaceEndpoint) endpoint;

        assertEquals("space:tspace:default?key=dummy&timeout=40000", spaceEndpoint.getEndpointUri());
        assertEquals(spaceEndpoint.getKey(), "dummy");
        assertEquals(spaceEndpoint.getTimeout(), 40000);
        assertSame(component, spaceEndpoint.getComponent());
    }

}
