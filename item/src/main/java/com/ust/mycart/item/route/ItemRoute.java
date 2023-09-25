package com.ust.mycart.item.route;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mongodb.MongoDbConstants;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.bson.Document;
import org.springframework.stereotype.Component;

import com.ust.mycart.item.entity.Item;
import com.ust.mycart.item.exception.CategoryIdErrorException;
import com.ust.mycart.item.exception.GreaterThanZeroException;
import com.ust.mycart.item.exception.IdNotFoundException;
import com.ust.mycart.item.exception.ProductAlreadyExistException;
import com.ust.mycart.item.headers.HeaderClass;
import com.ust.mycart.item.processor.DateAddingProcessor;
import com.ust.mycart.item.processor.MapCategoryToListProcessor;
import com.ust.mycart.item.processor.PostResponseProcessor;
import com.ust.mycart.item.processor.StockUpdationProcessor;

@Component
public class ItemRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		// Handled exception here
		onException(IdNotFoundException.class).handled(true)
				.setHeader(HeaderClass.CAMEL_HTTP_RESPONSE_CODE, constant(404))
				.setHeader("Content-Type", constant("application/json"))
				.setBody(constant("{\"message\":\"{{error.itemNotFound}}\"}"));

		onException(ProductAlreadyExistException.class).handled(true)
				.setHeader(HeaderClass.CAMEL_HTTP_RESPONSE_CODE, constant(409))
				.setHeader("Content-Type", constant("application/json"))
				.setBody(simple("{\"message\":\"{{error.productAlreadyExist}}\"}"));

		onException(CategoryIdErrorException.class).handled(true)
				.setHeader(HeaderClass.CAMEL_HTTP_RESPONSE_CODE, constant(404))
				.setHeader("Content-Type", constant("application/json"))
				.setBody(simple("{\"message\":\"{{error.categoryNotFound}}\"}"));

		onException(GreaterThanZeroException.class).handled(true)
				.setHeader(HeaderClass.CAMEL_HTTP_RESPONSE_CODE, constant(400))
				.setHeader("Content-Type", constant("application/json"))
				.setBody(simple("{\"message\":\"{{error.greaterThanZeroException}}\"}"));

		onException(Throwable.class).handled(true).setHeader(HeaderClass.CAMEL_HTTP_RESPONSE_CODE, constant(500))
				.setHeader("Content-Type", constant("application/json"))
				.setBody(simple("{\"message\":\"{{server.internalServerError}}\"}"));

		// REST entry points
		rest()
				// API to access item by item id
				.get("/items/{_id}").to("direct:getItemsById")

				// API to access item by category id and include a filter
				.get("/category/{category_id}").to("direct:getByCategoryId")

				// API to add an item
				.post("/item").to("direct:addItems")

				// API to update an item
				.put("/item").to("direct:updateItems").get("/api").to("direct:mydata");

		// Route to access item by item id
		from("direct:getItemsById").setBody(simple("${header._id}"))
				.to("mongodb:mycartdb?database=mycartdb&collection=item&operation=findById").choice()
				.when(simple("${body} == null")).log(LoggingLevel.INFO, "Item not found")
				.throwException(new IdNotFoundException("not found")).otherwise().log(LoggingLevel.INFO, "Item found")
				.marshal().json(JsonLibrary.Jackson).unmarshal().json(JsonLibrary.Jackson, Item.class)
				.setProperty("messagebody", simple("${body}")).setHeader("category_id", simple("${body.categoryId}"))
				.setBody(simple("${header.category_id}"))
				.setHeader(MongoDbConstants.FIELDS_PROJECTION, constant("{ categoryName: 1 }"))
				.to("mongodb:mycartdb?database=mycartdb&collection=category&operation=findById")
				.setProperty("categoryname", simple("${body[categoryName]}")).process(new PostResponseProcessor())
				.marshal().json().log(LoggingLevel.INFO, "Item fetched from database")
				.setHeader(HeaderClass.CAMEL_HTTP_RESPONSE_CODE, constant(200)).end();

		// Route to access item by category id and include a filter
		from("direct:getByCategoryId").setBody(simple("${header.category_id}"))
				.setHeader(MongoDbConstants.FIELDS_PROJECTION, constant("{ categoryName: 1 , categoryDep: 1}"))
				.to("mongodb:mycartdb?database=mycartdb&collection=category&operation=findById")
				.setProperty("categoryname", simple("${body[categoryName]}"))
				.setProperty("categorydept", simple("${body[categoryDep]}")).choice()
				.when(header("includeSpecial").isEqualTo("true"))
				.setHeader(HeaderClass.CAMEL_MONGODB_CRITERIA,
						simple("{\"categoryId\": '${header.category_id}',\"specialProduct\": true}"))
				.when(header("includeSpecial").isEqualTo("false"))
				.setHeader(HeaderClass.CAMEL_MONGODB_CRITERIA,
						simple("{\"categoryId\": '${header.category_id}',\"specialProduct\": false}"))
				.otherwise()
				.setHeader(HeaderClass.CAMEL_MONGODB_CRITERIA, simple("{\"categoryId\": '${header.category_id}'}"))
				.end().removeHeader(MongoDbConstants.FIELDS_PROJECTION)
				.to("mongodb:mycartdb?database=mycartdb&collection=item&operation=findAll").choice()
				.when(simple("${body.isEmpty()}")).log(LoggingLevel.INFO, "Category Not found")
				.throwException(new CategoryIdErrorException("not found")).otherwise()
				.log(LoggingLevel.INFO, "Category Found").setProperty("finalbody", simple("${body}"))
				.setBody(simple("${exchangeProperty.finalbody}")).process(new MapCategoryToListProcessor()).marshal()
				.json().setHeader(HeaderClass.CAMEL_HTTP_RESPONSE_CODE, constant(200)).end();

		// Route to add an item
		from("direct:addItems").setHeader("itemid", simple("${body[_id]}"))
				.setHeader("category_id", simple("${body[categoryId]}")).unmarshal()
				.json(JsonLibrary.Jackson, Item.class).setProperty("baseprice", simple("${body.itemPrice.basePrice}"))
				.setProperty("sellingprice", simple("${body.itemPrice.sellingPrice}"))
				.setProperty("messagebody", simple("${body}")).setBody(simple("${header.itemid}"))
				.to("mongodb:mycartdb?database=mycartdb&collection=item&operation=findById").choice()
				.when(simple("${body} == null")).log(LoggingLevel.INFO, "Add Order")
				.setBody(simple("${exchangeProperty.messagebody}")).choice()
				.when(simple("${exchangeProperty.baseprice} > 0 && ${exchangeProperty.sellingprice} > 0"))
				.log(LoggingLevel.INFO, "Greater than zero").setBody(simple("${header.category_id}"))
				.setHeader(MongoDbConstants.FIELDS_PROJECTION, constant("{ categoryName: 1 , _id: 0}"))
				.to("mongodb:mycartdb?database=mycartdb&collection=category&operation=findById")
				.setBody(simple("${body[categoryName]}")).removeHeader(MongoDbConstants.FIELDS_PROJECTION).choice()
				.when(simple("${body} == null")).throwException(new CategoryIdErrorException("invalid")).otherwise()
				.setProperty("categoryname", simple("${body}")).process(new DateAddingProcessor())
				.to("mongodb:mycartdb?database=mycartdb&collection=item&operation=insert")
				.setHeader(HeaderClass.CAMEL_HTTP_RESPONSE_CODE, constant(200)).process(new PostResponseProcessor())
				.marshal().json().endChoice().otherwise().log(LoggingLevel.INFO, "Not greater than zero")
				.throwException(new GreaterThanZeroException("should be greater")).endChoice().otherwise()
				.log(LoggingLevel.INFO, "Dont add").throwException(new ProductAlreadyExistException("Item exist"))
				.end();

		// Route to update an item
		from("direct:updateItems").split(simple("${body[items]}")).setHeader("itemid", simple("${body[_id]}"))
				.setProperty("soldout", simple("${body[stockDetails][soldOut]}"))
				.setProperty("damaged", simple("${body[stockDetails][damaged]}")).setBody(simple("${header.itemid}"))
				.to("mongodb:mycartdb?database=mycartdb&collection=item&operation=findById").choice()
				.when(simple("${body} == null")).log(LoggingLevel.INFO, "Item not found for id : ${header.itemid}")
				.throwException(new IdNotFoundException("not found")).otherwise().log(LoggingLevel.INFO, "Item found")
				.setProperty("availablestock", simple("${body[stockDetails][availableStock]}"))
				.process(new StockUpdationProcessor())
				.to("mongodb:mycartdb?database=mycartdb&collection=item&operation=save").end().end()
				.setHeader(HeaderClass.CAMEL_HTTP_RESPONSE_CODE, constant(204));

		from("direct:mydata").routeId("schedulerTask")
				.to("mongodb:mycartdb?database=mycartdb&collection=item&operation=findAll").split(simple("${body}"))
				.multicast().to("direct:itemTrendAnalyzer").end();

		from("direct:itemTrendAnalyzer").process(new Processor() {

			@Override
			public void process(Exchange exchange) throws Exception {
				// TODO Auto-generated method stub
				Document doc = exchange.getIn().getBody(Document.class);
				exchange.getIn().setBody(doc);
			}
		}).log("to xml : ${body}");

	}

}
