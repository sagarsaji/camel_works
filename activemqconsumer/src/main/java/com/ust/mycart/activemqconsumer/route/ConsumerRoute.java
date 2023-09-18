package com.ust.mycart.activemqconsumer.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import com.ust.mycart.activemqconsumer.exception.IdNotFoundException;
import com.ust.mycart.activemqconsumer.processor.StockUpdationProcessor;

@Component
public class ConsumerRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		onException(IdNotFoundException.class).handled(true).setHeader("CamelHttpResponseCode", constant(404))
				.setBody(simple("{{error.itemNotFound}}"));

		from("activemq:queue:updateItemQueue").split(simple("${body[items]}")).stopOnException()
				.routeId("activeMqConsumer1").setHeader("itemid", simple("${body[_id]}"))
				.setProperty("soldout", simple("${body[stockDetails][soldOut]}"))
				.setProperty("damaged", simple("${body[stockDetails][damaged]}")).setBody(simple("${header.itemid}"))
				.to("mongodb:mycartdb?database=mycartdb&collection=item&operation=findById").choice()
				.when(simple("${body} == null")).log("nothing present")
				.throwException(new IdNotFoundException("not found")).otherwise().log("item found")
				.setProperty("availablestock", simple("${body[stockDetails][availableStock]}"))
				.process(new StockUpdationProcessor())
				.to("mongodb:mycartdb?database=mycartdb&collection=item&operation=save").end().end()
				.setHeader("CamelHttpResponseCode", constant(200)).setBody(constant("Stock Updated..."));

	}

}
