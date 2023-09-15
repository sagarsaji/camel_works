package com.ust.exchange.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import com.ust.exchange.entity.Employee;

@Component
public class CamelRoute extends RouteBuilder {

	

	@Override
	public void configure() throws Exception {

		Employee e1 = new Employee("Sagar", 23, "developer");
		
		from("cron:marshallingTask?schedule=0/30 * 13 * * ?")
		.setBody(constant(e1))
		.log("logging in 30th second of each minute at 1pm")
		.to("direct:start");


		from("direct:start")
		.marshal().json()
		.setHeader("marshal",simple("${body}"))
		.log("marshalled json : ${body}").to("direct:unmarshal");
		
		
		from("direct:unmarshal")
		.unmarshal().json()
		.log("unmarshalled object : ${body}");

	}

	

}
