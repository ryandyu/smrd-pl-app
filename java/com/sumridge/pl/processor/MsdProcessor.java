package com.sumridge.pl.processor;

import java.util.List;

import com.sumridge.pl.bean.PlDetailBean;
import com.sumridge.pl.service.PlResultService;

public class MsdProcessor extends PlBaseRefreshProcessor {

	@Override
	public List<PlDetailBean> doProcess(Object obj,PlResultService plResultService) {
		LOG.info("refresh msd");
		
		List<PlDetailBean> rs = plResultService.calcResultByCusip((String)obj, true);
		return rs;
	}

}
