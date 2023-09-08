package com.example.Springcamel.routebuilder;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CamelRoute extends RouteBuilder {

	@Autowired
	private ProducerTemplate template;

	@Override
	public void configure() throws Exception {

		// filtering and sending files from one folder to another

//		from("file:C:/Users/245195/Desktop/My Files/File1?noop=true")
//		.filter(body().contains("hellow"))
//		.to("file:C:/Users/245195/Desktop/My Files/File2");
//		
		// transforming message
//		
		from("direct:start").log("Original: ${body}").transform().body(String.class, body -> "Transformed: " + body)
				.log("${body}");

		// example for content based routing

		from("file:C:/Users/245195/Desktop/My Files/File1?noop=true").choice()
		.when(body().contains("hello"))
				.to("direct:hellofile")
				.when(body().contains("hello world")).to("direct:helloworldfile")
				.when(body().contains("hellow")).to("direct:hellowfile")
				.otherwise().log("no file")
				.end();

		from("direct:hellofile").log("from hellofile: ${body}");
		from("direct:helloworldfile").log("from helloworldfile: ${body}");
		from("direct:hellowfile").log("from hellowfile: ${body}");

		// example for multicasting

		from("direct:multicast").multicast().to("direct:multi1", "direct:multi2");

		from("direct:multi1").routeId("multi1").log("${body}" + "${routeId}");
		from("direct:multi2").routeId("multi2").log("${body}" + "${routeId}");

		// example for recipient list
		from("direct:recipe").recipientList(simple("direct:${header.recipient}"));

		from("direct:recipient1").log("${body}");
		from("direct:recipient2").log("${body}");

		// example for wiretap
		from("direct:wiretap").wireTap("log:wire").to("file:C:/Users/245195/Desktop/My Files/outputFile");

		// example for filter
		from("direct:filter").split(body().tokenize(","))
		.filter().method(Evennumber.class, "even").log("${body}");
	}

	public void function() {
		template.sendBody("direct:start", "Message from route...");

		template.sendBody("direct:multicast", "Message has been multicasted...");

		String[] recipients = { "recipient1", "recipient2" };
		for (String recipient : recipients) {
			template.sendBodyAndHeader("direct:recipe", "Message via recipient list to....", "recipient", recipient);
		}

		template.sendBody("direct:wiretap", "Wiretapping the message...");

		template.sendBody("direct:filter", "1,2,3,4,5,6,7,8,9,10");
	}

}

class Evennumber {
	public static boolean even(int num) {
		return num % 2 == 0;
	}
}
