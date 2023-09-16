package com.ust.mycart.item.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class CategoryIdToStringProcessor implements Processor{

	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		String id = exchange.getIn().getHeader("category_id",String.class);
		exchange.getIn().setBody(id);
	}

}
