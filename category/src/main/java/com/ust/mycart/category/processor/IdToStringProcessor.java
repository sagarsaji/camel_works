package com.ust.mycart.category.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class IdToStringProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		String id = exchange.getIn().getHeader("_id", String.class);
		exchange.getIn().setBody(id);
	}

}
