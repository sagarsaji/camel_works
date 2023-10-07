package com.ust.mycart.sftp.processor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class StorefrontDateUpdationProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<>();
		map.put("_id", "storeFrontApp");
		map.put("date", new Date());
		exchange.getIn().setBody(map);
	}

}
