package com.lvl6.mobsters.controller;


import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.eventprotos.EventUserProto.SetFacebookIdRequestProto;
import com.lvl6.mobsters.eventprotos.EventUserProto.SetFacebookIdResponseProto;
import com.lvl6.mobsters.eventprotos.EventUserProto.SetFacebookIdResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventUserProto.SetFacebookIdResponseProto.SetFacebookIdStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.SetFacebookIdRequestEvent;
import com.lvl6.mobsters.events.response.SetFacebookIdResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.services.user.UserService;


@Component
public class SetFacebookIdController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected UserService userService; 

	@Override
	public RequestEvent createRequestEvent() {
		return new SetFacebookIdRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_SET_FACEBOOK_ID_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		SetFacebookIdRequestProto reqProto = 
				((SetFacebookIdRequestEvent) event).getSetFacebookIdRequestProto();

		//get the values client sent
		MinimumUserProto sender = reqProto.getSender();
		String fbId = reqProto.getFbId();
	    if (fbId != null && fbId.isEmpty()) {
	    	fbId = null;
	    }
		
		
		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = sender.getUserUuid();
		UUID userId = UUID.fromString(userIdString);

		//response to send back to client
		Builder responseBuilder = SetFacebookIdResponseProto.newBuilder();
		responseBuilder.setStatus(SetFacebookIdStatus.FAIL_OTHER);
		SetFacebookIdResponseEvent resEvent =
				new SetFacebookIdResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User user = getUserService().getUserWithId(userId);

			if (fbId != null && user != null) { 
				responseBuilder.setStatus(SetFacebookIdStatus.SUCCESS);
			} else {
				responseBuilder.setStatus(SetFacebookIdStatus.FAIL_OTHER);
			}

			//write to client
			resEvent.setSetFacebookIdResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);
			
			boolean isDifferent = checkIfNewTokenDifferent(user.getFacebookId(), fbId);
			
			//update the facebook id
			if (isDifferent) {
				getUserService().updateFacebookId(user, fbId);
			}

		} catch (Exception e) {
			log.error("exception in SetFacebookIdController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(SetFacebookIdStatus.FAIL_OTHER);
				resEvent.setSetFacebookIdResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception2 in SetFacebookIdController processRequestEvent", e2);
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

