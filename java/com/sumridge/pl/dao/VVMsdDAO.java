package com.sumridge.pl.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.infinispan.manager.CacheContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Component;

import com.sumridge.pl.bean.PlPropertyBean;
import com.sumridge.pl.util.Constant;
import com.sumridge.pl.util.PlUtil;
import com.sumridge.pl.util.RatingUtil;

@Component
public class VVMsdDAO {
    public static Logger LOG = LoggerFactory.getLogger(VVMsdDAO.class);
    
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private BenchmarkRuleDAO benchmarkRuleDAO;
	
	@Resource
	protected CacheContainer cacheManager;
    
	@Autowired(required=false)
    @Qualifier("databaseType")
    private String databaseType;
    
	public void buildVVMsd(final String cusip) {
		
        String sql = "select s.cusip,s.market_sector_des,s.maturity,s.ticker,s.cpn,s.factor,COALESCE(m.secid, s.cusip) as secid from vv_msd s "
                   + "left outer join smrd_secid_map m on m.isin=s.id_isin where s.cusip = ? "
                   + "union " 
                   + "select m.secid as cusip,s.market_sector_des,s.maturity,s.ticker,s.cpn,s.factor,m.secid, case when  from vv_msd s " 
                   + "join smrd_secid_map m on m.isin=s.id_isin where m.secid = ? " 
                   + "union " 
                   + "select cusip,market_sector_des,maturity,ticker,cpn,factor,cusip as secid, 'USD' as currency from smrd_wh..vv_muniview where cusip = ? ";
        
        String DBO = "ORACLE".equalsIgnoreCase(databaseType) ? "." : ".dbo.";
        
            sql = ""
                + "select  "
                + "t.cusip "
                + ", case "
                + "   when adj.asset_type = 2 then 'Corp' "
                + "   when adj.asset_type = 3 then 'Govt' "
                + "   when adj.asset_type = 6 then 'Corp' "
                + "   when adj.asset_type = 7 then 'Equity' "
                + "   when adj.asset_type = 8 then 'Pfd' "
                + "   when adj.asset_type = 11 then 'Agy' "
                + "   when im.idc_asset_type = 1 then 'Muni' "
                + "   when im.idc_asset_type = 2 and fi.debt_type = 14 then 'Mtge' "
                + "   when im.idc_asset_type = 2 then 'Corp' "
                + "   when im.idc_asset_type = 3 then 'Govt' "
                + "   when im.idc_asset_type = 11 then 'Agcy' "
                + "   else '?' "
                + "  end as market_sector_des "
                + ", case "
                + "   when adj.maturity is not null then adj.maturity "
                + "   when fi.maturity_date is not null then fi.maturity_date "
                + "   when md.bond_maturity_type=3 and fi.first_coupon_date is not null "
                + ("ORACLE".equalsIgnoreCase(databaseType) ? 
                      "   then to_date ('2152' || '-' || extract(month from fi.first_coupon_date) || '-' || extract(day from fi.first_coupon_date), 'YYYY-MM-DD') "
                    : "   then convert (date, '2152' + '-' + convert(varchar, datepart(mm, fi.first_coupon_date)) + '-' + convert(varchar, datepart(dd, fi.first_coupon_date))) ")
                + "  end as maturity "
                + ", coalesce(a.symbol, adj.ticker, x2.symbol, om.bond_ticker, " + ("ORACLE".equalsIgnoreCase(databaseType) ? "substr" : "substring") + "(om.primary_name, 1, 10)) as ticker "
                + ", case "
                + "    when adj.cpn is not null then adj.cpn "
                + "    else coalesce(fi.current_coupon_rate, fi.original_coupon_rate) "
                + "  end as cpn "
                + ", coalesce(adj.factor, 1) as factor "
                + ", case when trim(adj.currencyCode) is not null then adj.currencyCode when trim(im.primary_currency_code) is null then 'USD' else im.primary_currency_code end as currency "
                + ", mdy.rating moodyRating, sp.rating spRating, h.fitchRating, e.ice_rating iceRating "
                + ", case when im.idc_asset_type = 1 and de.instrument_id is not null then 'Y' when im.idc_asset_type != 1 and fi.default_indicator = 1 then 'Y' else 'N' end as defaulted "
                + ", t.secid "
                + "from  "
                + "(select distinct p.cusip, coalesce(m1.cusip, m2.cusip, p.cusip) as symbol, coalesce(m1.secid, m2.secid, p.cusip) as secid, coalesce(m1.isin, m2.isin, p.secid) as isin "
                + "from (select ? as cusip, null as secid from dual) p "
                + "left outer join  smrd_secid_map m1 on p.cusip=m1.secid "
                + "left outer join  smrd_secid_map m2 on p.cusip=m2.cusip "
                + ") t "
                + "join smrd_wh" + DBO + "idc_instrument_xref x on x.symbol_type in (1,5) and x.symbol=t.symbol "
//              + "join smrd_wh" + DBO + "idc_instrument_xref x on x.symbol_type = 2 and x.symbol=t.isin "
                + "join smrd_wh" + DBO + "idc_instrument_master im on x.instrument_id=im.instrument_id "
                + "join smrd_wh" + DBO + "idc_organization_master om on im.organization_id=om.organization_id "
                + "join smrd_wh" + DBO + "idc_fixed_income fi on x.instrument_id=fi.instrument_id "
                + "left outer join smrd_wh" + DBO + "idc_instrument_xref x2 on x.instrument_id=x2.instrument_id and x2.symbol_type=20 "
                + "left outer join smrd_wh" + DBO + "idc_maturity_details md on x.instrument_id=md.instrument_id "
                + "left outer join smrd_wh" + DBO + "idc_adj adj on x.instrument_id=adj.instrument_id "

                + "left outer join (select distinct instrument_id from smrd_wh" + DBO + "idc_default_event where default_event_type = '1') de on x.instrument_id=de.instrument_id "

                + "left outer join smrd_wh" + DBO + "idc_ratings mdy on x.instrument_id=mdy.instrument_id and mdy.agency='Moody''s' and mdy.rating_type = 'Long Rating' "
                + "left outer join smrd_wh" + DBO + "idc_ratings sp on x.instrument_id=sp.instrument_id and sp.agency='Standard & Poor''s' and sp.rating_Type='Long Rating' "
                + "left outer join (select cusip, min(fitchRating) fitchRating from pers_hcma h, smrd_date d where h.tradedate=d.batchDate group by cusip) h on h.cusip=t.symbol "
                + "left outer join smrd_cusip_ext e on t.cusip=e.cusip "
                
                + "left outer join smrd_active a on a.cusip=t.cusip and a.symbol like '%Y' "
                + " "
                + "union "
                + " "
                + "select "
                + "t.cusip "
                + ", case "
                + "   when b.productkey=2 then 'Equity' "
                + "   when b.productkey=3 then 'Muni' "
                + "   when b.productkey=4 then 'Pfd' "
                + "   when b.productkey=7 then 'Govt' "
                + "   when b.productkey=8 then 'Corp' "
                + "   when b.productkey=9 then 'Corp' "
                + "   when b.productkey=11 then 'Mtge' "
                + "   else ' ' "
                + "  end as market_sector_des "
                + ", b.maturity "
                + ", b.ticker "
                + ", b.coupon as cpn "
                + ", coalesce(b.factor, 1) as factor "
                + ", case when b.currency = ' ' or b.currency is null then 'USD' else b.currency end as currency "
                + ", null moodyRating, null spRating, null fitchRating, null iceRating "
                + ", 'N' as defaulted "
                + ", t.secid "
                + "from "
                + "(select distinct p.cusip, coalesce(m1.secid, m2.secid, p.cusip) as secid, coalesce(m1.isin, m2.isin, p.secid) as isin "
                + "from (select ? as cusip, null as secid from dual) p "
                + "left outer join  smrd_secid_map m1 on p.cusip=m1.secid "
                + "left outer join  smrd_secid_map m2 on p.cusip=m2.cusip "
                + ") t "
                + "join bbt_msd b on b.bbid=t.cusip "
                + "left outer join (select cusip, min(fitchRating) fitchRating from pers_hcma h, smrd_date d where h.tradedate=d.batchDate group by cusip) h on h.cusip=t.cusip "
                + "left outer join smrd_cusip_ext e on t.cusip=e.cusip "
                + "where "
                + "not exists (select 1 from smrd_wh" + DBO + "idc_instrument_xref x where x.symbol_type = 2 and b.secid=x.symbol) "
                + " "
                + "union "
                + " "
                + "select ccy as cusip, 'Ccy' as market_sector_des, null as maturity, ccy as ticker, null as cpn, 1 factor, ccy as currency "
                + ", null moodyRating, null spRating, null fitchRating, null iceRating "
                + ", 'N' as defaulted "
                + ", ccy as secid "
                + "from (select ? as ccy from dual) t "
                + "where " + ("ORACLE".equalsIgnoreCase(databaseType) ? "length" : "len") + "(rtrim(ccy)) = 3 "
                + " "
                + "union "
                + " "
                + "select b.cusip, 'Option' market_sector_des, null maturity, b.opt_bbsymbol ticker, null as cpn, coalesce(b.opt_contract_size, 100) as factor "
                + ", 'USD' as currency, null moodyRating, null spRating, null fitchRating, null iceRating "
                + ", 'N' as defaulted "
                + ", null secid "
                + "from smrd_option_map b where b.cusip = ? "
                + " ";
        
        jdbcTemplate.query(sql, new Object[] { cusip, cusip, cusip, cusip }, new RowCallbackHandler() {

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				PlPropertyBean sec = newVVMsd(rs, (PlPropertyBean) cacheManager.getCache(Constant.PROPERTY_CACHE).get(cusip));
				cacheManager.getCache(Constant.PROPERTY_CACHE).put(sec.getCusip(), sec);
			}

		});
	}
	
	private PlPropertyBean newVVMsd(ResultSet rs, PlPropertyBean bean) throws SQLException {
	    if(bean == null)
	        bean = new PlPropertyBean();
	    
	    try 
	    {
    		bean.setCusip(StringUtils.trim(rs.getString("cusip")));
    		bean.setSecId(StringUtils.trim(rs.getString("secid")));
            bean.setProduct(StringUtils.trim(rs.getString("market_sector_des")));
            bean.setTicker(StringUtils.trim(rs.getString("ticker")));
         
            if("Ccy".equals(bean.getProduct()) || "Equity".equals(bean.getProduct()) || "Pfd".equals(bean.getProduct()))
                bean.setDefaultBenchmark("$");
            else if("Option".equals(bean.getProduct()))
                bean.setDefaultBenchmark("$");
            else if(rs.getDate("maturity") != null)
    			bean.setDefaultBenchmark(benchmarkRuleDAO.getActiveAs(rs.getDate("maturity")));
    		else 
    			bean.setDefaultBenchmark("30Y");
    		
            if(bean.getCusip() != null && (bean.getCusip().startsWith("91279") || bean.getCusip().startsWith("9128")))
            {
                bean.setUltimateBenchmark(bean.getDefaultBenchmark()); //added 1/6/2017
                bean.setDefaultBenchmark(bean.getTicker());
                bean.setTicker("TSY");
            }
    
            bean.setCoupon(rs.getString("cpn"));
    		bean.setMaturity(PlUtil.formatDate(rs.getDate("maturity")));
    		
            bean.setFactor(rs.getDouble("factor"));
    		if("Ccy".equals(bean.getProduct()))
    		    bean.setFactor(100.0);
    		else if(bean.getFactor() < 1.0e-16)
    		    bean.setFactor(1.0);
    		
    		bean.setCurrency(rs.getString("currency"));
    		if(StringUtils.isBlank(bean.getCurrency()) || ".".equals(bean.getCurrency()))
    		    bean.setCurrency(Constant.REPORT_CCY);
    
    		//rating
            String mdyRating = rs.getString("moodyRating");
            String spRating = rs.getString("spRating");
            String fitchRating = rs.getString("fitchRating");
    		String iceRating = rs.getString("iceRating");
    		
    		String defaulted = rs.getString("defaulted");
    		
    		bean.setRating(RatingUtil.getRating(mdyRating, spRating, null, null));
    		
    		if("Y".equals(defaulted) && bean.getRating() == null)
    		    bean.setRating("D"); 
    		
    		LOG.info(bean.getCusip() + " " + bean.getRating() + " " + bean.getProduct() +  " " + bean.getTicker() + " " + bean.getCurrency() + " " + defaulted);
	    }
	    catch(Throwable e)
	    {
	        LOG.warn("got error " + bean.getCusip(), e);
	    }
		return bean;
	}
	
	public void buildVVMsdAll() {
	    LOG.info("build msd all start ...");
        String sql = "select s.cusip,s.market_sector_des,s.maturity,s.ticker,s.cpn,s.factor,COALESCE(m.secid, s.cusip) as secid from vv_msd s "
                   + "left outer join smrd_secid_map m on m.isin=s.id_isin where s.cusip in (select distinct cusip from livePosition) "
                   + "union " 
                   + "select m.secid as cusip,s.market_sector_des,s.maturity,s.ticker,s.cpn,s.factor,m.secid from vv_msd s "
                   + "join smrd_secid_map m on m.isin=s.id_isin where m.secid in (select distinct cusip from livePosition) "
                   + "union " 
                   + "select cusip,market_sector_des,maturity,ticker,cpn,factor,cusip as secid from smrd_wh..vv_muniview where cusip in (select distinct cusip from livePosition) " 
                   + "";

        String DBO = "ORACLE".equalsIgnoreCase(databaseType) ? "." : ".dbo.";

            sql = " "
                + "select  "
                + "t.cusip "
                + ", case "
                + "   when adj.asset_type = 2 then 'Corp' "
                + "   when adj.asset_type = 3 then 'Govt' "
                + "   when adj.asset_type = 6 then 'Corp' "
                + "   when adj.asset_type = 7 then 'Equity' "
                + "   when adj.asset_type = 8 then 'Pfd' "
                + "   when adj.asset_type = 11 then 'Agcy' "
                + "   when im.idc_asset_type = 1 then 'Muni' "
                + "   when im.idc_asset_type = 2 and fi.debt_type = 14 then 'Mtge' "
                + "   when im.idc_asset_type = 2 then 'Corp' "
                + "   when im.idc_asset_type = 3 then 'Govt' "
                + "   when im.idc_asset_type = 11 then 'Agcy' "
                + "   else '?' "
                + "  end as market_sector_des "
                + ", case "
                + "   when adj.maturity is not null then adj.maturity "
                + "   when fi.maturity_date is not null then fi.maturity_date "
                + "   when md.bond_maturity_type=3 and fi.first_coupon_date is not null "
                + ("ORACLE".equalsIgnoreCase(databaseType) ? 
                        "   then to_date ('2152' || '-' || extract(month from fi.first_coupon_date) || '-' || extract(day from fi.first_coupon_date), 'YYYY-MM-DD') "
                      : "   then convert (date, '2152' + '-' + convert(varchar, datepart(mm, fi.first_coupon_date)) + '-' + convert(varchar, datepart(dd, fi.first_coupon_date))) ")
                + "  end as maturity "
                + ", coalesce(adj.ticker, x2.symbol, om.bond_ticker, " + ("ORACLE".equalsIgnoreCase(databaseType) ? "substr" : "substring") + "(om.primary_name, 1, 10)) as ticker "
                + ", case "
                + "   when adj.cpn is not null then adj.cpn "
                + "   else coalesce(fi.current_coupon_rate, fi.original_coupon_rate) "
                + "  end as cpn "
                + ", coalesce(adj.factor, 1) as factor "
                + ", case when trim(adj.currencyCode) is not null then adj.currencyCode when trim(im.primary_currency_code) is null then 'USD' else im.primary_currency_code end as currency "
                + ", mdy.rating moodyRating, sp.rating spRating, h.fitchRating, e.ice_rating iceRating "
                + ", case when im.idc_asset_type = 1 and de.instrument_id is not null then 'Y' when im.idc_asset_type != 1 and fi.default_indicator = 1 then 'Y' else 'N' end as defaulted "
                + ", t.secid "
                + "from  "
                + "(select distinct p.cusip, coalesce(m1.cusip, m2.cusip, p.cusip) as symbol, coalesce(m1.secid, m2.secid, p.cusip) as secid, coalesce(m1.isin, m2.isin, p.secid) as isin "
                + "from liveposition p "
                + "left outer join  smrd_secid_map m1 on p.cusip=m1.secid "  //p.cusip is bbid
                + "left outer join  smrd_secid_map m2 on p.cusip=m2.cusip "  //p.cusip is cins
                + ") t "
                + "join smrd_wh" + DBO + "idc_instrument_xref x on x.symbol_type = 2 and (x.symbol=t.isin or x.symbol=t.isin || '_') "
                + "join smrd_wh" + DBO + "idc_instrument_master im on x.instrument_id=im.instrument_id "
                + "join smrd_wh" + DBO + "idc_organization_master om on im.organization_id=om.organization_id "
                + "join smrd_wh" + DBO + "idc_fixed_income fi on x.instrument_id=fi.instrument_id "
//              + "left join smrd_wh" + DBO + "idc_instrument_xref x2 on x.instrument_id=x2.instrument_id and x2.symbol_type in (1,5) "
                + "left outer join smrd_wh" + DBO + "idc_instrument_xref x2 on x.instrument_id=x2.instrument_id and x2.symbol_type=20 "
                + "left outer join smrd_wh" + DBO + "idc_maturity_details md on x.instrument_id=md.instrument_id "
                + "left outer join smrd_wh" + DBO + "idc_adj adj on x.instrument_id=adj.instrument_id "
                
                + "left outer join (select distinct instrument_id from smrd_wh" + DBO + "idc_default_event where default_event_type = '1') de on x.instrument_id=de.instrument_id "
                       
                + "left outer join smrd_wh" + DBO + "idc_ratings mdy on x.instrument_id=mdy.instrument_id and mdy.agency='Moody''s' and mdy.rating_type = 'Long Rating' "
                + "left outer join smrd_wh" + DBO + "idc_ratings sp on x.instrument_id=sp.instrument_id and sp.agency='Standard & Poor''s' and sp.rating_Type='Long Rating' "
                + "left outer join (select cusip, min(fitchRating) fitchRating from pers_hcma h, smrd_date d where h.tradedate=d.batchDate group by cusip) h on h.cusip=t.symbol "
                + "left outer join smrd_cusip_ext e on t.cusip=e.cusip "

                + " "
                + "union "
                + " "
                + "select "
                + "t.cusip "
                + ", case "
                + "   when b.productkey=2  then 'Equity' "
                + "   when b.productkey=3  then 'Muni' "
                + "   when b.productkey=4  then 'Pfd' "
                + "   when b.productkey=7  then 'Govt' "
                + "   when b.productkey=8  then 'Corp' "
                + "   when b.productkey=9  then 'Corp' "
                + "   when b.productkey=11 then 'Mtge' "
                + "   else '' "
                + "  end as market_sector_des "
                + ", b.maturity  "
                + ", b.ticker  "
                + ", b.coupon as cpn "
                + ", coalesce(b.factor, 1) as factor "
                + ", case when b.currency = ' ' or b.currency is null then 'USD' else b.currency end as currency "
                + ", null moodyRating, null spRating, h.fitchRating, e.ice_rating iceRating "
                + ", 'N' as defaulted "
                + ", t.secid "
                + "from "
                + "(select distinct p.cusip, coalesce(m1.secid, m2.secid, p.cusip) as secid, coalesce(m1.isin, m2.isin, p.secid) as isin "
                + "from liveposition p "
                + "left outer join  smrd_secid_map m1 on p.cusip=m1.secid "
                + "left outer join  smrd_secid_map m2 on p.cusip=m2.cusip "
                + ") t "
                + "join bbt_msd b on b.bbid=t.cusip "
                + "left outer join (select cusip, min(fitchRating) fitchRating from pers_hcma h, smrd_date d where h.tradedate=d.batchDate group by cusip) h on h.cusip=t.cusip "
                + "left outer join smrd_cusip_ext e on t.cusip=e.cusip "
                + "where "
                + "not exists (select 1 from smrd_wh" + DBO + "idc_instrument_xref x where x.symbol_type = 2 and b.secid=x.symbol) "
                + " "
                + "union "
                + " "
                + "select t.cusip, 'Option' market_sector_des, null maturity, b.opt_bbsymbol ticker, null as cpn, coalesce(b.opt_contract_size, 100) as factor "
                + ", 'USD' as currency, null moodyRating, null spRating, null fitchRating, null iceRating "
                + ", 'N' as defaulted "
                + ", null secid "
                + "from liveposition t "
                + "join smrd_option_map b on b.cusip=t.cusip "
                + " "; 
        
		jdbcTemplate.query(sql, new RowCallbackHandler() {

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				PlPropertyBean sec = newVVMsd(rs, null);
				cacheManager.getCache(Constant.PROPERTY_CACHE).put(sec.getCusip(), sec);
			}
		});
		
        LOG.info("build msd all end ...");
	}

}


