package com.lvl6.mobsters.controller;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.controller.utils.CreateEventProtoUtil;
import com.lvl6.mobsters.controller.utils.CreateNoneventProtoUtil;
import com.lvl6.mobsters.eventprotos.EventClanProto.CreateClanRequestProto;
import com.lvl6.mobsters.eventprotos.EventClanProto.CreateClanResponseProto;
import com.lvl6.mobsters.eventprotos.EventClanProto.CreateClanResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventClanProto.CreateClanResponseProto.CreateClanStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.CreateClanRequestEvent;
import com.lvl6.mobsters.events.response.CreateClanResponseEvent;
import com.lvl6.mobsters.events.response.UpdateClientUserResponseEvent;
import com.lvl6.mobsters.noneventprotos.ClanProto.UserClanStatus;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumClanProto;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.po.nonstaticdata.Clan;
import com.lvl6.mobsters.po.nonstaticdata.ClanForUser;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.po.nonstaticdata.UserCurrencyHistory;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.properties.MobstersTableConstants;
import com.lvl6.mobsters.services.clan.ClanService;
import com.lvl6.mobsters.services.clanforuser.ClanForUserService;
import com.lvl6.mobsters.services.user.UserService;
import com.lvl6.mobsters.services.usercurrencyhistory.UserCurrencyHistoryService;


