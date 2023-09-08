package com.ust.product.route;


import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ust.product.entity.Order;
import com.ust.product.entity.exception.ProductNotFoundException;
import com.ust.product.route.service.OrderService;


@Component
public class CamelRoute extends RouteBuilder {


	@Autowired
	private OrderService orderService;
	
	
	@Override
	public void configure() throws Exception {

		onException(ProductNotFoundException.class)
		.handled(true)
		.log("${body}")
		.setBody().simple("${exception.message}")
		.setHeader("CamelHttpResponseCode", constant(404))
		.end();

		
		rest()
			.post("/addorder").to("direct:addorder")
			.get("/getorder").to("direct:getorders")
			.get("/getorder/{_id}").to("direct:getorderbyid")
			.get("/getproduct/{pid}").to("direct:getproduct");
		
		
		
		from("direct:addorder")
		.unmarshal().json(JsonLibrary.Jackson,Order.class)
		.bean(orderService,"addOrderId")
		.setHeader("CamelMongoDbCriteria", simple("{ _id: ${header.oid} }"))
		.to("mongodb:orderdb?database=orderdb&collection=order&operation=findOneByQuery")
		.choice()
			.when(simple("${body} == null"))
				.log("add orders")
				.bean(orderService,"addOrders")
				.log("${body}")
				.choice()
					.when(simple("${body} != null"))
						.log("add to mongodb")
						.to("mongodb:orderdb?database=orderdb&collection=order&operation=insert")
						.setBody(constant("Order placed..."))
					.otherwise()
						.log("invalid")
						.setBody(constant("The entered product id does not exist"))
					.endChoice()
			.otherwise()
				.log("order id already exist")
				.setHeader("CamelHttpResponseCode",constant(409))
				.setBody(constant("The given order id exist"))
		.end();
		

		
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
