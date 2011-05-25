package com.vmantek.camel.jpos.example.service;

import com.vmantek.camel.jpos.example.Constants;
import com.vmantek.camel.jpos.util.CamelISOSource;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.camel.spring.SpringRouteBuilder;
import org.jpos.iso.ISOMsg;
import org.jpos.q2.QBeanSupport;
import org.jpos.transaction.Context;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

public class ExampleCamelService extends QBeanSupport implements Constants
{
    private CamelContext camelContext;
    private String baseDir;

    @Override
    protected void startService() throws Exception
    {
        baseDir = cfg.get("baseDir");
        ApplicationContext applicationContext = new GenericXmlApplicationContext();
        camelContext=new SpringCamelContext(applicationContext);
        camelContext.addRoutes(new MyRouteBuilder());
        camelContext.start();
    }

    @Override
    protected void stopService() throws Exception
    {
        if(camelContext!=null)
        {
            try
            {
                camelContext.stop();
                camelContext=null;
            }
            catch (Exception e)
            {
            }
        }
    }

    class TMDispatchProcessor implements Processor
    {
        CamelContext context;
        String destination;

        TMDispatchProcessor(CamelContext context, String destination)
        {
            this.context = context;
            this.destination = destination;
        }

        public void process(Exchange exchange) throws Exception
        {
            ISOMsg m= (ISOMsg) exchange.getIn().getBody();
            CamelISOSource source=new CamelISOSource(context,destination);
            Context ctx=new Context();
            ctx.put(SOURCE,source);
            ctx.put(REQUEST,m);
            exchange.getOut().setBody(ctx);
        }
    }

    class MyRouteBuilder extends SpringRouteBuilder
    {
        @Override
        public void configure() throws Exception
        {
            final TMDispatchProcessor tmDispatcher = new TMDispatchProcessor(getContext(), "vm://test");

            //Wait for any file in "in" directory.
            from("file:"+baseDir+"/in?moveFailed="+baseDir+"/errors/${file:name.noext}-${date:now:yyyyMMddHHmmssSSS}.${file:ext}").
                    convertBodyTo(String.class).
                    convertBodyTo(ISOMsg.class).
                    to("direct:process");

            from("direct:process").
                    process(tmDispatcher).
                    to("space:transient:default?key=txnmgr");

            //Wait for any response on vm://test , convert it to a string
            // and put it in a file in directory "out"
            from("vm://test").
                    convertBodyTo(String.class).
                    to("file:"+baseDir+"/out");
        }
    }
}
