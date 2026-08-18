package com.sumridge.pl.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.Resource;

import org.infinispan.manager.CacheContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.sumridge.pl.util.Constant;

public class RatingDao
{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Resource
    protected CacheContainer cacheManager;

    public void buildRatingList()
    {
        String sql = "select rownum rank, s.ratingCode spRating, m.ratingCode moodyRating, f.ratingCode fitchRating "
                + "from spRating s "
                + "join moodyRating m on s.rating_rank=m.rating_rank "
                + "join fitchRating f on s.ratingCode=f.ratingCode "
                + "order by rank asc "
                + "";
        
        jdbcTemplate.query(sql, new RowCallbackHandler() {

            @Override
            public void processRow(ResultSet rs) throws SQLException
            {
                
            }
        });
    }
}
