package com.ust.mycart.item.route;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.tomcat.util.json.JSONFilter;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.ust.mycart.item.entity.Item;
import com.ust.mycart.item.exception.CategoryIdErrorException;
import com.ust.mycart.item.exception.GreaterThanZeroException;
import com.ust.mycart.item.exception.IdNotFoundException;
import com.ust.mycart.item.exception.ProductAlreadyExistException;
import com.ust.mycart.item.processor.CategoryNameAddingProcessor;
import com.ust.mycart.item.processor.IdToStringProcessor;
import com.ust.mycart.item.processor.MapCategoryToListProcessor;

@Component
public class ItemRoute extends RouteBuilder{
	
	@Autowired
	private ConsumerTemplate template;

	@Override
	public void configure() throws Exception {
		// TODO Auto-generated method stub
		
		onException(IdNotFoundException.class)
		.handled(true)
		.setHeader("CamelHttpResponseCode",constant(404))
		.setBody(constant("Item ID not found"));
		
		onException(ProductAlreadyExistException.class)
		.handled(true)
		.setHeader("CamelHttpResponseCode",constant(409))
		.setBody(constant("Item ID already exist"));
		
		onException(CategoryIdErrorException.class)
		.handled(true)
		.setHeader("CamelHttpResponseCode",constant(404))
		.setBody(constant("Not Found"));
		
		onException(GreaterThanZeroException.class)
		.handled(true)
		.setHeader("CamelHttpResponseCode",constant(400))
		.setBody(constant("Base Price and Selling Price should be greater than zero"));
		
		
		rest()
			.get("/getitems/{_id}").to("direct:getitemsbyid")
			.get("/items/{category_id}").to("direct:getbycategoryid")
			.post("/additem").to("direct:additems");
		
		
		
		
		
		from("direct:getitemsbyid")
		.setHeader("itemid",simple("${header._id}"))
		.process(new IdToStringProcessor())
		.to("mongodb:mycartdb?database=mycartdb&collection=item&operation=findById")
		.choice()
			.when(simple("${body} == null"))
				.throwException(new IdNotFoundException("not found"))
			.otherwise()
				.marshal().json()
		.end();
		
		
		
		from("direct:getbycategoryid")
		.choice()
			.when(header("includeSpecial").isEqualTo("true"))
				.setHeader("CamelMongoDbCriteria", simple("{\"categoryId\": '${header.category_id}',\"specialProduct\": true}"))
			.when(header("includeSpecial").isEqualTo("false"))
				.setHeader("CamelMongoDbCriteria", simple("{\"categoryId\": '${header.category_id}',\"specialProduct\": false}"))
			.otherwise()
				.setHeader("CamelMongoDbCriteria", simple("{\"categoryId\": '${header.category_id}'}"))
		.end()
		.to("mongodb:mycartdb?database=mycartdb&collection=item&operation=findAll")
		.choice()
			.when(simple("${body.isEmpty()}"))
				.throwException(new CategoryIdErrorException("not found"))
			.otherwise()
				.log("${body}")
				.process(new MapCategoryToListProcessor())
				.marshal().json()
	    .end();

		
		
		
		
		from("direct:additems")
		.unmarshal().json(JsonLibrary.Jackson,Item.class)
		.marshal().json()
		.setHeader("itemid",simple("${body[_id]}"))
		.unmarshal().json(JsonLibrary.Jackson,Item.class)
		.log("hello : ${body.itemPrice.basePrice}")
		.setProperty("baseprice",simple("${body.itemPrice.basePrice}"))
		.setProperty("sellingprice",simple("${body.itemPrice.sellingPrice}"))
		.setProperty("messagebody",simple("${body}"))
		.log("${exchangeProperty.baseprice}")
		.process(new IdToStringProcessor())
		.to("mongodb:mycartdb?database=mycartdb&collection=item&operation=findById")
		.choice()
			.when(simple("${body} == null"))
				.log("add order")
				.setBody(simple("${exchangeProperty.messagebody}"))
				.choice()
					.when(simple("${exchangeProperty.baseprice} > 0 && ${exchangeProperty.sellingprice} > 0"))
						.log("greater than 0")
						.setHeader("catId",simple("${body.categoryId}"))
						.setHeader(Exchange.HTTP_METHOD,constant("GET"))
						.toD("http://localhost:9091/category/getcategoryname/${header.catId}?bridgeEndpoint=true")
						.log("${body}")
						.choice()
							.when(simple("${body} == null"))
								.throwException(new CategoryIdErrorException("invalid"))
							.otherwise()
								.setProperty("categoryname",simple("${body}"))
								.log("hello : ${exchangeProperty.categoryname}")
								.process(new CategoryNameAddingProcessor())
								.to("mongodb:mycartdb?database=mycartdb&collection=item&operation=insert")
								.setHeader("CamelHttpResponseCode",constant(201))
								.setBody(constant("Order Placed..."))
								.log("${body}")
						.endChoice()
					.otherwise()
						.log("not greater than 0")
						.throwException(new GreaterThanZeroException("should be greater"))
				.endChoice()
			.otherwise()
				.log("dont add")
				.throwException(new ProductAlreadyExistException("Item exist"))
		.end();
		
		
		
	}

}
