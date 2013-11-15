package com.lvl6.aoc2.eventhandlers;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.integration.Message;
import org.springframework.integration.MessagingException;
import org.springframework.integration.core.MessageHandler;

import com.lvl6.aoc2.controller.EventController;
import com.lvl6.aoc2.controller.utils.EventControllersUtil;
import com.lvl6.aoc2.dto.ApplicationMode;
import com.lvl6.aoc2.events.RequestEvent;
import com.lvl6.aoc2.utils.Attachment;

public abstract class AbstractGameEventHandler implements MessageHandler, ApplicationContextAware {

	protected static Logger log = LoggerFactory.getLogger(AbstractGameEventHandler.class);

	
	
	@Autowired
	EventControllersUtil eventControllersUtil;

	public EventControllersUtil getEventControllersUtil() {
		return eventControllersUtil;
	}

	public void setEventControllersUtil(EventControllersUtil eventControllersUtil) {
		this.eventControllersUtil = eventControllersUtil;
	}
	
	
	@Autowired
	ApplicationMode applicationMode;

	public ApplicationMode getApplicationMode() {
		return applicationMode;
	}

	public void setApplicationMode(ApplicationMode applicationMode) {
		this.applicationMode = applicationMode;
	}


	ApplicationContext context;


	@Override
	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		context = arg0;
	}

	@Override
	public void handleMessage(Message<?> msg) throws MessagingException {
		log.debug("Received message: ");
		for (String key : msg.getHeaders().keySet()) {
			log.debug(key + ": " + msg.getHeaders().get(key));
		}
		// log.info("Payload: "+msg.getPayload());
		Attachment attachment = context.getBean(Attachment.class);
		byte[] payload = (byte[]) msg.getPayload();
		attachment.readBuff = ByteBuffer.wrap(payload);
		while (attachment.eventReady()) {
			RequestEvent event = getEvent(attachment);
			log.debug("Recieved event from client: " + event.getPlayerId());
			delegateEvent(payload, event, (String) msg.getHeaders().get("ip_connection_id"),
					attachment.eventType);
			attachment.reset();

		}
	}

	/**
	 * read an event from the attachment's payload
	 */
	protected RequestEvent getEvent(Attachment attachment) {
		RequestEvent event = null;
		ByteBuffer bb = ByteBuffer.wrap(Arrays.copyOf(attachment.payload, attachment.payloadSize));

		// get the controller and tell it to instantiate an event for us
		EventController ec = getEventControllersUtil().getEventControllerByEventType(attachment.eventType);

		if (ec == null) {
			return null;
		}
		event = ec.createRequestEvent();
		event.setTag(attachment.tag);

		// read the event from the payload
		event.read(bb);
		return event;
	}

	protected abstract void delegateEvent(byte[] bytes, RequestEvent event, String ip_connection_id, Integer eventType);

}