package com.ust.exchange;



import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TexttocsvApplication extends RouteBuilder{

	public static void main(String[] args) {
		SpringApplication.run(TexttocsvApplication.class, args);
	}

	@Override
	public void configure() throws Exception {
		
		from("file:start?noop=true").process(new Processor() {
			
			@Override
			public void process(Exchange exchange) throws Exception {
				// TODO Auto-generated method stub
				StringBuilder sb = new StringBuilder();
				String msg = exchange.getIn().getBody(String.class);
				String[] msg1 = msg.split(",");
				for(String ss:msg1) {
					System.out.println(ss);
				}
				for(String s:msg1) {
					sb.append(s+",");
				}
				exchange.getIn().setBody(sb);
			}
		})
		.to("file:destination?fileName=data.csv");
		
		
	}

}
