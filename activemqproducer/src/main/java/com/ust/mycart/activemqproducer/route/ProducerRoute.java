package com.ust.mycart.activemqproducer.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

import com.ust.mycart.activemqproducer.headers.HeaderClass;

@Component
public class ProducerRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		// Handled exception here
		onException(Throwable.class).handled(true).setHeader(HeaderClass.CAMEL_HTTP_RESPONSE_CODE, constant(500))
				.setHeader("Content-Type", constant("application/json"))
				.setBody(constant("{\"message\":\"{{server.internalServerError}}\"}"));

		// REST Entry Point
		rest()
				// API to update item and send to activeMQ
				.put("/item").to("direct:updateItem");

		// Route that sends message to the activeMQ
		from("direct:updateItem").log(LoggingLevel.DEBUG, "Received message : ${body}")
				.log(LoggingLevel.INFO, "Message sending to activeMQ").unmarshal().json(JsonLibrary.Jackson)
				.to("activemq:queue:updateItemQueue").setHeader(HeaderClass.CAMEL_HTTP_RESPONSE_CODE, constant(204));

	}

}
