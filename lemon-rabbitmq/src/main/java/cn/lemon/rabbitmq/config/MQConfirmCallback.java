package cn.lemon.rabbitmq.config;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;

/**
 * MQ确认回调
 * @author lonyee
 */
public class MQConfirmCallback implements RabbitTemplate.ConfirmCallback {


    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        System.out.println("回调id:" + correlationData);
        if (ack) {
            System.out.println("消息发送成功");
        } else {
            System.out.println("消息发送失败:" + cause);
        }
    }
}
