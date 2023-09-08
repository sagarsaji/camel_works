package com.ust.camelrestmongodb.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ust.camelrestmongodb.entity.Employee;
import com.ust.camelrestmongodb.exception.ResourceNotFoundException;
import com.ust.camelrestmongodb.route.service.EmployeeService;

@Component
public class CamelRoute extends RouteBuilder {
	
	@Autowired
	private EmployeeService employeeService;

	@Override
	public void configure() throws Exception {
		
		onException(ResourceNotFoundException.class)
        .handled(true)
        .setBody(constant("Resource not found camel"))
        .setHeader("CamelHttpResponseCode", constant(500))
        .log("${exception.message}")
        .end();
		
		rest()
			.post("/addemp").type(Employee.class).to("direct:addemp")
			.get("/getallemp").to("direct:getallemp")
			.get("/getempbyage/{age}").to("direct:getempbyage")
			.put("/updateemp/{name}").to("direct:updateemp");
		
		from("direct:addemp").unmarshal().json(JsonLibrary.Jackson,Employee.class)
		.bean(employeeService,"addemp").marshal().json(JsonLibrary.Jackson);
		
		
		from("direct:getallemp")
		.bean("employeeService","getallemp").marshal().json();
		
		from("direct:getempbyage")
		.bean("employeeService","getempbyage(${header.age})").marshal().json();
		
		from("direct:updateemp").unmarshal().json(JsonLibrary.Jackson,Employee.class)
		.bean("employeeService","updateemp(${header.name})").marshal().json();
		
	}

}
