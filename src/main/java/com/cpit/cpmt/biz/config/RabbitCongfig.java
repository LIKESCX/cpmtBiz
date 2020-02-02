package com.cpit.cpmt.biz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
@Configuration
public class RabbitCongfig {

	//短信用
	public static final String SMS_QUEUE_NAME = "cpmt_sms";

	//websocket用
	public static final String WEBSOCKET_TOPIC_NAME = "cpmt-topic";

	//websocket用
	public static final String WEBSOCKET_EXCHANGE_NAME = "cpmt-exchange";

	//处理运营商上报数据用
	public static final String EXC_QUEUE_NAME = "exc-report-msg";


	@Bean
    public Queue topic(){
        return new Queue(WEBSOCKET_TOPIC_NAME, false);
    }
    @Bean
    FanoutExchange exchange() {
        return new FanoutExchange(WEBSOCKET_EXCHANGE_NAME,false,true);
    }
	 @Bean
	 public Queue Queue(){
	     return new Queue(EXC_QUEUE_NAME);
	 }
	 
	 @Bean
	 public Queue cpmtSmsQueue() {
		 return new Queue(SMS_QUEUE_NAME, true);
	 }

	 @Bean
	 Binding bindingExchange(Queue topic,FanoutExchange exchange) {
        return BindingBuilder.bind(topic).to(exchange);
	 }
}
