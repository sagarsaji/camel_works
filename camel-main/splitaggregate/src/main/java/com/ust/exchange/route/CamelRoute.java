package com.ust.exchange.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;


import com.ust.exchange.aggregation.MySumAggregationStrategy;
import com.ust.exchange.message.MessageBody;

@Component
public class CamelRoute extends RouteBuilder{

	@Override
	public void configure() throws Exception {
		
		from("direct:start").split(body().tokenize(","))
		.filter().method(MessageBody.class,"even").log("Splitted : ${body}")
		.to("direct:even");

		
		from("direct:even").aggregate(constant(true),new MySumAggregationStrategy())
		.completionSize(5)
		.to("direct:final");
		
		from("direct:final").log("Aggregated: ${body}");
		
		
	}

}
