package com.sumridge.pl.bean;

public class CurrencyBean
{
    private String ccy;
    private String inverse;
    private String deliverable;
    private double openRate;
    private double closeRate;
    
    public String getCcy()
    {
        return ccy;
    }
    public void setCcy(String ccy)
    {
        this.ccy = ccy;
    }
    public String getInverse()
    {
        return inverse;
    }
    public void setInverse(String inverse)
    {
        this.inverse = inverse;
    }
    public String getDeliverable()
    {
        return deliverable;
    }
    public void setDeliverable(String deliverable)
    {
        this.deliverable = deliverable;
    }
    public double getOpenRate()
    {
        return openRate;
    }
    public void setOpenRate(double openRate)
    {
        this.openRate = openRate;
    }
    public double getCloseRate()
    {
        return closeRate;
    }
    public void setCloseRate(double closeRate)
    {
        this.closeRate = closeRate;
    }

}
