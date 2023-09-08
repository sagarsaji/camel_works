package com.ust.exchange.route;

import java.io.InputStream;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ust.exchange.entity.Employee;

@Component
public class MarshalRoute extends RouteBuilder {

	@Autowired
	private ProducerTemplate template;

	@Override
	public void configure() throws Exception {
		
		Employee e1 = new Employee("Sagar", 23, "developer");
		
		from("cron:marshallingTask?schedule=30 * 10 * * ?")
		.setBody(constant(e1))
		.log("logging in 30th second of each minute at 10am")
		.to("direct:start");


		from("direct:start")
		.marshal().json()
		.setHeader("marshal",simple("${body}"))
		.log("marshalled json : ${body}").to("direct:unmarshal");
		
		
		from("direct:unmarshal")
		.unmarshal().json()
		.log("unmarshalled object : ${body}");

	}

	public void function() {
//		Employee e1 = new Employee("Sagar", 23, "developer");
//		template.sendBody("timer:myTimer", e1);
	}

}
