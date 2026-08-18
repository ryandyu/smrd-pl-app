package com.sumridge.pl.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.infinispan.manager.CacheContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Component;

import com.sumridge.pl.bean.MenuBean;
import com.sumridge.pl.bean.TradeBookBean;
import com.sumridge.pl.util.Constant;

@Component
public class TradeBookDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Resource
	protected CacheContainer cacheManager;
	
	private List<MenuBean> list;

	public void buildTradeBook(String account) {
		String sql = "select bookname,desk,bookAccount from smrd_tradebook where bookAccount = ? and status = 'A'";
		jdbcTemplate.query(sql, new Object[] { account }, new RowCallbackHandler() {

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				TradeBookBean bean = new TradeBookBean();
				bean.setTraderAccount(rs.getString("bookAccount"));
				bean.setBookname(rs.getString("bookname"));
				bean.setDesk(rs.getString("desk"));
				cacheManager.getCache(Constant.ACCOUNT_CACHE).put(bean.getTraderAccount(), bean);
			}
		});
	}
	
	public void buildBookList(){
		String sql = "select bookname from smrd_tradebook where status = 'A'";
		if(list == null) {
			list = new ArrayList<MenuBean>();
			jdbcTemplate.query(sql, new RowCallbackHandler() {
				@Override
				public void processRow(ResultSet rs) throws SQLException {
					MenuBean bean = new MenuBean();
					bean.setName(rs.getString("bookname"));
					list.add(bean);
				}
			});
		}
	}

	/**
     * @return the list
     */
    public List<MenuBean> getList() {
    	return list;
    }
	

}
