package com.vmantek.camel.jpos.example.tm;

import com.vmantek.camel.jpos.example.util.TxnSupport;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOSource;
import org.jpos.transaction.Context;

import java.io.Serializable;

public class SendToSource extends TxnSupport
{
    @Override
    protected int doPrepare(long id, Context ctx) throws Exception
    {
        ISOSource source = (ISOSource) ctx.get(SOURCE);
        ISOMsg rsp = (ISOMsg) ctx.get(RESPONSE);
        source.send(rsp);
        return PREPARED | READONLY;
    }

    public void commit(long id, Serializable context)
    {
    }

    public void abort(long id, Serializable context)
    {
    }
}
