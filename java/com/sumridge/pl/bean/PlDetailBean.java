package com.sumridge.pl.bean;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.ProvidedId;

import com.sumridge.pl.util.Constant;

//@Indexed
//@ProvidedId
public class PlDetailBean
{
    // @Field
    private String desk;
    // @Field
    private String book;
    // @Field
    private String product;
    // @Field
    private String cusip;
    // @Field
    private String currency = Constant.REPORT_CCY;

    private String traderAccount;
    private String bucket = "$";
    private double quantity;
    private double buyQuantity;
    private double sellQuantity;
    private double buyValue;
    private double sellValue;
    private double openPrice;
    private double plPrice;

    private double markPrice;
    private double udlPrice = 0d;

    private double pl;

    private double ir01 = 0;
    private RiskCurveBean ir01Curve;

    private double cr01 = 0;
    private RiskCurveBean cr01Curve;

    private double val;
    private boolean plCheck;
    private String defaultBenchmark;
    private String traderId;
    private double positionPnl;
    private double tradePnl;
    private Integer createDays;
    private Integer activityDays;
    private int buyCount;
    private int sellCount;
    private double marketValue;
    private double currentQuantity;
    private double openQuantity;
    private Integer creditSector;
    private String desc;
    private String isin;
    private String ticker;
    private String rating;

    /**
     * @return the isin
     */
    public String getIsin()
    {
        return isin;
    }

    /**
     * @param isin the isin to set
     */
    public void setIsin(String isin)
    {
        this.isin = isin;
    }

    /**
     * @return the desc
     */
    public String getDesc()
    {
        return desc;
    }

    /**
     * @param desc the desc to set
     */
    public void setDesc(String desc)
    {
        this.desc = desc;
    }

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
     * @return the currentQuantity
     */
    public double getCurrentQuantity()
    {
        return currentQuantity;
    }

    /**
     * @param currentQuantity the currentQuantity to set
     */
    public void setCurrentQuantity(double currentQuantity)
    {
        this.currentQuantity = currentQuantity;
    }

    /**
     * @return the openQuantity
     */
    public double getOpenQuantity()
    {
        return openQuantity;
    }

    /**
     * @param openQuantity the openQuantity to set
     */
    public void setOpenQuantity(double openQuantity)
    {
        this.openQuantity = openQuantity;
    }

    /**
     * @return the buyCount
     */
    public int getBuyCount()
    {
        return buyCount;
    }

    /**
     * @param buyCount the buyCount to set
     */
    public void setBuyCount(int buyCount)
    {
        this.buyCount = buyCount;
    }

    /**
     * @return the sellCount
     */
    public int getSellCount()
    {
        return sellCount;
    }

    /**
     * @param sellCount the sellCount to set
     */
    public void setSellCount(int sellCount)
    {
        this.sellCount = sellCount;
    }

    /**
     * @return the marketValue
     */
    public double getMarketValue()
    {
        return marketValue;
    }

    /**
     * @param marketValue the marketValue to set
     */
    public void setMarketValue(double marketValue)
    {
        this.marketValue = marketValue;
    }

    /**
     * @return the createDays
     */
    public Integer getCreateDays()
    {
        return createDays;
    }

    /**
     * @param createDays the createDays to set
     */
    public void setCreateDays(Integer createDays)
    {
        this.createDays = createDays;
    }

    /**
     * @return the activityDays
     */
    public Integer getActivityDays()
    {
        return activityDays;
    }

    /**
     * @param activityDays the activityDays to set
     */
    public void setActivityDays(Integer activityDays)
    {
        this.activityDays = activityDays;
    }

    /**
     * @return the tradePnl
     */
    public double getTradePnl()
    {
        return tradePnl;
    }

    /**
     * @param tradePnl the tradePnl to set
     */
    public void setTradePnl(double tradePnl)
    {
        this.tradePnl = tradePnl;
    }

    /**
     * @return the positionPnl
     */
    public double getPositionPnl()
    {
        return positionPnl;
    }

    /**
     * @param positionPnl the positionPnl to set
     */
    public void setPositionPnl(double positionPnl)
    {
        this.positionPnl = positionPnl;
    }

    /**
     * @return the traderId
     */
    public String getTraderId()
    {
        return traderId;
    }

