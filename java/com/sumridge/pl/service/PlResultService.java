package com.sumridge.pl.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.lucene.search.Query;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.infinispan.manager.CacheContainer;
import org.infinispan.query.CacheQuery;
import org.infinispan.query.Search;
import org.infinispan.query.SearchManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sumridge.pl.bean.CurrencyBean;

import com.sumridge.pl.bean.PlDetailBean;
import com.sumridge.pl.bean.PlPropertyBean;
import com.sumridge.pl.bean.PlSignalBean;
import com.sumridge.pl.bean.PositionBean;
import com.sumridge.pl.bean.PriceBean;
import com.sumridge.pl.bean.RiskCurveBean;
import com.sumridge.pl.bean.TradeBean;
import com.sumridge.pl.bean.TradeBookBean;
import com.sumridge.pl.dao.PLResultDAO;
import com.sumridge.pl.util.Constant;
import com.sumridge.pl.util.Constant.Signal;
import com.sumridge.pl.util.PlUtil;

@Service
public class PlResultService
{
    public static Logger LOG = LoggerFactory.getLogger(PlResultService.class);

    @Resource
    protected CacheContainer cacheManager;

    @Resource
    private PLResultDAO pLResultDAO;

    @Resource
    protected LinkedBlockingQueue<PlSignalBean> signalBuffer;

    private final Lock lock = new ReentrantLock();

    private int calcTotalCount = 0;

    private static String _key(PositionBean bean)
    {
        return bean != null ? bean.getTraderAccount() + Constant.TAG + bean.getSecurityId() : "DK";
    }

    private static String _key(TradeBean bean)
    {
        return bean != null ? bean.getTraderAccount() + Constant.TAG + bean.getSecurityId() : "DK";
    }

    public void calcTotalResult()
    {

        try
        {
            if (calcTotalCount == 0)
            {
                Thread.sleep(5 * 60 * 1000);
                ++calcTotalCount;
            }
        }
        catch (Throwable t)
        {

        }

        LOG.info("refresh all");
        List<PlDetailBean> updateList = new ArrayList<PlDetailBean>();

        for (Object key : cacheManager.getCache(Constant.POSITION_CACHE).keySet())
        {
            PositionBean bean = (PositionBean) cacheManager.getCache(Constant.POSITION_CACHE).get(key);

            PlDetailBean plBean = (PlDetailBean) cacheManager.getCache(Constant.RESULT_CACHE).get(key);

            PlDetailBean detail = getDetail(bean);
            if (detail != null)
            {

                if (plBean == null || Math.abs(plBean.getMarkPrice() - detail.getMarkPrice()) > 0.001)
                {
                    LOG.info(" >>> " + detail.getCusip() + " " + detail.getMarkPrice() + " " + (plBean != null ? plBean.getMarkPrice() : 0));
                    updateList.add(detail);
                }

                cacheManager.getCache(Constant.RESULT_CACHE).put(key, detail);

            }

        }

        PlSignalBean signal = new PlSignalBean();
        signal.setProcessObject(updateList);
        signal.setSignal(Signal.Total);
        signalBuffer.add(signal);

        pLResultDAO.storeResultToDB();

        updateLastUpdate();
    }

    public void updateLastUpdate()
    {
        cacheManager.getCache(Constant.COMMON_CACHE).put(Constant.LAST_UPDATE, PlUtil.getSystemTime());
    }

    public PlDetailBean calcResultByPosition(PositionBean bean)
    {
        String key = _key(bean);
        PlDetailBean detail = getDetail(bean);
        if (detail != null)
        {
            cacheManager.getCache(Constant.RESULT_CACHE).put(key, detail);
        }
        updateLastUpdate();
        return detail;
    }

    public PlDetailBean calcResultByTrade(TradeBean bean)
    {
        String key = _key(bean);
        PositionBean pos = (PositionBean) cacheManager.getCache(Constant.POSITION_CACHE).get(key);
        PlDetailBean detail = getDetail(pos);
        if (detail != null)
        {
            cacheManager.getCache(Constant.RESULT_CACHE).put(key, detail);
        }
        updateLastUpdate();
        return detail;
    }

    public List<PlDetailBean> calcResultByPrice(PriceBean bean)
    {
        String cusip = bean.getCusip();
        return calcResultByCusip(cusip, false);
    }

