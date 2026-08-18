package com.sumridge.pl.util;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class PlUtil {
   public static Date getSystemDate(){
	   Calendar cal =  Calendar.getInstance();
	   cal.set(Calendar.HOUR_OF_DAY, 0);
	   cal.set(Calendar.MINUTE, 0);
	   cal.set(Calendar.SECOND, 0);
       cal.set(GregorianCalendar.MILLISECOND, 0);
	   return cal.getTime();
   }
   public static String getSystemTime() {
	   DateFormat format = new SimpleDateFormat("HH:mm:ss");
	   return format.format(new Date());
   }
   static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
  
   public static String getString(Object o) {  
       StringBuffer sb = new StringBuffer();  
       sb.append("bean:[");  
       Field[] farr = o.getClass().getDeclaredFields();  
       for (Field field : farr) {  
           try {  
               field.setAccessible(true);  
               sb.append(field.getName());  
               sb.append("=");  
               if (field.get(o) instanceof Date) {  
                   sb.append(sdf.format(field.get(o)));  
               } else {  
                   sb.append(field.get(o));  
               }  
               sb.append("|");  
           } catch (Exception e) {  
               e.printStackTrace();  
           }  
       }  
       sb.append("]");  
       return sb.toString();  
   }  
   public static String formatDate(Date date) {
        String formatStr = "yyyy-MM-dd";
		if (date != null) {
			DateFormat format = new SimpleDateFormat(formatStr);
			return format.format(date);
			
		} else {
			return null;
		}
	}
}
