package com.sumridge.pl.bean;

public class RiskCurveBean
{
    protected double t0M;
    protected double t3M;
    protected double t6M;
    protected double t1Y;
    protected double t2Y;
    protected double t3Y;
    protected double t5Y;
    protected double t7Y;
    protected double t10Y;
    protected double t20Y;
    protected double t30Y;
    
    public void scale(double multipler)
    {
        t0M *= multipler;
        t3M *= multipler;
        t6M *= multipler;
        t1Y *= multipler;
        t2Y *= multipler;
        t3Y *= multipler;
        t5Y *= multipler;
        t7Y *= multipler;
        t10Y *= multipler;
        t20Y *= multipler;
        t30Y *= multipler;
    }
    
    public static void copyTo(RiskCurveBean bean, com.sumridge.xml.jaxb.risk.CurveRiskType curve)
    {
        curve.setT0M(bean.getT0M());
        curve.setT3M(bean.getT3M());
        curve.setT6M(bean.getT6M());
        curve.setT1Y(bean.getT1Y());
        curve.setT2Y(bean.getT2Y());
        curve.setT3Y(bean.getT3Y());
        curve.setT5Y(bean.getT5Y());
        curve.setT7Y(bean.getT7Y());
        curve.setT10Y(bean.getT10Y());
        curve.setT20Y(bean.getT20Y());
        curve.setT30Y(bean.getT30Y());
    }
    
    public static void copyFrom(RiskCurveBean bean, com.sumridge.xml.jaxb.price.CurveRiskType curve)
    {
        bean.setT0M(curve.getT0M());
        bean.setT3M(curve.getT3M());
        bean.setT6M(curve.getT6M());
        bean.setT1Y(curve.getT1Y());
        bean.setT2Y(curve.getT2Y());
        bean.setT3Y(curve.getT3Y());
        bean.setT5Y(curve.getT5Y());
        bean.setT7Y(curve.getT7Y());
        bean.setT10Y(curve.getT10Y());
        bean.setT20Y(curve.getT20Y());
        bean.setT30Y(curve.getT30Y());
    }
    
    public double getT0M()
    {
        return t0M;
    }
    public void setT0M(double t0m)
    {
        t0M = t0m;
    }
    public double getT3M()
    {
        return t3M;
    }
    public void setT3M(double t3m)
    {
        t3M = t3m;
    }
    public double getT6M()
    {
        return t6M;
    }
    public void setT6M(double t6m)
    {
        t6M = t6m;
    }
    public double getT1Y()
    {
        return t1Y;
    }
    public void setT1Y(double t1y)
    {
        t1Y = t1y;
    }
    public double getT2Y()
    {
        return t2Y;
    }
    public void setT2Y(double t2y)
    {
        t2Y = t2y;
    }
    public double getT3Y()
    {
        return t3Y;
    }
    public void setT3Y(double t3y)
    {
        t3Y = t3y;
    }
    public double getT5Y()
    {
        return t5Y;
    }
    public void setT5Y(double t5y)
    {
        t5Y = t5y;
    }
    public double getT7Y()
    {
        return t7Y;
    }
    public void setT7Y(double t7y)
    {
        t7Y = t7y;
    }
    public double getT10Y()
    {
        return t10Y;
    }
    public void setT10Y(double t10y)
    {
        t10Y = t10y;
    }
    public double getT20Y()
    {
        return t20Y;
    }
    public void setT20Y(double t20y)
    {
        t20Y = t20y;
    }

    public double getT30Y()
    {
        return t30Y;
    }
    public void setT30Y(double t30y)
    {
        t30Y = t30y;
    }
    
    
}
