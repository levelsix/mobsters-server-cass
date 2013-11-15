package com.lvl6.aoc2.eventhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

@Component
public class AmqpErrorHandler implements ErrorHandler {
	
	private static final Logger log = LoggerFactory.getLogger(AmqpErrorHandler.class);
	
	@Override
	public void handleError(Throwable e) {
		log.error("Error processing amqp message", e);
	}

}
