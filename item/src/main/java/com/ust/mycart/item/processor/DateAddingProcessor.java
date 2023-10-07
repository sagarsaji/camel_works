package com.ust.mycart.item.processor;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.ust.mycart.item.entity.Item;

public class DateAddingProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		Item item = exchange.getProperty("messagebody", Item.class);
		exchange.getIn().setBody(item);

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = dateFormat.format(new Date());
		item.setLastUpdateDate(date);

		exchange.getIn().setBody(item);
	}

}
