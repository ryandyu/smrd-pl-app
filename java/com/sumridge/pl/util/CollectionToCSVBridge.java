package com.sumridge.pl.util;

import java.util.Collection;

import org.hibernate.search.bridge.StringBridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CollectionToCSVBridge implements StringBridge
{
    public static Logger LOG = LoggerFactory.getLogger(CollectionToCSVBridge.class);
    
    public String objectToString(Object value)
    {
       if(value != null)
       {
           if(value.getClass() == String.class)
               return (String)value;
           else
           {
               Collection<?> col = (Collection<?>)value;
               return col.toString();
           }
       }
       
       return null;
   }
}