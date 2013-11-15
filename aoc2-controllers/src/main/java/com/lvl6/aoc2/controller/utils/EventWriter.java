package com.lvl6.aoc2.controller.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.aoc2.events.GameEvent;
import com.lvl6.aoc2.events.ResponseEvent;

public abstract class EventWriter  {

	
	private static Logger log = LoggerFactory.getLogger(EventWriter.class);
	public EventWriter() {
		super();
	}

	public void handleEvent(GameEvent event) {
		try {
			processEvent(event);
		} catch (Exception e) {
			log.error("Error handling event: {}", event, e);
		}
	}

	protected abstract void processEvent(GameEvent event) throws Exception;

	
	public abstract void processGlobalChatResponseEvent(ResponseEvent event);

	public abstract void processPreDBResponseEvent(ResponseEvent event, String udid);

	
}