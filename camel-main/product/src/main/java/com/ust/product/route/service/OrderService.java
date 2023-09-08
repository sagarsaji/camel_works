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

	public void addOrders(Exchange exchange) {

		Order neworders = exchange.getIn().getHeader("messagebody", Order.class);
		exchange.getIn().setBody(neworders);
		List<String> products = new ArrayList<>();
		for (int pid : neworders.getProductid()) {
			String product = consumer.receiveBody("http://localhost:9090/product/getproducts/product/"+pid, String.class);
			exchange.getIn().setBody(product);
			System.out.println(exchange.getIn().getBody());
			if(product.compareTo("The given product id is not found") == 0) {
				Order order = null;
				exchange.getIn().setHeader("CamelHttpResponseCode",404);
				exchange.getIn().setBody(order);
			}
			else {
				System.out.println("id " + pid + " found");
				exchange.getIn().setHeader("CamelHttpResponseCode",201);
				products.add(product);
				neworders.setProducts(products);
				Date currentdate = new Date();
		
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String date = sdf.format(currentdate);
				neworders.setDateoforder(date);
				exchange.getIn().setBody(neworders);
			}

		}


	}

}
