package com.ust.mycart.item.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.ust.mycart.item.entity.Item;

public class CategoryNameAddingProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		Item item = exchange.getProperty("messagebody", Item.class);
		exchange.getIn().setBody(item);
		System.out.println(item);
		String categoryname = exchange.getProperty("categoryname", String.class);
		item.setCategoryName(categoryname);
		exchange.getIn().setBody(item);
		exchange.setProperty("messageprocessed", item);
	}

}
