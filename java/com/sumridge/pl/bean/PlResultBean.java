package com.sumridge.pl.bean;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(name = "root")  
@XmlAccessorType(XmlAccessType.FIELD)
public class PlResultBean {

	private String text;
    private List<PlNodeBean> children;
	/**
     * @return the text
     */
    public String getText() {
    	return text;
    }
	/**
     * @param text the text to set
     */
    public void setText(String text) {
    	this.text = text;
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
	
	
}
