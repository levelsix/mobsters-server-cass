package com.lvl6.mobsters.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.controller.utils.CreateEventProtoUtil;
import com.lvl6.mobsters.controller.utils.MiscUtil;
import com.lvl6.mobsters.controller.utils.StructureStuffUtil;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.IncreaseMonsterInventorySlotRequestProto;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.IncreaseMonsterInventorySlotRequestProto.IncreaseSlotType;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.IncreaseMonsterInventorySlotResponseProto;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.IncreaseMonsterInventorySlotResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.IncreaseMonsterInventorySlotResponseProto.IncreaseMonsterInventorySlotStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.IncreaseMonsterInventorySlotRequestEvent;
import com.lvl6.mobsters.events.response.IncreaseMonsterInventorySlotResponseEvent;
import com.lvl6.mobsters.events.response.UpdateClientUserResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.po.nonstaticdata.StructureForUser;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.po.nonstaticdata.UserCurrencyHistory;
import com.lvl6.mobsters.po.nonstaticdata.UserFacebookInviteForSlot;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.properties.MobstersTableConstants;
import com.lvl6.mobsters.services.structureforuser.StructureForUserService;
import com.lvl6.mobsters.services.user.UserService;
import com.lvl6.mobsters.services.usercurrencyhistory.UserCurrencyHistoryService;
import com.lvl6.mobsters.userfacebookinviteforslot.UserFacebookInviteForSlotService;
import com.lvl6.mobsters.utils.QueryConstructionUtil;


