package com.sumridge.pl.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Component;

@Component
public class BenchmarkRuleDAO
{
	@Autowired
	private JdbcTemplate jdbcTemplate;
	protected Logger log = Logger.getLogger(BenchmarkRuleDAO.class);
    private static String DEFAULT = "30Y";

    List<Benchmark> _list;

    public void init()
    {
        if(_list == null)
        {
        	try
            {
        		String sql = " select * from smrd_benchmark_rule order by start_date";
        		//List<Map<String, Object>> rsList = jdbcTemplate.queryForList(sql);
        		_list = new LinkedList<Benchmark>();
        		jdbcTemplate.query(sql, new RowCallbackHandler() {

        			@Override
        			public void processRow(ResultSet rs) throws SQLException {
        				Benchmark ben = new Benchmark();
        				ben.setActiveAs(rs.getString("alias"));
        				ben.setBenchmark(rs.getString("benchmark"));
        				ben.setEndDate(rs.getDate("end_date"));
        				ben.setStartDate(rs.getDate("start_date"));
        				_list.add(ben);
        			}
        		});
            }
            catch (Exception e) 
            {
                log.error("BenchmarkRule::init", e);
            }
        }

        
    }

    public String getActiveAs(Date date)
    {
        for(Benchmark r : _list)
        {
            if(r.startDate.compareTo(date) <= 0 && r.endDate.compareTo(date) > 0)
            {
                return r.activeAs;
            }
        }

        return DEFAULT;
    }

    public String getBenchmark(Date date)
    {
        if(date == null)
            return DEFAULT;
        
        for(Benchmark r : _list)
        {
            if(r.getStartDate() == null || r.getEndDate() == null)
                continue;
            
            if(r.startDate.compareTo(date) <= 0 && r.endDate.compareTo(date) > 0)
            {
                return r.benchmark;
            }
        }

        return DEFAULT;
    }

    public String getAlias(Date date)
    {
        if(date == null)
            return DEFAULT;
        
        for(Benchmark r : _list)
        {
            if(r.getStartDate() == null || r.getEndDate() == null)
                continue;
            
            if(r.startDate.compareTo(date) <= 0 && r.endDate.compareTo(date) > 0)
            {
                return r.activeAs;
            }
        }

        return DEFAULT;
    }

    public static class Benchmark
    {
        String activeAs;
        String benchmark;
        Date   startDate;
        Date   endDate;

        public String getActiveAs()
        {
            return activeAs;
        }
        public void setActiveAs(String activeAs)
        {
            this.activeAs = activeAs;
        }
        public String getBenchmark()
        {
            return benchmark;
        }
        public void setBenchmark(String benchmark)
        {
            this.benchmark = benchmark;
        }
        public Date getEndDate()
        {
            return endDate;
        }
        public void setEndDate(Date endDate)
        {
            this.endDate = endDate;
        }
        public Date getStartDate()
        {
            return startDate;
        }
        public void setStartDate(Date startDate)
        {
            this.startDate = startDate;
        }

     }
}
