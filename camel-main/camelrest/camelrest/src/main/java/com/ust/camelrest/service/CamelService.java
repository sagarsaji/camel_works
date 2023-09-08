package com.ust.camelrest.service;


import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.springframework.stereotype.Component;

import com.ust.camelrest.entity.Employee;

import jakarta.annotation.PostConstruct;


@Component
public class CamelService {
	
	List<Employee> lst = new ArrayList<>();
	
	public Employee addemp(Employee employee) {
	    lst.add(employee);
	    return employee;
	}


	
	public Employee getemp(@Header("age") int age) {
		Employee em = null;
		for(Employee emp:lst) {
			if(emp.getAge()==age) {
				em = emp;
			}
		}
		return em;
	}
	
	public List<Employee> getallemp(){
		return lst;
	}
	
	@PostConstruct
	public void emps() {
		lst.add(new Employee("Sagar", 23, "developer"));
		lst.add(new Employee("Abcd", 12, "developer 1"));
		lst.add(new Employee("Defg", 21, "developer 2"));
	}

}
