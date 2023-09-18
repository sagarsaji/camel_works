package com.ust.mycart.activemqconsumer.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.bson.Document;


public class StockUpdationProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		Document item = exchange.getIn().getBody(Document.class);
		Document stockdetails = (Document) item.get("stockDetails");

		int soldout = exchange.getProperty("soldout", Integer.class);
		int damage = exchange.getProperty("damaged", Integer.class);
		int available = exchange.getProperty("availablestock", Integer.class);

		available = available - soldout - damage;

		stockdetails.put("availableStock", available);
		item.put("stockDetails", stockdetails);

		exchange.getIn().setBody(item);
	}

}
