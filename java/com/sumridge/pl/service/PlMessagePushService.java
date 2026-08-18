package com.sumridge.pl.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.infinispan.manager.CacheContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sumridge.mq.client.message.Publication;
import com.sumridge.mq.client.publisher.Publisher;
import com.sumridge.pl.bean.PlDetailBean;
import com.sumridge.pl.bean.RiskCurveBean;
import com.sumridge.xml.jaxb.JAXBMarshaller;
import com.sumridge.xml.jaxb.XmlJaxbUtils;
import com.sumridge.xml.jaxb.common.SecurityType;
import com.sumridge.xml.jaxb.risk.CurveRiskType;
import com.sumridge.xml.jaxb.risk.Risk;

@Service
public class PlMessagePushService
{
    @Autowired
    private Publisher riskPublisher;

    @Resource
    protected CacheContainer cacheManager;

    private String riskTopic = "risk";

    private JAXBMarshaller marshller = JAXBMarshaller.getInstance("com.sumridge.xml.jaxb.common" + ":com.sumridge.xml.jaxb.risk");

    private Logger log = LoggerFactory.getLogger(PlMessagePushService.class);

    protected HashMap<String, String> map = new HashMap<String, String>();

    public void pushAll()
    {

    }

    public void pushMsg(List<PlDetailBean> msgList)
    {
        log.debug("push message to risk:" + msgList.size());
        
        for (PlDetailBean detail : msgList)
        {
            if (detail != null)
            {
                try
                {
                    Risk risk = makeRiskByDetail(detail);
/*
                    if (risk.getCurrentQuantity() != 0 || risk.getOpenQuantity() != 0 || risk.getBuyCount() != 0 || risk.getSellCount() != 0)
                    {
                        if (map.containsKey(detail.getCusip()))
                        {
                            log.warn(detail.getCusip() + " " + risk.getMarkPrice());
                        }

                        if (risk.getMarkPrice() < 1)
                        {
                            PriceBean price = (PriceBean) cacheManager.getCache(Constant.PRICE_CACHE).get(detail.getCusip());

                            if (price == null)
                                log.warn(detail.getCusip() + " has no price bean");
                            else
                            {
                                map.put(detail.getCusip(), "N");
                                log.warn(detail.getCusip() + " " + price.getPlPrice() + " " + risk.getTraderAccount() + " " + risk.getOpenPrice() + " " + risk.getCurrentQuantity());
                            }
                        }
                    }
*/
                    publish(risk);

                }
                catch (Throwable e)
                {
                    log.error("push msg error", e);
                }
            }

        }

    }

