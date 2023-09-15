package com.ust.mycart.item.entity;

public class StockDetails {

	private int availableStock;
	private String unitOfMeasure;

	public StockDetails(int availableStock, String unitOfMeasure) {
		super();
		this.availableStock = availableStock;
		this.unitOfMeasure = unitOfMeasure;
	}

	public int getAvailableStock() {
		return availableStock;
	}

	public void setAvailableStock(int availableStock) {
		this.availableStock = availableStock;
	}

	public String getUnitOfMeasure() {
		return unitOfMeasure;
	}

	public void setUnitOfMeasure(String unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;
	}

	public StockDetails() {
		super();
	}

}
