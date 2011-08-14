package com.vmantek.camel.jpos.util;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.ProducerTemplate;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOSource;

import java.io.IOException;

/**
 * Creates a camel enabled ISOSource to be used in a jPOS application.
 */
public class CamelISOSource implements ISOSource
{
    ProducerTemplate producerTemplate;
    Endpoint endpoint;

    public CamelISOSource(CamelContext camelContext, Endpoint endpoint)
    {
        producerTemplate = camelContext.createProducerTemplate();
        this.endpoint = endpoint;
    }

    public CamelISOSource(CamelContext camelContext, String uri)
    {
        this(camelContext,camelContext.getEndpoint(uri));
    }

    public void send(ISOMsg m) throws IOException, ISOException
    {
        producerTemplate.sendBody(endpoint, m);
    }

    public boolean isConnected()
    {
        return true;
    }
}
