package com.ust.springactuator.route;

	
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class CamelRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {

//		rest("/test").get("/hello").to("direct:hello");
//		
//		from("direct:hello").setBody(simple("Hello World"));

		rest("/camel").post().to("http://localhost:8083/test?bridgeEndpoint=true")
		.to("direct:start");

		from("direct:start").setHeader("2", constant("customheader"))
				.to("http://localhost:8083/test?bridgeEndpoint=true");

	}

}
