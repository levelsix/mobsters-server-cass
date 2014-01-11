package com.lvl6.mobsters.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.eventprotos.EventApnsProto.EnableAPNSRequestProto;
import com.lvl6.mobsters.eventprotos.EventApnsProto.EnableAPNSResponseProto;
import com.lvl6.mobsters.eventprotos.EventApnsProto.EnableAPNSResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventApnsProto.EnableAPNSResponseProto.EnableAPNSStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.EnableAPNSRequestEvent;
import com.lvl6.mobsters.events.response.EnableAPNSResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.services.user.UserService;


@Component
public class EnableAPNSController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	@Autowired
	protected UserService userService;
	
	
	public EnableAPNSController() {
	    numAllocatedThreads = 1;
	}
	
	@Override
	public RequestEvent createRequestEvent() {
		return new EnableAPNSRequestEvent();
	}
	
	

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_ENABLE_APNS_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		EnableAPNSRequestProto reqProto = 
				((EnableAPNSRequestEvent) event).getEnableAPNSRequestProto();

		//get the values client sent
		MinimumUserProto senderProto = reqProto.getSender();
		String deviceToken = reqProto.getDeviceToken();
	    if (deviceToken != null && deviceToken.length() == 0) deviceToken = null;
	    
		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = senderProto.getUserUuid();
		UUID userId = UUID.fromString(userIdString);

		//response to send back to client
		Builder responseBuilder = EnableAPNSResponseProto.newBuilder();
		responseBuilder.setSender(senderProto);
		responseBuilder.setStatus(EnableAPNSStatus.NOT_ENABLED);
		EnableAPNSResponseEvent resEvent = new EnableAPNSResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User user = getUserService().getUserWithId(userId);

			if (deviceToken != null && user != null) { 
				responseBuilder.setStatus(EnableAPNSStatus.SUCCESS);
			} else {
				responseBuilder.setStatus(EnableAPNSStatus.NOT_ENABLED);
			}
			
			//write to client
			resEvent.setEnableAPNSResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);
			
			boolean isDifferent = checkIfNewTokenDifferent(user.getDeviceToken(), deviceToken);

			if (isDifferent) {
				getUserService().updateDeviceToken(user, deviceToken);
			}

		} catch (Exception e) {
			log.error("exception in EnableAPNSController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(EnableAPNSStatus.NOT_ENABLED);
				resEvent.setEnableAPNSResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception2 in EnableAPNSController processRequestEvent", e2);
			}
		}
	}

	private boolean checkIfNewTokenDifferent(String oldToken, String newToken) {
		boolean oldTokenIsNothing = oldToken == null || oldToken.length() == 0;
		boolean newTokenIsNothing = newToken == null || newToken.length() == 0;

		if (oldTokenIsNothing && newTokenIsNothing) {
			return false;
		}

		if (!oldTokenIsNothing && !newTokenIsNothing) {
			return !oldToken.equals(newToken);
		}

		return true;
	}
	

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

}
