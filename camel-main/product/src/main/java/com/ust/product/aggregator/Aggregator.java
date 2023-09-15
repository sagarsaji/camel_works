package com.ust.product.aggregator;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ust.product.entity.Order;

@Component
public class Aggregator implements AggregationStrategy {

	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		// TODO Auto-generated method stub
		if (oldExchange == null) {
			return newExchange;
		}
		
		String oldorder = oldExchange.getIn().getBody(String.class);
		String neworder = newExchange.getIn().getBody(String.class);
		
		List<String> finalist = new ArrayList<>();
		
		finalist.add(oldorder);
		finalist.add(neworder);
		
		oldExchange.getIn().setBody(finalist);
		
		return oldExchange;
		
	}

}
