package com.lvl6.mobsters.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.controller.utils.CreateNoneventProtoUtils;
import com.lvl6.mobsters.controller.utils.FacebookStuffUtils;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.InviteFbFriendsForSlotsRequestProto;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.InviteFbFriendsForSlotsRequestProto.FacebookInviteStructure;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.InviteFbFriendsForSlotsResponseProto;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.InviteFbFriendsForSlotsResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.InviteFbFriendsForSlotsResponseProto.InviteFbFriendsForSlotsStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.InviteFbFriendsForSlotsRequestEvent;
import com.lvl6.mobsters.events.response.InviteFbFriendsForSlotsResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoWithFacebookId;
import com.lvl6.mobsters.noneventprotos.UserProto.UserFacebookInviteForSlotProto;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.po.nonstaticdata.UserFacebookInviteForSlot;
import com.lvl6.mobsters.services.user.UserService;
import com.lvl6.mobsters.userfacebookinviteforslot.UserFacebookInviteForSlotService;



@Component
public class InviteFbFriendsForSlotsController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	
	@Autowired
	protected FacebookStuffUtils facebookStuffUtils;
	
	@Autowired
	protected UserService userService;
	
	@Autowired
	protected UserFacebookInviteForSlotService userFacebookInviteForSlotService;
	
	@Autowired
	protected CreateNoneventProtoUtils createNoneventProtoUtils;
	
	@Override
	public RequestEvent createRequestEvent() {
		return new InviteFbFriendsForSlotsRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_INVITE_FB_FRIENDS_FOR_SLOTS_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		InviteFbFriendsForSlotsRequestProto reqProto = 
				((InviteFbFriendsForSlotsRequestEvent) event).getInviteFbFriendsForSlotsRequestProto();

		//get the values client sent
		MinimumUserProtoWithFacebookId senderFb = reqProto.getSender();
		MinimumUserProto sender = senderFb.getMinUserProto();
		String userIdString = sender.getUserUuid();
		List<FacebookInviteStructure> invites = reqProto.getInvitesList();

		Map<String, UUID> fbIdsToUserStructIds = new HashMap<String, UUID>();
	    Map<String, Integer> fbIdsToUserStructFbLvl = new HashMap<String, Integer>();
	    List<String> fbIdsOfFriends = getFacebookStuffUtils().demultiplexFacebookInviteStructure(invites,
	    		fbIdsToUserStructIds, fbIdsToUserStructFbLvl);
	    
		
		//uuid's are not strings, need to convert from string to uuid, vice versa
		UUID userId = UUID.fromString(userIdString);
		Date clientDate = new Date();

		//response to send back to client
		Builder responseBuilder = InviteFbFriendsForSlotsResponseProto.newBuilder();
		responseBuilder.setStatus(InviteFbFriendsForSlotsStatus.FAIL_OTHER);
		InviteFbFriendsForSlotsResponseEvent resEvent =
				new InviteFbFriendsForSlotsResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User aUser = getUserService().getUserWithId(userId);
			//get all the invites the user sent
			List<UUID> specificIds = null;
			boolean filterByAccepted = false;
			boolean isAccepted = false;
			boolean filterByRedeemed = false;
			boolean isRedeemed = false;
			Map<UUID, UserFacebookInviteForSlot> idsToInvites = 
					getUserFacebookInviteForSlotService().getSpecificOrAllInvitesForInviter(
							userId, specificIds, filterByAccepted, isAccepted,
							filterByRedeemed, isRedeemed);

			//will contain the facebook ids of new users the user can invite
			//new is defined as: for each facebookId the tuple
			//(inviterId, recipientId)=(userId, facebookId) 
			//doesn't already exist in the table 
			List<String> newFacebookIdsToInvite = new ArrayList<String>();
			boolean legit = checkLegit(responseBuilder, userId, aUser, fbIdsOfFriends,
					idsToInvites, newFacebookIdsToInvite);

			boolean successful = false;
			List<UserFacebookInviteForSlot> newInvites = new ArrayList<UserFacebookInviteForSlot>();
			if (legit) {
				//will populate inviteIds
				successful = writeChangesToDb(aUser, newFacebookIdsToInvite, clientDate,
						fbIdsToUserStructIds, fbIdsToUserStructFbLvl, newInvites);
			}
			
			if (successful) {
				//client needs to know what the new invites are
				for (UserFacebookInviteForSlot invite : newInvites) {
					UserFacebookInviteForSlotProto proto = getCreateNoneventProtoUtils()
		      				.createUserFacebookInviteForSlotProtoFromInvite(invite, aUser, senderFb);
		      		responseBuilder.addInvitesNew(proto);
				}
				responseBuilder.setStatus(InviteFbFriendsForSlotsStatus.SUCCESS);
			}

			//write to client
			resEvent.setInviteFbFriendsForSlotsResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);
			
			if (successful) {
				List<UUID> recipientUserIds = getUserService()
						.getUserIdsForFacebookIds(newFacebookIdsToInvite);
				InviteFbFriendsForSlotsResponseProto responseProto = responseBuilder.build();
				for (UUID recipientUserId : recipientUserIds) {
					String recipientUserIdStr = recipientUserId.toString();
					InviteFbFriendsForSlotsResponseEvent newResEvent =
							new InviteFbFriendsForSlotsResponseEvent(recipientUserIdStr);
					newResEvent.setTag(0);
					newResEvent.setInviteFbFriendsForSlotsResponseProto(responseProto);
					getEventWriter().handleEvent(newResEvent);
		      	}
				
			}

		} catch (Exception e) {
			log.error("exception in InviteFbFriendsForSlotsController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(InviteFbFriendsForSlotsStatus.FAIL_OTHER);
				resEvent.setInviteFbFriendsForSlotsResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in InviteFbFriendsForSlotsController processRequestEvent", e2);
			}
		}
	}
	
	/*
	 * Return true if user request is valid; false otherwise and set the
	 * builder status to the appropriate value. newUserIdsToInvite will be 
	 * modified
	 */
	private boolean checkLegit(Builder resBuilder, UUID userId, User u,
			List<String> fbIdsOfFriends, Map<UUID, UserFacebookInviteForSlot> idsToInvites,
			List<String> newFacebookIdsToInvite) {

		if (null == u) {
			log.error("user is null. no user exists with id=" + userId);
			return false;
		}

		//if the user already invited some friends, don't invite again, keep
		//only new ones
		List<String> newFacebookIdsToInviteTemp = getUserFacebookInviteForSlotService()
				.getNewInvites(fbIdsOfFriends, idsToInvites);
		newFacebookIdsToInvite.addAll(newFacebookIdsToInviteTemp);
		
		return true;
	}

	//the list "inviteIds" will contain another return value from this method 
	private boolean writeChangesToDb(User aUser, List<String> newFacebookIdsToInvite, 
	  		Date clientDate, Map<String, UUID> fbIdsToUserStructIds,
	  		Map<String, Integer> fbIdsToUserStructsFbLvl,
	  		List<UserFacebookInviteForSlot> newInvites) {
		try {
			
			if (newFacebookIdsToInvite.isEmpty()) {
				return true;
			}
			//call service method to create and store the new invites
			UUID userId = aUser.getId();
			List<UserFacebookInviteForSlot> temp = getUserFacebookInviteForSlotService()
					.insertIntoUserFbInviteForSlot(userId, newFacebookIdsToInvite,
							clientDate, fbIdsToUserStructIds, fbIdsToUserStructsFbLvl);

			newInvites.addAll(temp);
			return true;

		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
	}


	public FacebookStuffUtils getFacebookStuffUtils() {
		return facebookStuffUtils;
	}

	public void setFacebookStuffUtils(FacebookStuffUtils facebookStuffUtils) {
		this.facebookStuffUtils = facebookStuffUtils;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public UserFacebookInviteForSlotService getUserFacebookInviteForSlotService() {
		return userFacebookInviteForSlotService;
	}

	public void setUserFacebookInviteForSlotService(
			UserFacebookInviteForSlotService userFacebookInviteForSlotService) {
		this.userFacebookInviteForSlotService = userFacebookInviteForSlotService;
	}

	public CreateNoneventProtoUtils getCreateNoneventProtoUtils() {
		return createNoneventProtoUtils;
	}

	public void setCreateNoneventProtoUtils(
			CreateNoneventProtoUtils createNoneventProtoUtils) {
		this.createNoneventProtoUtils = createNoneventProtoUtils;
	}

}
