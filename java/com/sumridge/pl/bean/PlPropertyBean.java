package com.sumridge.pl.bean;

import com.sumridge.pl.util.Constant;

public class PlPropertyBean
{

    private String cusip;
    private String secId;

    private String product;

    private String defaultBenchmark;
    private String benchmark;
    private String ultimateBenchmark;

    private String hedgeSector;
    private Integer creditSector;

    private String ticker;
    private String coupon;
    private String maturity;
    private double factor = 1.0;
    private String currency = Constant.REPORT_CCY;
    private String rating;

    /**
     * @return the creditSector
     */
    public Integer getCreditSector()
    {
        return creditSector;
    }

    /**
     * @param creditSector the creditSector to set
     */
    public void setCreditSector(Integer creditSector)
    {
        this.creditSector = creditSector;
    }

    /**
     * @return the ticker
     */
    public String getTicker()
    {
        return ticker;
    }

    /**
     * @param ticker the ticker to set
     */
    public void setTicker(String ticker)
    {
        this.ticker = ticker;
    }

    /**
     * @return the coupon
     */
    public String getCoupon()
    {
        return coupon;
    }

    /**
     * @param coupon the coupon to set
     */
    public void setCoupon(String coupon)
    {
        this.coupon = coupon;
    }

    /**
     * @return the maturity
     */
    public String getMaturity()
    {
        return maturity;
    }

    /**
     * @param maturity the maturity to set
     */
    public void setMaturity(String maturity)
    {
        this.maturity = maturity;
    }

    /**
     * @return the cusip
     */
    public String getCusip()
    {
        return cusip;
    }

    /**
     * @param cuisp the cusip to set
     */
    public void setCusip(String cusip)
    {
        this.cusip = cusip;
    }

    /**
     * @return the product
     */
    public String getProduct()
    {
        return product;
    }

    /**
     * @param product the product to set
     */
    public void setProduct(String product)
    {
        this.product = product;
    }

    /**
     * @return the defaultBenchmark
     */
    public String getDefaultBenchmark()
    {
        return defaultBenchmark;
    }

    /**
     * @param defaultBenchmark the defaultBenchmark to set
     */
    public void setDefaultBenchmark(String defaultBenchmark)
    {
        this.defaultBenchmark = defaultBenchmark;
    }

    /**
     * @return the hedgeSector
     */
    public String getHedgeSector()
    {
        return hedgeSector;
    }

    /**
     * @param hedgeSector the hedgeSector to set
     */
    public void setHedgeSector(String hedgeSector)
    {
        this.hedgeSector = hedgeSector;
    }

    /**
     * @return the ultimateBenchmark
     */
    public String getUltimateBenchmark()
    {
        return ultimateBenchmark;
    }

    /**
     * @param ultimateBenchmark the ultimateBenchmark to set
     */
    public void setUltimateBenchmark(String ultimateBenchmark)
    {
        this.ultimateBenchmark = ultimateBenchmark;
    }

    /**
     * @return the benchmark
     */
    public String getBenchmark()
    {

        return benchmark;
    }

    /**
     * @param benchmark the benchmark to set
     */
    public void setBenchmark(String benchmark)
    {
        this.benchmark = benchmark;
    }

    public double getFactor()
    {
        return factor;
    }

    public void setFactor(double factor)
    {
        this.factor = factor;
    }

    public String getSecId()
    {
        return secId;
    }

    public void setSecId(String secId)
    {
        this.secId = secId;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getRating()
    {
        return rating;
    }

    public void setRating(String rating)
    {
        this.rating = rating;
    }
}
