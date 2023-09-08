package com.ust.exchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.ust.exchange.message.MessageBody;

@SpringBootApplication
public class SplitaggregateApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext config = SpringApplication.run(SplitaggregateApplication.class, args);
		MessageBody message = config.getBean(MessageBody.class);
		message.function();
	}

}
