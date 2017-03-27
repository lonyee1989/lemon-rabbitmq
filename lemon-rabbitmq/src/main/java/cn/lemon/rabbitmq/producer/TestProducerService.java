package cn.lemon.rabbitmq.producer;

import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.stereotype.Service;

import cn.lemon.rabbitmq.protocol.User;

@Service
public class TestProducerService extends BasicService {
	
	public void send(String remark) {
		User user = new User();
		user.setId(10210);
		user.setName("测试");
		user.setRemark(remark);
		System.out.println("sender: "+ user.toString());
		this.sendMessage("consumerService", "getMessage", ""+user.getId(), user);
	}

	@Override
	public void confirm(CorrelationData correlationData, boolean ack, String cause) {
		System.out.println("回调id:" + correlationData);
        if (ack) {
            System.out.println("消息发送成功");
        } else {
            System.out.println("消息发送失败:" + cause);
        }
	}
}
