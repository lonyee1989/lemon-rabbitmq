package cn.lemon.rabbitmq.producer;

import cn.lemon.rabbitmq.protocol.User;
import org.springframework.stereotype.Service;

/**
 * 业务消息发送服务
 */
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
}
