package com.lvl6.mobsters.controller;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.staticdata.utils.StructureRetrieveUtils;
import com.lvl6.mobsters.eventprotos.EventUserProto.UserCreateRequestProto;
import com.lvl6.mobsters.eventprotos.EventUserProto.UserCreateResponseProto;
import com.lvl6.mobsters.eventprotos.EventUserProto.UserCreateResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventUserProto.UserCreateResponseProto.UserCreateStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.UserCreateRequestEvent;
import com.lvl6.mobsters.events.response.UserCreateResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.properties.MobstersTableConstants;
import com.lvl6.mobsters.services.structureforuser.StructureForUserService;
import com.lvl6.mobsters.services.user.UserService;
import com.lvl6.mobsters.utils.CoordinatePair;


@Component
public class UserCreateController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected StructureRetrieveUtils structureRetrieveUtils; 
	
	@Autowired
	protected StructureForUserService structureForUserService; 

	@Autowired
	protected UserService userService;

	
	
	public UserCreateController() {
		numAllocatedThreads = 3;
	}
	
	@Override
	public RequestEvent createRequestEvent() {
		return new UserCreateRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_USER_CREATE_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		UserCreateRequestProto reqProto = 
				((UserCreateRequestEvent) event).getUserCreateRequestProto();

		//get the values client sent
		String udid = reqProto.getUdid();
	    String name = reqProto.getName();
	    String referrerCode = (reqProto.hasReferrerCode()) ? reqProto.getReferrerCode() : null;
	    String deviceToken = (reqProto.hasDeviceToken() && reqProto.getDeviceToken().length() > 0) ? reqProto.getDeviceToken() : null;
	    Date createTime = new Date();
	    Date timeOfStructPurchase = createTime; //new Date(reqProto.getTimeOfStructPurchase());
	    Date timeOfStructBuild = createTime; //new Date(reqProto.getTimeOfStructBuild());
	    CoordinatePair structCoords = new CoordinatePair(reqProto.getStructCoords().getX(), reqProto.getStructCoords().getY());
	    boolean usedDiamondsToBuild = reqProto.getUsedDiamondsToBuilt();
	    String facebookId = reqProto.getFacebookId();


		//response to send back to client
		Builder responseBuilder = UserCreateResponseProto.newBuilder();
		responseBuilder.setStatus(UserCreateStatus.FAIL_OTHER);
		UserCreateResponseEvent resEvent =
				new UserCreateResponseEvent(udid);
		resEvent.setTag(event.getTag());
		try {
			//get whatever we need from the database
			//TODO: implement the referral stuff 
			User referrer = null;
			
			//validate request
			boolean legitUserCreate = checkLegitUserCreate(responseBuilder, udid, facebookId,
					name, timeOfStructPurchase, timeOfStructBuild, structCoords, 
					referrer, reqProto.hasReferrerCode());
			//TODO: FINISH LATER

			User user = null;
		    int playerCash = 0;
		    int playerOil = 0;
		    int playerGems = 0;
		    
			boolean successful = false;
			if (legitUserCreate) {
//				successful = writeChangesToDb(inDb, us, s, usingGems, clientDate);
			}

			if (successful) {
				responseBuilder.setStatus(UserCreateStatus.SUCCESS);
			}

			//write to client
			resEvent.setUserCreateResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in UserCreateController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(UserCreateStatus.FAIL_OTHER);
				resEvent.setUserCreateResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in UserCreateController processRequestEvent", e2);
			}
		}
	}
	

	private boolean checkLegitUserCreate(Builder resBuilder, String udid, String facebookId,
			String name, Date timeOfStructPurchase, Date timeOfStructBuild,
			CoordinatePair coordinatePair, User referrer, boolean hasReferrerCode) {

		if (udid == null || name == null || timeOfStructPurchase == null || coordinatePair == null || timeOfStructBuild == null) {
			resBuilder.setStatus(UserCreateStatus.FAIL_OTHER);
			log.error("parameter passed in is null. udid=" + udid + ", name=" + name + ", timeOfStructPurchase=" + timeOfStructPurchase
					+ ", coordinatePair=" + coordinatePair + ", timeOfStructBuild=" + timeOfStructBuild);
			return false;
		}
		if (hasReferrerCode && referrer == null) {
			resBuilder.setStatus(UserCreateStatus.FAIL_INVALID_REFER_CODE);
			log.info("refer code passed in is invalid.");
			return false;
		}
		List<User> users = getUserService().getUserByUDIDorFbId(udid, facebookId);
		User udidUser = null;
		User fbUser = null;

		for(User u : users) {
			String userUdid = u.getUdid();
			String userFacebookId = u.getFacebookId();

			if (null != userUdid && userUdid.equals(udid)) {
				udidUser = u;
			} else if (null != userFacebookId && userFacebookId.equals(facebookId)) {
				fbUser = u;
			}
		}

		if (null != udidUser) {
			resBuilder.setStatus(UserCreateStatus.FAIL_USER_WITH_UDID_ALREADY_EXISTS);
			log.error("user with udid " + udid + " already exists");
			return false;
		}
		if (null != fbUser) {
			resBuilder.setStatus(UserCreateStatus.FAIL_USER_WITH_FACEBOOK_ID_EXISTS);
			log.error("user with facebookId " + facebookId + " already exists");
			return false;
		}
		if (name.length() < MobstersTableConstants.USER_CREATE__MIN_NAME_LENGTH || 
				name.length() > MobstersTableConstants.USER_CREATE__MAX_NAME_LENGTH) {
			resBuilder.setStatus(UserCreateStatus.FAIL_INVALID_NAME);
			log.error("name length is off. length is " + name.length() + ", should be in between " +
					MobstersTableConstants.USER_CREATE__MIN_NAME_LENGTH + " and " +
					MobstersTableConstants.USER_CREATE__MAX_NAME_LENGTH);
			return false;
		}

		resBuilder.setStatus(UserCreateStatus.SUCCESS);
		return true;
	}

	public StructureRetrieveUtils getStructureRetrieveUtils() {
		return structureRetrieveUtils;
	}

	public void setStructureRetrieveUtils(
			StructureRetrieveUtils structureRetrieveUtils) {
		this.structureRetrieveUtils = structureRetrieveUtils;
	}

	public StructureForUserService getStructureForUserService() {
		return structureForUserService;
	}

	public void setStructureForUserService(
			StructureForUserService structureForUserService) {
		this.structureForUserService = structureForUserService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
}
