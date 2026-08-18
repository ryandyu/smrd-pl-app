package com.sumridge.pl.processor;

import java.util.List;

import com.sumridge.pl.bean.PlDetailBean;
import com.sumridge.pl.service.PlResultService;

public class TotalProcessor extends PlBaseRefreshProcessor {

	@Override
	public List<PlDetailBean> doProcess(Object obj,PlResultService plResultService) {
		LOG.info("refresh total");
		
		return null;
	}

}
