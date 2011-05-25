package com.vmantek.camel.jpos.space;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.jpos.space.Space;

public class SpaceEndpoint extends DefaultEndpoint
{
    private Space space;
    private long timeout=-1;
    private String key;

    public SpaceEndpoint(Space space,String uri, SpaceComponent component)
    {
        super(uri, component);
        this.space=space;
    }

    public Space getSpace()
    {
        return space;
    }

    public long getTimeout()
    {
        return timeout;
    }

    public void setTimeout(long timeout)
    {
        this.timeout = timeout;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    @Override
    public Producer createProducer() throws Exception
    {
        return new SpaceProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception
    {
        return new SpaceConsumer(this,processor);
    }

    @Override
    public boolean isSingleton()
    {
        return true;
    }
}
