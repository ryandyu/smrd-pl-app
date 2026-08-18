package com.sumridge.pl.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

import com.sumridge.xml.jaxb.JAXBMarshaller;
import com.sumridge.xml.jaxb.trade.Trade;

/**
 * Converter message to data object
 * 
 * @author liu
 * 
 */
@Component
public class TradeMessageConverter implements MessageConverter {
	public static Logger LOG = LoggerFactory
			.getLogger(TradeMessageConverter.class);

	JAXBMarshaller marshller = JAXBMarshaller
    .getInstance("com.sumridge.xml.jaxb.common"
            + ":com.sumridge.xml.jaxb.trade");

	public Object fromMessage(Message message) throws JMSException,
			MessageConversionException {

//		message.acknowledge();

		if (message instanceof TextMessage) {
			TextMessage textMsg = (TextMessage) message;
			String txtMsg = textMsg.getText();

			try {
			    //unmarshal xml text to Position object.
				Trade trade = (Trade) marshller.unmarshal(txtMsg);
				return trade;
			} catch (JAXBException e) {
				
				LOG.error("Exception in Message Converter: ", e);
				throw new MessageConversionException(e.getMessage(), e);
			}
		}
		return null;
	}

	public Message toMessage(Object object, Session arg1) throws JMSException,
			MessageConversionException {

		return null;
	}
}
