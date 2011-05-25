package com.vmantek.camel.jpos.space;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class SpaceConsumerTest extends CamelTestSupport
{
    @Test
    public void testSendText() throws Exception
    {
        MockEndpoint mockEndpoint = getMockEndpoint("mock:result");
        mockEndpoint.expectedBodiesReceived("Hello World");
        template.sendBody("space:tspace:default?key=dummy&timeout=40000","Hello World");
        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception
    {
        return new RouteBuilder()
        {
            @Override
            public void configure() throws Exception
            {
                from("space:tspace:default?key=dummy&timeout=40000").to("mock:result");
            }
        };
    }
}
