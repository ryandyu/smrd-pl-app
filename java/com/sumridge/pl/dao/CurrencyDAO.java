package com.sumridge.pl.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.Resource;

import org.infinispan.manager.CacheContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Component;

import com.sumridge.pl.bean.CurrencyBean;
import com.sumridge.pl.util.Constant;

@Component
public class CurrencyDAO
{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Resource
    protected CacheContainer cacheManager;

    protected String sqlA = "select code,inverse,deliverable,close_rate from smrd_currency";
    protected String sqlI = "select code,inverse,deliverable,close_rate from smrd_currency where code = ?";
    
    public void buildCurrencyList()
    {
        jdbcTemplate.query(sqlA, new RowCallbackHandler() {

            @Override
            public void processRow(ResultSet rs) throws SQLException
            {
                fetchByRow(rs);
            }
        });
    }

    public CurrencyBean getCurrency(String ccy)
    {
        CurrencyBean bean = (CurrencyBean)cacheManager.getCache(Constant.CURRENCY_CACHE).get(ccy);
        if(bean == null)
        {
            jdbcTemplate.query(sqlI, new Object[] { ccy }, new RowCallbackHandler() {

                @Override
                public void processRow(ResultSet rs) throws SQLException
                {
                    fetchByRow(rs);
                }
            });
            
            bean = (CurrencyBean)cacheManager.getCache(Constant.CURRENCY_CACHE).get(ccy);
        }
        
        return bean;
    }
     
    private void fetchByRow(ResultSet rs) throws SQLException
    {
        CurrencyBean bean = new CurrencyBean();
        bean.setCcy(rs.getString("code"));
        bean.setInverse(rs.getString("inverse"));
        bean.setDeliverable(rs.getString("deliverable"));
        bean.setCloseRate(rs.getDouble("close_rate"));
    
        cacheManager.getCache(Constant.CURRENCY_CACHE).put(bean.getCcy(), bean);
    } 
}
