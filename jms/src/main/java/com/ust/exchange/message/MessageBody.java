package com.ust.exchange.message;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageBody {
	
	@Autowired
	private ProducerTemplate template;
	
	public void function() {
		
		template.sendBody("activemq:queue:inputqueue","Hello from JMS....");
		
	}

}
