package com.sumridge.pl.bean;

public class PriceBean
{
    private int office;
    private int account;
    private String cusip;
    private double dv01;
    private double cr01;
    private double plPrice;
    private RiskCurveBean dv01Curve;
    private RiskCurveBean cr01Curve;

    /**
     * @return the office
     */
    public int getOffice()
    {
        return office;
    }

    /**
     * @param office the office to set
     */
    public void setOffice(int office)
    {
        this.office = office;
    }

    /**
     * @return the account
     */
    public int getAccount()
    {
        return account;
    }

    /**
     * @param account the account to set
     */
    public void setAccount(int account)
    {
        this.account = account;
    }

    /**
     * @return the cusip
     */
    public String getCusip()
    {
        return cusip;
    }

    /**
     * @param cusip the cusip to set
     */
    public void setCusip(String cusip)
    {
        this.cusip = cusip;
    }

    /**
     * @return the dv01
     */
    public double getDv01()
    {
        return dv01;
    }

    /**
     * @param dv01 the dv01 to set
     */
    public void setDv01(double dv01)
    {
        this.dv01 = dv01;
    }

    /**
     * @return the cr01
     */
    public double getCr01()
    {
        return cr01;
    }

    /**
     * @param cr01 the cr01 to set
     */
    public void setCr01(double cr01)
    {
        this.cr01 = cr01;
    }

    /**
     * @return the plPrice
     */
    public double getPlPrice()
    {
        return plPrice;
    }

    /**
     * @param plPrice the plPrice to set
     */
    public void setPlPrice(double plPrice)
    {
        this.plPrice = plPrice;
    }

    public RiskCurveBean getDv01Curve()
    {
        return dv01Curve;
    }

    public void setDv01Curve(RiskCurveBean dv01Curve)
    {
        this.dv01Curve = dv01Curve;
    }

    public RiskCurveBean getCr01Curve()
    {
        return cr01Curve;
    }

    public void setCr01Curve(RiskCurveBean cr01Curve)
    {
        this.cr01Curve = cr01Curve;
    }

}
