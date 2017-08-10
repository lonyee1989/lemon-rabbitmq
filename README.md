# lemon-rabbitmq
乐檬框架之rabbitMQ的解耦实现方式（基于springboot框架）

lemon-rabbitmq   消息生产端

lemon-rabbitmq-protocol   消息协议层

lemon-rabbitmq-consumer   消息消费端


------------------------------------
#### 消息协议层

通过Jackson2序列化/反序列化，实现消息传递和对象接收


#### 消息生产端

1、通过发送带有消费端服务类名称和方法请求头，控制消费端调用执行消费发送的信息

2、利用消息回调接口ConfirmCallback确保消息安全发送至broker服务器，处理效率比事物方式更高

```java
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
```

#### 消息消费端

1、监听MQ消息，利用method.invoke调用指定的服务类名称和方法，消费接收到的消息

2、使用Ack/Nack手动确认消息处理状态，保证broker消息被正确消费

```java
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
```
