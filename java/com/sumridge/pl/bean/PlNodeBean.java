package com.sumridge.pl.bean;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "PlNodeBean")  
@XmlAccessorType(XmlAccessType.FIELD)
public class PlNodeBean {
	private String plName;
	private int qty;
	private int blank;
	private int m3;
	private int m6;
	private int y2;
	private int y3;
	private int y5;
	private int y7;
	private int y10;
	private int y30;
	private int total;
	private boolean expanded = Boolean.FALSE;
	private List<PlNodeBean> children;
	private boolean leaf;
	private boolean highlight;
	private String ticker;
	private String coupon;
	private String maturity;
	
	
	
	
	/**
     * @return the qty
     */
    public int getQty() {
    	return qty;
    }
	/**
     * @param qty the qty to set
     */
    public void setQty(int qty) {
    	this.qty = qty;
    }
	/**
     * @return the ticker
     */
    public String getTicker() {
    	return ticker;
    }
	/**
     * @param ticker the ticker to set
     */
    public void setTicker(String ticker) {
    	this.ticker = ticker;
    }
	/**
     * @return the coupon
     */
    public String getCoupon() {
    	return coupon;
    }
	/**
     * @param coupon the coupon to set
     */
    public void setCoupon(String coupon) {
    	this.coupon = coupon;
    }
	/**
     * @return the maturity
     */
    public String getMaturity() {
    	return maturity;
    }
	/**
     * @param maturity the maturity to set
     */
    public void setMaturity(String maturity) {
    	this.maturity = maturity;
    }
	/**
     * @return the highlight
     */
    public boolean isHighlight() {
    	return highlight;
    }
	/**
     * @param highlight the highlight to set
     */
    public void setHighlight(boolean highlight) {
    	this.highlight = highlight;
    }
	/**
     * @return the plName
     */
    public String getPlName() {
    	return plName;
    }
	/**
     * @param plName the plName to set
     */
    public void setPlName(String plName) {
    	this.plName = plName;
    }
	/**
     * @return the blank
     */
    public int getBlank() {
    	return blank;
    }
	/**
     * @param blank the blank to set
     */
    public void setBlank(int blank) {
    	this.blank = blank;
    }
	/**
     * @return the m3
     */
    public int getM3() {
    	return m3;
    }
	/**
     * @param m3 the m3 to set
     */
    public void setM3(int m3) {
    	this.m3 = m3;
    }
	/**
     * @return the m6
     */
    public int getM6() {
    	return m6;
    }
	/**
     * @param m6 the m6 to set
     */
    public void setM6(int m6) {
    	this.m6 = m6;
    }
	/**
     * @return the y2
     */
    public int getY2() {
    	return y2;
    }
	/**
     * @param y2 the y2 to set
     */
    public void setY2(int y2) {
    	this.y2 = y2;
    }
	/**
     * @return the y3
     */
    public int getY3() {
    	return y3;
    }
	/**
     * @param y3 the y3 to set
     */
    public void setY3(int y3) {
    	this.y3 = y3;
    }
	/**
     * @return the y5
     */
    public int getY5() {
    	return y5;
    }
	/**
     * @param y5 the y5 to set
     */
    public void setY5(int y5) {
    	this.y5 = y5;
    }
	/**
     * @return the y7
     */
    public int getY7() {
    	return y7;
    }
	/**
     * @param y7 the y7 to set
     */
    public void setY7(int y7) {
    	this.y7 = y7;
    }
	/**
     * @return the y10
     */
    public int getY10() {
    	return y10;
    }
	/**
     * @param y10 the y10 to set
     */
    public void setY10(int y10) {
    	this.y10 = y10;
    }
	/**
     * @return the y30
     */
    public int getY30() {
    	return y30;
    }
	/**
     * @param y30 the y30 to set
     */
    public void setY30(int y30) {
    	this.y30 = y30;
    }
	/**
     * @return the total
     */
    public int getTotal() {
    	return total;
    }
    
	/**
     * @param total the total to set
     */
    public void setTotal(int total) {
    	this.total = total;
    }
	/**
     * @return the expanded
     */
    public boolean isExpanded() {
    	return expanded;
    }
	/**
     * @param expanded the expanded to set
     */
    public void setExpanded(boolean expanded) {
    	this.expanded = expanded;
    }
	/**
     * @return the children
     */
    public List<PlNodeBean> getChildren() {
    	return children;
    }
	/**
     * @param children the children to set
     */
    public void setChildren(List<PlNodeBean> children) {
    	this.children = children;
    }
	/**
     * @return the leaf
     */
    public boolean isLeaf() {
    	return leaf;
    }
	/**
     * @param leaf the leaf to set
     */
    public void setLeaf(boolean leaf) {
    	this.leaf = leaf;
    }
	
	
}
