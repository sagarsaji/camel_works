package com.ust.exchange.route;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ust.exchange.entity.Employee;

@Component
public class CamelRoute extends RouteBuilder {

	@Autowired
	private ProducerTemplate template;

	@Override
	public void configure() throws Exception {

		from("direct:start").marshal().json().to("direct:marshalled");
		from("direct:marshalled").log("to json : ${body}").unmarshal().json().to("direct:unmarshalled");
		from("direct:unmarshalled").log("to object : ${body}");

	}

	public void function() {
		Employee e1 = new Employee("Sagar", 23, "developer");
		template.sendBody("direct:start", e1);
	}

}
