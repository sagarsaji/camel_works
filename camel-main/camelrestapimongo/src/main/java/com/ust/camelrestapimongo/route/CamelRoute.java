package com.ust.camelrestapimongo.route;



import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ust.camelrestapimongo.entity.Product;
import com.ust.camelrestapimongo.exception.ProductAlreadyExistException;


@Component
public class CamelRoute extends RouteBuilder {
	
	
	@Value("${spring.data.mongodb.database}")
    private String database;

    @Value("${spring.session.mongodb.collection-name}")
    private String collection;

	@Override
	public void configure() throws Exception {
		
		onException(ProductAlreadyExistException.class).handled(true)
		.setHeader("CamelHttpResponseCode",constant(409))
		.setBody(constant("The given product id already exist"));

		rest()
			.post("/addproduct").type(Product.class).to("direct:addproducts")
			.get("/getproducts").to("direct:getproducts")
			.get("/getproducts/product/{_id}").to("direct:getbyproductid")
			.get("/getproduct/{pname}").to("direct:getbyproductname")
			.put("/updateproduct").to("direct:updateproduct")
			.delete("/deleteproduct/{_id}").to("direct:deleteproduct");
		
		
		
		from("direct:addproducts")
		.unmarshal().json()
	    .setHeader("pid", simple("${body[_id]}"))
	    .log("id received : ${header.pid}")
	    .setHeader("CamelMongoDbCriteria", simple("{ _id: ${header.pid} }"))
	    .setHeader("messagebody",simple("${body}"))
	    .log("${body}")
	    .to("mongodb:productdb?database=" + database + "&collection=" + collection + "&operation=findOneByQuery")
	    .log("MongoDB Response: ${body}")
	    .choice()
	    	.when(simple("${body} == null"))
    		.log("do insert")
    			.setBody(simple("${header.messagebody}"))
	    		.log("before entering mongo: ${body}")
	    		.to("mongodb:productdb?database=" + database + "&collection=" + collection + "&operation=insert")
	    		.setHeader("CamelHttpResponseCode",constant(201))
	    		.setBody(simple("Item Added"))
			.otherwise()
				.setHeader("CamelHttpResponseCode",constant(409))
				.setBody(simple("Product Id already exist"))
		.end();
	    
	
		from("direct:getproducts")
		.to("mongodb:productdb?database=" + database + "&collection=" + collection + "&operation=findAll")
		.marshal().json();

		
		from("direct:getbyproductid")
		.setHeader("CamelMongoDbCriteria", simple("{ _id: ${header._id} }"))
	    .to("mongodb:productdb?database=" + database + "&collection=" + collection + "&operation=findAll")
	    .log("${body}")
	    .choice()
	    	.when(simple("${body.isEmpty()}"))
	    		.log("${body}")
	    		.setHeader("CamelHttpResponseCode",constant(404))
	    		.log("${header.CamelHttpResponseCode}")
	    		.setBody(constant("The given product id is not found"))
	    	.otherwise()
	    		.setHeader("CamelHttpResponseCode",constant(200))
	    		.marshal().json()
	    .end();

		
		from("direct:getbyproductname")
	    .setHeader("CamelMongoDbCriteria", simple("{ pname: { $regex: '.*${header.pname}.*', $options: 'i' } }"))
	    .to("mongodb:employeedb?database=" + database + "&collection=" + collection + "&operation=findAll")
	    .choice()
    		.when(simple("${body.isEmpty()}"))
    			.log("${body}")
    			.setHeader("CamelHttpResponseCode",constant(404))
    			.setBody(constant("The given product name is not found"))
    		.otherwise()
    			.setHeader("CamelHttpResponseCode",constant(200))
    			.marshal().json()
    	.end();

		
		from("direct:updateproduct")
		.unmarshal().json()
		.setHeader("update", simple("${body[_id]}"))
		.setHeader("updatemessage",simple("${body}"))
		.setHeader("CamelMongoDbCriteria", simple("{ _id: ${header.update} }"))
		.to("mongodb:productdb?database=" + database + "&collection=" + collection + "&operation=findOneByQuery")
		.log("${body}")
		.choice()
			.when(simple("${body} != null"))
				.log("update cheyam")
				.setBody(simple("${header.updatemessage}"))
				.to("mongodb:productdb?database=" + database + "&collection=" + collection + "&operation=save")
				.setHeader("CamelHttpResponseCode",constant(200))
				.setBody(constant("Updated..."))
			.otherwise()
				.log("product id is wrong")
				.setHeader("CamelHttpResponseCode",constant(404))
				.setBody(constant("The given product id is not found"))
		.end();

		
		from("direct:deleteproduct")
		.setHeader("CamelMongoDbCriteria", simple("{ _id: ${header._id} }"))
	    .to("mongodb:productdb?database=" + database + "&collection=" + collection + "&operation=findOneByQuery")
	    .log("${body}")
		.choice()
			.when(simple("${body} != null"))
				.log("delete cheyam")
				.setHeader("CamelHttpResponseCode",constant(200))
				.to("mongodb:productdb?database=" + database + "&collection=" + collection + "&operation=remove")
				.setBody(constant("Item Deleted..."))
			.otherwise()
				.log("product id is wrong")
				.setHeader("CamelHttpResponseCode",constant(404))
				.setBody(constant("The given product id is not found"))
		.end();


	}

}