package com.ust.header.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HeaderController {
	
	@PostMapping("/test")
	public void headers(@RequestHeader Map<String,String> headers){
		System.out.println(headers);
	}

}
