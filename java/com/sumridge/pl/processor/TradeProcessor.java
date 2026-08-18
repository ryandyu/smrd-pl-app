package com.sumridge.pl.processor;

import java.util.ArrayList;
import java.util.List;

import com.sumridge.pl.bean.PlDetailBean;
import com.sumridge.pl.bean.TradeBean;
import com.sumridge.pl.service.PlResultService;

public class TradeProcessor extends PlBaseRefreshProcessor {

	@Override
	public List<PlDetailBean> doProcess(Object obj,PlResultService plResultService) {
		LOG.debug("refresh trade");
		PlDetailBean bean = plResultService.calcResultByTrade((TradeBean) obj);
		List<PlDetailBean> rs = new ArrayList<PlDetailBean>();
		rs.add(bean);
		return rs;
	}

}
