package com.ust.product.route.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ust.product.entity.Order;

@Component
public class OrderService {

	
	@Autowired
	private ConsumerTemplate consumer;

	public void addOrderId(Exchange exchange) {

		Order neworder = exchange.getIn().getBody(Order.class);
		exchange.getIn().setBody(neworder);
		int orderid = 100_000 + (int) (Math.random() * 900_000);
		neworder.set_id(orderid);
		exchange.getIn().setHeader("oid", orderid);
		Order order = new Order(neworder.get_id(), neworder.getProductid(), neworder.getQuantity(), null, null);
		exchange.getIn().setBody(order);
		exchange.getIn().setHeader("messagebody", order);

	}

	

}
