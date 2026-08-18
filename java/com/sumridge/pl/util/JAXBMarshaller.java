package com.sumridge.pl.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

/**
 */
public class JAXBMarshaller
{
    private Logger log = Logger.getLogger(JAXBMarshaller.class);
    private JAXBContext _jc;
    private Marshaller m;
    private Unmarshaller u;

    private static ConcurrentHashMap<String, JAXBMarshaller> marshallerMap = new ConcurrentHashMap<String, JAXBMarshaller>();

    public static JAXBMarshaller getInstance(String contextPath)
    {
        if (marshallerMap.containsKey(contextPath))
        {
            return marshallerMap.get(contextPath);
        }
        else
        {
            System.out.println("Creating new JAXBMarshaller context " + contextPath);
            JAXBMarshaller marshaller = new JAXBMarshaller(contextPath);
            marshallerMap.put(contextPath, marshaller);
            return marshaller;
        }
    }

    public static JAXBMarshaller getInstance(Class<?> contextPath)
    {
        if (marshallerMap.containsKey(contextPath.getName()))
        {
            return marshallerMap.get(contextPath.getName());
        }
        else
        {
            System.out.println("Creating new JAXBMarshaller context " + contextPath.getName());
            JAXBMarshaller marshaller = new JAXBMarshaller(contextPath);
            marshallerMap.put(contextPath.getName(), marshaller);
            return marshaller;
        }
    }

    @SuppressWarnings("rawtypes")
    public static JAXBMarshaller getInstance(Class[] contextPath, Map<String, Object> propMap)
    {
        if (marshallerMap.containsKey(contextPath[0].getName()))
        {
            return marshallerMap.get(contextPath[0].getName());
        }
        else
        {
            System.out.println("Creating new JAXBMarshaller context " + contextPath[0].getName());
            JAXBMarshaller marshaller = new JAXBMarshaller(contextPath, propMap);
            marshallerMap.put(contextPath[0].getName(), marshaller);
            return marshaller;
        }
    }

    @SuppressWarnings("rawtypes")
    private JAXBMarshaller(Class[] contextPath, Map<String, Object> propMap)
    {
        try
        {
            _jc = JAXBContext.newInstance(contextPath, propMap);
            m = _jc.createMarshaller();
            u = _jc.createUnmarshaller();
        }
        catch (Exception e)
        {
            log.error("Exception ", e);
        }
    }

    private JAXBMarshaller(Class<?> contextPath)
    {
        try
        {
            _jc = JAXBContext.newInstance(contextPath);
            m = _jc.createMarshaller();
            u = _jc.createUnmarshaller();
        }
        catch (Exception e)
        {
            log.error("Exception ", e);
        }
    }

    private JAXBMarshaller(String contextPath)
    {
        try
        {
            _jc = JAXBContext.newInstance(contextPath);
            m = _jc.createMarshaller();
            u = _jc.createUnmarshaller();
        }
        catch (Exception e)
        {
            log.error("Exception ", e);
        }
    }

    public synchronized String marshal(Object obj, boolean formatted) throws JAXBException
    {
        // Validator v = _jc.createValidator();
        // boolean valid = v.validateRoot(trade);

        StringWriter sw = new StringWriter();
        if (formatted)
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        m.marshal(obj, sw);

        return sw.toString();
    }

    public synchronized String marshal(Object obj) throws JAXBException
    {
        return marshal(obj, false);
    }

    public synchronized void marshal(Object obj, ByteArrayOutputStream out) throws JAXBException
    {
        m.marshal(obj, out);
    }

    public synchronized void generateSchema(SchemaOutputResolver resolver) throws IOException
    {
        _jc.generateSchema(resolver);
    }

    public synchronized Object unmarshal(String xml) throws JAXBException
    {
        InputSource is = new InputSource(new StringReader(xml));
        // u.setValidating(true);
        return u.unmarshal(is);
    }

    public synchronized Object unmarshal(InputSource is) throws JAXBException
    {
        // u.setValidating(true);
        return u.unmarshal(is);
    }

    public synchronized Object unmarshal(InputStream is) throws JAXBException
    {
        // u.setValidating(true);
        return u.unmarshal(is);
    }
}