    public List<PlDetailBean> calcResultByCusip(String cusip, boolean force)
    {
        List lst = getPositionListByCusip(cusip);
        List<PlDetailBean> detailList = new ArrayList<PlDetailBean>();
        for (Object obj : lst)
        {
            PositionBean pos = (PositionBean) obj;
            String key = _key(pos);
            PlDetailBean detail = getDetail(pos);
            if (detail != null)
            {
                cacheManager.getCache(Constant.RESULT_CACHE).put(key, detail);
                if (detail.getQuantity() != 0 || !StringUtils.equals(cusip, pos.getSecurityId()) || force)
                {
                    detailList.add(detail);
                }
            }
        }

        updateLastUpdate();
        return detailList;
    }

    public List getPositionListByCusip(String cusip)
    {
        try
        {
            if (cusip == null)
                return null;

            SearchManager searchManager = Search.getSearchManager(cacheManager.getCache(Constant.POSITION_CACHE));
            QueryBuilder queryBuilder = searchManager.buildQueryBuilderForClass(PositionBean.class).get();

            Query luceneQuery = null;

            if (cusip.length() == 3)
                luceneQuery = queryBuilder.keyword().onFields("securityId", "stlCurrencies").matching(cusip).createQuery();
            else
                luceneQuery = queryBuilder.keyword().onField("securityId").matching(cusip).createQuery();

            CacheQuery query = searchManager.getQuery(luceneQuery, PositionBean.class);
            List lst = query.list();

            return lst;
        }
        catch (Throwable t)
        {

        }
        return new ArrayList<PositionBean>();
    }

