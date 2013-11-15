package com.lvl6.aoc2.controller.utils;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.aoc2.controller.EventController;


@Component
public class EventControllersUtil implements InitializingBean {

	
	
	private static final Logger log = LoggerFactory.getLogger(EventControllersUtil.class);
	
	
	@Autowired
	protected List<EventController> eventControllerList;

	public void setEventControllerList(List<EventController> eventControllerList) {
		this.eventControllerList = eventControllerList;
	}

	protected HashMap<Integer, EventController> eventControllers = new HashMap<Integer, EventController>();

	@Override
	public void afterPropertiesSet() throws Exception {
		loadcommonEventControllers();
	}

	public EventController getEventControllerByEventType(Integer eventType) {
		if (eventType == null) {
			throw new RuntimeException("EventProtocolRequest (eventType) is null");
		}
		if (eventControllerList.size() > eventControllers.size()) {
			loadcommonEventControllers();
		}
		if (eventControllers.containsKey(eventType)) {
			EventController ec = eventControllers.get(eventType);
			if (ec == null) {
				log.error("no eventcontroller for eventType: " + eventType);
				throw new RuntimeException("EventController of type: " + eventType + " not found");
			}
			return ec;
		}
		throw new RuntimeException("EventController of type: " + eventType + " not found");
	}

	/**
	 * Dynamically loads GameControllers
	 */
	private void loadcommonEventControllers() {
		log.info("Adding event controllers to commonEventControllers controllerType-->controller map");
		for (EventController ec : eventControllerList) {
			eventControllers.put(ec.getEventType(), ec);
		}
	}

}
