package com.sumridge.pl.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RatingUtil
{
    /* 
     * Blend of a security's Moody's, S&P, Fitch, and DBRS ratings. The rating agencies are evenly weighted 
     * when calculating the composite. It is calculated by taking the average of the existing ratings, 
     * rounded down to the lower rating in case the composite is between two ratings, and drives the Bloomberg Composite Rating 
     * IG HY Indicator (RB417, BB_COMPSTE_RATING_IG_HY_INDCTR). A composite will not be generated if the bond is rated by 
     * only one of the four rating agencies. This composite is not intended to be a credit opinion. 
     * Expected ratings and unsolicited ratings, designated by "e" and "u", are not included in calculating the composite.
     * 
     */
    
    private static final Map<String, Integer> moodyRatingMap;
    
    public static Integer getMoodyRatingInteger(String rating){
        return moodyRatingMap.get(rating);
    }
    
    static {
        moodyRatingMap = new HashMap<String, Integer>();
        moodyRatingMap.put("AAA",  1);
        moodyRatingMap.put("AA1",  2);
        moodyRatingMap.put("AA2",  3);
        moodyRatingMap.put("AA3",  4);
        moodyRatingMap.put("A1",   5);
        moodyRatingMap.put("A2",   6);
        moodyRatingMap.put("A3",   7);
        moodyRatingMap.put("BAA1", 8);
        moodyRatingMap.put("BAA2", 9);
        moodyRatingMap.put("BAA3", 10);
        moodyRatingMap.put("BA1",  11);
        moodyRatingMap.put("BA2",  12);
        moodyRatingMap.put("BA3",  13);
        moodyRatingMap.put("B1",   14);
        moodyRatingMap.put("B2",   15);
        moodyRatingMap.put("B3",   16);
        moodyRatingMap.put("CAA1", 17);
        moodyRatingMap.put("CAA2", 18);
        moodyRatingMap.put("CAA3", 19);
        moodyRatingMap.put("CA",   20);
        moodyRatingMap.put("C",    21);
    }
    
    private static final Map<String, Integer> spRatingMap;
    
    public static Integer getSpRatingInteger(String rating){
        return spRatingMap.get(rating);
    }
    
    static {
        spRatingMap = new HashMap<String, Integer>();
        spRatingMap.put("AAA",  1);
        spRatingMap.put("AA+",  2);
        spRatingMap.put("AA",   3);
        spRatingMap.put("AA-",  4);
        spRatingMap.put("A+",   5);
        spRatingMap.put("A",    6);
        spRatingMap.put("A-",   7);
        spRatingMap.put("BBB+", 8);
        spRatingMap.put("BBB",  9);
        spRatingMap.put("BBB-", 10);
        spRatingMap.put("BB+",  11);
        spRatingMap.put("BB",   12);
        spRatingMap.put("BB-",  13);
        spRatingMap.put("B+",   14);
        spRatingMap.put("B",    15);
        spRatingMap.put("B-",   16);
        spRatingMap.put("CCC+", 17);
        spRatingMap.put("CCC",  18);
        spRatingMap.put("CCC-", 19);
        spRatingMap.put("CC",   20);
        spRatingMap.put("C",    21);
        spRatingMap.put("D",    22);
    }

    private static final Map<String, Integer> fitchRatingMap;
    
    public static Integer getFitchRatingInteger(String rating){
        return fitchRatingMap.get(rating);
    }
    
    static {
        fitchRatingMap = new HashMap<String, Integer>();
        fitchRatingMap.put("AAA",  1);
        fitchRatingMap.put("AA+",  2);
        fitchRatingMap.put("AA",   3);
        fitchRatingMap.put("AA-",  4);
        fitchRatingMap.put("A+",   5);
        fitchRatingMap.put("A",    6);
        fitchRatingMap.put("A-",   7);
        fitchRatingMap.put("BBB+", 8);
        fitchRatingMap.put("BBB",  9);
        fitchRatingMap.put("BBB-", 10);
        fitchRatingMap.put("BB+",  11);
        fitchRatingMap.put("BB",   12);
        fitchRatingMap.put("BB-",  13);
        fitchRatingMap.put("B+",   14);
        fitchRatingMap.put("B",    15);
        fitchRatingMap.put("B-",   16);
        fitchRatingMap.put("CCC+", 17);
        fitchRatingMap.put("CCC",  18);
        fitchRatingMap.put("CCC-", 19);
        fitchRatingMap.put("CC",   20);
        fitchRatingMap.put("C",    21);
    }
    
    public static String getRating(String m, String s, String f, String i)
    {
        int cr = 0, mr = 0, sr = 0, fr = 0, ir = 0;

        if (m != null)
            mr = toInt(getMoodyRatingInteger(m.trim().toUpperCase()));
        
        if (s != null)
            sr = toInt(getSpRatingInteger(s.trim().toUpperCase()));

        if (f != null)
            fr = toInt(getFitchRatingInteger(f.trim().toUpperCase()));

        if(i != null)
            ir = toInt(getMoodyRatingInteger(i.trim().toUpperCase()));
        
        cr = Math.max(Math.max(mr, sr), fr);
        
        if(cr < 1)
            cr = ir;
        
        if (cr < 1)
        {
            return null;
        }
        else
        {
            for (Iterator<Map.Entry<String, Integer>> itr = spRatingMap.entrySet().iterator(); itr.hasNext();)
            {
                Map.Entry<String, Integer> entry = itr.next();
                if (Math.abs(entry.getValue() - cr) <= 0.05)
                {
                    return entry.getKey();
                }
            }
            
            return null;
        }
    }
    
   private static int toInt(Integer i)
   {
       return i != null ? i.intValue() : 0;
   }
}
