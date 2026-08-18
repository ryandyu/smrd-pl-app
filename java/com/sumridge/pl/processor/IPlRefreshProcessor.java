package com.sumridge.pl.processor;

import java.util.List;

import com.sumridge.pl.bean.PlDetailBean;
import com.sumridge.pl.service.PlResultService;

public interface IPlRefreshProcessor {
  public List<PlDetailBean> doProcess(Object obj,PlResultService plResultService);
}