    private Risk makeRiskByDetail(PlDetailBean detail)
    {
        Risk risk = new Risk();
        risk.setRiskId(detail.getTraderAccount() + "-" + detail.getCusip());
        risk.setTraderAccount(detail.getTraderAccount());
        risk.setTraderDesk(detail.getDesk());
        risk.setTraderId(detail.getTraderId());
        risk.setTradePnl(detail.getTradePnl());

        SecurityType securityType = new SecurityType();
        XmlJaxbUtils.addCusip(securityType, detail.getCusip());
        XmlJaxbUtils.addISIN(securityType, detail.getIsin());
        XmlJaxbUtils.addTicker(securityType, detail.getTicker() != null ? detail.getTicker().split(" ")[0] : null);
        securityType.setDescription(detail.getDesc());
        risk.setSecurity(securityType);

        risk.setOpenPrice(detail.getOpenPrice());
        risk.setOpenQuantity(detail.getOpenQuantity());
        risk.setCurrentQuantity(detail.getCurrentQuantity());
        risk.setMarkPrice(detail.getMarkPrice());
        risk.setStreetPrice(detail.getUdlPrice());
        risk.setMarketValue(detail.getMarketValue());
        risk.setPositionPnl(detail.getPositionPnl());
        risk.setIr01Bucket(detail.getBucket());
        risk.setIr01(detail.getIr01());
        risk.setCr01Bucket(detail.getDefaultBenchmark());
        risk.setCr01(detail.getCr01());
        risk.setCr01Weight(detail.getCreditSector() == null ? 3 : detail.getCreditSector());
        risk.setBuyCount(detail.getBuyCount());
        risk.setBuyQuantity(detail.getBuyQuantity());
        risk.setBuyValue(detail.getBuyValue());
        risk.setSellCount(detail.getSellCount());
        risk.setSellQuantity(detail.getSellQuantity());
        risk.setSellValue(detail.getSellValue());
        risk.setActivityDays(detail.getActivityDays() == null ? 0 : detail.getActivityDays());
        risk.setCreateDays(detail.getCreateDays() == null ? 0 : detail.getCreateDays());
        risk.setUpdateTms(new Date());
        risk.setProduct(detail.getProduct());
        risk.setPnlCheck(detail.isPlCheck());

        //rating
        risk.setRatingBucket(detail.getRating());
        
        // TODO IR01 Curve
        CurveRiskType ir01Curve = new CurveRiskType();
        risk.setIr01Curve(ir01Curve);
        if (detail.getIr01Curve() != null)
        {
            RiskCurveBean.copyTo(detail.getIr01Curve(), ir01Curve);
        } 
        else
        {
            String ir01Bucket = risk.getIr01Bucket();
            
            if("3M".equals(ir01Bucket))
                ir01Curve.setT3M(risk.getIr01());
            else if("6M".equals(ir01Bucket))
                ir01Curve.setT6M(risk.getIr01());
            else if("1Y".equals(ir01Bucket))
                ir01Curve.setT1Y(risk.getIr01());
            else if("2Y".equals(ir01Bucket))
                ir01Curve.setT2Y(risk.getIr01());
            else if("3Y".equals(ir01Bucket))
                ir01Curve.setT3Y(risk.getIr01());
            else if("5Y".equals(ir01Bucket))
                ir01Curve.setT5Y(risk.getIr01());
            else if("7Y".equals(ir01Bucket))
                ir01Curve.setT7Y(risk.getIr01());
            else if("10Y".equals(ir01Bucket))
                ir01Curve.setT10Y(risk.getIr01());
            else if("20Y".equals(ir01Bucket))
                ir01Curve.setT20Y(risk.getIr01());
            else if("30Y".equals(ir01Bucket) || "O30Y".equals(ir01Bucket))
                ir01Curve.setT30Y(risk.getIr01());
            else
                ir01Curve.setT0M(risk.getIr01());  
        }

        // TODO CR01 Curve
        CurveRiskType cr01Curve = new CurveRiskType();
        risk.setCr01Curve(cr01Curve);
        if (detail.getCr01Curve() != null)
        {
            RiskCurveBean.copyTo(detail.getCr01Curve(), cr01Curve);
        }
        else
        {
            String cr01Bucket = risk.getCr01Bucket();

            if ("3M".equals(cr01Bucket))
                cr01Curve.setT3M(risk.getCr01());
            else if ("6M".equals(cr01Bucket))
                cr01Curve.setT6M(risk.getCr01());
            else if ("1Y".equals(cr01Bucket))
                cr01Curve.setT1Y(risk.getCr01());
            else if ("2Y".equals(cr01Bucket))
                cr01Curve.setT2Y(risk.getCr01());
            else if ("3Y".equals(cr01Bucket))
                cr01Curve.setT3Y(risk.getCr01());
            else if ("5Y".equals(cr01Bucket))
                cr01Curve.setT5Y(risk.getCr01());
            else if ("7Y".equals(cr01Bucket))
                cr01Curve.setT7Y(risk.getCr01());
            else if ("10Y".equals(cr01Bucket))
                cr01Curve.setT10Y(risk.getCr01());
            else if ("20Y".equals(cr01Bucket))
                cr01Curve.setT20Y(risk.getCr01());
            else if ("30Y".equals(cr01Bucket) || "O30Y".equals(cr01Bucket))
                cr01Curve.setT30Y(risk.getCr01());
            else
                cr01Curve.setT0M(risk.getCr01());
        }

        return risk;
    }

    private void publish(Risk risk) throws Exception
    {
        String messageXml = marshller.marshal(risk);
        
        Publication msg = createPublication(messageXml);
        
        msg.setTopic("risk/" + risk.getTraderDesk() + "/" + risk.getTraderAccount() + "/" + risk.getRiskId());
        
        riskPublisher.publish(msg);
    }

    private Publication createPublication(String xml) throws Exception
    {
        Publication publication = new Publication();
        publication.setTopic(riskTopic);
        publication.setSow(true);
        publication.setText(xml);

        return publication;
    }

}
