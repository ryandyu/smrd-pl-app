package com.sumridge.pl.processor;

import java.util.List;

import com.sumridge.pl.bean.PlDetailBean;
import com.sumridge.pl.bean.PlPropertyBean;
import com.sumridge.pl.service.PlResultService;

public class SetupProcessor extends PlBaseRefreshProcessor {

	@Override
	public List<PlDetailBean> doProcess(Object obj,PlResultService plResultService) {
		LOG.debug("refresh setup");
		List<PlDetailBean> rs = plResultService.calcResultByProperty((PlPropertyBean) obj);
		return rs;
	}

}
