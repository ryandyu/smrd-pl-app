package com.sumridge.pl.processor;

import java.util.List;

import com.sumridge.pl.bean.PlDetailBean;
import com.sumridge.pl.bean.PriceBean;
import com.sumridge.pl.service.PlResultService;

public class PriceProcessor extends PlBaseRefreshProcessor {

	@Override
	public List<PlDetailBean> doProcess(Object obj,PlResultService plResultService) {
		LOG.debug("refresh price");
		List<PlDetailBean> rs = plResultService.calcResultByPrice((PriceBean) obj);
		return rs;
	}

}
