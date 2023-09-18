package com.ust.mycart.activemqproducer.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ProducerRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		// TODO Auto-generated method stub

		rest().put("/item").to("direct:updateItem");

		from("direct:updateItem").to("activemq:queue:updateItemQueue");

	}

}