    private PlDetailBean getDetail(PositionBean bean)
    {

        if (bean.getTraderAccount() == null)
        {
            LOG.warn("secId=" + bean.getSecurityId() + " has no account");
            return null;
        }

        TradeBookBean trade = (TradeBookBean) cacheManager.getCache(Constant.ACCOUNT_CACHE).get(bean.getTraderAccount());
        if (trade == null)
        {
            LOG.warn("unknown account=" + bean.getTraderAccount());
            return null;
        }

        String key = _key(bean);

        PlDetailBean result = new PlDetailBean();

        result.setIsin(bean.getIsin());
        result.setCusip(bean.getSecurityId());
        result.setTraderId(bean.getTraderId());
        result.setBook(trade.getBookname());
        result.setDesk(trade.getDesk().trim());
        result.setOpenPrice(bean.getOpenPrice());
        result.setTraderAccount(bean.getTraderAccount().trim());
        result.setQuantity(bean.getCurrentQuantity() / 1000);
        result.setPlPrice(0);
        result.setOpenQuantity(bean.getOpenQuantity());
        result.setCurrentQuantity(bean.getCurrentQuantity());
        result.setCurrency(bean.getCurrency());
        result.setActivityDays(bean.getActivityDays());
        result.setCreateDays(bean.getCreateDays());

        double factor = 1.0, openFxRate = 1.0, markFxRate = 1.0;

        CurrencyBean ccyPrice = (CurrencyBean) cacheManager.getCache(Constant.CURRENCY_CACHE).get(bean.getCurrency());
        if (ccyPrice != null)
            openFxRate = ccyPrice.getCloseRate();
        else if (!Constant.REPORT_CCY.equals(bean.getCurrency()))
            LOG.warn(bean.getSecurityId() + " " + bean.getCurrency() + " has not open rate");

        PriceBean fxPrice = (PriceBean) cacheManager.getCache(Constant.PRICE_CACHE).get(bean.getCurrency());
        if (fxPrice != null)
            markFxRate = fxPrice.getPlPrice();
        else if (!Constant.REPORT_CCY.equals(bean.getCurrency()))
            LOG.warn(bean.getSecurityId() + " " + bean.getCurrency() + " has not mark rate");

        PlPropertyBean plProperty = (PlPropertyBean) cacheManager.getCache(Constant.PROPERTY_CACHE).get(bean.getSecurityId());
        if (plProperty != null)
        {
            result.setBucket(getBucket(plProperty, trade.getDesk(), trade.getTraderAccount()));
            result.setProduct(plProperty.getProduct());
            if (plProperty.getDefaultBenchmark() != null)
            {
                result.setDefaultBenchmark(plProperty.getDefaultBenchmark());
            }
            else
            {
                result.setDefaultBenchmark("$");
            }

            factor = plProperty.getFactor();
            result.setCreditSector(plProperty.getCreditSector());
            result.setDesc(plProperty.getTicker() + " " + (plProperty.getCoupon() != null ? plProperty.getCoupon() : "") + " "
                    + (plProperty.getMaturity() != null ? plProperty.getMaturity() : "") + " " + bean.getCurrency());
            result.setTicker(plProperty.getTicker());
            result.setRating(plProperty.getRating());
        }

        if (Math.abs(markFxRate - 1.0) > 1.0e-3)
        {
            LOG.warn(">>> FX " + bean.getIsin() + " " + bean.getSecurityId() + " " + bean.getCurrency() + " " + markFxRate + " " + factor);
        }

        if ("Option".equals(result.getProduct()) && result.getTicker() != null)
        {
            PriceBean udlPrice = (PriceBean) cacheManager.getCache(Constant.PRICE_CACHE).get(result.getTicker().split(" ")[0]);
            if (udlPrice != null)
                result.setUdlPrice(udlPrice.getPlPrice());
        }

        double buyQuantity = 0, buyValue = 0;
        double sellQuantity = 0, sellValue = 0;
        int buyCount = 0;
        int sellCount = 0;
        for (TradeBean tradeDetail : bean.getTradeList().values())
        {

            if (DateUtils.isSameDay(tradeDetail.getTradeDate(), PlUtil.getSystemDate()))
            {
                if ("X".equals(tradeDetail.getStatusInd()))
                {
                    ;
                }
                else
                {
                    if ("B".equals(tradeDetail.getBuySellInd()))
                    {
                        buyCount++;
                        buyQuantity += Math.abs(tradeDetail.getQuantity());
                        buyValue += Math.abs(tradeDetail.getPrincipal());
                    }
                    else
                    {
                        sellCount++;
                        sellQuantity += Math.abs(tradeDetail.getQuantity());
                        sellValue += Math.abs(tradeDetail.getPrincipal());
                    }
                }
            }
        }
        result.setBuyQuantity(buyQuantity);
        result.setSellQuantity(sellQuantity);
        result.setBuyValue(buyValue);
        result.setSellValue(sellValue);
        result.setBuyCount(buyCount);
        result.setSellCount(sellCount);

        PriceBean price = (PriceBean) cacheManager.getCache(Constant.PRICE_CACHE).get(bean.getSecurityId());
        if (price != null)
        {
            result.setPlPrice(price.getPlPrice());
            result.setMarkPrice(price.getPlPrice());

            double multipler = 1.0;

            result.setMarketValue(markFxRate * factor * bean.getCurrentQuantity() * price.getPlPrice() / 100);
            result.setIr01(markFxRate * factor * bean.getCurrentQuantity() * price.getDv01() / 1000000);
            result.setCr01(markFxRate * factor * bean.getCurrentQuantity() * price.getCr01() / 1000000);

            if (price.getDv01Curve() != null)
            {
                if (result.getIr01Curve() == null)
                    result.setIr01Curve(new RiskCurveBean());

                result.getIr01Curve().setT0M(markFxRate * factor * bean.getCurrentQuantity() * price.getDv01Curve().getT0M() / 1000000);
                result.getIr01Curve().setT3M(markFxRate * factor * bean.getCurrentQuantity() * price.getDv01Curve().getT3M() / 1000000);
                result.getIr01Curve().setT6M(markFxRate * factor * bean.getCurrentQuantity() * price.getDv01Curve().getT6M() / 1000000);
                result.getIr01Curve().setT1Y(markFxRate * factor * bean.getCurrentQuantity() * price.getDv01Curve().getT1Y() / 1000000);
                result.getIr01Curve().setT2Y(markFxRate * factor * bean.getCurrentQuantity() * price.getDv01Curve().getT2Y() / 1000000);
                result.getIr01Curve().setT3Y(markFxRate * factor * bean.getCurrentQuantity() * price.getDv01Curve().getT3Y() / 1000000);
                result.getIr01Curve().setT5Y(markFxRate * factor * bean.getCurrentQuantity() * price.getDv01Curve().getT5Y() / 1000000);
                result.getIr01Curve().setT7Y(markFxRate * factor * bean.getCurrentQuantity() * price.getDv01Curve().getT7Y() / 1000000);
                result.getIr01Curve().setT10Y(markFxRate * factor * bean.getCurrentQuantity() * price.getDv01Curve().getT10Y() / 1000000);
                result.getIr01Curve().setT20Y(markFxRate * factor * bean.getCurrentQuantity() * price.getDv01Curve().getT20Y() / 1000000);
                result.getIr01Curve().setT30Y(markFxRate * factor * bean.getCurrentQuantity() * price.getDv01Curve().getT30Y() / 1000000);
            }
            else
            {
                result.setIr01Curve(null);
            }

            if (price.getCr01Curve() != null)
            {
                if (result.getCr01Curve() == null)
                    result.setCr01Curve(new RiskCurveBean());

                result.getCr01Curve().setT0M(markFxRate * factor * bean.getCurrentQuantity() * price.getCr01Curve().getT0M() / 1000000);
                result.getCr01Curve().setT3M(markFxRate * factor * bean.getCurrentQuantity() * price.getCr01Curve().getT3M() / 1000000);
                result.getCr01Curve().setT6M(markFxRate * factor * bean.getCurrentQuantity() * price.getCr01Curve().getT6M() / 1000000);
                result.getCr01Curve().setT1Y(markFxRate * factor * bean.getCurrentQuantity() * price.getCr01Curve().getT1Y() / 1000000);
                result.getCr01Curve().setT2Y(markFxRate * factor * bean.getCurrentQuantity() * price.getCr01Curve().getT2Y() / 1000000);
                result.getCr01Curve().setT3Y(markFxRate * factor * bean.getCurrentQuantity() * price.getCr01Curve().getT3Y() / 1000000);
                result.getCr01Curve().setT5Y(markFxRate * factor * bean.getCurrentQuantity() * price.getCr01Curve().getT5Y() / 1000000);
                result.getCr01Curve().setT7Y(markFxRate * factor * bean.getCurrentQuantity() * price.getCr01Curve().getT7Y() / 1000000);
                result.getCr01Curve().setT10Y(markFxRate * factor * bean.getCurrentQuantity() * price.getCr01Curve().getT10Y() / 1000000);
                result.getCr01Curve().setT20Y(markFxRate * factor * bean.getCurrentQuantity() * price.getCr01Curve().getT20Y() / 1000000);
                result.getCr01Curve().setT30Y(markFxRate * factor * bean.getCurrentQuantity() * price.getCr01Curve().getT30Y() / 1000000);
            }
            else
            {
                result.setCr01Curve(null);
            }
        }

        double positionPnl = factor
                * (markFxRate * bean.getCurrentQuantity() * result.getPlPrice() / 100 - openFxRate * bean.getOpenQuantity() * bean.getOpenPrice() / 100);
        result.setPositionPnl(positionPnl);

        double tradePnl = factor * getSumPl(bean);
        result.setTradePnl(tradePnl);

        if (checkPl(bean))
        {
            result.setPl(positionPnl + tradePnl);
            result.setPositionPnl(factor * bean.getOpenQuantity() * (markFxRate * result.getPlPrice() - openFxRate * bean.getOpenPrice()) / 100);
            result.setTradePnl(result.getPl() - result.getPositionPnl());
        }
        else
        {
            result.setPl(0);
            result.setPlCheck(true);
        }

        if ("Ccy".equals(result.getProduct()))
        {
            result.setMarketValue(markFxRate * bean.getCurrentQuantity());
            result.setPositionPnl(bean.getOpenQuantity() * (markFxRate - openFxRate));
            result.setTradePnl(0.0);
            result.setPl(result.getPositionPnl() + result.getTradePnl());
            result.setIr01(0.0);
            result.setCr01(0.0);
            result.setPlCheck(false);
        }
        else if ("Equity".equals(result.getProduct()) || "Option".equals(result.getProduct()) || "Pfd".equals(result.getProduct()))
        {
            if ("Equity".equals(result.getProduct()) || "Option".equals(result.getProduct()))
                result.setDesc(plProperty.getTicker() + " " + result.getProduct() + " " + bean.getCurrency());

            result.setDefaultBenchmark("$");
            result.setMarketValue(result.getMarketValue() * 100.0);
            result.setIr01(result.getIr01() * 100.0);
            result.setCr01(result.getCr01() * 100.0);

            if (result.getIr01Curve() != null)
                result.getIr01Curve().scale(100.0);
            if (result.getCr01Curve() != null)
                result.getCr01Curve().scale(100.0);

            result.setQuantity(result.getQuantity() * 1000.0);
            result.setPl(result.getPl() * 100.0);
            result.setPositionPnl(result.getPositionPnl() * 100.0);
            result.setTradePnl(result.getTradePnl() * 100.0);
        }

        return result;
    }

