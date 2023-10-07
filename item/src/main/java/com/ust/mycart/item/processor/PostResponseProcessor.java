package com.ust.mycart.item.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.ust.mycart.item.entity.Item;

import com.ust.mycart.item.entity.Response;

public class PostResponseProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub

		Item item = exchange.getProperty("messagebody", Item.class);
		exchange.getIn().setBody(item);
		String categoryname = exchange.getProperty("categoryname", String.class);

		Response response = new Response();

		response.set_id(item.get_id());
		response.setItemName(item.getItemName());
		response.setCategoryName(categoryname);
		response.setItemPrice(item.getItemPrice());
		response.setStockDetails(item.getStockDetails());
		response.setSpecialProduct(item.getSpecialProduct());

		exchange.getIn().setBody(response);

	}

}
