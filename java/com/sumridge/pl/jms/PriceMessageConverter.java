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

import com.sumridge.pl.util.JAXBMarshaller;
//import com.sumridge.xml.jaxb.JAXBMarshaller;

import com.sumridge.xml.jaxb.price.Price;

/**
 * Converter message to data object
 * 
 * @author liu
 * 
 */
@Component
public class PriceMessageConverter implements MessageConverter
{
    public static Logger LOG = LoggerFactory.getLogger(PriceMessageConverter.class);

    private static final String xmlConextPath = Price.class.getPackage().getName();

    //JAXBMarshaller marshller = JAXBMarshaller.getInstance("com.sumridge.xml.jaxb.common:com.sumridge.xml.jaxb.price");

    public Object fromMessage(Message message) throws JMSException, MessageConversionException
    {

        // message.acknowledge();

        if (message instanceof TextMessage)
        {
            TextMessage textMsg = (TextMessage) message;
            String txtMsg = textMsg.getText();

            try
            {
                // unmarshal xml text to Position object.

                // Price price = (Price) marshller.unmarshal(txtMsg);

                Price price = (Price) JAXBMarshaller.getInstance(xmlConextPath).unmarshal(txtMsg);

                return price;
            }
            catch (JAXBException e)
            {
                LOG.error("Exception in Message Converter: ", e);
                throw new MessageConversionException(e.getMessage(), e);
            }
        }

        return null;
    }

    public Message toMessage(Object object, Session arg1) throws JMSException, MessageConversionException
    {
        return null;
    }
}
