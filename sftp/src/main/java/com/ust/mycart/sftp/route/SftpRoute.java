package com.ust.mycart.sftp.route;



import java.util.List;



import org.apache.camel.LoggingLevel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;


import com.ust.mycart.sftp.aggregator.CategoryNameAggregator;
import com.ust.mycart.sftp.aggregator.JsonBodyAggregator;
import com.ust.mycart.sftp.aggregator.ListAggregator;
import com.ust.mycart.sftp.entity.JsonBody;

import com.ust.mycart.sftp.processor.ControlRefUpdationProcessor;
import com.ust.mycart.sftp.processor.ItemTrendAnalyzer;
import com.ust.mycart.sftp.processor.JsonResponseProcessor;
import com.ust.mycart.sftp.processor.RecentDateProcessor;
import com.ust.mycart.sftp.processor.StorefrontDateUpdationProcessor;

@Component
public class SftpRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		
		

		onException(Throwable.class).handled(true).setHeader("CamelHttpResponseCode", constant(500))
				.setHeader("Content-Type", constant("application/json"))
				.setBody(constant("{\"message\": Internal Server Problem occured}"));


		from("cron:myData?schedule=0/10 * * * * *")
		.to("mongodb:mycartdb?database=mycartdb&collection=item&operation=findAll")
		.marshal().json()
		.multicast()
			.to("direct:itemTrendBody")
			.to("direct:storeFrontBody")
		.end();
		
		
		from("direct:itemTrendBody")
		.unmarshal().json(JsonLibrary.Jackson,List.class)
		.setProperty("list",simple("${body}"))
		.split(simple("${body}"),new ListAggregator())
			.setProperty("listbody",simple("${body}"))
			.setProperty("recentDate",simple("${body[lastUpdateDate]}"))
			.process(new RecentDateProcessor())
			.setHeader("CamelMongoDbCriteria", simple("{\"_id\" : \"date\"}"))
			.to("mongodb:mycartdb?database=mycartdb&collection=controlref&operation=findOneByQuery")
			.setProperty("controlrefdate",simple("${body[date]}"))
			.choice()
				.when(exchangeProperty("recentDateNew").isGreaterThan(exchangeProperty("controlrefdate")))
					.setProperty("messagebody",exchangeProperty("listbody"))
			.end()
		.end()
		.setBody(simple("${body}"))
		.to("direct:itemTrendAnalyzer");
		

		from("direct:itemTrendAnalyzer")
		.routeId("itemTrendAnalyzer")
		.choice()
			.when(simple("${body.size()} == 0"))
				.log(LoggingLevel.INFO,"empty")
			.otherwise()
				.marshal().json()
				.unmarshal().json(JsonLibrary.Jackson, List.class)
				.setProperty("listbody", simple("${body}"))
				.split(simple("${body}"),new CategoryNameAggregator())
					.setProperty("recentdate",simple("${body[lastUpdateDate]}"))
					.setProperty("messagebody", simple("${body}"))
					.setProperty("categoryid", simple("${body[categoryId]}"))
					.setBody(simple("${exchangeProperty.categoryid}"))
					.to("mongodb:mycartdb?database=mycartdb&collection=category&operation=findById")
					.setProperty("categoryname", simple("${body[categoryName]}"))
				.end()
				.process(new ItemTrendAnalyzer())
				.marshal().jaxb(true)
				.log(LoggingLevel.INFO,"Converted to XML")
				.setHeader("CamelFileName", simple("itemTrendAnalyzer_${date:now:yyyyMMdd_HHmmss}.xml"))
				.to("file:C:/Users/245195/Documents/testfolder/final/itemTrendAnalyzer")
				.process(new ControlRefUpdationProcessor())
				.to("mongodb:mycartdb?database=mycartdb&collection=controlref&operation=save")
				.log(LoggingLevel.INFO,"Date updated")
		.end();
		
		
		from("direct:storeFrontBody")
		.unmarshal().json(JsonLibrary.Jackson,List.class)
		.setProperty("list",simple("${body}"))
		.split(simple("${body}"),new ListAggregator())
			.setProperty("listbody",simple("${body}"))
			.setProperty("recentDate",simple("${body[lastUpdateDate]}"))
			.process(new RecentDateProcessor())
			.setHeader("CamelMongoDbCriteria", simple("{\"_id\" : \"storeFrontApp\"}"))
			.to("mongodb:mycartdb?database=mycartdb&collection=controlref&operation=findOneByQuery")
			.setProperty("storefrontdate",simple("${body[date]}"))
			.choice()
				.when(exchangeProperty("recentDateNew").isGreaterThan(exchangeProperty("storefrontdate")))
					.setProperty("messagebody",exchangeProperty("listbody"))
			.end()
		.end()
		.setBody(simple("${body}"))
		.to("direct:storeFrontApp");
		
		
		from("direct:storeFrontApp")
		.routeId("storeFrontApp")
		.choice()
			.when(simple("${body.size()} == 0"))
				.log(LoggingLevel.INFO,"empty")
			.otherwise()
				.split(simple("${body}"),new JsonBodyAggregator())
					.unmarshal().json(JsonLibrary.Jackson,JsonBody.class)
					.setProperty("messagebody",simple("${body}"))
					.setBody(simple("${body.categoryId}"))
					.to("mongodb:mycartdb?database=mycartdb&collection=category&operation=findById")
					.setProperty("categoryname",simple("${body[categoryName]}"))
					.process(new JsonResponseProcessor())
				.end()
				.marshal().json(JsonLibrary.Jackson,true)
				.log(LoggingLevel.INFO,"Converted to JSON")
				.setHeader("CamelFileName", simple("storeFrontApp_${date:now:yyyyMMdd_HHmmss}.json"))
				.to("file:C:/Users/245195/Documents/testfolder/final/storeFrontApp")
				.process(new StorefrontDateUpdationProcessor())
				.to("mongodb:mycartdb?database=mycartdb&collection=controlref&operation=save")
				.log("date updated")
		.end();
		
		

	}
}
