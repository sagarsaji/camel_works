package com.ust.mycart.item.processor;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.bson.Document;

import com.ust.mycart.item.entity.CategoryResponse;

public class MapCategoryToListProcessor implements Processor {

	@SuppressWarnings("unchecked")
	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		String categoryname = exchange.getProperty("categoryname", String.class);
		String categorydept = exchange.getProperty("categorydept", String.class);

		CategoryResponse response = new CategoryResponse();

		response.setCategoryName(categoryname);
		response.setCategoryDept(categorydept);

		List<Document> item = exchange.getIn().getBody(List.class);
		//exchange.getIn().setBody(item);

		response.setItems(item);
		exchange.getIn().setBody(response);
	}

}
