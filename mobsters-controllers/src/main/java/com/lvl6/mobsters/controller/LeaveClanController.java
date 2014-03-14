package com.lvl6.mobsters.controller;


import java.util.ArrayList;
import java.util.Collection;
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
import com.lvl6.mobsters.eventprotos.EventClanProto.LeaveClanRequestProto;
import com.lvl6.mobsters.eventprotos.EventClanProto.LeaveClanResponseProto;
import com.lvl6.mobsters.eventprotos.EventClanProto.LeaveClanResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventClanProto.LeaveClanResponseProto.LeaveClanStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.LeaveClanRequestEvent;
import com.lvl6.mobsters.events.response.LeaveClanResponseEvent;
import com.lvl6.mobsters.noneventprotos.ClanProto.UserClanStatus;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.po.nonstaticdata.Clan;
import com.lvl6.mobsters.po.nonstaticdata.ClanForUser;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.services.clan.ClanService;
import com.lvl6.mobsters.services.clanforuser.ClanForUserService;
import com.lvl6.mobsters.services.user.UserService;


@Component
public class LeaveClanController extends EventController {

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
	protected CreateEventProtoUtil createEventProtoUtil;
	
	public LeaveClanController() {
		numAllocatedThreads = 4;
	}
	
	@Override
	public RequestEvent createRequestEvent() {
		return new LeaveClanRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_LEAVE_CLAN_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		LeaveClanRequestProto reqProto = 
				((LeaveClanRequestEvent) event).getLeaveClanRequestProto();

		//get the values client sent
		MinimumUserProto senderProto = reqProto.getSender();
	    String clanIdStr = senderProto.getClan().getClanUuid();
		
		//uuid's are not strings, need to convert from string to uuid, vice versa
	    String userIdString = senderProto.getUserUuid();
	    UUID userId = UUID.fromString(userIdString);
	    
		UUID clanId = null;
		if (null != clanIdStr && !clanIdStr.isEmpty()) {
			 clanId = UUID.fromString(clanIdStr);
		}

		//response to send back to client
	    LeaveClanResponseProto.Builder responseBuilder =
	    		LeaveClanResponseProto.newBuilder();
	    responseBuilder.setStatus(LeaveClanStatus.FAIL_OTHER);
	    responseBuilder.setSender(senderProto);
		LeaveClanResponseEvent resEvent =
				new LeaveClanResponseEvent(userIdString);
		resEvent.setTag(event.getTag());
		
		try {
			//get whatever we need from the database
			User user = getUserService().getUserWithId(userId);
			Clan clan = getClanService().getClanWithId(clanId);

			List<UUID> clanOwnerIdList = new ArrayList<UUID>();
			List<ClanForUser> allClanPpl = new ArrayList<ClanForUser>();
			boolean legit = checkLegitRequest(responseBuilder, user, userId, clan, clanId,
					clanOwnerIdList, allClanPpl);
			
			boolean successful = false;
			if (legit) {
				UUID clanOwnerId = clanOwnerIdList.get(0);
				successful = writeChangesToDb(user, userId, clan, clanId, clanOwnerId,
						allClanPpl);
			}
			
			if (successful) {
				responseBuilder.setStatus(LeaveClanStatus.SUCCESS);
			}
			
			//write to promoter
			resEvent.setLeaveClanResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);
			
			if (successful) {
			}

		} catch (Exception e) {
			log.error("exception in LeaveClanController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(LeaveClanStatus.FAIL_OTHER);
				resEvent.setLeaveClanResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception2 in LeaveClanController processRequestEvent", e2);
			}
		}
	}
	
	//allClanPpl includes all members and non members
	private boolean checkLegitRequest(Builder resBuilder, User user, UUID userId,
			Clan clan, UUID clanId, List<UUID> clanOwnerIdList, List<ClanForUser> allClanPpl) {

		if (user == null || clan == null) {
			log.error("user is null");
			return false;      
		}
		UUID userClanId = user.getClanId();
		if (null == userClanId || !userClanId.equals(clanId)) {
			resBuilder.setStatus(LeaveClanStatus.FAIL_NOT_IN_CLAN);
			log.error("user's clan id is " + userClanId + ", clan id is " + clanId);
			return false;
		}
		
		//querying for all members and non members
		Set<String> statuses = null; 
		List<ClanForUser> allClanPeople = getClanForUserService()
				.getUserClansForStatuses(clanId, statuses); 
		
		//extract the leader id from all clan members and non members
		statuses = new HashSet<String>();
		statuses.add(UserClanStatus.LEADER.name());
		Set<UUID> leaderId = getClanForUserService().getUserIdsForStatuses(allClanPeople,
				statuses);
		
		//checking if there is more than one leader 
		if (null != leaderId && leaderId.size() > 1) {
			log.error("clan has more than one leader :O  leaders=" + leaderId);
			return false;
		}
		
		//since it's leader leaving clan, assume delete clan
		if (leaderId.contains(userId)) {
			//extract the ids from all clan members only
			statuses.add(UserClanStatus.JUNIOR_LEADER.name());
			statuses.add(UserClanStatus.CAPTAIN.name());
			statuses.add(UserClanStatus.MEMBER.name());
			
			//checking there are still members left in the clan
			Collection<UUID> allMembers = getClanForUserService().getUserIdsForStatuses(
					allClanPeople, statuses);
			if (allMembers.size() > 1) {
				resBuilder.setStatus(LeaveClanStatus.FAIL_OWNER_OF_CLAN_WITH_OTHERS_STILL_IN);
		        log.error("user is owner and he's not alone in clan, can't leave without switching ownership. user clan members are " 
		            + allMembers);
		        return false;
			}
		}
		
		clanOwnerIdList.addAll(leaderId);
		allClanPpl.addAll(allClanPeople);
		return true;
	}

	
	private boolean writeChangesToDb(User user, UUID userId, Clan clan, UUID clanId,
			UUID clanOwnerId, List<ClanForUser> allMembersAndNonMembers) {
		try {
			
			if (userId.equals(clanOwnerId)) {
				log.info("deleting info related to clan=" + clan);
				//delete the clan and all the requests. since got here, there are no
				//more users in this clan
				getClanService().deleteClan(clan, allMembersAndNonMembers);
				
			} else {
				//delete user from the clan
				ClanForUser cfu = getClanForUserService().getClanForUserForUserId(userId,
						allMembersAndNonMembers);
				if (null != cfu) {
					//should not be null
					getClanForUserService().deleteUserClan(cfu);
				}
			}
			
			//update the user's clan id
			getUserService().updateDeleteClanId(user);
			
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

	public CreateEventProtoUtil getCreateEventProtoUtil() {
		return createEventProtoUtil;
	}

	public void setCreateEventProtoUtil(CreateEventProtoUtil createEventProtoUtil) {
		this.createEventProtoUtil = createEventProtoUtil;
	}
	
}
