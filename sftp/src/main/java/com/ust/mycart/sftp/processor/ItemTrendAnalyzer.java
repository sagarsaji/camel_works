package com.ust.mycart.sftp.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ust.mycart.sftp.inventory.Category;
import com.ust.mycart.sftp.inventory.Inventory;
import com.ust.mycart.sftp.inventory.Item;

public class ItemTrendAnalyzer implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub

		@SuppressWarnings("unchecked")
		/**
		 * Getting the list body from property
		 */
		List<Map<String, Object>> products = exchange.getProperty("listbody", List.class);

		/**
		 * Getting the map that contains category id and category name
		 */
		@SuppressWarnings("unchecked")
		Map<String, String> categorynames = exchange.getProperty("categoryMap", Map.class);

		ObjectMapper objectMapper = new ObjectMapper();

		Inventory inventory = new Inventory();
		/**
		 * The map that has category id as the key and category object as value.
		 * category object contains category id,name and item object
		 */
		Map<String, Category> categoryMap = new HashMap<>();

		for (Map<String, Object> product : products) {
			String categoryId = product.get("categoryId").toString();

			// Check if the category already exists in the map
			Category category = categoryMap.get(categoryId);

			if (category == null) {
				// If not, create a new category
				category = new Category();
				category.setId(categoryId);
				category.setName(categorynames.get(category.getId()));
				categoryMap.put(categoryId, category);
			}

			// Create and add the item to the category
			JsonNode stockdetails = objectMapper.convertValue(product.get("stockDetails"), JsonNode.class);
			JsonNode itemprice = objectMapper.convertValue(product.get("itemPrice"), JsonNode.class);

			Item item = new Item();
			item.setItemId(product.get("_id").toString());
			item.setCategoryId(categoryId);
			item.setAvailableStock(stockdetails.get("availableStock").intValue());
			item.setSellingPrice(itemprice.get("sellingPrice").intValue());

			category.getItems().add(item);

		}

		// Add all categories from the map to the inventory
		inventory.getCategories().addAll(categoryMap.values());

		exchange.getIn().setBody(inventory);

	}

}
