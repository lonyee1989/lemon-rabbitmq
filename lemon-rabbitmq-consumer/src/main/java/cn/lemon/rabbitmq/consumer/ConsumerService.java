package cn.lemon.rabbitmq.consumer;

import org.springframework.stereotype.Service;

import cn.lemon.rabbitmq.protocol.User;

@Service
public class ConsumerService {
	
    public User getMessage(User user) {  
		System.out.println("Listener: " + user.toString());
		user.setName("OK");
		return user;
    }
    
}
