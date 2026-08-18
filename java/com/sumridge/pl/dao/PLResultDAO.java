package com.sumridge.pl.dao;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.annotation.Resource;

import org.infinispan.manager.CacheContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.sumridge.pl.bean.PlDetailBean;
import com.sumridge.pl.util.Constant;
import com.sumridge.pl.util.PlUtil;

@Component
public class PLResultDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Resource
	protected CacheContainer cacheManager;
    
	@Autowired(required=false)
    @Qualifier("databaseType")
    private String databaseType;
    
	public static Logger LOG = LoggerFactory.getLogger(PLResultDAO.class);
	
	public void storeResultToDB() {
        String sql = "if exists(select 1 from smrd_pnl where BOOKCODE=? and cusip=? and pnlDate = ? ) "
                + "update smrd_pnl set bidPrice=?,offerPrice=?,totalPNL=?,quantity=?,cr01=?,ir01=?,secId=?,marketValue=?,realPNL=?,UNREALPNL=?,isin=?,jpmAccount=?, update_tms=getdate() Where BOOKCODE=? and cusip=? and pnlDate = ? "
                + "else INSERT INTO smrd_pnl (BOOKCODE,cusip,pnlDate,bidPrice,offerPrice,totalPNL,quantity,cr01,ir01,secId,marketValue,realPNL,UNREALPNL,isin,jpmAccount) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
        sql = "merge into smrd_pnl p "
            + "using (select ? as bookcode, ? as cusip,? as pnlDate, ? as bidPrice, ? as offerPrice, ? as totalPNL, ? as quantity "
            + ", ? as cr01, ? as ir01, ? as secId, ? as secDesc, ? as marketValue, ? as realPNL, ? as unrealPNL "
            + ", ? as udlPrice, ? as isin, ? as jpmAccount from dual) o "                
            + "on (p.bookcode=o.bookcode and p.cusip=o.cusip and p.pnldate=o.pnldate) "
            + "when matched then update set p.bidPrice=o.bidPrice, p.offerPrice=o.offerPrice, p.totalPNL=o.totalPNL, p.quantity=o.quantity "
            + "  , p.cr01=o.cr01, p.ir01=o.ir01, p.secId=o.secId, p.secDesc=o.secDesc, p.marketValue=o.marketValue, p.realPNL=o.realPNL, p.unrealPNL=o.unrealPNL "
            + "  , p.udlPrice=o.udlPrice, p.isin=o.isin, p.jpmAccount=o.jpmAccount, p.update_tms=current_timestamp "
            + "when not matched then insert (bookcode, cusip, pnlDate, bidPrice, offerPrice, totalPNL, quantity "
            + "  , cr01, ir01, secId, secDesc, marketValue, realPNL, unrealPNL, udlPrice, isin, jpmAccount)  "
            + "  VALUES (o.bookcode, o.cusip, o.pnlDate, o.bidPrice, o.offerPrice, o.totalPNL, o.quantity "
            + "  , o.cr01, o.ir01, o.secId, o.secDesc, o.marketValue, o.realPNL, o.unrealPNL, o.udlPrice, o.isin, o.jpmAccount) "
            + ("ORACLE".equalsIgnoreCase(databaseType) ? "" : ";")
            + "";
        
        final Object[] cacheValue = cacheManager.getCache(Constant.RESULT_CACHE).values().toArray();
        LOG.debug("flush to DB "  + cacheValue.length + " start");

        final Date sqlSystemDate = new Date(PlUtil.getSystemDate().getTime());
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				PlDetailBean detail = (PlDetailBean) cacheValue[i];
				
				int col = 0;
                try {
				ps.setString(++col, detail.getBook());
				ps.setString(++col, detail.getCusip());
				ps.setDate(++col, sqlSystemDate);
				
				ps.setDouble(++col, getDouble(detail.getOpenPrice(),6));
				ps.setDouble(++col, getDouble(detail.getPlPrice(),6));
				ps.setDouble(++col, getDouble(detail.getPl(),2));
				ps.setDouble(++col, detail.getQuantity());
				ps.setDouble(++col, getDouble(detail.getCr01(),2));
				ps.setDouble(++col, getDouble(detail.getIr01(),2));
				ps.setString(++col, "Option".equals(detail.getProduct()) ? "OPTE" : detail.getBucket());
                ps.setString(++col, "Option".equals(detail.getProduct()) ? detail.getTicker() : null);
                ps.setDouble(++col, getDouble(detail.getMarketValue(),2));
                ps.setDouble(++col, getDouble(detail.getTradePnl(),2));
                ps.setDouble(++col, getDouble(detail.getPositionPnl(),2));
                ps.setDouble(++col, getDouble(detail.getUdlPrice(),6));
                ps.setString(++col, detail.getIsin());
				ps.setString(++col, detail.getTraderAccount());
				}
				catch (java.lang.NumberFormatException e)
                {
				    LOG.warn("error " + detail.getCusip());
				    throw e;
                }
/*				
				ps.setString(++col, detail.getBook());
				ps.setString(++col, detail.getCusip());
				ps.setDate(++col, sqlSystemDate);
				
				ps.setString(++col, detail.getBook());
				ps.setString(++col, detail.getCusip());
				ps.setDate(++col, sqlSystemDate);
				
				ps.setDouble(++col, getDouble(detail.getOpenPrice(),6));
				ps.setDouble(++col, getDouble(detail.getPlPrice(),6));
				ps.setDouble(++col, getDouble(detail.getPl(),2));
				ps.setDouble(++col, detail.getQuantity());
				ps.setDouble(++col, getDouble(detail.getCr01(),2));
				ps.setDouble(++col, getDouble(detail.getIr01(),2));
				ps.setString(++col, detail.getBucket());
				ps.setDouble(++col, getDouble(detail.getMarketValue(),2));
                ps.setDouble(++col, getDouble(detail.getTradePnl(),2));
                ps.setDouble(++col, getDouble(detail.getPositionPnl(),2));
                ps.setString(++col, detail.getIsin());
				ps.setString(++col, detail.getTraderAccount());
*/				
			}

			private double getDouble(double value, int scale) {
			    if(Double.isInfinite(value) || Double.isNaN(value)) {
			        LOG.warn("bad value " + value);
			    }
	            BigDecimal dec = BigDecimal.valueOf(value);
	            dec = dec.setScale(scale,BigDecimal.ROUND_HALF_DOWN);
	            return dec.doubleValue();
            }

			public int getBatchSize() {
				return cacheValue.length;
			}
		});
		
        LOG.debug("flush to DB "  + cacheValue.length + " end");
	}
	
	public void deleteResult() {
		Date sqlSystemDate = new Date(PlUtil.getSystemDate().getTime());
		String sql = "delete from smrd_pnl where pnlDate = ? ";
		jdbcTemplate.update(sql,new Object[]{sqlSystemDate});
	}

}
