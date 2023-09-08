package com.ust.exchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;


import com.ust.exchange.route.MarshalRoute;

@SpringBootApplication
public class MarshalApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext config =  SpringApplication.run(MarshalApplication.class, args);
		MarshalRoute camel = config.getBean(MarshalRoute.class);
		camel.function();
	}

}
