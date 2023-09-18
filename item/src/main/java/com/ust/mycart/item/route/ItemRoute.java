package com.ust.mycart.item.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

import com.ust.mycart.item.entity.Item;
import com.ust.mycart.item.exception.CategoryIdErrorException;
import com.ust.mycart.item.exception.GreaterThanZeroException;
import com.ust.mycart.item.exception.IdNotFoundException;
import com.ust.mycart.item.exception.ProductAlreadyExistException;
import com.ust.mycart.item.processor.CategoryNameAddingProcessor;
import com.ust.mycart.item.processor.MapCategoryToListProcessor;
import com.ust.mycart.item.processor.StockUpdationProcessor;

@Component
public class ItemRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		// Handled exception here
		onException(IdNotFoundException.class).handled(true).setHeader("CamelHttpResponseCode", constant(404))
				.setBody(simple("{{error.itemNotFound}}"));

		onException(ProductAlreadyExistException.class).handled(true).setHeader("CamelHttpResponseCode", constant(409))
				.setBody(constant("{{error.productAlreadyExist}}"));

		onException(CategoryIdErrorException.class).handled(true).setHeader("CamelHttpResponseCode", constant(404))
				.setBody(constant("{{error.categoryNotFound}}"));

		onException(GreaterThanZeroException.class).handled(true).setHeader("CamelHttpResponseCode", constant(400))
				.setBody(constant("{{error.greaterThanZeroException}}"));

		onException(Throwable.class).handled(true).setBody(simple("An error occured : ${exception.message}"));

		// Rest entry points
		rest()
				// API to access item by item id
				.get("/items/{_id}").to("direct:getItemsById")

				// API to access item by category id and include a filter
				.get("/category/{category_id}").to("direct:getByCategoryId")

				// API to add an item
				.post("/item").to("direct:addItems")

				// API to update an item
				.put("/item").to("direct:updateItems");

		from("direct:getItemsById").setBody(simple("${header._id}"))
				.to("mongodb:mycartdb?database=mycartdb&collection=item&operation=findById").choice()
				.when(simple("${body} == null")).throwException(new IdNotFoundException("not found")).otherwise()
				.marshal().json().end();

		from("direct:getByCategoryId").setBody(simple("${header.category_id}"))
				.setHeader("CamelMongoDbFieldsProjection", constant("{ categoryName: 1 , categoryDep: 1}"))
				.to("mongodb:mycartdb?database=mycartdb&collection=category&operation=findById").end().log("${body}")
				.setProperty("categoryname", simple("${body[categoryName]}"))
				.setProperty("categorydept", simple("${body[categoryDep]}"))
				.log("hello : ${exchangeProperty.categoryname}").log("hii : ${exchangeProperty.categorydept}")

				.choice().when(header("includeSpecial").isEqualTo("true"))
				.setHeader("CamelMongoDbCriteria",
						simple("{\"categoryName\": '${exchangeProperty.categoryname}',\"specialProduct\": true}"))
				.when(header("includeSpecial").isEqualTo("false"))
				.setHeader("CamelMongoDbCriteria",
						simple("{\"categoryName\": '${exchangeProperty.categoryname}',\"specialProduct\": false}"))
				.otherwise()
				.setHeader("CamelMongoDbCriteria", simple("{\"categoryName\": '${exchangeProperty.categoryname}'}"))
				.end().removeHeader("CamelMongoDbFieldsProjection")

				.to("mongodb:mycartdb?database=mycartdb&collection=item&operation=findAll").log("${body}")

				.choice().when(simple("${body.isEmpty()}")).throwException(new CategoryIdErrorException("not found"))
				.otherwise().log("${body}").setProperty("finalbody", simple("${body}"))
				.setBody(simple("${exchangeProperty.finalbody}")).process(new MapCategoryToListProcessor()).marshal()
				.json().end()

				.end();

		from("direct:addItems").setHeader("itemid", simple("${body[_id]}"))
				.setHeader("category_id", simple("${body[categoryId]}")).unmarshal()
				.json(JsonLibrary.Jackson, Item.class).log("hello : ${body.itemPrice.basePrice}")
				.setProperty("baseprice", simple("${body.itemPrice.basePrice}"))
				.setProperty("sellingprice", simple("${body.itemPrice.sellingPrice}"))
				.setProperty("messagebody", simple("${body}")).log("${exchangeProperty.baseprice}")
				.setBody(simple("${header.itemid}"))
				.to("mongodb:mycartdb?database=mycartdb&collection=item&operation=findById").choice()
				.when(simple("${body} == null")).log("add order").setBody(simple("${exchangeProperty.messagebody}"))
				.choice().when(simple("${exchangeProperty.baseprice} > 0 && ${exchangeProperty.sellingprice} > 0"))
				.log("greater than 0").setBody(simple("${header.category_id}"))
				.setHeader("CamelMongoDbFieldsProjection", constant("{ categoryName: 1 , _id: 0}"))
				.to("mongodb:mycartdb?database=mycartdb&collection=category&operation=findById")
				.setBody(simple("${body[categoryName]}")).removeHeader("CamelMongoDbFieldsProjection").choice()
				.when(simple("${body} == null")).throwException(new CategoryIdErrorException("invalid")).otherwise()
				.setProperty("categoryname", simple("${body}")).log("hello : ${exchangeProperty.categoryname}")
				.process(new CategoryNameAddingProcessor())
				.to("mongodb:mycartdb?database=mycartdb&collection=item&operation=insert")
				.setHeader("CamelHttpResponseCode", constant(201)).setBody(constant("Order Placed...")).log("${body}")
				.endChoice().otherwise().log("not greater than 0")
				.throwException(new GreaterThanZeroException("should be greater")).endChoice().otherwise()
				.log("dont add").throwException(new ProductAlreadyExistException("Item exist")).end();

		from("direct:updateItems").split(simple("${body[items]}")).stopOnException()
				.setHeader("itemid", simple("${body[_id]}"))
				.setProperty("soldout", simple("${body[stockDetails][soldOut]}"))
				.setProperty("damaged", simple("${body[stockDetails][damaged]}")).setBody(simple("${header.itemid}"))
				.to("mongodb:mycartdb?database=mycartdb&collection=item&operation=findById").choice()
				.when(simple("${body} == null")).log("nothing present")
				.throwException(new IdNotFoundException("not found")).otherwise().log("item found")
				.setProperty("availablestock", simple("${body[stockDetails][availableStock]}"))
				.log("available stock : ${exchangeProperty.availablestock}").process(new StockUpdationProcessor())
				.to("mongodb:mycartdb?database=mycartdb&collection=item&operation=save").end().end()
				.setHeader("CamelHttpResponseCode", constant(200)).setBody(constant("Stock Updated..."));

	}

}
