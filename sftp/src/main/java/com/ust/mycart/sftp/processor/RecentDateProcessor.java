package com.ust.mycart.sftp.processor;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class RecentDateProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		String lastdate = exchange.getProperty("recentDate", String.class);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date lastupdatedate = dateFormat.parse(lastdate);
		exchange.setProperty("recentDateNew", lastupdatedate);

	}

}
