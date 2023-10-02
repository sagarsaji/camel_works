package com.ust.mycart.mycartstore.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import com.ust.mycart.mycartstore.headers.HeaderClass;

@Component
public class MyCartStoreRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		// Handled exceptions here
		onException(Throwable.class).maximumRedeliveries(3).redeliveryDelay(1000).backOffMultiplier(3)
				.useExponentialBackOff().handled(true).setHeader(HeaderClass.CAMEL_HTTP_RESPONSE_CODE, constant(500))
				.setHeader("Content-Type", constant("application/json"))
				.setBody(simple("{\"message\":\"${exception.message}\"} "));

		// REST Entry points
		rest()
				// API to fetch item by item id from previous GET service
				.get("/item/{_id}").to("direct:getByItemId")

				// API to fetch item by category id from previous GET service
				.get("/category/{category_id}").to("direct:getByCategoryId");

		// Route to fetch item by item id from previous GET service
		from("direct:getByItemId").toD("http://localhost:9090/mycart/items/${header._id}?bridgeEndpoint=true")
				.log(LoggingLevel.INFO, "item fetched");

		// Route to fetch item by category id from previous GET service
		from("direct:getByCategoryId")
				.toD("http://localhost:9090/mycart/category/${header.category_id}?bridgeEndpoint=true")
				.log(LoggingLevel.INFO, "item fetched");

	}

}
