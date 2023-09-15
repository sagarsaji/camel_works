package com.ust.product.entity;

import java.util.List;

import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class Order {

	@JsonProperty("_id")
	private int _id;
	@JsonProperty("productid")
	private List<Integer> productid;
	@JsonProperty("quantity")
	private int quantity;
	@JsonProperty("products")
	private List<JsonNode> products;
	@JsonProperty("dateoforder")
	private String dateoforder;

	public Order(int _id, List<Integer> productid, int quantity, List<JsonNode> products, String dateoforder) {
		super();
		this._id = _id;
		this.productid = productid;
		this.quantity = quantity;
		this.products = products;
		this.dateoforder = dateoforder;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public List<Integer> getProductid() {
		return productid;
	}

	public void setProductid(List<Integer> productid) {
		this.productid = productid;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public List<JsonNode> getProducts() {
		return products;
	}

	public void setProducts(List<JsonNode> products) {
		this.products = products;
	}

	public String getDateoforder() {
		return dateoforder;
	}

	public void setDateoforder(String dateoforder) {
		this.dateoforder = dateoforder;
	}

	public Order() {
		super();
	}

}
