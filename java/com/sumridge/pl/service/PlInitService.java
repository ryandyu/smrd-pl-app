package com.sumridge.pl.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.sumridge.pl.dao.BenchmarkRuleDAO;
import com.sumridge.pl.dao.CurrencyDAO;
import com.sumridge.pl.dao.PLResultDAO;
import com.sumridge.pl.dao.TradeBookDAO;
import com.sumridge.pl.dao.VVMsdDAO;

@Service
public class PlInitService {
	@Resource
	private BenchmarkRuleDAO benchmarkRuleDAO;
	@Resource
	private VVMsdDAO vVMsdDAO;
    @Resource
    private CurrencyDAO currencyDAO;
	@Resource
	private TradeBookDAO tradeBookDAO;
	@Resource
	private PLResultDAO pLResultDAO;

	public void doInit() {
		benchmarkRuleDAO.init();
		vVMsdDAO.buildVVMsdAll();
        currencyDAO.buildCurrencyList();
		tradeBookDAO.buildBookList();
		pLResultDAO.deleteResult();
	}
}
