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
            //this block should be very familiar...
            //We get the ISOMsg from the exchange input
            ISOMsg m= (ISOMsg) exchange.getIn().getBody();

            //We create an ISOSource so our code knows where to send replies!
            CamelISOSource source=new CamelISOSource(context,destination);
            Context ctx=new Context();
            ctx.put(SOURCE,source);
            ctx.put(REQUEST,m);

            //We set the exchange output body the jPOS Context
            exchange.getOut().setBody(ctx);
        }
    }

    class MyRouteBuilder extends SpringRouteBuilder
    {
        @Override
        public void configure() throws Exception
        {
            //Creates a processor which creates our ISOSource
            final TMDispatchProcessor tmDispatcher = new TMDispatchProcessor(getContext(), "vm://test");

            //Wait for any file in "in" directory, convert the xml to an ISOMsg and send it to the "direct:process" endpoint
            from("file:"+baseDir+"/in?moveFailed="+baseDir+"/errors/${file:name.noext}-${date:now:yyyyMMddHHmmssSSS}.${file:ext}").
                    convertBodyTo(String.class).
                    convertBodyTo(ISOMsg.class).
                    to("direct:process");

            //Listen on the "direct:process" endpoint, invokes the tmDispatcher processor created above,
            // and sends the jPOS context to a jPOS transient Space with the key "txnmgr"
            from("direct:process").
                    process(tmDispatcher).
                    to("space:transient:default?key=txnmgr");

            //Wait for any response on vm://test, which is where our ISOSource is configured to return all replies,
            // and convert it to a String and put it in a file in directory "out"
            from("vm://test").
                    convertBodyTo(String.class).
                    to("file:"+baseDir+"/out");
        }
    }
}
