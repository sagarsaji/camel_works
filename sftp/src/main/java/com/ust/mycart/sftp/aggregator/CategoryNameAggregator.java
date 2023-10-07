package com.ust.mycart.sftp.aggregator;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

public class CategoryNameAggregator implements AggregationStrategy {

	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		// TODO Auto-generated method stub
		if (oldExchange == null) {
			// Initialize the category map
			Map<String, String> categoryMap = new HashMap<>();

			// Get category information from newExchange
			String categoryId = newExchange.getProperty("categoryid", String.class);
			String categoryName = newExchange.getProperty("categoryname", String.class);
			String recentDate = newExchange.getProperty("recentdate", String.class);

			// Add the category to the map
			categoryMap.put(categoryId, categoryName);

			// Set the category map as a property on newExchange
			newExchange.setProperty("categoryMap", categoryMap);
			newExchange.setProperty("recentDate", recentDate);
			return newExchange;
		} else {
			// Get the existing category map from oldExchange
			@SuppressWarnings("unchecked")
			Map<String, String> categoryMap = oldExchange.getProperty("categoryMap", Map.class);

			// Get category information from newExchange
			String categoryId = newExchange.getProperty("categoryid", String.class);
			String categoryName = newExchange.getProperty("categoryname", String.class);

			// Add the category to the map
			categoryMap.put(categoryId, categoryName);

			String recentDate = oldExchange.getProperty("recentDate", String.class);
			recentDate = newExchange.getProperty("recentdate", String.class);

			// Set the updated category map as a property on oldExchange
			oldExchange.setProperty("categoryMap", categoryMap);
			oldExchange.setProperty("recentDate", recentDate);
			return oldExchange;
		}

	}

}
