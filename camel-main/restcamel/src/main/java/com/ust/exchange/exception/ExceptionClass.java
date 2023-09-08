package com.ust.exchange.exception;

import org.springframework.stereotype.Component;

@Component
public class ExceptionClass extends Exception{

	public ExceptionClass() {
		super("Exception occured");
	}
	
	
}
