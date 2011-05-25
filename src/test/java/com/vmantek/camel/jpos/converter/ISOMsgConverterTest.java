package com.vmantek.camel.jpos.converter;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ISOMsgConverterTest
{
    ISOMsgConverter converter;

    String s="<isomsg>\n" +
             "  <field id=\"0\" value=\"0100\"/>\n" +
             "  <field id=\"3\" value=\"000000\"/>\n" +
             "  <field id=\"4\" value=\"000000000100\"/>\n" +
             "  <field id=\"14\" value=\"4912\"/>\n" +
             "  <field id=\"18\" value=\"5812\"/>\n" +
             "  <field id=\"19\" value=\"840\"/>\n" +
             "  <field id=\"22\" value=\"0120\"/>\n" +
             "  <field id=\"25\" value=\"08\"/>\n" +
             "  <field id=\"32\" value=\"999999\"/>\n" +
             "  <field id=\"37\" value=\"000000457169\"/>\n" +
             "  <field id=\"41\" value=\"91375366\"/>\n" +
             "  <field id=\"42\" value=\"999008665000\"/>\n" +
             "  <field id=\"49\" value=\"840\"/>\n" +
             "  <field id=\"60\" value=\"00\" type=\"binary\"/>\n" +
             "  <isomsg id=\"63\">\n" +
             "    <field id=\"1\" value=\"0000\"/>\n" +
             "  </isomsg>\n" +
             "</isomsg>";

    @Before
    public void setUp() throws ISOException
    {
        converter=new ISOMsgConverter();
    }

    @Test
    public void testConvertToIsoMsg() throws ISOException
    {
        ISOMsg m=converter.toISOMsg(s);
        assertEquals("0100",m.getMTI());
        assertEquals("0000",m.getString("63.1"));
        assertTrue(m.getBytes(60)[0]==0);
    }

    @Test
    public void testConvertToXmlString() throws ISOException
    {
        ISOMsg m=new ISOMsg("0100");
        m.set(4,"000000000100");
        String xmlString=converter.toXmlString(m);
        String expected="<isomsg>\n" +
                        "  <!-- org.jpos.iso.packager.XMLPackager -->\n" +
                        "  <field id=\"0\" value=\"0100\"/>\n" +
                        "  <field id=\"4\" value=\"000000000100\"/>\n" +
                        "</isomsg>\n";
        assertEquals(expected,xmlString);
    }
}
