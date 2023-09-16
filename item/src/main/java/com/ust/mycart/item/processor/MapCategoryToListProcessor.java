package com.ust.mycart.item.processor;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.BasicDBObject;

public class MapCategoryToListProcessor implements Processor {

	@SuppressWarnings("unchecked")
	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		String categoryname = exchange.getProperty("categoryname", String.class);
		String categorydept = exchange.getProperty("categorydept", String.class);

		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode jsonNode = objectMapper.createObjectNode();

		jsonNode.put("categoryName", categoryname);
		jsonNode.put("categoryDepartment", categorydept);

		List<BasicDBObject> item = exchange.getIn().getBody(List.class);
		exchange.getIn().setBody(item);

		JsonNode itemJson = objectMapper.valueToTree(item);

		jsonNode.set("items", itemJson);
		exchange.getIn().setBody(jsonNode);
	}

}