@Component
public class CreateClanController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected UserService userService; 
	
	@Autowired
	protected ClanService clanService;
	
	@Autowired
	protected ClanForUserService clanForUserService;
	
	@Autowired
	protected UserCurrencyHistoryService userCurrencyHistoryService;

	@Autowired
	protected CreateNoneventProtoUtil createNoneventProtoUtil;
	
	@Autowired
	protected CreateEventProtoUtil createEventProtoUtil;
	
	public CreateClanController() {
		numAllocatedThreads = 4;
	}
	
	@Override
	public RequestEvent createRequestEvent() {
		return new CreateClanRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_CREATE_CLAN_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		CreateClanRequestProto reqProto = 
				((CreateClanRequestEvent) event).getCreateClanRequestProto();

		//get the values client sent
		MinimumUserProto senderProto = reqProto.getSender();
	    String clanName = reqProto.getName();
	    String tag = reqProto.getTag();
	    boolean requestToJoinRequired = reqProto.getRequestToJoinClanRequired();
	    String description = reqProto.getDescription();
	    Date createDate = new Date();
	    int gemsSpent = reqProto.getGemsSpent();
	    int cashChange = reqProto.getCashChange();
		
		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = senderProto.getUserUuid();
		UUID userId = UUID.fromString(userIdString);

		//response to send back to client
	    CreateClanResponseProto.Builder responseBuilder =
	    		CreateClanResponseProto.newBuilder();
	    responseBuilder.setStatus(CreateClanStatus.FAIL_OTHER);
	    responseBuilder.setSender(senderProto);
		
		CreateClanResponseEvent resEvent = new CreateClanResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User user = getUserService().getUserWithId(userId);

			boolean legit = checkLegitRequest(responseBuilder, user, clanName, tag);
			
			boolean successful = false;
			if (legit) {
				//just in case user doesn't input one, set default description
		      	if (null == description || description.isEmpty()) {
		      		description = "Welcome to " + clanName + "!";
		      	}
		      	
		      	successful = writeChangesToDb(responseBuilder, clanName, tag, description,
		      			createDate, requestToJoinRequired, user, userId, gemsSpent,
		      			cashChange);
			}
			
			if (successful) {
				responseBuilder.setStatus(CreateClanStatus.SUCCESS);
			}
			
			resEvent.setCreateClanResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);

			if (successful) {
				UpdateClientUserResponseEvent update = getCreateEventProtoUtil()
						.createUpdateClientUserResponseEvent(user);
				getEventWriter().handleEvent(update);
			}
			
		} catch (Exception e) {
			log.error("exception in CreateClanController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(CreateClanStatus.FAIL_OTHER);
				resEvent.setCreateClanResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception2 in CreateClanController processRequestEvent", e2);
			}
		}
	}
	
	private boolean checkLegitRequest(Builder resBuilder, User user, String clanName,
			String tag) {

		if (user == null || clanName == null || tag == null || clanName.isEmpty() ||
				tag.isEmpty()) {
			resBuilder.setStatus(CreateClanStatus.FAIL_OTHER);
			log.error("user, clanName, or tag is null. user=" + user + "\t clanName=" +
					clanName + "\t tag=" + tag);
			return false;      
		}

		int cashCost = MobstersTableConstants.CLAN__CREATE_CLAN_CASH_PRICE;
		if (user.getCash() < cashCost) {
			resBuilder.setStatus(CreateClanStatus.FAIL_NOT_ENOUGH_CASH);
			log.error("user only has " + user.getCash() + ", needs " + cashCost);
			return false;
		}
		int maxLength = MobstersTableConstants.CLAN__MAX_NAME_LENGTH;
		if (clanName.length() > maxLength) {
			resBuilder.setStatus(CreateClanStatus.FAIL_OTHER);
			log.error("clan name " + clanName + " is more than "+ maxLength + " characters");
			return false;
		}

		maxLength = MobstersTableConstants.CLAN__MAX_CLAN_TAG_LENGTH;
	    if (tag.length() > maxLength) {
	      resBuilder.setStatus(CreateClanStatus.FAIL_INVALID_TAG_LENGTH);
	      log.error("clan tag " + tag + " is more than " + maxLength + " characters");
	      return false;
	    }
	    
	    if (null != user.getClanId()) {
	    	resBuilder.setStatus(CreateClanStatus.FAIL_ALREADY_IN_CLAN);
	    	log.error("user already in clan with id " + user.getClanId());
	    	return false;
	    }
	    
	    Clan clan = getClanService().getClanWithNameOrTag(clanName, tag);
	    if (null != clan) {
	    	if (clan.getName().equalsIgnoreCase(clanName)) {
	    		resBuilder.setStatus(CreateClanStatus.FAIL_NAME_TAKEN);
	    		log.error("clan name already taken with name " + clanName);
	    		return false;
	    	}
	    	if (clan.getTag().equalsIgnoreCase(tag)) {
	    		resBuilder.setStatus(CreateClanStatus.FAIL_TAG_TAKEN);
	    		log.error("clan tag already taken with tag " + tag);
	    		return false;
	    	}
	    	log.error("clan name or tag already taken. name=" + clanName + "\t tag=" + tag +
	    			"\t clan=" + clan);
	    	return false;
	    }
	    	
		return true;
	}

	
	private boolean writeChangesToDb(Builder resBuilder, String clanName, String tag,
			String description, Date createDate, boolean requestToJoinRequired, User u,
			UUID userId, int gemsSpent, int cashChange) {
		try {
			//create the clan in the db
			log.info("creating the newly created clan.");
			Clan aClan = getClanService().insertClan(clanName, createDate, description,
					tag, requestToJoinRequired);
			log.info("newly created clan=" + aClan);
			UUID clanId = aClan.getId();
			
			//delete all the user clans for this user
			List<ClanForUser> cfuList = getClanForUserService().getAllUserClansForUser(userId);
			if (null != cfuList && !cfuList.isEmpty()) {
				log.info("deleting all the clan for user entries for current user. entries=" +
						cfuList);
				getClanForUserService().deleteUserClans(cfuList);
				log.info("deleting all the clan for user entries for current user.");
			}
			
			String status = UserClanStatus.LEADER.name();
			log.info("creating leader entry in clan_for_user table");
			ClanForUser cfu = getClanForUserService().insertClanForUser(userId, clanId, status, createDate);
			log.info("leader entry created=" + cfu);
			
			//change the user's clan_id
			u.setClanId(clanId);
			log.info("updated user's clanId to newly created clan's id. user=" + u);
			
			int gemChange = -1 * Math.abs(gemsSpent);
			cashChange = 1 * Math.abs(cashChange);
			//deduct money from user
			List<UserCurrencyHistory> uchList = createCurrencyHistory(u, clanId,
					createDate, cashChange, gemChange);

			int oilChange = 0;
			getUserService().updateUserResources(u, gemChange, oilChange, cashChange);
				
			//keep track of currency stuff
			if (!uchList.isEmpty()) {
				getUserCurrencyHistoryService().saveCurrencyHistories(uchList);
				log.info("recorded user currency change, change=" + uchList);
			}
			
			//giving the clan to the client
			MinimumClanProto mcp = getCreateNoneventProtoUtil()
					.createMinimumClanProtoFromClan(aClan);
			resBuilder.setClanInfo(mcp);
			
			return true;

		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
	}
	
	private List<UserCurrencyHistory> createCurrencyHistory(User u, UUID clanId,
			Date createDate, int cashChange, int gemChange) {
		String cashStr = MobstersDbTables.USER__CASH;
		String gemsStr = MobstersDbTables.USER__GEMS;
		String reasonForChange = MobstersTableConstants.UCHRFC__CREATE_CLAN;
		StringBuilder sb = new StringBuilder();
		sb.append("clanId=");
		sb.append(clanId);
		String details = sb.toString();

		boolean saveToDb = false;
		UserCurrencyHistory cash = getUserCurrencyHistoryService()
				.createNewUserCurrencyHistory(u, createDate, cashStr, cashChange,
						reasonForChange, details, saveToDb);
		UserCurrencyHistory gems = getUserCurrencyHistoryService()
				.createNewUserCurrencyHistory(u, createDate, gemsStr, gemChange,
						reasonForChange, details, saveToDb);
		
		List<UserCurrencyHistory> uchList = new ArrayList<UserCurrencyHistory>();
		if (null != cash) {
			uchList.add(cash);
		}
		if (null != gems) {
			uchList.add(gems);
		}
		return uchList;
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

	public UserCurrencyHistoryService getUserCurrencyHistoryService() {
		return userCurrencyHistoryService;
	}

	public void setUserCurrencyHistoryService(
			UserCurrencyHistoryService userCurrencyHistoryService) {
		this.userCurrencyHistoryService = userCurrencyHistoryService;
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
