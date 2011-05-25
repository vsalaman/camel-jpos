package com.vmantek.camel.jpos.space;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultProducer;
import org.jpos.space.Space;

public class SpaceProducer extends DefaultProducer
{
    private SpaceEndpoint endpoint;

    public SpaceProducer(SpaceEndpoint endpoint)
    {
        super(endpoint);
        this.endpoint=endpoint;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public void process(Exchange exchange) throws Exception
    {
        final Space space = endpoint.getSpace();
        final long timeout = endpoint.getTimeout();
        final String key = endpoint.getKey();
        final Message in = exchange.getIn();
        final Object body = in.getBody();

        if(body!=null)
        {
            space.out(key, body,timeout);
        }
    }
}
