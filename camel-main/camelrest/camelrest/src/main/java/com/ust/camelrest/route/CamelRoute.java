package com.ust.camelrest.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

import com.ust.camelrest.entity.Employee;
import com.ust.camelrest.service.CamelService;

@Component
public class CamelRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		rest().get("/getmsg").produces("text/plain").to("direct:rest");
		from("direct:rest").setBody(constant("Hello World"));

		rest().post("/postmsg").type(Employee.class).to("direct:addemp");
		from("direct:addemp").unmarshal().json(JsonLibrary.Jackson, Employee.class)
		.bean(CamelService.class, "addemp").marshal().json(JsonLibrary.Jackson);

		rest().get("/getmsg/{age}").to("direct:getbyid");
		from("direct:getbyid").bean(CamelService.class, "getemp(${header.age})").marshal().json();

		rest().get("/getallemp").to("direct:getallemp");
		from("direct:getallemp").bean(CamelService.class, "getallemp").marshal().json();

	}

}
