package com.ust.mycart.item.entity;

import java.util.List;

import org.bson.Document;

public class CategoryResponse {

	private String categoryName;
	private String categoryDept;
	private List<Document> items;

	public CategoryResponse(String categoryName, String categoryDept, List<Document> items) {
		super();
		this.categoryName = categoryName;
		this.categoryDept = categoryDept;
		this.items = items;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getCategoryDept() {
		return categoryDept;
	}

	public void setCategoryDept(String categoryDept) {
		this.categoryDept = categoryDept;
	}

	public List<Document> getItems() {
		return items;
	}

	public void setItems(List<Document> items) {
		this.items = items;
	}

	public CategoryResponse() {
		super();
	}

}
