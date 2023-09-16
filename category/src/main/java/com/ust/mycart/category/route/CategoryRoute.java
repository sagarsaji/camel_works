package com.ust.mycart.category.route;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import com.ust.mycart.category.processor.IdToStringProcessor;

@Component
public class CategoryRoute extends RouteBuilder{

	@Override
	public void configure() throws Exception {
		// TODO Auto-generated method stub
		
		rest()
			.get("/getcategoryname/{_id}").to("direct:getcategoryname")
			.get("/getcategorydept/{_id}").to("direct:getcategorydept");
		
		
		from("direct:getcategoryname")
		.process(new IdToStringProcessor())
		.to("mongodb:mycartdb?database=mycartdb&collection=category&operation=findById")
		.setBody(simple("${body[categoryName]}"))
		.log("${body}");
		
		
		from("direct:getcategorydept")
		.process(new IdToStringProcessor())
		.to("mongodb:mycartdb?database=mycartdb&collection=category&operation=findById")
		.setBody(simple("${body[categoryDep]}"))
		.log("${body}");
		
	}

}
