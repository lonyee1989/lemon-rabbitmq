package cn.lemon.rabbitmq.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;

public abstract class BasicService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	public RabbitTemplate rabbitTemplate;
	@Value("${spring.rabbitmq.routingkey}")
	private String routingkey;
	@Value("${spring.rabbitmq.appid}")
	private String appId;

    public void sendMessage(final String serviceName, final String serviceMethodName,final String correlationId, Object request) {
    	logger.info("sendMessage [this.{}, serviceMethodName:{} serviceName:{} correlationId: {}]", this.getClass(), serviceMethodName, serviceName, correlationId);
    	rabbitTemplate.setCorrelationKey(correlationId);
    	rabbitTemplate.convertAndSend(routingkey, request, new MessagePostProcessor() {            
        	@Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setAppId(appId);
                message.getMessageProperties().setTimestamp(new Date());
                message.getMessageProperties().setMessageId(UUID.randomUUID().toString());
                message.getMessageProperties().setCorrelationId(correlationId.getBytes());
                message.getMessageProperties().setHeader("ServiceMethodName", serviceMethodName);
                message.getMessageProperties().setHeader("ServiceName", serviceName);
                return message;
            }
        }, new CorrelationData(correlationId));
    }
	
}
