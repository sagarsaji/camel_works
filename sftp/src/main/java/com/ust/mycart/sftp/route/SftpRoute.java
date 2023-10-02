package com.ust.mycart.sftp.route;

import java.util.List;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

import com.ust.mycart.sftp.aggregator.CategoryNameAggregator;
import com.ust.mycart.sftp.processor.ItemTrendAnalyzer;

@Component
public class SftpRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		rest().get("/api").to("direct:data");

		from("direct:data").to("mongodb:mycartdb?database=mycartdb&collection=item&operation=findAll").marshal().json()
				.setProperty("itemdetails", simple("${body}"))
				.to("file:C:/Users/245195/Documents/testfolder?fileName=item.json").multicast()
				.to("direct:itemTrendAnalyzer").end()
				.to("mongodb:mycartdb?database=mycartdb&collection=category&operation=findAll").marshal().json()
				.to("file:C:/Users/245195/Documents/testfolder?fileName=category.json").end();

		from("direct:itemTrendAnalyzer").routeId("itemTrendAnalyzer").unmarshal().json(JsonLibrary.Jackson, List.class)
				.setProperty("listbody", simple("${body}")).split(simple("${body}"), new CategoryNameAggregator())
				.setProperty("messagebody", simple("${body}")).setProperty("categoryid", simple("${body[categoryId]}"))
				.setBody(simple("${exchangeProperty.categoryid}"))
				.to("mongodb:mycartdb?database=mycartdb&collection=category&operation=findById")
				.setProperty("categoryname", simple("${body[categoryName]}")).end().process(new ItemTrendAnalyzer())
				.marshal().jaxb(true)
				.to("file:C:/Users/245195/Documents/testfolder/final?fileName=itemTrendAnalyzer.xml")
				.log(LoggingLevel.INFO, "Converted to XML");

//		from("direct:storeFrontApp")
//		.routeId("storeFrontApp")
//		.unmarshal().json()
//		.split(simple("${body}"))
//			.process(new Processor() {
//				
//				@Override
//				public void process(Exchange exchange) throws Exception {
//					// TODO Auto-generated method stub
//					JsonNode product = exchange.getIn().getBody(JsonNode.class);
//					
//					
//					ObjectMapper objectMapper = new ObjectMapper();
//					ObjectNode jsonNode = objectMapper.createObjectNode();
//					
//					jsonNode.put("_id",product.get("_id").asText());
//					jsonNode.put("itemName",product.get("itemName").asText());
//					jsonNode.set("itemPrice",product.get("itemPrice"));
//					jsonNode.set("stockDetails",product.get("stockDetails"));
//					jsonNode.put("specialProduct",product.get("specialProduct").asBoolean());
//					
//					exchange.getIn().setBody(jsonNode);
//				}
//			})
//			.marshal().json()
//			.log("${body}")
//		.end();

	}

}
