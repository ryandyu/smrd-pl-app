package com.sumridge.pl.bean;

import java.util.Date;

import com.sumridge.pl.util.Constant;

public class TradeBean {
	private String tradeId;
	private String buySellInd;
	private double price;
	private double quantity;
	private double principal;
	private String traderAccount;
	private String securityId;
	private String statusInd;
	private Date   tradeDate;
	private String isin;
	
	private String tradeCcy = Constant.REPORT_CCY;
	private String settleCcy = Constant.REPORT_CCY;
	private double settleFxRate = 1.0;
	
	/**
	 * @return the isin
	 */
	public String getIsin() {
		return isin;
	}

	/**
	 * @param isin
	 *            the isin to set
	 */
	public void setIsin(String isin) {
		this.isin = isin;
	}

	public String getTradeId() {
		return tradeId;
	}

	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
	}

	/**
	 * @return the tradeDate
	 */
	public Date getTradeDate() {
		return tradeDate;
	}

	/**
	 * @param tradeDate
	 *            the tradeDate to set
	 */
	public void setTradeDate(Date tradeDate) {
		this.tradeDate = tradeDate;
	}

	/**
	 * @return the statusInd
	 */
	public String getStatusInd() {
		return statusInd;
	}

	/**
	 * @param statusInd
	 *            the statusInd to set
	 */
	public void setStatusInd(String statusInd) {
		this.statusInd = statusInd;
	}

	/**
	 * @return the buySellInd
	 */
	public String getBuySellInd() {
		return buySellInd;
	}

	/**
	 * @param buySellInd
	 *            the buySellInd to set
	 */
	public void setBuySellInd(String buySellInd) {
		this.buySellInd = buySellInd;
	}

	/**
	 * @return the price
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * @param price
	 *            the price to set
	 */
	public void setPrice(double price) {
		this.price = price;
	}

	public double getPrincipal()
    {
        return principal;
    }

    public void setPrincipal(double principal)
    {
        this.principal = principal;
    }

    /**
	 * @return the quantity
	 */
	public double getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity
	 *            the quantity to set
	 */
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the traderAccount
	 */
	public String getTraderAccount() {
		return traderAccount;
	}

	/**
	 * @param traderAccount
	 *            the traderAccount to set
	 */
	public void setTraderAccount(String traderAccount) {
		this.traderAccount = traderAccount;
	}

	/**
	 * @return the securityId
	 */
	public String getSecurityId() {
		return securityId;
	}

	/**
	 * @param securityId
	 *            the securityId to set
	 */
	public void setSecurityId(String securityId) {
		this.securityId = securityId;
	}

    public String getTradeCcy()
    {
        return tradeCcy;
    }

    public void setTradeCcy(String tradeCcy)
    {
        this.tradeCcy = tradeCcy;
    }

    public String getSettleCcy()
    {
        return settleCcy;
    }

    public void setSettleCcy(String settleCcy)
    {
        this.settleCcy = settleCcy;
    }

    public double getSettleFxRate()
    {
        return settleFxRate;
    }

    public void setSettleFxRate(double settleFxRate)
    {
        this.settleFxRate = settleFxRate;
    }

}
