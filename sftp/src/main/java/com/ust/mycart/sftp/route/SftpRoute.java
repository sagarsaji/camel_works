package com.ust.mycart.sftp.route;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

@Component
public class SftpRoute extends RouteBuilder{

	@Override
	public void configure() throws Exception {
		
		from("cron:scheduler?schedule=* 58 11 * * ?")
		.routeId("schedulerTask")
		.to("mongodb:mycartdb?database=mycartdb&collection=item&operation=findAll")
		.split(simple("${body}"))
			.multicast()
				.to("direct:itemTrendAnalyzer")
		.end();
		
		from("direct:itemTrendAnalyzer")
		.unmarshal().json(JsonLibrary.Jackson)
		.marshal().jaxb()
		.log("${body}");
		
		
		
//		from("direct:itemReviewAggregator").log("hello world 2");
//		from("direct:storeFrontApp").log("hello world 3");
		
		
	}

}
