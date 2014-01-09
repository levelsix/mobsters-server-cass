package com.lvl6.mobsters.controller;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.controller.utils.CreateNoneventProtoUtil;
import com.lvl6.mobsters.controller.utils.MiscUtil;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.AcceptAndRejectFbInviteForSlotsRequestProto;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.AcceptAndRejectFbInviteForSlotsResponseProto;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.AcceptAndRejectFbInviteForSlotsResponseProto.AcceptAndRejectFbInviteForSlotsStatus;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.AcceptAndRejectFbInviteForSlotsResponseProto.Builder;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.AcceptAndRejectFbInviteForSlotsRequestEvent;
import com.lvl6.mobsters.events.response.AcceptAndRejectFbInviteForSlotsResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoWithFacebookId;
import com.lvl6.mobsters.noneventprotos.UserProto.UserFacebookInviteForSlotProto;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.po.nonstaticdata.UserFacebookInviteForSlot;
import com.lvl6.mobsters.services.user.UserService;
import com.lvl6.mobsters.userfacebookinviteforslot.UserFacebookInviteForSlotService;


@Component
public class AcceptAndRejectFbInviteForSlotsController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected MiscUtil miscUtil;
	
	@Autowired
	protected UserFacebookInviteForSlotService userFacebookInviteForSlotService;

	@Autowired
	protected UserService userService;
	
	@Autowired
	protected CreateNoneventProtoUtil createNoneventProtoUtil;
	
	@Override
	public RequestEvent createRequestEvent() {
		return new AcceptAndRejectFbInviteForSlotsRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_ACCEPT_AND_REJECT_FB_INVITE_FOR_SLOTS_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		AcceptAndRejectFbInviteForSlotsRequestProto reqProto = 
				((AcceptAndRejectFbInviteForSlotsRequestEvent) event).getAcceptAndRejectFbInviteForSlotsRequestProto();

		//get the values client sent
		MinimumUserProtoWithFacebookId senderFb = reqProto.getSender();
		MinimumUserProto sender = senderFb.getMinUserProto();
		String userFacebookId = senderFb.getFacebookId();
		
		//just accept these
	    List<String> acceptedInviteIdsStr = reqProto.getAcceptedInviteUuidsList();
	    List<UUID> acceptedInviteIds = new ArrayList<UUID>();
	    if(null != acceptedInviteIdsStr && !acceptedInviteIdsStr.isEmpty()) {
	    	acceptedInviteIds = getMiscUtil()
	    			.createUUIDListFromStrings(acceptedInviteIdsStr);
	    }
	    
	    //delete these from the table
	    List<String> rejectedInviteIdsStr = reqProto.getRejectedInviteUuidsList();
	    List<UUID> rejectedInviteIds = new ArrayList<UUID>();
	    if(null != rejectedInviteIdsStr && !rejectedInviteIdsStr.isEmpty()) {
	    	acceptedInviteIds = getMiscUtil()
	    			.createUUIDListFromStrings(rejectedInviteIdsStr);
	    }
	    Date clientDate = new Date();
		
		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = sender.getUserUuid();
		UUID userId = UUID.fromString(userIdString);

		//response to send back to client
		Builder responseBuilder = AcceptAndRejectFbInviteForSlotsResponseProto.newBuilder();
		responseBuilder.setStatus(AcceptAndRejectFbInviteForSlotsStatus.FAIL_OTHER);
		
		AcceptAndRejectFbInviteForSlotsResponseEvent resEvent =
				new AcceptAndRejectFbInviteForSlotsResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			//these will be populated. by isValidRequest()
			Map<UUID, UserFacebookInviteForSlot> idsToInvitesInDb =
					new HashMap<UUID, UserFacebookInviteForSlot>();
			
			//validate request
			boolean validRequest = isValidRequest(responseBuilder, userId, userFacebookId,
					acceptedInviteIds, rejectedInviteIds, idsToInvitesInDb);

			boolean successful = false;
			if (validRequest) {
				successful = writeChangesToDb(userId, userFacebookId, acceptedInviteIds,
		    	  		rejectedInviteIds, idsToInvitesInDb, clientDate);
			}

			if (successful) {
				//need to retrieve all the inviters, for the accepted invites, from the db
				Collection<UserFacebookInviteForSlot> acceptedInvites = getMiscUtil()
						.getValuesForIds(acceptedInviteIds, idsToInvitesInDb);
				
		      	List<UUID> userIds = getUserFacebookInviteForSlotService()
		      			.getInviterUserIds(acceptedInvites);
		      	Map<UUID, User> userIdsToUsers = getUserService()
		      			.getUserIdsToUsersForIds(userIds);
		      	
		      	//for each invite protofy them
		      	for (UserFacebookInviteForSlot invite: acceptedInvites) {
		      		UUID inviterId = invite.getInviterUserId();
		      		User inviter = userIdsToUsers.get(inviterId);
		      		MinimumUserProtoWithFacebookId inviterProto = null;

		      		//create the proto for the invites
		      		UserFacebookInviteForSlotProto inviteProto = getCreateNoneventProtoUtil()
		      				.createUserFacebookInviteForSlotProtoFromInvite(invite, inviter, inviterProto);

		      		responseBuilder.addAcceptedInvites(inviteProto);
		      	}
		      	responseBuilder.setStatus(AcceptAndRejectFbInviteForSlotsStatus.SUCCESS);
			}

			//write to client
			resEvent.setAcceptAndRejectFbInviteForSlotsResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);
			
			if (successful) {
				//write to the inviters this user accepted their invite
				AcceptAndRejectFbInviteForSlotsResponseProto responseProto =
						responseBuilder.build();
				for (UUID inviteId : acceptedInviteIds) {
					UserFacebookInviteForSlot invite = idsToInvitesInDb.get(inviteId);
					UUID inviterId = invite.getInviterUserId();
					String inviterIdStr = inviterId.toString();
					
					AcceptAndRejectFbInviteForSlotsResponseEvent newResEvent =
							new AcceptAndRejectFbInviteForSlotsResponseEvent(inviterIdStr);
					newResEvent.setTag(0);
					newResEvent.setAcceptAndRejectFbInviteForSlotsResponseProto(responseProto);
					getEventWriter().handleEvent(newResEvent);

				}
			}

		} catch (Exception e) {
			log.error("exception in AcceptAndRejectFbInviteForSlotsController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(AcceptAndRejectFbInviteForSlotsStatus.FAIL_OTHER);
				resEvent.setAcceptAndRejectFbInviteForSlotsResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in AcceptAndRejectFbInviteForSlotsController processRequestEvent", e2);
			}
		}
	}
	
	private boolean isValidRequest(Builder resBuilder, UUID userId, String userFacebookId,
	  		List<UUID> acceptedInviteIds, List<UUID> rejectedInviteIds,
	  		Map<UUID, UserFacebookInviteForSlot> idsToInvites) {
		
		if (null == userFacebookId || userFacebookId.isEmpty()) {
	  		log.error("facebookId is null. id=" + userFacebookId + "\t acceptedInvitesIds=" +
	  				acceptedInviteIds + "\t rejectedInviteIds=" + rejectedInviteIds);
	  		return false;
	  	}
	  	//search for these invites accepted and rejected
	  	List<UUID> inviteIds = new ArrayList<UUID>(acceptedInviteIds);
	  	inviteIds.addAll(rejectedInviteIds);
	  	
	  	//retrieve the invites for this recipient that haven't been accepted nor redeemed
	  	boolean filterByAccepted = true;
	  	boolean isAccepted = false;
	  	boolean filterByRedeemed = true;
	  	boolean isRedeemed = false;
	  	Map<UUID, UserFacebookInviteForSlot> idsToInvitesInDb = 
	  			getUserFacebookInviteForSlotService().getSpecificOrAllInvitesForRecipient(
	  					userFacebookId, inviteIds, filterByAccepted,
	  					isAccepted, filterByRedeemed, isRedeemed);
	  	Set<UUID> validIds = idsToInvitesInDb.keySet();
	  	
	  	//only want the acceptedInvite ids that aren't yet accepted nor redeemed
	  	log.info("acceptedInviteIds before filter: " + acceptedInviteIds);
	  	getMiscUtil().retainValidListEntries(validIds, acceptedInviteIds);
	  	log.info("acceptedInviteIds after filter: " + acceptedInviteIds);

	  	//only want the rejectedInvite ids that aren't yet accepted nor redeemed
	  	getMiscUtil().retainValidListEntries(validIds, rejectedInviteIds);

	  	
	  	//check to make sure this user is not accepting any invites from an inviter
	  	//this user has already accepted, or in other words
	  	//check to make sure this user has not previously accepted any invites from 
	  	//any of the inviters of the acceptedInviteIds

	  	//pair up inviterUserIds with the acceptedInviteIds
	  	Map<UUID, UUID> acceptedInviterIdsToInviteIds = 
	  			getUserFacebookInviteForSlotService().getInviterUserIdsToInviteIds(
	  					acceptedInviteIds, idsToInvitesInDb); 
	  	
	  	//look in the invite table for accepted invites (includes redeemed),
	  	//select the inviter user ids from the invites that have
	  	//recipientFacebookId = userFacebookId
	  	isAccepted = true;
	  	Set<UUID> redeemedInviterIds = getUserFacebookInviteForSlotService()
	  			.getUniqueInviterUserIdsForRequesterId(userFacebookId, filterByAccepted, isAccepted);

	  	//if any of the acceptedInviteIds contains an inviterId this user has already accepted
	  	//an invite from,
	  	//delete inviteId from the acceptedInviteIds list and put the
	  	//inviteId into the rejectedInviteIds list,
	  	//done so because the db probably has recorded that the inviter used up this user
	  	//and is trying to use this user again
	  	log.info("acceptedInviteIds before inviter used check: " + acceptedInviteIds);
	  	getUserFacebookInviteForSlotService().retainInvitesFromUnusedInviters(
	  			redeemedInviterIds, acceptedInviterIdsToInviteIds, acceptedInviteIds,
	  			rejectedInviteIds);
	  	log.info("acceptedInviteIds after inviter used check: " + acceptedInviteIds);

	  	idsToInvites.putAll(idsToInvitesInDb);

		return true;
	}

	private boolean writeChangesToDb(UUID userId, String userFacebookId,
	  		List<UUID> acceptedInviteIds, List<UUID> rejectedInviteIds,
	  		Map<UUID, UserFacebookInviteForSlot> idsToInvitesInDb,
	  		Date acceptTime) {
		try {
			
			//update the acceptTimes for the acceptedInviteIds
		  	//these acceptedInviteIds are for unaccepted, unredeemed invites
		  	if (!acceptedInviteIds.isEmpty()) {
				getUserFacebookInviteForSlotService().updateUserFacebookInviteForSlotAcceptTime(
						acceptedInviteIds, idsToInvitesInDb, acceptTime);
				log.info("\t\t\t\t\t\t\t acceptedInviteIds updated:" + acceptedInviteIds +
						"\t allInvites=" + idsToInvitesInDb + "\t time=" + acceptTime);
			}
		  	
		  	//DELETE THE rejectedInviteIds THAT ARE ALREADY IN DB
		  	//these deleted invites are for unaccepted, unredeemed invites
		  	if (!rejectedInviteIds.isEmpty()) {
		  		getUserFacebookInviteForSlotService()
		  		.deleteUserFacebookInvitesForIds(rejectedInviteIds);
		  	}
			return true;

		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
	}

	public MiscUtil getMiscUtil() {
		return miscUtil;
	}

	public void setMiscUtil(MiscUtil miscUtil) {
		this.miscUtil = miscUtil;
	}

	public UserFacebookInviteForSlotService getUserFacebookInviteForSlotService() {
		return userFacebookInviteForSlotService;
	}

	public void setUserFacebookInviteForSlotService(
			UserFacebookInviteForSlotService userFacebookInviteForSlotService) {
		this.userFacebookInviteForSlotService = userFacebookInviteForSlotService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public CreateNoneventProtoUtil getCreateNoneventProtoUtil() {
		return createNoneventProtoUtil;
	}

	public void setCreateNoneventProtoUtil(
			CreateNoneventProtoUtil createNoneventProtoUtil) {
		this.createNoneventProtoUtil = createNoneventProtoUtil;
	}

}
