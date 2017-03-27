package cn.lemon.rabbitmq.producer;

import java.util.Date;
import java.util.UUID;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Value;

public abstract class BasicService implements ConfirmCallback {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	public RabbitTemplate rabbitTemplate;
	@Value("${spring.rabbitmq.routingkey}")
	private String routingkey;
	@Value("${spring.rabbitmq.appid}")
	private String appId;

    public void sendMessage(final String serviceName, final String serviceMethodName,final String correlationId, Object request) {
    	logger.info("sendMessage [this.{}, serviceMethodName:{} serviceName:{} correlationId: {}]", this.getClass(), serviceMethodName, serviceName, correlationId);
    	rabbitTemplate.setConfirmCallback(this);
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

    /**
     * 抽象回调方法
     */
	@Override
	public abstract void confirm(CorrelationData correlationData, boolean ack, String cause);
	
}
