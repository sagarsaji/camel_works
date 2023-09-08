package com.ust.exchange.aggregation;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class MySumAggregationStrategy implements AggregationStrategy {

	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		
		if(oldExchange==null) {
			return newExchange;
		}
		
		String n1 = oldExchange.getIn().getBody(String.class);
		String n2 = newExchange.getIn().getBody(String.class);
		String n3 = n1+","+n2;
		oldExchange.getIn().setBody(n3);
		return oldExchange;
	}

}
