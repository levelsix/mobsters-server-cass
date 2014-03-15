package com.lvl6.mobsters.controller;


import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.controller.utils.ClanStuffUtil;
import com.lvl6.mobsters.controller.utils.CreateEventProtoUtil;
import com.lvl6.mobsters.controller.utils.CreateNoneventProtoUtil;
import com.lvl6.mobsters.eventprotos.EventClanProto.RequestJoinClanRequestProto;
import com.lvl6.mobsters.eventprotos.EventClanProto.RequestJoinClanResponseProto;
import com.lvl6.mobsters.eventprotos.EventClanProto.RequestJoinClanResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventClanProto.RequestJoinClanResponseProto.RequestJoinClanStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.RequestJoinClanRequestEvent;
import com.lvl6.mobsters.events.response.RequestJoinClanResponseEvent;
import com.lvl6.mobsters.noneventprotos.ClanProto.UserClanStatus;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.po.nonstaticdata.Clan;
import com.lvl6.mobsters.po.nonstaticdata.ClanForUser;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.properties.MobstersTableConstants;
import com.lvl6.mobsters.services.clan.ClanService;
import com.lvl6.mobsters.services.clanforuser.ClanForUserService;
import com.lvl6.mobsters.services.user.UserService;


@Component
public class RequestJoinClanController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected UserService userService; 
	
	@Autowired
	protected ClanService clanService;
	
	@Autowired
	protected ClanForUserService clanForUserService;
	
	@Autowired
	protected ClanStuffUtil clanStuffUtil;

	@Autowired
	protected CreateNoneventProtoUtil createNoneventProtoUtil;
	
	@Autowired
	protected CreateEventProtoUtil createEventProtoUtil;
	
	public RequestJoinClanController() {
		numAllocatedThreads = 4;
	}
	
	@Override
	public RequestEvent createRequestEvent() {
		return new RequestJoinClanRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_REQUEST_JOIN_CLAN_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		RequestJoinClanRequestProto reqProto = 
				((RequestJoinClanRequestEvent) event).getRequestJoinClanRequestProto();

		//get the values client sent
		MinimumUserProto senderProto = reqProto.getSender();
	    String clanIdStr = senderProto.getClan().getClanUuid();
	    Date timeOfEntry = new Date();
		
		//uuid's are not strings, need to convert from string to uuid, vice versa
	    String userIdString = senderProto.getUserUuid();
	    UUID userId = UUID.fromString(userIdString);
	    
		UUID clanId = null;
		if (null != clanIdStr && !clanIdStr.isEmpty()) {
			 clanId = UUID.fromString(clanIdStr);
		}

		//response to send back to client
	    RequestJoinClanResponseProto.Builder responseBuilder =
	    		RequestJoinClanResponseProto.newBuilder();
	    responseBuilder.setStatus(RequestJoinClanStatus.FAIL_OTHER);
	    responseBuilder.setSender(senderProto);
		RequestJoinClanResponseEvent resEvent =
				new RequestJoinClanResponseEvent(userIdString);
		resEvent.setTag(event.getTag());
		
		//TODO: LOCK THE CLAN?
		try {
			//get whatever we need from the database
			User user = getUserService().getUserWithId(userId);
			Clan clan = getClanService().getClanWithId(clanId);

			boolean legit = checkLegitRequest(responseBuilder, user, userId, clan, clanId);
			
			boolean requestToJoinRequired = clan.isRequestToJoinRequired();
			boolean successful = false;
			if (legit) {
//				if (requestToJoinRequired) {
				//clan raid contribution stuff
//					MinimumUserProtoForClans mupfc = getCreateNoneventProtoUtil().createminimumuser
//				MinimumUserProtoForClans mupfc = CreateInfoProtoUtils.createMinimumUserProtoForClans(
//			              user, UserClanStatus.REQUESTING, 0F);
//			          resBuilder.setRequester(mupfc);
//				} else {
////				//clan raid contribution stuff
//				MinimumUserProtoForClans mupfc = CreateInfoProtoUtils.createMinimumUserProtoForClans(
//			              user, UserClanStatus.MEMBER, 0F);
//			          resBuilder.setRequester(mupfc);
//				}
				successful = writeChangesToDb(responseBuilder, user, userId, clan, clanId,
						requestToJoinRequired, timeOfEntry);
			}
			
			//write to promoter
			resEvent.setRequestJoinClanResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);
			
			if (successful) {
			}

		} catch (Exception e) {
			log.error("exception in RequestJoinClanController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(RequestJoinClanStatus.FAIL_OTHER);
				resEvent.setRequestJoinClanResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception2 in RequestJoinClanController processRequestEvent", e2);
			}
		}
	}
	
	//allClanPpl includes all members and non members
	private boolean checkLegitRequest(Builder resBuilder, User user, UUID userId,
			Clan clan, UUID clanId) {

		if (user == null || clan == null) {
			log.error("user is null");
			return false;      
		}
		UUID userClanId = user.getClanId();
		if (null != userClanId) {
			resBuilder.setStatus(RequestJoinClanStatus.FAIL_ALREADY_IN_CLAN);
			log.error("user's clan id is " + userClanId + ", clan id is " + clanId);
			return false;
		}
		
		ClanForUser userClanForUser = getClanForUserService()
				.getUserClanForUserAndClanId(userId, clanId); 
		String requesting = UserClanStatus.REQUESTING.name();
		if (null != userClanForUser && userClanForUser.getStatus().equals(requesting)) {
			resBuilder.setStatus(RequestJoinClanStatus.FAIL_REQUEST_ALREADY_FILED);
			log.error("user clan request already exists: " + userClanForUser);
			return false;      
			
		} else if (null != userClanForUser) {
			resBuilder.setStatus(RequestJoinClanStatus.FAIL_ALREADY_IN_CLAN);
			log.error("user clan already exists: " + userClanForUser);
			return false;
		}
		
		//TODO: FIGURE OUT BETTER WAY TO GET SIZE OF CLAN
		Set<String> statuses = new HashSet<String>();
		statuses.add(UserClanStatus.LEADER.name());
		statuses.add(UserClanStatus.JUNIOR_LEADER.name());
		statuses.add(UserClanStatus.CAPTAIN.name());
		statuses.add(UserClanStatus.MEMBER.name());
		List<ClanForUser> pplInClan = getClanForUserService()
				.getUserClansForStatuses(clanId, statuses);
		int maxSize = MobstersTableConstants.CLAN__MAX_NUM_MEMBERS; 
		
		if (pplInClan.size() >= maxSize) {
			resBuilder.setStatus(RequestJoinClanStatus.FAIL_CLAN_IS_FULL);
		}
		
		return true;
	}

	
	private boolean writeChangesToDb(Builder resBuilder, User user, UUID userId,
			Clan clan, UUID clanId, boolean requestToJoinRequired, Date timeOfEntry) {
		try {
			//clan can be open, or user needs to send a request to join the clan
			UserClanStatus userClanStatus;
		    if (requestToJoinRequired) {
		      userClanStatus = UserClanStatus.REQUESTING;
		      resBuilder.setStatus(RequestJoinClanStatus.SUCCESS_REQUEST);
		    } else {
		      userClanStatus = UserClanStatus.MEMBER;
		      resBuilder.setStatus(RequestJoinClanStatus.SUCCESS_JOIN);
		    }
		    
		    if (!requestToJoinRequired) {
		    	//delete all clan requests this user made since is going to join clan
		    	
		    }
		    
		    String status = userClanStatus.name();
		    //make clan for user entry for this user
		    getClanForUserService().insertClanForUser(userId, clanId, status, timeOfEntry);
		    
		    
		    
			
			return true;

		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public ClanService getClanService() {
		return clanService;
	}

	public void setClanService(ClanService clanService) {
		this.clanService = clanService;
	}

	public ClanForUserService getClanForUserService() {
		return clanForUserService;
	}

	public void setClanForUserService(ClanForUserService clanForUserService) {
		this.clanForUserService = clanForUserService;
	}

	public ClanStuffUtil getClanStuffUtil() {
		return clanStuffUtil;
	}

	public void setClanStuffUtil(ClanStuffUtil clanStuffUtil) {
		this.clanStuffUtil = clanStuffUtil;
	}

	public CreateNoneventProtoUtil getCreateNoneventProtoUtil() {
		return createNoneventProtoUtil;
	}

	public void setCreateNoneventProtoUtil(
			CreateNoneventProtoUtil createNoneventProtoUtil) {
		this.createNoneventProtoUtil = createNoneventProtoUtil;
	}

	public CreateEventProtoUtil getCreateEventProtoUtil() {
		return createEventProtoUtil;
	}

	public void setCreateEventProtoUtil(CreateEventProtoUtil createEventProtoUtil) {
		this.createEventProtoUtil = createEventProtoUtil;
	}
	
}
