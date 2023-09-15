package com.ust.mycart.item.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.mongodb.BasicDBObject;

public class MapCategoryToListProcessor implements Processor{

	@SuppressWarnings("unchecked")
	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		List<BasicDBObject> item = exchange.getIn().getBody(List.class);
		exchange.getIn().setBody(item);
		Map<String,List<BasicDBObject>> map = new HashMap<>();
		String category = exchange.getIn().getHeader("category_id",String.class);
		map.put(category, item);
		exchange.getIn().setBody(map);
	}

}
