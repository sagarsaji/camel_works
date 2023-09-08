package com.ust.camelrestmongodb.route.service;

import java.util.List;
import java.util.Optional;

import org.apache.camel.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.ust.camelrestmongodb.entity.Employee;
import com.ust.camelrestmongodb.exception.ResourceNotFoundException;
import com.ust.camelrestmongodb.repository.EmployeeRepo;

@Component
public class EmployeeService {
	
	@Autowired
	private EmployeeRepo repo;
	
	public Employee addemp(Employee employee) {
		repo.save(employee);
		return employee;
	}
	
	public List<Employee> getallemp(){
		List<Employee> lst =  repo.findAll();
		return lst;
	}
	
	public Employee getempbyage(@Header("age") int age) throws ResourceNotFoundException {
		Employee emp = repo.findByAge(age);
		if(emp==null)
			throw new ResourceNotFoundException("Resource not found");
		return emp;
	}
	
	public Employee updateemp(@Header("name") String name, Employee employee) throws ResourceNotFoundException{
		Employee em = null;
		Optional<Employee> emp = repo.findByName(name);
		if(emp.isPresent()) {
			em = emp.get();
			int id = em.getId();
			em.setId(id);
			em.setName(employee.getName());
			em.setAge(employee.getAge());
			em.setDesignation(employee.getDesignation());
			repo.save(em);
		}		
		else {
			throw new ResourceNotFoundException("not found");
		}
		return em;
	}
}
