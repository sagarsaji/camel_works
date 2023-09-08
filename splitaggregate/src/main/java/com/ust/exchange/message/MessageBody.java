package com.ust.exchange.message;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageBody {
	
	@Autowired
	private ProducerTemplate template;
	
	public void function() {
		template.sendBody("direct:start","1,2,3,4,5,6,7,8,9,10");
	}
	
	public boolean even(int n) {
		return n%2==0;
	}

}