    /**
     * @param traderId the traderId to set
     */
    public void setTraderId(String traderId)
    {
        this.traderId = traderId;
    }

    /**
     * @return the buyQuantity
     */
    public double getBuyQuantity()
    {
        return buyQuantity;
    }

    /**
     * @param buyQuantity the buyQuantity to set
     */
    public void setBuyQuantity(double buyQuantity)
    {
        this.buyQuantity = buyQuantity;
    }

    /**
     * @return the sellQuantity
     */
    public double getSellQuantity()
    {
        return sellQuantity;
    }

    /**
     * @param sellQuantity the sellQuantity to set
     */
    public void setSellQuantity(double sellQuantity)
    {
        this.sellQuantity = sellQuantity;
    }

    public double getBuyValue()
    {
        return buyValue;
    }

    public void setBuyValue(double buyValue)
    {
        this.buyValue = buyValue;
    }

    public double getSellValue()
    {
        return sellValue;
    }

    public void setSellValue(double sellValue)
    {
        this.sellValue = sellValue;
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
     * @return the plCheck
     */
    public boolean isPlCheck()
    {
        return plCheck;
    }

    /**
     * @param plCheck the plCheck to set
     */
    public void setPlCheck(boolean plCheck)
    {
        this.plCheck = plCheck;
    }

    /**
     * @return the traderAccount
     */
    public String getTraderAccount()
    {
        return traderAccount;
    }

    /**
     * @param traderAccount the traderAccount to set
     */
    public void setTraderAccount(String traderAccount)
    {
        this.traderAccount = traderAccount;
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

    /**
     * @return the val
     */
    public double getVal()
    {
        return val;
    }

    /**
     * @param val the val to set
     */
    public void setVal(double val)
    {
        this.val = val;
    }

    /**
     * @return the desk
     */
    public String getDesk()
    {
        return desk;
    }

    /**
     * @param desk the desk to set
     */
    public void setDesk(String desk)
    {
        this.desk = desk;
    }

    /**
     * @return the book
     */
    public String getBook()
    {
        return book;
    }

    /**
     * @param book the book to set
     */
    public void setBook(String book)
    {
        this.book = book;
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
     * @return the bucket
     */
    public String getBucket()
    {
        return bucket;
    }

    /**
     * @param bucket the bucket to set
     */
    public void setBucket(String bucket)
    {
        this.bucket = bucket;
    }

    /**
     * @return the quantity
     */
    public double getQuantity()
    {
        return quantity;
    }

    /**
     * @param quantity the quantity to set
     */
    public void setQuantity(double quantity)
    {
        this.quantity = quantity;
    }

    /**
     * @return the openPrice
     */
    public double getOpenPrice()
    {
        return openPrice;
    }

    /**
     * @param openPrice the openPrice to set
     */
    public void setOpenPrice(double openPrice)
    {
        this.openPrice = openPrice;
    }

    /**
     * @return the markPrice
     */
    public double getMarkPrice()
    {
        return markPrice;
    }

    /**
     * @param markPrice the markPrice to set
     */
    public void setMarkPrice(double markPrice)
    {
        this.markPrice = markPrice;
    }

    public double getUdlPrice()
    {
        return udlPrice;
    }

    public void setUdlPrice(double udlPrice)
    {
        this.udlPrice = udlPrice;
    }

    /**
     * @return the pl
     */
    public double getPl()
    {
        return pl;
    }

    /**
     * @param pl the pl to set
     */
    public void setPl(double pl)
    {
        this.pl = pl;
    }

    /**
     * @return the ir01
     */
    public double getIr01()
    {
        return ir01;
    }

    /**
     * @param ir01 the ir01 to set
     */
    public void setIr01(double ir01)
    {
        this.ir01 = ir01;
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

    public RiskCurveBean getIr01Curve()
    {
        return ir01Curve;
    }

    public void setIr01Curve(RiskCurveBean ir01Curve)
    {
        this.ir01Curve = ir01Curve;
    }

    public RiskCurveBean getCr01Curve()
    {
        return cr01Curve;
    }

    public void setCr01Curve(RiskCurveBean cr01Curve)
    {
        this.cr01Curve = cr01Curve;
    }

    public String getTicker()
    {
        return ticker;
    }

    public void setTicker(String ticker)
    {
        this.ticker = ticker;
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
