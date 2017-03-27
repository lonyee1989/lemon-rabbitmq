package cn.lemon.rabbitmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@EnableAutoConfiguration
public class MQConsumerApplication {
	
    public static void main(String[] args) {
        SpringApplication.run(MQConsumerApplication.class, args);
    }
}
