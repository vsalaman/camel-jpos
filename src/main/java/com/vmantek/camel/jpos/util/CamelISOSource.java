package com.vmantek.camel.jpos.util;

import org.apache.camel.CamelContext;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOFilter.VetoException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOSource;

import java.io.IOException;

public class CamelISOSource implements ISOSource
{
    CamelContext camelContext;
    String uri;

    public CamelISOSource(CamelContext camelContext, String uri)
    {
        this.camelContext = camelContext;
        this.uri = uri;
    }

    @Override
    public void send(ISOMsg m) throws IOException, ISOException, VetoException
    {
        camelContext.createProducerTemplate().sendBody(uri,m);
    }

    @Override
    public boolean isConnected()
    {
        return true;
    }
}
