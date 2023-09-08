package com.ust.camelrestmongodb.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ust.camelrestmongodb.entity.Employee;

public interface EmployeeRepo extends MongoRepository<Employee,Integer> {

	Employee findByAge(int age);

	Optional<Employee> findByName(String name);



}
