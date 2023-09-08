package com.ust.exchange.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Header;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ust.exchange.entity.Employee;
import com.ust.exchange.exception.ExceptionClass;

import jakarta.annotation.PostConstruct;

@RestController
public class CamelController{
	
	List<Employee> lst = new ArrayList<>();
	
	@PostMapping("/add")
	public Employee addEmp(@RequestBody Employee employee) {
		lst.add(employee);
		return employee;
	}
	
	@GetMapping("/show")
	public List<Employee> show(){
		return lst;
	}
	
	@GetMapping("/show/{age}")
	public Employee showbyid(@PathVariable @Header("age") int age) throws ExceptionClass{
		
		if(age>50) {
			throw new ExceptionClass();
		}
		
		Employee em = null;
		for(Employee emp:lst) {
			if(emp.getAge()==age) {
				em = emp;
			}
		}
		return em;
	}
	
	@PostConstruct
	public void data() {
		lst.add(new Employee("Sagar", 23, "developer"));
		lst.add(new Employee("Abcd", 12, "developer 1"));
		lst.add(new Employee("Defg", 21, "developer 2"));
	}
	
	

}