@Component
public class IncreaseMonsterInventorySlotController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected UserService userService; 
	
	@Autowired
	protected StructureForUserService structureForUserService; 
	
	@Autowired
	protected UserFacebookInviteForSlotService userFacebookInviteForSlotService;
	
	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;
	
	@Autowired
	protected StructureStuffUtil structureStuffUtil;

	@Autowired
	protected UserCurrencyHistoryService userCurrencyHistoryService;

	@Autowired
	protected CreateEventProtoUtil createEventProtoUtil;
	
	@Autowired
	protected MiscUtil miscUtil;
	
	@Override
	public RequestEvent createRequestEvent() {
		return new IncreaseMonsterInventorySlotRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_INCREASE_MONSTER_INVENTORY_SLOT_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		IncreaseMonsterInventorySlotRequestProto reqProto = 
				((IncreaseMonsterInventorySlotRequestEvent) event).getIncreaseMonsterInventorySlotRequestProto();

		//get the values client sent
		MinimumUserProto senderProto = reqProto.getSender();
	    String userIdString = senderProto.getUserUuid();
	    IncreaseSlotType increaseType = reqProto.getIncreaseSlotType();
	    String userStructureIdString = reqProto.getUserStructId();
	    //the invites to redeem     
	    List<String> userFbInviteStringIds = reqProto.getUserFbInviteForSlotIdsList();
	    Date curTime = new Date();

		//uuid's are not strings, need to convert from string to uuid, vice versa
		UUID userId = UUID.fromString(userIdString);
		UUID userStructureId = UUID.fromString(userStructureIdString);
		List<UUID> userFbInviteIds = getMiscUtil().createUUIDListFromStrings(userFbInviteStringIds);

		//response to send back to client
		Builder responseBuilder = IncreaseMonsterInventorySlotResponseProto.newBuilder();
		responseBuilder.setStatus(IncreaseMonsterInventorySlotStatus.FAIL_OTHER);
		IncreaseMonsterInventorySlotResponseEvent resEvent =
				new IncreaseMonsterInventorySlotResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User aUser = getUserService().getUserWithId(userId);
			StructureForUser sfu = getStructureForUserService().getSpecificUserStruct(userStructureId);
			Map<UUID, UserFacebookInviteForSlot> idsToAcceptedInvites = 
	    			new HashMap<UUID, UserFacebookInviteForSlot>();
			
			//validate request
			boolean validRequest = isValidRequest(responseBuilder, aUser, userId, sfu,
					userStructureId, increaseType, userFbInviteIds, idsToAcceptedInvites);

			boolean successful = false;
			if (validRequest) {
				int gemCost = getStructureStuffUtil().getGemPriceForStruct(sfu); 
				successful = writeChangesToDb(aUser, sfu, increaseType, gemCost, curTime,
		    	  		idsToAcceptedInvites);
			}

			if (successful) {
				responseBuilder.setStatus(IncreaseMonsterInventorySlotStatus.SUCCESS);
			}

			//write to client
			resEvent.setIncreaseMonsterInventorySlotResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);
			
			if (successful) {
				//since modified user's resources need to send update client user event
				UpdateClientUserResponseEvent resEventUpdate = getCreateEventProtoUtil()
						.createUpdateClientUserResponseEvent(aUser);
				resEventUpdate.setTag(event.getTag());
				getEventWriter().handleEvent(resEventUpdate); 
				
				//delete the user's facebook invites for slots if he purchased them
		      	deleteInvitesForSlotsAfterPurchase(userId, increaseType,
		      			idsToAcceptedInvites);
			}

		} catch (Exception e) {
			log.error("exception in IncreaseMonsterInventorySlotController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(IncreaseMonsterInventorySlotStatus.FAIL_OTHER);
				resEvent.setIncreaseMonsterInventorySlotResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in IncreaseMonsterInventorySlotController processRequestEvent", e2);
			}
		}
	}
	
	private boolean isValidRequest(Builder resBuilder, User u,  UUID userId,
			StructureForUser sfu, UUID userStructId, IncreaseSlotType aType,
			List<UUID> userFbInviteIds,
	  		Map<UUID, UserFacebookInviteForSlot> idsToAcceptedInvites) {
		if (null == u) {
	  		log.error("user is null. no user exists with id=" + userId);
	  		return false;
	  	}
	  	if (null == sfu) {
	  		log.error("doesn't exist, user struct with id=" + userStructId);
	  		return false;
	  	}
	  	
	  	//THE CHECK IF USER IS REDEEMING FACEBOOK INVITES
	  	if (IncreaseSlotType.REDEEM_FACEBOOK_INVITES == aType) {
	  		//get accepted and unredeemed invites
	  		Map<UUID, UserFacebookInviteForSlot> idsToAcceptedTemp =
	  				getUserFacebookInviteForSlotService().getUnredeemedInvites(userId,
	  						userFbInviteIds);
	  		//check if requested invites even exist
	  		if (null == idsToAcceptedTemp || idsToAcceptedTemp.isEmpty()) {
	  			log.error("no invites exist with ids: " + userFbInviteIds);
	  			return false;
	  		}

	  		boolean consistentInvites = getUserFacebookInviteForSlotService()
	  				.areConsistentInvites(userStructId, idsToAcceptedTemp);
	  		if (!consistentInvites) {
	  			resBuilder.setStatus(IncreaseMonsterInventorySlotStatus.FAIL_INCONSISTENT_INVITE_DATA);
	  			log.error("data across invites aren't consistent: user struct id/fb lvl. invites=" +
	  					idsToAcceptedTemp + "\t expectedUserStructId=" + userStructId);
	  			return false;
	  		}

	  		//required min num invites depends on the structure
	  		int minNumInvites = getStructureStuffUtil().getMinNumInvitesForStruct(sfu);
	  		//check if user has enough invites to gain a slot
	  		int acceptedAmount = idsToAcceptedTemp.size(); 
	  		if(acceptedAmount < minNumInvites) {
	  			resBuilder.setStatus(IncreaseMonsterInventorySlotStatus.FAIL_INSUFFICIENT_FACEBOOK_INVITES);
	  			log.error("user doesn't meet num accepted facebook invites to increase slots. " +
	  					"minRequired=" + minNumInvites + "\t has:" + acceptedAmount);
	  			return false;
	  		}
	  		//give the caller the invites, at this point, the number of invites is at least
	  		//equal to minNumInvites and could be more
	  		idsToAcceptedInvites.putAll(idsToAcceptedTemp);

	  		//THE CHECK IF USER IS BUYING SLOTS
	  	} else if (IncreaseSlotType.PURCHASE == aType) {
	  		//gemprice depends on the structure;
	  		int gemPrice = getStructureStuffUtil().getGemPriceForStruct(sfu);

	  		//check if user has enough money
	  		int userGems = u.getGems();
	  		if (userGems < gemPrice) {
	  			resBuilder.setStatus(IncreaseMonsterInventorySlotStatus.FAIL_INSUFFICIENT_FUNDS);
	  			log.error("user does not have enough gems to buy more monster inventory slots. userGems=" +
	  					userGems + "\t gemPrice=" + gemPrice);
	  			return false;
	  		}
	  		
	  		List<UUID> specificInviteIds = null;
	  		boolean filterByAccepted = false;
	  		boolean isAccepted = false;
	  		boolean filterByRedeemed = true;
	  		boolean isRedeemed = false;
	  		//get all the unredeemed invites for this user/inviter
	  		Map<UUID, UserFacebookInviteForSlot> idsToAcceptedTemp =
	  				getUserFacebookInviteForSlotService()
	  				.getSpecificOrAllInvitesForInviter(userId, specificInviteIds,
	  						filterByAccepted, isAccepted, filterByRedeemed, isRedeemed);
	  		idsToAcceptedInvites.putAll(idsToAcceptedTemp);

	  	} else {
	  		return false;
	  	}
		return true;
	}

	private boolean writeChangesToDb(User aUser, StructureForUser sfu, 
	  		IncreaseSlotType increaseType, int gemCost, Date curTime,
	  		Map<UUID, UserFacebookInviteForSlot> idsToAcceptedInvites) {
		try {
			//upgrade the struct
			int prevFbInviteStructLvl = sfu.getFbInviteStructLvl();
			int fbInviteLvlDelta = 1;
			getStructureForUserService().updateUserStructFbInviteLvl(sfu, fbInviteLvlDelta);
			
			//
			if (IncreaseSlotType.REDEEM_FACEBOOK_INVITES == increaseType) {
				int minNumInvites = getStructureStuffUtil().getMinNumInvitesForStruct(sfu);
				//if num accepted invites more than min required, just take the earliest ones
				List<UUID> inviteIdsTheRest = new ArrayList<UUID>();
				List<UserFacebookInviteForSlot> nEarliestInvites = 
						getUserFacebookInviteForSlotService().getnEarliestInvites(
								idsToAcceptedInvites, minNumInvites, inviteIdsTheRest);
				
				//redeem the nEarliestInvites, update their redeem times
				getUserFacebookInviteForSlotService()
				.updateUserFacebookInviteForSlotRedeemTime(curTime, nEarliestInvites);
				
				//delete all the remaining invites
				getUserFacebookInviteForSlotService()
				.deleteUserFacebookInvitesForIds(inviteIdsTheRest);
			}
			
			int gemChange = -1 * gemCost;
			if (IncreaseSlotType.PURCHASE == increaseType && 0 != gemChange) {
				//create history first
				List<UserCurrencyHistory> uchList = createCurrencyHistory(aUser, curTime,
						gemChange, sfu, prevFbInviteStructLvl);
				int oilChange = 0;
				int cashChange = 0;
				getUserService().updateUserResources(aUser, gemChange, oilChange, cashChange);

				//save currency stuff
				if (!uchList.isEmpty()) {
					getUserCurrencyHistoryService().saveCurrencyHistories(uchList);
				}
			}
			
			return true;

		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
	}
		

	private List<UserCurrencyHistory> createCurrencyHistory(User aUser, Date curTime, 
			int gemChange, StructureForUser sfu, int prevFbInviteStructLvl) {
		String gemsStr = MobstersDbTables.USER__GEMS;
		String reasonForChange = MobstersTableConstants.UCHRFC__INCREASE_MONSTER_INVENTORY;
		StringBuilder detailsSb = new StringBuilder();
		
		detailsSb.append("(sfuId,prevFbInviteStructLvl,cur)= ");
		detailsSb.append("(");
		detailsSb.append(sfu.getId());
		detailsSb.append(",");
		detailsSb.append(prevFbInviteStructLvl);
		detailsSb.append(",");
		detailsSb.append(sfu.getFbInviteStructLvl());
		detailsSb.append(")");
		String details = detailsSb.toString();
		
		boolean saveToDb = false;
		UserCurrencyHistory gems = getUserCurrencyHistoryService()
				.createNewUserCurrencyHistory(aUser, curTime, gemsStr, gemChange,
						reasonForChange, details, saveToDb);
		
		List<UserCurrencyHistory> uchList = new ArrayList<UserCurrencyHistory>();
		if (null != gems) {
			uchList.add(gems);
		}
		return uchList;
	}
	
	//after user buys slots, delete all accepted and unaccepted invites for slots
	private void deleteInvitesForSlotsAfterPurchase(UUID userId, IncreaseSlotType aType,
			Map<UUID, UserFacebookInviteForSlot> idsToAcceptedInvites) {
		if (!IncreaseSlotType.PURCHASE.equals(aType)) {
			return;
		}
		
		Collection<UserFacebookInviteForSlot> ufifsList = idsToAcceptedInvites.values();
		getUserFacebookInviteForSlotService().deleteUserFacebookInvites(ufifsList);
	}
	

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public StructureForUserService getStructureForUserService() {
		return structureForUserService;
	}

	public void setStructureForUserService(
			StructureForUserService structureForUserService) {
		this.structureForUserService = structureForUserService;
	}

	public UserFacebookInviteForSlotService getUserFacebookInviteForSlotService() {
		return userFacebookInviteForSlotService;
	}

	public void setUserFacebookInviteForSlotService(
			UserFacebookInviteForSlotService userFacebookInviteForSlotService) {
		this.userFacebookInviteForSlotService = userFacebookInviteForSlotService;
	}

	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}

	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}

	public UserCurrencyHistoryService getUserCurrencyHistoryService() {
		return userCurrencyHistoryService;
	}

	public void setUserCurrencyHistoryService(
			UserCurrencyHistoryService userCurrencyHistoryService) {
		this.userCurrencyHistoryService = userCurrencyHistoryService;
	}

	public StructureStuffUtil getStructureStuffUtil() {
		return structureStuffUtil;
	}

	public void setStructureStuffUtil(StructureStuffUtil structureStuffUtil) {
		this.structureStuffUtil = structureStuffUtil;
	}

	public CreateEventProtoUtil getCreateEventProtoUtil() {
		return createEventProtoUtil;
	}

	public void setCreateEventProtoUtil(CreateEventProtoUtil createEventProtoUtil) {
		this.createEventProtoUtil = createEventProtoUtil;
	}

	public MiscUtil getMiscUtil() {
		return miscUtil;
	}

	public void setMiscUtil(MiscUtil miscUtil) {
		this.miscUtil = miscUtil;
	}
	
}
