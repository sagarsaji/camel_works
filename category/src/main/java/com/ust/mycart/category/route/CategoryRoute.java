package com.ust.mycart.category.route;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class CategoryRoute extends RouteBuilder{

	@Override
	public void configure() throws Exception {
		// TODO Auto-generated method stub
		
		rest()
			.get("/getcategoryname/{_id}").to("direct:getcategoryname");
		
		from("direct:getcategoryname")
		.process(new Processor() {
			
			@Override
			public void process(Exchange exchange) throws Exception {
				// TODO Auto-generated method stub
				String id = exchange.getIn().getHeader("_id",String.class);
				exchange.getIn().setBody(id);
			}
		})
		.to("mongodb:mycartdb?database=mycartdb&collection=category&operation=findById")
		.setBody(simple("${body[categoryName]}"))
		.log("${body}");
		
	}

}
