package com.ust.mycart.activemqconsumer.processor;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.bson.Document;

public class StockUpdationProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub

		Document item = exchange.getIn().getBody(Document.class);
		Document stockdetails = (Document) item.get("stockDetails");
		String lastupdatedate = (String) item.get("lastUpdateDate");

		int soldout = exchange.getProperty("soldout", Integer.class);
		int damage = exchange.getProperty("damaged", Integer.class);
		int available = exchange.getProperty("availablestock", Integer.class);

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		lastupdatedate = dateFormat.format(new Date());

		available = available - soldout - damage;

		stockdetails.put("availableStock", available);
		item.put("stockDetails", stockdetails);
		item.put("lastUpdateDate", lastupdatedate);

		exchange.getIn().setBody(item);

	}

}
