package com.lvl6.mobsters.controller;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.controller.utils.ClanStuffUtil;
import com.lvl6.mobsters.controller.utils.CreateEventProtoUtil;
import com.lvl6.mobsters.eventprotos.EventClanProto.PromoteDemoteClanMemberRequestProto;
import com.lvl6.mobsters.eventprotos.EventClanProto.PromoteDemoteClanMemberResponseProto;
import com.lvl6.mobsters.eventprotos.EventClanProto.PromoteDemoteClanMemberResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventClanProto.PromoteDemoteClanMemberResponseProto.PromoteDemoteClanMemberStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.PromoteDemoteClanMemberRequestEvent;
import com.lvl6.mobsters.events.response.PromoteDemoteClanMemberResponseEvent;
import com.lvl6.mobsters.noneventprotos.ClanProto.UserClanStatus;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.po.nonstaticdata.ClanForUser;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.services.clanforuser.ClanForUserService;
import com.lvl6.mobsters.services.user.UserService;


@Component
public class PromoteDemoteClanMemberController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected UserService userService; 
	
	@Autowired
	protected ClanForUserService clanForUserService;
	
	@Autowired
	protected ClanStuffUtil clanStuffUtil;

	@Autowired
	protected CreateEventProtoUtil createEventProtoUtil;
	
	public PromoteDemoteClanMemberController() {
		numAllocatedThreads = 4;
	}
	
	@Override
	public RequestEvent createRequestEvent() {
		return new PromoteDemoteClanMemberRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_PROMOTE_DEMOTE_CLAN_MEMBER_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		PromoteDemoteClanMemberRequestProto reqProto = 
				((PromoteDemoteClanMemberRequestEvent) event).getPromoteDemoteClanMemberRequestProto();

		//get the values client sent
		MinimumUserProto senderProto = reqProto.getSender();
	    UserClanStatus newUserClanStatus = reqProto.getUserClanStatus();
	    String clanIdStr = senderProto.getClan().getClanUuid();
		
		//uuid's are not strings, need to convert from string to uuid, vice versa
	    UUID clanId = UUID.fromString(clanIdStr);
	    
		String userIdString = senderProto.getUserUuid();
		UUID userId = UUID.fromString(userIdString);
		String victimIdStr = reqProto.getVictimUuid();
		UUID victimId = UUID.fromString(victimIdStr);

		List<UUID> userIds = new ArrayList<UUID>();
		userIds.add(userId);
		userIds.add(victimId);
		
		//response to send back to client
	    PromoteDemoteClanMemberResponseProto.Builder responseBuilder =
	    		PromoteDemoteClanMemberResponseProto.newBuilder();
	    responseBuilder.setStatus(PromoteDemoteClanMemberStatus.FAIL_OTHER);
	    responseBuilder.setSender(senderProto);
	    responseBuilder.setVictimUuid(victimIdStr);
	    responseBuilder.setUserClanStatus(newUserClanStatus);
		
		PromoteDemoteClanMemberResponseEvent resEvent =
				new PromoteDemoteClanMemberResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			Map<UUID,User> users = getUserService().getUserIdsToUsersForIds(userIds);
			Map<UUID, ClanForUser> userIdsToUserClans = getClanForUserService()
					.getSpecificOrAllUserClansForClan(clanId, userIds);

			boolean legit = checkLegitRequest(responseBuilder, userId, victimId,
		      		newUserClanStatus, users, userIdsToUserClans);
			
			boolean successful = false;
			if (legit) {
				User victim = users.get(victimId);
				ClanForUser oldInfo = userIdsToUserClans.get(victimId);
				
				successful = writeChangesToDb(victim, victimId, clanId, oldInfo,
						newUserClanStatus);
			}
			
			if (successful) {
				responseBuilder.setStatus(PromoteDemoteClanMemberStatus.SUCCESS);
			}
			
			//write to promoter
			resEvent.setPromoteDemoteClanMemberResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);
			
			if (successful) {
				//write to victim
				PromoteDemoteClanMemberResponseEvent resEvent2 =
						new PromoteDemoteClanMemberResponseEvent(victimIdStr);
				resEvent2.setPromoteDemoteClanMemberResponseProto(responseBuilder.build());
				//set tag?
				getEventWriter().handleEvent(resEvent2);
			}

		} catch (Exception e) {
			log.error("exception in PromoteDemoteClanMemberController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(PromoteDemoteClanMemberStatus.FAIL_OTHER);
				resEvent.setPromoteDemoteClanMemberResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception2 in PromoteDemoteClanMemberController processRequestEvent", e2);
			}
		}
	}
	
	private boolean checkLegitRequest(Builder resBuilder, UUID userId, UUID victimId,
			UserClanStatus newUserClanStatus, Map<UUID, User> userIdsToUsers,
			Map<UUID, ClanForUser> userIdsToUserClans) {

	    if (null == userIdsToUsers || userIdsToUsers.size() != 2 ||
	    		null == userIdsToUserClans || userIdsToUserClans.size() != 2) {
	      log.error("user or userClan objects do not total 2. users=" + userIdsToUsers +
	      		"\t userIdsToUserClans=" + userIdsToUserClans);
	      return false;      
	    }
	    
	    //check if users are in the db
	    if (!userIdsToUserClans.containsKey(userId) || !userIdsToUsers.containsKey(userId)) {
	    	log.error("user promoting or demoting not in clan or db. userId=" + userId +
	    			"\t userIdsToUserClans=" + userIdsToUserClans + "\t userIdsToUsers=" +
	    			userIdsToUsers);
	    	resBuilder.setStatus(PromoteDemoteClanMemberStatus.FAIL_NOT_IN_CLAN);
	    	return false;
	    }
	    if (!userIdsToUserClans.containsKey(victimId) || !userIdsToUsers.containsKey(victimId)) {
	    	log.error("user to be promoted or demoted not in clan or db. victim=" + victimId +
	    			"\t userIdsToUserClans=" + userIdsToUserClans + "\t userIdsToUsers=" +
	    			userIdsToUsers);
	    	resBuilder.setStatus(PromoteDemoteClanMemberStatus.FAIL_NOT_IN_CLAN);
	    	return false;
	    }
	    
	    //check if user can demote/promote the other one
	    ClanForUser promoterDemoter = userIdsToUserClans.get(userId);
	    ClanForUser victim = userIdsToUserClans.get(victimId);
	    
	    String first = promoterDemoter.getStatus();
	    String second = victim.getStatus();
	    //can't promote someone if currently captain or the other person is = or higher than user
	    if (UserClanStatus.CAPTAIN.name().equals(first) || 
	    		!getClanStuffUtil().firstUserClanStatusAboveSecond(first, null, second, null)) {
	    	log.error("user not authorized to promote or demote otherUser. clanStatus of user=" +
	    			first + "\t clanStatus of other user=" + second);
	    	resBuilder.setStatus(PromoteDemoteClanMemberStatus.FAIL_NOT_AUTHORIZED);
	    	return false;
	    }
	    //user can't promote someone to status higher than himself
	    if (!getClanStuffUtil().firstUserClanStatusAboveSecond(first, null, null, newUserClanStatus)) {
	    	log.error("user not authorized to promote or demote otherUser. clanStatus of user=" +
	    			first + "\t clanStatus of other user=" + second + "\t newClanStatus of other user=" +
	    			newUserClanStatus);
	    	resBuilder.setStatus(PromoteDemoteClanMemberStatus.FAIL_NOT_AUTHORIZED);
	    	return false;
	    }
	    //user not even in clan...
	    if (UserClanStatus.REQUESTING.name().equals(second)) {
	    	log.error("user can't promote, demote a non-clan member. UserClan for user=" +
	    			promoterDemoter + "\t UserClan for victim=" + victim + "\t users=" + userIdsToUsers);
	    	resBuilder.setStatus(PromoteDemoteClanMemberStatus.FAIL_NOT_AUTHORIZED);
	    	return false;
	    }

		return true;
	}

	
	private boolean writeChangesToDb(User victim, UUID victimId, UUID clanId,
	  		ClanForUser oldInfo, UserClanStatus newUserClanStatus) {
		try {
			String newStatus = newUserClanStatus.name();
			getClanForUserService().updateUserClanStatus(oldInfo, newStatus);
			
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

	public CreateEventProtoUtil getCreateEventProtoUtil() {
		return createEventProtoUtil;
	}

	public void setCreateEventProtoUtil(CreateEventProtoUtil createEventProtoUtil) {
		this.createEventProtoUtil = createEventProtoUtil;
	}
	
}
