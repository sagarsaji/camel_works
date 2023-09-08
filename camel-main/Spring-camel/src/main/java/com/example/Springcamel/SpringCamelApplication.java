package com.example.Springcamel;


import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import com.example.Springcamel.routebuilder.CamelRoute;



@SpringBootApplication
public class SpringCamelApplication {

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext contextt = SpringApplication.run(SpringCamelApplication.class, args);
		CamelRoute application = contextt.getBean(CamelRoute.class);
		application.function();
	}
	
	
	
}
