package com.sumridge.pl.bean;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.ProvidedId;

import com.sumridge.pl.util.CollectionToCSVBridge;
import com.sumridge.pl.util.Constant;

@Indexed
@ProvidedId
public class PositionBean {
	@Field
	private String traderAccount;
	private double currentQuantity;
	private double openQuantity;
	private double openPrice;
	@Field
	private String securityId;
	private Map<String, TradeBean> tradeList = new ConcurrentHashMap<String, TradeBean>();

	private String traderId;
	private Integer createDays;
	private Integer activityDays;
	private String isin;
	
	private String currency = Constant.REPORT_CCY;
	
	@Field
	@FieldBridge(impl = CollectionToCSVBridge.class)
	private Set<String> stlCurrencies = new HashSet<String>();

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

	/**
	 * @return the createDays
	 */
	public Integer getCreateDays() {
		return createDays;
	}

	/**
	 * @param createDays
	 *            the createDays to set
	 */
	public void setCreateDays(Integer createDays) {
		this.createDays = createDays;
	}

	/**
	 * @return the activityDays
	 */
	public Integer getActivityDays() {
		return activityDays;
	}

	/**
	 * @param activityDays
	 *            the activityDays to set
	 */
	public void setActivityDays(Integer activityDays) {
		this.activityDays = activityDays;
	}

	/**
	 * @return the traderId
	 */
	public String getTraderId() {
		return traderId;
	}

	/**
	 * @param traderId
	 *            the traderId to set
	 */
	public void setTraderId(String traderId) {
		this.traderId = traderId;
	}

	/**
	 * @return the openPrice
	 */
	public double getOpenPrice() {
		return openPrice;
	}

	/**
	 * @param openPrice
	 *            the openPrice to set
	 */
	public void setOpenPrice(double openPrice) {
		this.openPrice = openPrice;
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
	 * @return the currentQuantity
	 */
	public double getCurrentQuantity() {
		return currentQuantity;
	}

	/**
	 * @param currentQuantity
	 *            the currentQuantity to set
	 */
	public void setCurrentQuantity(double currentQuantity) {
		this.currentQuantity = currentQuantity;
	}

	/**
	 * @return the openQuantity
	 */
	public double getOpenQuantity() {
		return openQuantity;
	}

	/**
	 * @param openQuantity
	 *            the openQuantity to set
	 */
	public void setOpenQuantity(double openQuantity) {
		this.openQuantity = openQuantity;
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

	/**
	 * @return the tradeList
	 */
	public Map<String, TradeBean> getTradeList() {
		return tradeList;
	}

	/**
	 * @param tradeList
	 *            the tradeList to set
	 */
	public void setTradeList(Map<String, TradeBean> tradeList) {
		this.tradeList = tradeList;
	}

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public Set<String> getStlCurrencies()
    {
        return stlCurrencies;
    }

    public void setStlCurrencies(Set<String> stlCurrencies)
    {
        this.stlCurrencies = stlCurrencies;
    }

}
