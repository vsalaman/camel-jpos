package com.vmantek.camel.jpos.converter;

import org.apache.camel.Converter;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.XMLPackager;


@SuppressWarnings({"UnusedDeclaration"})
@Converter
public class ISOMsgConverter
{
    XMLPackager packager;

    public ISOMsgConverter() throws ISOException
    {
        this.packager=new XMLPackager();
    }

    @Converter
    public String toXmlString(ISOMsg m) throws ISOException
    {
        ISOMsg c= (ISOMsg) m.clone();
        c.setPackager(packager);
        return new String(c.pack());
    }

    @Converter
    public ISOMsg toISOMsg(String s) throws ISOException
    {
        ISOMsg c= new ISOMsg();
        c.setPackager(packager);
        c.unpack(s.getBytes());
        return c;
    }
}
