package com.ust.exchange.route;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import com.ust.exchange.exception.ExceptionClass;

@Component
public class CamelRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		onException(ExceptionClass.class).handled(true).process(new Processor() {

			@Override
			public void process(Exchange exchange) throws Exception {

				Exception exe = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
				exchange.getIn().setBody(exe.getMessage());

			}
		});

		rest().get("/showdata").to("http://localhost:8080/show?bridgeEndPoint=true");

		rest().post("/adddata").to("http://localhost:8080/add?bridgeEndPoint=true");

//		rest().get("/showdata/{age}").to("direct:rest");
//		
//	    from("direct:rest").setHeader("age", simple("${header.age}"))
//	    .toD("http://localhost:8080/show/${header.age}?bridgeEndpoint=true");

		rest().get("/showdata/{age}").to("direct:rest");

		from("direct:rest").toD("http://localhost:8080/show/${header.age}?bridgeEndpoint=true");

	}

}
