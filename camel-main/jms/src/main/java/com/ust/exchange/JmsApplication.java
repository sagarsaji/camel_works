package com.ust.exchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.ust.exchange.message.MessageBody;

@SpringBootApplication
public class JmsApplication{

	public static void main(String[] args) {
		ConfigurableApplicationContext config =  SpringApplication.run(JmsApplication.class, args);
		MessageBody message = config.getBean(MessageBody.class);
		message.function();
	}

	

}
