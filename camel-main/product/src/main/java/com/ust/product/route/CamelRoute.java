package com.ust.product.route;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ust.product.aggregator.Aggregator;
import com.ust.product.entity.Order;
import com.ust.product.entity.exception.ProductNotFoundException;
import com.ust.product.route.service.OrderService;



@Component
public class CamelRoute extends RouteBuilder {


	@Autowired
	private OrderService orderService;
	
	@Autowired
	private ConsumerTemplate consumer;
	
	@Override
	public void configure() throws Exception {
		
		onException(ProductNotFoundException.class)
		.handled(true)
		.setHeader(Exchange.HTTP_RESPONSE_CODE,constant(404))
		.setBody(constant("Product id does not exist"));

		

		
		rest()
			.post("/addorder").to("direct:addorder")
			.get("/getorder").to("direct:getorders")
			.get("/getorder/{_id}").to("direct:getorderbyid")
			.get("/getproduct/{pid}").to("direct:getproduct");
		
		
	
		
		from("direct:addorder")
		.unmarshal().json(JsonLibrary.Jackson,Order.class)
		.bean(orderService,"addOrderId")
		.setHeader("productids",simple("${body.productid}"))
		.setProperty("mymessage", simple("${body}"))
		.setHeader("CamelMongoDbCriteria",simple("{ _id: ${header.oid} }"))
		.to("mongodb:orderdb?database=orderdb&collection=order&operation=findOneByQuery")
		.choice()
			.when(simple("${body} == null"))
				.log("add orders")
				.to("direct:addproducts")
			.otherwise()
				.log("order id exist")
		.end();
		
		
		from("direct:addproducts")
		.split(simple("${header.productids}"), new Aggregator())
			.stopOnException()
			.setHeader("pids",simple("${body}"))
			.setHeader(Exchange.HTTP_METHOD,constant("GET"))
			.toD("http://localhost:9090/product/getproducts/product/${header.pids}?bridgeEndpoint=true&throwExceptionOnFailure=false")
			.setHeader("productfromlocalhost",simple("${body}"))
			.choice()
				.when(header("CamelHttpResponseCode").isEqualTo(404))
					.log("not found")
					.setHeader("CamelHttpResponseCode",constant(404))
					.process(new Processor() {
						
						@Override
						public void process(Exchange exchange) throws Exception {
							// TODO Auto-generated method stub
							
							String errorStatus = "true";
							String statusMessage = "Product not available";
							String productId = exchange.getIn().getHeader("pids",String.class);
							
							ObjectMapper objectMapper = new ObjectMapper();
					        ObjectNode jsonNode = objectMapper.createObjectNode();
					        
					        jsonNode.put("errorStatus", errorStatus);
					        jsonNode.put("statusMessage", statusMessage);
					        jsonNode.put("productId", productId);
					        
					        String jsonString = objectMapper.writeValueAsString(jsonNode);
//					        JsonNode jsonode = objectMapper.readTree(jsonString); 
							exchange.getIn().setBody(jsonString);
						}
					})
				.otherwise()
					.log("found")
					.setHeader("CamelHttpResponseCode",constant(200))
					.process(new Processor() {
						
						@Override
						public void process(Exchange exchange) throws Exception {
							// TODO Auto-generated method stub
							
							String errorStatus = "false";
							String statusMessage = "Order Success";
							String productId = exchange.getIn().getHeader("pids",String.class);
							
							ObjectMapper objectMapper = new ObjectMapper();
					        ObjectNode jsonNode = objectMapper.createObjectNode();
					        
					        jsonNode.put("errorStatus", errorStatus);
					        jsonNode.put("statusMessage", statusMessage);
					        jsonNode.put("productId", productId);
					        
					        String jsonString = objectMapper.writeValueAsString(jsonNode);
					        
//					        JsonNode jsonode = objectMapper.readTree(jsonString); 
							exchange.getIn().setBody(jsonString);
						}
					})
			.end()
		.end()
		.log("${body}")
		.to("mongodb:orderdb?database=orderdb&collection=order&operation=insert");
		
		
		


		
		from("direct:getorders")
		.to("mongodb:orderdb?database=orderdb&collection=order&operation=findAll")
		.marshal().json();

		
		
		from("direct:getorderbyid")
		.setHeader("CamelMongoDbCriteria", simple("{ _id: ${header._id} }"))
		.log("${body}")
		.to("mongodb:orderdb?database=orderdb&collection=order&operation=findOneByQuery")
		.choice()
			.when(simple("${body} == null"))
				.setHeader("CamelHttpResponseCode",constant(404))
				.setBody(constant("Order id not found"))
			.otherwise()
				.setHeader("CamelHttpResponseCode",constant(200))
				.marshal().json()
		.end();
		
		
		
		from("direct:getproduct")
		.log("${header.pid}")
		.doTry()
			.toD("http://localhost:9090/product/getproducts/product/${header.pid}?bridgeEndpoint=true")
		.doCatch(Exception.class)
			.setHeader("CamelHttpResponseCode",constant(404))
			.setBody(constant("Product id not found"))
		.end()
		.log("${body}");
		

	}

}
