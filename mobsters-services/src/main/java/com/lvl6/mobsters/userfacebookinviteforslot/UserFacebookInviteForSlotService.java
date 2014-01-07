package com.lvl6.mobsters.userfacebookinviteforslot;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.lvl6.mobsters.entitymanager.nonstaticdata.UserFacebookInviteForSlotEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.UserFacebookInviteForSlot;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

public interface UserFacebookInviteForSlotService {

	//CONTROLLER LOGIC STUFF
	//if user struct ids and user struct fb lvls are inconsistent, return false
	public abstract boolean areConsistentInvites(UUID userStructId,
			Map<UUID, UserFacebookInviteForSlot> idsToInvites);
	
	public abstract List<UserFacebookInviteForSlot> getnEarliestInvites(
			Map<UUID, UserFacebookInviteForSlot> idsToAcceptedInvites, int n,
			List<UUID> inviteIdsTheRest);
	
	public abstract void orderUserFacebookAcceptedInvitesForSlots(
			List<UserFacebookInviteForSlot> invites);
	
	//RETRIEVING STUFF
//	public abstract UserFacebookInviteForSlot getInviteForId(UUID inviteId);
	
	public abstract Map<UUID, UserFacebookInviteForSlot> getInvitesForIds(
			List<UUID> inviteIds);
	
	public abstract Map<UUID, UserFacebookInviteForSlot> getSpecificOrAllInvitesForInviter(
	  		UUID userId, List<UUID> specificInviteIds, boolean filterByAccepted,
	  		boolean isAccepted, boolean filterByRedeemed, boolean isRedeemed);
	
	public abstract Map<UUID, UserFacebookInviteForSlot> getSpecificOrAllInvitesForRecipient(
	  		String recipientFacebookId, List<UUID> specificInviteIds, boolean filterByAccepted,
	  		boolean isAccepted, boolean filterByRedeemed, boolean isRedeemed);
	
	public abstract Set<UUID> getUniqueInviterUserIdsForRequesterId(String facebookId,
	  		boolean filterByAccepted, boolean isAccepted);
	
	public abstract Map<UUID, UserFacebookInviteForSlot> getUnredeemedInvites(UUID inviterId,
			List<UUID> userFbInviteIds);
	
	
	//INSERTING STUFF
	
	//SAVING STUFF****************************************************************
	
	public abstract void saveUserFacebookInvites(List<UserFacebookInviteForSlot> ufiList);
	
	
	//UPDATING STUFF
	public abstract void updateUserFacebookInviteForSlotRedeemTime(Date redeemTime,
			List<UserFacebookInviteForSlot> redeemedInvites);
	
	
	//DELETING STUFF
	public abstract void deleteUserFacebookInvite(UUID userFacebookInviteUuid);
	
	public abstract void deleteUserFacebookInvitesForIds(List<UUID> userFacebookInviteList);
	
	public abstract void deleteUserFacebookInvites(
			Collection<UserFacebookInviteForSlot> ufifsList);
	
	
	//for the setter dependency injection or something
	public abstract UserFacebookInviteForSlotEntityManager getUserFacebookInviteForSlotEntityManager();
	
	public abstract void setUserFacebookInviteForSlotEntityManager(UserFacebookInviteForSlotEntityManager monsterEnhancingForUserEntityManager);
	
	public abstract QueryConstructionUtil getQueryConstructionUtil();
	
	public abstract void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil);
	
}