    private boolean checkPl(PositionBean bean)
    {
        int qty = 0;
        for (TradeBean trade : bean.getTradeList().values())
        {
            int sign = getSign(trade);
            qty += trade.getQuantity() * sign;
        }
        return Math.abs(bean.getOpenQuantity() - bean.getCurrentQuantity() - qty) < 0.001;
    }

    private double getSumPl(PositionBean bean)
    {
        double total = 0.0;

        for (TradeBean trade : bean.getTradeList().values())
        {
            double fxRate = 1.0;
            PriceBean fxPrice = (PriceBean) cacheManager.getCache(Constant.PRICE_CACHE).get(trade.getSettleCcy());
            if (fxPrice != null)
                fxRate = fxPrice.getPlPrice();
            else if (!Constant.REPORT_CCY.equals(trade.getSettleCcy()))
                LOG.warn(trade.getSecurityId() + " " + trade.getSettleCcy() + " has not mark rate");

            int sign = getSign(trade);
            total += fxRate * trade.getSettleFxRate() * trade.getPrice() * trade.getQuantity() / 100 * sign;
        }
        return total;
    }

    public int getSign(TradeBean trade)
    {
        int sign = 1;
        if ("X".equals(trade.getStatusInd()))
        {
            if (DateUtils.isSameDay(trade.getTradeDate(), PlUtil.getSystemDate()))
            {
                sign = 0;
            }
            else
            {
                if ("B".equals(trade.getBuySellInd()))
                {
                    sign = 1;
                }
                else
                {
                    sign = -1;
                }
            }
        }
        else
        {
            if (trade.getBuySellInd().equals("B"))
            {
                sign = -1;
            }
            else
            {
                sign = 1;
            }
        }
        return sign;
    }

