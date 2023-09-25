package com.ust.mycart.item.processor;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ust.mycart.item.entity.Item;
import com.ust.mycart.item.entity.ItemPrice;
import com.ust.mycart.item.entity.StockDetails;

public class CategoryNameAddingProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		Item item = exchange.getProperty("messagebody", Item.class);
		exchange.getIn().setBody(item);
		String categoryname = exchange.getProperty("categoryname", String.class);

		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode jsonNode = objectMapper.createObjectNode();

		ItemPrice itemPrice = item.getItemPrice();
		StockDetails stockDetails = item.getStockDetails();

		JsonNode itemPriceJson = objectMapper.valueToTree(itemPrice);
		JsonNode stockDetailsJson = objectMapper.valueToTree(stockDetails);

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = dateFormat.format(new Date());
		item.setLastUpdateDate(date);

		jsonNode.put("_id", item.get_id());
		jsonNode.put("itemName", item.getItemName());
		jsonNode.put("categoryName", categoryname);
		jsonNode.set("itemPrice", itemPriceJson);
		jsonNode.set("stockDetails", stockDetailsJson);
		jsonNode.put("specialProduct", item.getSpecialProduct());
		jsonNode.put("lastUpdateDate", date);

		exchange.getIn().setBody(jsonNode);
	}

}