/*

select 
t.cusip
, case
   when adj.asset_type = 2 then 'Corp'
   when adj.asset_type = 3 then 'Govt'
   when adj.asset_type = 6 then 'Corp'
   when adj.asset_type = 7 then 'Equity'
   when adj.asset_type = 8 then 'Pfd'
   when adj.asset_type = 11 then 'Agy'
   when im.idc_asset_type = 1 then 'Muni'
   when im.idc_asset_type = 2 and fi.debt_type = 14 then 'Mtge'
   when im.idc_asset_type = 2 then 'Corp'
   when im.idc_asset_type = 3 then 'Govt'
   when im.idc_asset_type = 11 then 'Agcy'
   else '?'
  end as market_sector_des
, case
   when adj.maturity is not null then adj.maturity
   when fi.maturity_date is not null then fi.maturity_date
   when md.bond_maturity_type=3 and fi.first_coupon_date is not null
        then to_date ('2152' || '-' || extract(month from fi.first_coupon_date) || '-' || extract(day from fi.first_coupon_date), 'YYYY-MM-DD')
  end as maturity
, coalesce(a.symbol, adj.ticker, x2.symbol, om.bond_ticker, substr(om.primary_name, 1, 10)) as ticker
, case
    when adj.cpn is not null then adj.cpn
    else coalesce(fi.current_coupon_rate, fi.original_coupon_rate)
  end as cpn
, coalesce(adj.factor, 1) as factor
, case when im.primary_currency_code = ' ' or im.primary_currency_code is null then 'USD' else im.primary_currency_code end as currency
, mdy.rating moodyRating, sp.rating spRating, h.fitchRating, e.ice_rating iceRating
, t.secid
from 
(select distinct p.cusip, coalesce(m1.cusip, m2.cusip, p.cusip) as symbol, coalesce(m1.secid, m2.secid, p.cusip) as secid, coalesce(m1.isin, m2.isin, p.secid) as isin 
from (select 'BK4385310' as cusip, null as secid from dual) p 
left outer join  smrd_secid_map m1 on p.cusip=m1.secid 
left outer join  smrd_secid_map m2 on p.cusip=m2.cusip 
) t 
join smrd_wh.idc_instrument_xref x on x.symbol_type in (1,5) and x.symbol=t.symbol
--(select distinct p.cusip, coalesce(m1.secid, m2.secid, p.cusip) as secid, coalesce(m1.isin, m2.isin, p.secid) as isin 
--from liveposition p 
--left outer join  smrd_secid_map m1 on p.cusip=m1.secid   //p.cusip is bbid
--left outer join  smrd_secid_map m2 on p.cusip=m2.cusip   //p.cusip is cins
--) t 
--join smrd_wh.idc_instrument_xref x on x.symbol_type = 2 and (x.symbol=t.isin or x.symbol=t.isin || '_')
join smrd_wh.idc_instrument_master im on x.instrument_id=im.instrument_id
join smrd_wh.idc_organization_master om on im.organization_id=om.organization_id
join smrd_wh.idc_fixed_income fi on x.instrument_id=fi.instrument_id
left outer join smrd_wh.idc_instrument_xref x2 on x.instrument_id=x2.instrument_id and x2.symbol_type=20
left outer join smrd_wh.idc_maturity_details md on x.instrument_id=md.instrument_id
left outer join smrd_wh.idc_adj adj on x.instrument_id=adj.instrument_id

left outer join smrd_wh.idc_ratings mdy on x.instrument_id=mdy.instrument_id and mdy.agency='Moody''s' and mdy.rating_type = 'Long Rating'
left outer join smrd_wh.idc_ratings sp on x.instrument_id=sp.instrument_id and sp.agency='Standard & Poor''s' and sp.rating_Type='Long Rating'
left outer join (select cusip, min(fitchRating) fitchRating from pers_hcma h, smrd_date d where h.tradedate=d.batchDate group by cusip) h on h.cusip=t.cusip
left outer join smrd_cusip_ext e on t.cusip=e.cusip

left outer join smrd_active a on a.cusip=t.cusip and a.symbol like '%Y'

union

select
t.cusip
, case
   when b.productkey=2  then 'Equity'
   when b.productkey=3  then 'Muni'
   when b.productkey=4  then 'Pfd'
   when b.productkey=7  then 'Govt'
   when b.productkey=8  then 'Corp'
   when b.productkey=9  then 'Corp'
   when b.productkey=11 then 'Mtge'
   else ''
  end as market_sector_des
, b.maturity 
, b.ticker 
, b.coupon as cpn
, coalesce(b.factor, 1) as factor
, case when b.currency = ' ' or b.currency is null then 'USD' else b.currency end as currency
, null moodyRating, null spRating, h.fitchRating, e.ice_rating iceRating
, t.secid
from
(select distinct p.cusip, coalesce(m1.secid, m2.secid, p.cusip) as secid, coalesce(m1.isin, m2.isin, p.secid) as isin
from liveposition p
left outer join  smrd_secid_map m1 on p.cusip=m1.secid
left outer join  smrd_secid_map m2 on p.cusip=m2.cusip
) t
join bbt_msd b on b.bbid=t.cusip
left outer join (select cusip, min(fitchRating) fitchRating from pers_hcma h, smrd_date d where h.tradedate=d.batchDate group by cusip) h on h.cusip=t.cusip
left outer join smrd_cusip_ext e on t.cusip=e.cusip
where
not exists (select 1 from smrd_wh.idc_instrument_xref x where x.symbol_type = 2 and b.secid=x.symbol)

*/