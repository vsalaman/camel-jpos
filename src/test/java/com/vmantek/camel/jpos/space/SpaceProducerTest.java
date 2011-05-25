package com.vmantek.camel.jpos.space;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class SpaceProducerTest extends CamelTestSupport
{
    @Test
    public void testSimple() throws Exception
    {
        MockEndpoint mockEndpoint = getMockEndpoint("mock:result");
        mockEndpoint.expectedBodiesReceived("Bye Hello World");
        template.sendBody("direct:start","Hello World");
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
                from("direct:start").to("space:tspace:default?key=dummy&timeout=40000");
                from("space:tspace:default?key=dummy&timeout=40000").process(new MyProcessor()).to("mock:result");
            }
        };
    }

    private static class MyProcessor implements Processor
    {
        public void process(Exchange exchange) throws Exception
        {
            String body = exchange.getIn().getBody(String.class);
            final String output = "Bye " + body;
            exchange.getOut().setBody(output);
        }
    }
}
