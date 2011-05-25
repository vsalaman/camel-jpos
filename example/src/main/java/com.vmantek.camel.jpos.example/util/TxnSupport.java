package com.vmantek.camel.jpos.example.util;

import com.vmantek.camel.jpos.example.Constants;
import org.jpos.transaction.Context;
import org.jpos.transaction.TransactionParticipant;

import java.io.Serializable;

public abstract class TxnSupport implements TransactionParticipant, Constants
{
    protected int doPrepare(long id, Context ctx) throws Exception
    {
        return ABORTED; // misconfigured participant
    }

    public int prepare(long id, Serializable o)
    {
        Context ctx = (Context) o;
        try
        {
            return doPrepare(id, ctx);
        }
        catch (Throwable t)
        {
            ctx.log("prepare exception in " + this.getClass().getName());
            ctx.log(t);
            ctx.put(RC, t.getMessage());
        }
        return ABORTED;
    }
}
