package com.vmantek.camel.jpos.example;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jpos.q2.Q2;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings({"FieldCanBeLocal"})
public class Loader
{
    private Map<String, Object> properties = new HashMap<String, Object>();
    private final String key = "jpos-camel-example";
    private final String resourceBundleName = "/jpos-example-bundle.xml";

    /**
     * Instantiates a Q2 system from an internal resource bundle,
     * while using a temporary directory structure, useful for testing.
     * @throws Exception
     */
    private void loadQ2() throws Exception
    {
        //--- Setup our temporary directory structure
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        File baseDir = new File(tmpDir, key);
        if (!baseDir.exists()) { baseDir.mkdir(); }
        File dbdir = new File(baseDir, "db");
        if (!dbdir.exists()) { dbdir.mkdir(); }
        File deployDir = new File(baseDir, "deploy");
        if (!deployDir.exists()) { deployDir.mkdir(); }
        File[] deployFiles = deployDir.listFiles();
        if (deployFiles != null)
        {
            for (File file : deployFiles)
            {
                file.delete();
            }
        }

        File dataDir = new File(baseDir, "db");
        if (!dataDir.exists()) { dataDir.mkdir(); }
        File logDir = new File(baseDir, "log");
        if (!logDir.exists()) { logDir.mkdir(); }

        //--- Store references to some of these directories for future use.
        properties.put("dbDir", dataDir.getAbsolutePath());
        properties.put("logDir", logDir.getAbsolutePath());
        properties.put("baseDir", baseDir.getAbsolutePath());

        //--- We create our Q2 instance
        Q2 q2=new Q2(deployDir.getAbsolutePath());

        //--- We load our Q2 bundle from a classpath resources
        final String bundle = transform(Loader.class.getResource(resourceBundleName));

        //--- We deploy each component in the bundle
        Document doc = createSAXBuilder().build(new StringReader(bundle));
        Iterator iter = doc.getRootElement().getChildren().iterator();
        for (int i = 0; iter.hasNext(); i++)
        {
            Element e = (Element) iter.next();
            q2.deployElement(e, String.format("%02d_%s.xml", i, e.getName()), false, true);
        }

        //--- And finally we start Q2
        q2.start();
    }

    private SAXBuilder createSAXBuilder()
    {
        SAXBuilder builder = new SAXBuilder();
        builder.setFeature("http://xml.org/sax/features/namespaces", true);
        builder.setFeature("http://apache.org/xml/features/xinclude", true);
        return builder;
    }

    /**
     * Uses FreeMarker to transform a resource, resolving any placeholder references.
     * @param url pointer to resource to be transformed.
     * @return a string containing the transformed resource.
     * @throws IOException
     * @throws TemplateException
     */
    private String transform(URL url) throws IOException, TemplateException
    {
        final StringTemplateLoader loader = new StringTemplateLoader();
        StringBuffer sb = new StringBuffer();
        InputStream is = null;
        try
        {
            is = url.openStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null)
            {
                sb.append(line);
            }
            br.close();
        }
        finally
        {
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException e)
                {
                }
            }
        }

        final String content = sb.toString();
        loader.putTemplate("template", content);

        Configuration cfg = new Configuration();
        cfg.setTemplateLoader(loader);
        cfg.setTagSyntax(Configuration.SQUARE_BRACKET_TAG_SYNTAX);
        Template t = cfg.getTemplate("template");
        StringWriter sw = new StringWriter();
        t.process(properties, sw);
        return sw.toString();
    }

    public static void main(String[] args) throws Exception
    {
        BasicConfigurator.configure();
        org.apache.log4j.Logger logger = org.apache.log4j.Logger.getRootLogger();
        logger.setLevel(Level.WARN);

        Loader loader = new Loader();
        loader.loadQ2();
    }
}
