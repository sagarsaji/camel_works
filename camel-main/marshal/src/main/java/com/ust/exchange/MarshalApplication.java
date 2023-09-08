package com.ust.exchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.ust.exchange.route.CamelRoute;

@SpringBootApplication
public class MarshalApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext config =  SpringApplication.run(MarshalApplication.class, args);
		CamelRoute camel = config.getBean(CamelRoute.class);
		camel.function();
	}

}
