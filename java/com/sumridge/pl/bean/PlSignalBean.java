package com.sumridge.pl.bean;

import com.sumridge.pl.util.Constant.Signal;

public class PlSignalBean {
	private Signal signal;
	private Object processObject;
	private String aggerate;
	
	
	

	/**
     * @return the aggerate
     */
    public String aggerate() {
    	return aggerate;
    }

	/**
     * @param aggerate the aggerate to set
     */
    public void setAggerate(String agg) {
    	this.aggerate = agg;
    }

	/**
	 * @return the signal
	 */
	public Signal getSignal() {
		return signal;
	}

	/**
	 * @param signal
	 *            the signal to set
	 */
	public void setSignal(Signal signal) {
		this.signal = signal;
	}

	/**
	 * @return the processObject
	 */
	public Object getProcessObject() {
		return processObject;
	}

	/**
	 * @param processObject
	 *            the processObject to set
	 */
	public void setProcessObject(Object processObject) {
		this.processObject = processObject;
	}

}
