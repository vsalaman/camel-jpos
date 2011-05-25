package com.vmantek.camel.jpos.example.util;

import org.jpos.transaction.AbortParticipant;
import org.jpos.transaction.Context;
import org.jpos.util.Log;
import org.jpos.util.LogEvent;
import org.jpos.util.Logger;

import java.io.Serializable;

/**
 * A modified Debug participant from the jPOSEE distribution.
 */
public class Debug extends Log implements AbortParticipant
{
    public int prepare(long id, Serializable o)
    {
        return PREPARED | READONLY;
    }

    public int prepareForAbort(long id, Serializable o)
    {
        return PREPARED | READONLY;
    }

    public void commit(long id, Serializable o)
    {
        Logger.log(createEvent("commit", id, (Context) o));
    }

    public void abort(long id, Serializable o)
    {
        Logger.log(createEvent("abort", id, (Context) o));
    }

    private LogEvent createEvent(String action, long id, Context ctx)
    {
        LogEvent evt = createLogEvent(action);
        evt.addMessage("<id>" + id + "</id>");
        evt.addMessage(ctx);
        return evt;
    }
}