    private String getBucket(PlPropertyBean plProperty, String desk, String account)
    {
        String benchmark = null;
        if (plProperty != null)
        {
            if (StringUtils.isNotBlank(plProperty.getHedgeSector()))
            {
                benchmark = plProperty.getHedgeSector();
            }
            else
            {
                String cusip = plProperty.getCusip();
                if (cusip != null && (cusip.startsWith("91279") || cusip.startsWith("9128")))
                {
                    benchmark = plProperty.getUltimateBenchmark() != null ? plProperty.getUltimateBenchmark() : plProperty.getDefaultBenchmark();
                }
                else if ("Option".equals(plProperty.getProduct()))
                {
                    benchmark = "$";
                }
                else if ("Ccy".equals(plProperty.getProduct()) || "Equity".equals(plProperty.getProduct()) || "Pfd".equals(plProperty.getProduct()))
                {
                    benchmark = "$";
                }
                else if ("Muni".equals(plProperty.getProduct()))
                {
                    benchmark = plProperty.getDefaultBenchmark();
                }
                else if (StringUtils.isNotBlank(plProperty.getUltimateBenchmark()) && !"YIELD".equals(plProperty.getUltimateBenchmark()))
                {
                    benchmark = plProperty.getUltimateBenchmark();
                }
                else if (account.equals("101-047432") || account.equals("101-047429") || account.equals("101-047488") || account.equals("101-047424"))
                {
                    benchmark = plProperty.getDefaultBenchmark();
                }
                else
                {
                    benchmark = "$";
                }
            }
        }
        if (StringUtils.isBlank(benchmark))
        {
            benchmark = "$";
        }
        else
        {
            benchmark = benchmark.replaceAll("O", "");
        }

        return benchmark.trim();
    }

    public List<PlDetailBean> calcResultByProperty(PlPropertyBean pro)
    {

        return calcResultByCusip(pro.getCusip(), true);
    }
}
