package com.vmantek.camel.jpos.space;

import org.apache.camel.AsyncCallback;
import org.apache.camel.AsyncProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.camel.impl.converter.AsyncProcessorTypeConverter;
import org.apache.camel.util.AsyncProcessorHelper;
import org.jpos.space.Space;

import java.util.concurrent.atomic.AtomicBoolean;

public class SpaceConsumer extends DefaultConsumer implements Runnable
{
    private Thread consumeThread;
    private AtomicBoolean running = new AtomicBoolean(false);
    private AsyncProcessor processor;

    public SpaceConsumer(SpaceEndpoint endpoint, Processor processor)
    {
        super(endpoint, processor);
        this.processor = AsyncProcessorTypeConverter.convert(processor);
    }

    protected void doStart() throws Exception
    {
        running.set(true);
        consumeThread = new Thread(this);
        consumeThread.start();
    }

    protected void doStop() throws Exception
    {
        if (consumeThread != null)
        {
            running.set(false);
            try
            {
                consumeThread.interrupt();
            }
            catch (Exception e)
            {
            }
            consumeThread = null;
        }
    }

    @Override
    public void run()
    {
        final SpaceEndpoint endpoint = (SpaceEndpoint) getEndpoint();
        Space space = endpoint.getSpace();
        Long timeout = endpoint.getTimeout();

        while (running.get())
        {
            Exchange exchange=null;
            try
            {
                Object o = space.in(endpoint.getKey(),timeout);
                if(o!=null)
                {
                    exchange = endpoint.createExchange();
                    exchange.getIn().setBody(o);
                    try
                    {
                        sendToConsumers(exchange);
                        if (exchange.getException() != null)
                        {
                            getExceptionHandler().handleException("Error processing exchange", exchange, exchange.getException());
                        }
                    }
                    catch (Exception e)
                    {
                        getExceptionHandler().handleException("Error processing exchange", exchange, e);
                    }
                }
            }
            catch (Throwable e)
            {
                if (exchange != null)
                {
                    getExceptionHandler().handleException("Error processing exchange", exchange, e);
                }
                else
                {
                    getExceptionHandler().handleException(e);
                }
            }
        }
    }

    private void sendToConsumers(Exchange exchange)
    {
        AsyncCallback callback = new AsyncCallback()
        {
            public void done(boolean doneSync)
            {
            }
        };
        AsyncProcessorHelper.process(processor, exchange, callback);
    }
}
