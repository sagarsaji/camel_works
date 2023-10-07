package com.ust.mycart.sftp.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.ust.mycart.sftp.entity.JsonBody;
import com.ust.mycart.sftp.entity.JsonResponse;

public class JsonResponseProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		JsonBody jsonResponse = exchange.getProperty("messagebody", JsonBody.class);
		exchange.getIn().setBody(jsonResponse);
		JsonResponse response = new JsonResponse();

		String categoryname = exchange.getProperty("categoryname", String.class);

		response.set_id(jsonResponse.get_id());
		response.setItemName(jsonResponse.getItemName());
		response.setCategoryName(categoryname);
		response.setItemPrice(jsonResponse.getItemPrice());
		response.setStockDetails(jsonResponse.getStockDetails());
		response.setSpecialProduct(jsonResponse.getSpecialProduct());

		exchange.setProperty("jsonmessage", response);

		exchange.getIn().setBody(response);
	}

}
