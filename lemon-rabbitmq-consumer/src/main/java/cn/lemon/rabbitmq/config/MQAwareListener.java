package cn.lemon.rabbitmq.config;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.Resource;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;

/**
 * 消息监听
 * @author lonyee
 *
 */
@Component
public class MQAwareListener implements ChannelAwareMessageListener, ApplicationContextAware {

    @Resource
    private MessageConverter messageConverter;
    @Resource
    private RabbitTemplate rabbitTemplate;
	@Value("${spring.rabbitmq.appid}")
	private String appId;

    private ApplicationContext ctx;

    @Override
    public void onMessage(Message message, Channel channel) throws IOException {
        System.out.println("----- received" + message.getMessageProperties());
		try {
			Object msg = messageConverter.fromMessage(message);
			if (!appId.equals(message.getMessageProperties().getAppId())){
		        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
		        throw new SecurityException("非法应用appId:" + message.getMessageProperties().getAppId());
			}
			Object service = ctx.getBean(message.getMessageProperties().getHeaders().get("ServiceName").toString());
			String serviceMethodName = message.getMessageProperties().getHeaders().get("ServiceMethodName").toString();
			Method method = service.getClass().getMethod(serviceMethodName, msg.getClass());
	        method.invoke(service, msg);
	        //确认消息成功消费
	        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			System.out.println("------ err"+ e.getMessage());
	        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
		}
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }

}
