package com.sumridge.pl.processor;

import java.util.ArrayList;
import java.util.List;

import com.sumridge.pl.bean.PlDetailBean;
import com.sumridge.pl.bean.PositionBean;
import com.sumridge.pl.service.PlResultService;

public class PositionProcessor extends PlBaseRefreshProcessor {

	@Override
	public List<PlDetailBean> doProcess(Object obj,PlResultService plResultService) {
		LOG.debug("refresh postion");
		PlDetailBean bean = plResultService.calcResultByPosition((PositionBean) obj);
		List<PlDetailBean> rs = new ArrayList<PlDetailBean>();
		rs.add(bean);
		return rs;
	}

}
