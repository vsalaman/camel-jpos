package com.vmantek.camel.jpos.example.tm;

import com.vmantek.camel.jpos.example.util.TxnSupport;
import org.jpos.iso.ISOMsg;
import org.jpos.transaction.Context;

import java.io.Serializable;

public class DummyProcessing extends TxnSupport
{
    @Override
    protected int doPrepare(long id, Context ctx) throws Exception
    {
        ISOMsg req = (ISOMsg) ctx.get(REQUEST);

        //Dummy operation: we clone the request and change MTI
        ISOMsg rsp = (ISOMsg) req.clone();
        rsp.setResponseMTI();
        ctx.put(RESPONSE, rsp);

        return PREPARED;
    }

    public void commit(long id, Serializable context)
    {
    }

    public void abort(long id, Serializable context)
    {
    }
}
