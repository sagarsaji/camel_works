package com.ust.mycart.activemqconsumer.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import com.ust.mycart.activemqconsumer.headers.HeaderClass;
import com.ust.mycart.activemqconsumer.processor.StockUpdationProcessor;

@Component
public class ConsumerRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		// Handled exception here
		onException(Throwable.class).handled(true).setHeader(HeaderClass.CAMEL_HTTP_RESPONSE_CODE, constant(500))
				.setHeader("Content-Type", constant("application/json"))
				.setBody(constant("{\"message\":\"{{server.internalServerError}}\"}"));

		// Route that consumes message from activeMQ and does the update operation
		from("activemq:queue:updateItemQueue").split(simple("${body[items]}"))
				.setHeader("itemid", simple("${body[_id]}"))
				.setProperty("soldout", simple("${body[stockDetails][soldOut]}"))
				.setProperty("damaged", simple("${body[stockDetails][damaged]}")).setBody(simple("${header.itemid}"))
				.to("mongodb:mycartdb?database=mycartdb&collection=item&operation=findById").choice()
				.when(simple("${body} == null")).log(LoggingLevel.INFO, "Item not found for id : ${header.itemid}")
				.otherwise().log(LoggingLevel.INFO, "Item found for id : ${header.itemid}")
				.setProperty("availablestock", simple("${body[stockDetails][availableStock]}"))
				.process(new StockUpdationProcessor())
				.to("mongodb:mycartdb?database=mycartdb&collection=item&operation=save").endChoice().end();

	}

}
