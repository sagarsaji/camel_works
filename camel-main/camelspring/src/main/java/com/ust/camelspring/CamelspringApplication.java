package com.ust.camelspring;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.ust.camelspring.message.MessageBody;

@SpringBootApplication
public class CamelspringApplication extends RouteBuilder {

	public static void main(String[] args) {
		ConfigurableApplicationContext config = SpringApplication.run(CamelspringApplication.class, args);
		MessageBody message = config.getBean(MessageBody.class);
		message.function();
	}

	@Override
	public void configure() throws Exception {
		from("direct:start").log("${body}");
	}

}
