package com.ust.camelrestapimongo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Product {

	@JsonProperty("_id")
	private int _id;
	@JsonProperty("pname")
	private String pname;
	@JsonProperty("description")
	private String description;
	@JsonProperty("colour")
	private String colour;
	@JsonProperty("quantity")
	private int quantity;

	public Product(int _id, String pname, String description, String colour, int quantity) {
		super();
		this._id = _id;
		this.pname = pname;
		this.description = description;
		this.colour = colour;
		this.quantity = quantity;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getPname() {
		return pname;
	}

	public void setPname(String pname) {
		this.pname = pname;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getColour() {
		return colour;
	}

	public void setColour(String colour) {
		this.colour = colour;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Product() {
		super();
	}

	   @Override
	    public String toString() {
	        return "Product{" +
	                "_id=" + _id +
	                ", pname='" + pname + '\'' +
	                ", description='" + description + '\'' +
	                ", colour='" + colour + '\'' +
	                ", quantity=" + quantity +
	                '}';
	    }
	
	

}
