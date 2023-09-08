package com.ust.scheduledjob.route;

import org.apache.camel.builder.RouteBuilder;

import org.springframework.stereotype.Component;

@Component
public class ScheduledRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		from("cron:mytask?schedule=0/20+*+*+*+*+?").log("hello world");

	}

}
