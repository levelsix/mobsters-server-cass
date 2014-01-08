package com.lvl6.mobsters.userfacebookinviteforslot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.nonstaticdata.UserFacebookInviteForSlotEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.UserFacebookInviteForSlot;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.utils.QueryConstructionUtil;


@Component
public class UserFacebookInviteForSlotServiceImpl implements UserFacebookInviteForSlotService {
	
	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	private final String TABLE_NAME = MobstersDbTables.TABLE_USER_FACEBOOK_INVITE_FOR_SLOT;
	
	@Autowired
	protected UserFacebookInviteForSlotEntityManager userFacebookInviteForSlotEntityManager;
	
	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;
	
	
	//CONTROLLER LOGIC STUFF****************************************************************
	@Override
	public boolean areConsistentInvites(UUID userStructId,
			Map<UUID, UserFacebookInviteForSlot> idsToInvites) {
		if (null == idsToInvites || idsToInvites.isEmpty()) {
			return false;
		}
		
		UUID prevUserStructId = UUID.randomUUID();
		int prevUserStructFbLvl = -1;
		
		for (UserFacebookInviteForSlot invite : idsToInvites.values()) {
			UUID tempUserStructId = invite.getUserStructId();
			int tempUserStructFbLvl = invite.getUserStructFbLvl();
			
			//invite must be tied to a userStructId
			if (-1 == prevUserStructFbLvl) {
				//initializing both the prev* variables
				prevUserStructId = tempUserStructId;
	  			prevUserStructFbLvl = tempUserStructFbLvl;
	  			
			} else if (!prevUserStructId.equals(tempUserStructId) ||
					prevUserStructFbLvl != tempUserStructFbLvl) {
				//the userStructIds or userStructFbLvls are inconsistent
				return false;
			}
		}
		return false;
	}

	@Override
	public List<UserFacebookInviteForSlot> getnEarliestInvites(
			Map<UUID, UserFacebookInviteForSlot> idsToAcceptedInvites, int n,
			List<UUID> inviteIdsTheRest) {
		
		List<UserFacebookInviteForSlot> earliestAcceptedInvites =
	  			new ArrayList<UserFacebookInviteForSlot>(idsToAcceptedInvites.values());
		orderUserFacebookAcceptedInvitesForSlots(earliestAcceptedInvites);

		if (n < earliestAcceptedInvites.size()) {
			int amount = earliestAcceptedInvites.size();

			//want to get the remaining invites after the first n
			for (UserFacebookInviteForSlot invite : earliestAcceptedInvites.subList(n, amount)) {
				UUID id = invite.getId();
				inviteIdsTheRest.add(id);
			}

			//get first n invites
			return earliestAcceptedInvites.subList(0, n);
		} else {
			//num invites guaranteed to not be less than n
			return earliestAcceptedInvites;
		}
	}
	
	@Override
	public void orderUserFacebookAcceptedInvitesForSlots(
			List<UserFacebookInviteForSlot> invites) {
		Collections.sort(invites, new Comparator<UserFacebookInviteForSlot>() {
	  		@Override
	  		public int compare(UserFacebookInviteForSlot lhs, UserFacebookInviteForSlot rhs) {
	  			//sorting by accept time, which should not be null
	  			Date lhsDate = lhs.getTimeAccepted();
	  			Date rhsDate = rhs.getTimeAccepted();

	  			if (null == lhsDate && null == rhsDate) 
	  				return 0;
	  			else if (null == lhsDate) 
	  				return -1;
	  			else if (null == rhsDate) 
	  				return 1;
	  			else if (lhsDate.getTime() < rhsDate.getTime())
	  				return -1;
	  			else if (lhsDate.getTime() == rhsDate.getTime())
	  				return 0;
	  			else
	  				return 1;
	  		}
	  	});
	}
	
	@Override
	public List<String> getNewInvites(List<String> fbIdsOfFriends,
			Map<UUID, UserFacebookInviteForSlot> idsToInvites) {
		//CONTAINS THE DUPLICATE INVITES, THAT NEED TO BE DELETED
		//E.G. TWO INVITES EXIST WITH SAME INVITERID AND RECIPIENTID
		List<UUID> inviteIdsOfDuplicateInvites = new ArrayList<UUID>();

		//running collection of recipient ids already seen
		Set<String> processedRecipientIds = new HashSet<String>();

		//for each recipientId separate the unique ones from the duplicates
		for (UUID inviteId : idsToInvites.keySet()) {
			UserFacebookInviteForSlot invite = idsToInvites.get(inviteId);
			String recipientId = invite.getRecipientFbId(); 

			//if seen this recipientId place it in the duplicates list
			if (processedRecipientIds.contains(recipientId)) {
				//done to ensure a user does not invite another user more than once
				//i.e. tuple (inviterId, recipientId) is unique
				inviteIdsOfDuplicateInvites.add(inviteId);
			} else {
				//keep track of the recipientIds seen so far (the unique ones)
				processedRecipientIds.add(recipientId);
			}
		}

		//DELETE THE DUPLICATE INVITES THAT ARE ALREADY IN DB
		//maybe need to determine which invites should be deleted, as in most recent or somethings
		//because right now, any of the nonunique invites could be deleted
		if (!inviteIdsOfDuplicateInvites.isEmpty()) {
			deleteUserFacebookInvitesForIds(inviteIdsOfDuplicateInvites);
			log.warn("duplicate invites deleted: " + inviteIdsOfDuplicateInvites);
		}


		List<String> newFacebookIdsToInvite = new ArrayList<String>();
		//don't want to generate an invite to a recipient the user has already invited
		//going through the facebook ids client sends and select only the new ones
		for (String prospectiveRecipientId : fbIdsOfFriends) {

			//keep only the recipient ids that have not been seen/invited
			if(!processedRecipientIds.contains(prospectiveRecipientId)) {
				newFacebookIdsToInvite.add(prospectiveRecipientId);
			}
		}
		return newFacebookIdsToInvite;
	}

	
	//RETRIEVING STUFF****************************************************************
//	@Override
//	public UserFacebookInviteForSlot getInviteForId(UUID inviteId) {
//		log.debug("retrieving user facebook invite for inviteId " + inviteId);
//		UserFacebookInviteForSlot invite = getUserFacebookInviteForSlotEntityManager()
//				.get().get(inviteId); 
//		
//		return invite;
//	}
	@Override
	public Map<UUID, UserFacebookInviteForSlot> getInvitesForIds(List<UUID> inviteIds) {
		log.debug("retrieving user facebook invites for invite ids " + inviteIds);
		
		Map<String, Object> equalityConditions = null;
		String equalityCondDelim = null;
		Map<String, Object> greaterThanConditions = null;
		String greaterThanCondDelim = null;
		Map<String, Object> isConditions = null;
		String isCondDelim = null;
		
		//can't make type Map<String, List<UUID> because selectRowsQueryAllConditions()
		//will complain
		Map<String, Collection<?>> inConditions = new HashMap<String, Collection<?>>();
		inConditions.put(MobstersDbTables.USER_FACEBOOK_INVITE_FOR_SLOT__ID, inviteIds);
		String inCondDelim = getQueryConstructionUtil().getAnd();
		String delimAcrossConditions = inCondDelim;
		
		String cqlQuery = getQueryConstructionUtil().selectRowsQueryAllConditions(
				TABLE_NAME, equalityConditions, equalityCondDelim, greaterThanConditions,
				greaterThanCondDelim, inConditions, inCondDelim, isConditions, isCondDelim,
				delimAcrossConditions,
				null);
		List<UserFacebookInviteForSlot> ufifsList =
				getUserFacebookInviteForSlotEntityManager().get().find(cqlQuery);
		
		Map<UUID, UserFacebookInviteForSlot> inviteIdsToInvites =
				new HashMap<UUID, UserFacebookInviteForSlot>();
		for (UserFacebookInviteForSlot ufifs : ufifsList) {
			UUID userMonsterId = ufifs.getId();
			inviteIdsToInvites.put(userMonsterId, ufifs);
		}
		
		return inviteIdsToInvites;
	}
	
	
	@Override
	public Map<UUID, UserFacebookInviteForSlot> getSpecificOrAllInvitesForInviter(
		UUID userId, List<UUID> specificInviteIds, boolean filterByAccepted,
		boolean isAccepted, boolean filterByRedeemed, boolean isRedeemed) {

		//construct the search parameters
		Map<String, Object> equalityConditions = new HashMap<String, Object>();
		equalityConditions.put(MobstersDbTables.USER_FACEBOOK_INVITE_FOR_SLOT__INVITER_USER_ID, userId);
		String equalityCondDelim = getQueryConstructionUtil().getAnd();
		
		Map<String, Object> greaterThanConditions = null;
		String greaterThanCondDelim = null;
		
		Map<String, Collection<?>> inConditions = null;
		if (null != specificInviteIds && !specificInviteIds.isEmpty()) {
			inConditions = new HashMap<String, Collection<?>>();
			inConditions.put(MobstersDbTables.USER_FACEBOOK_INVITE_FOR_SLOT__ID, specificInviteIds);
		}
		String inCondDelim = equalityCondDelim;
		
		
		Map<String, Object> isConditions = new HashMap<String, Object>();
		if (filterByAccepted) {
			String value = getQueryConstructionUtil().getNullStr(); 
			if (isAccepted) {
				value = getQueryConstructionUtil().getNotNull();
			}
			isConditions.put(MobstersDbTables.USER_FACEBOOK_INVITE_FOR_SLOT__TIME_ACCEPTED,
					value);
		}
		if (filterByRedeemed) {
			String value = getQueryConstructionUtil().getNullStr();
			if (isRedeemed) {
				value = getQueryConstructionUtil().getNotNull();
			}
			isConditions.put(MobstersDbTables.USER_FACEBOOK_INVITE_FOR_SLOT__TIME_REDEEMED,
					value);
		}
		String isCondDelim = equalityCondDelim;
		
		String delimAcrossConditions = getQueryConstructionUtil().getAnd();
		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement)
		List<Object> values = new ArrayList<Object>();
		String cqlQuery = getQueryConstructionUtil().selectRowsQueryAllConditions(
				TABLE_NAME, equalityConditions, equalityCondDelim, greaterThanConditions,
				greaterThanCondDelim, inConditions, inCondDelim, isConditions,
				isCondDelim, delimAcrossConditions, values);
		List<UserFacebookInviteForSlot> mefuList = getUserFacebookInviteForSlotEntityManager().get().find(cqlQuery);
		
		
		Map<UUID, UserFacebookInviteForSlot> idsToInvites =
				new HashMap<UUID, UserFacebookInviteForSlot>();
		for (UserFacebookInviteForSlot ufifs : mefuList) {
			UUID userMonsterId = ufifs.getId();
			idsToInvites.put(userMonsterId, ufifs);
		}
		
		return idsToInvites;
	}
	
	
	@Override
	public Map<UUID, UserFacebookInviteForSlot> getSpecificOrAllInvitesForRecipient(
			String recipientFacebookId, List<UUID> specificInviteIds, boolean filterByAccepted,
			boolean isAccepted, boolean filterByRedeemed, boolean isRedeemed) {

		log.debug("retrieving user facebook invite for recipientFbId=" +
				recipientFacebookId + "\t specificInviteIds=" + specificInviteIds);

		//construct the search parameters
		Map<String, Object> equalityConditions = new HashMap<String, Object>();
		equalityConditions.put(MobstersDbTables.USER_FACEBOOK_INVITE_FOR_SLOT__RECIPIENT_FACEBOOK_ID,
				recipientFacebookId);
		String equalityCondDelim = getQueryConstructionUtil().getAnd();

		Map<String, Object> greaterThanConditions = null;
		String greaterThanCondDelim = null;

		Map<String, Collection<?>> inConditions = null;
		if (null != specificInviteIds && !specificInviteIds.isEmpty()) {
			inConditions = new HashMap<String, Collection<?>>();
			inConditions.put(MobstersDbTables.USER_FACEBOOK_INVITE_FOR_SLOT__ID, specificInviteIds);
		}
		String inCondDelim = equalityCondDelim;


		Map<String, Object> isConditions = new HashMap<String, Object>();
		if (filterByAccepted) {
			String value = getQueryConstructionUtil().getNullStr(); 
			if (isAccepted) {
				value = getQueryConstructionUtil().getNotNull();
			}
			isConditions.put(MobstersDbTables.USER_FACEBOOK_INVITE_FOR_SLOT__TIME_ACCEPTED,
					value);
		}
		if (filterByRedeemed) {
			String value = getQueryConstructionUtil().getNullStr();
			if (isRedeemed) {
				value = getQueryConstructionUtil().getNotNull();
			}
			isConditions.put(MobstersDbTables.USER_FACEBOOK_INVITE_FOR_SLOT__TIME_REDEEMED,
					value);
		}
		String isCondDelim = equalityCondDelim;

		String delimAcrossConditions = getQueryConstructionUtil().getAnd();
		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement)
		List<Object> values = new ArrayList<Object>();
		String cqlQuery = getQueryConstructionUtil().selectRowsQueryAllConditions(
				TABLE_NAME, equalityConditions, equalityCondDelim, greaterThanConditions,
				greaterThanCondDelim, inConditions, inCondDelim, isConditions,
				isCondDelim, delimAcrossConditions, values);
		List<UserFacebookInviteForSlot> mefuList = getUserFacebookInviteForSlotEntityManager()
				.get().find(cqlQuery);


		Map<UUID, UserFacebookInviteForSlot> idsToInvites =
				new HashMap<UUID, UserFacebookInviteForSlot>();
		for (UserFacebookInviteForSlot ufifs : mefuList) {
			UUID userMonsterId = ufifs.getId();
			idsToInvites.put(userMonsterId, ufifs);
		}

		return idsToInvites;
	}
	
	@Override
	public Set<UUID> getUniqueInviterUserIdsForRequesterId(String facebookId,
	  		boolean filterByAccepted, boolean isAccepted) {
		//TODO: FIGURE OUT HOW TO ONLY GET THE IDS AND NOT THE WHOLE ROW
		List<UUID> specificInviteIds = null;
		boolean filterByRedeemed = false;
		boolean isRedeemed = false;
		
		Map<UUID, UserFacebookInviteForSlot> idsToInvites = 
				getSpecificOrAllInvitesForRecipient(facebookId, specificInviteIds,
						filterByAccepted, isAccepted, filterByRedeemed, isRedeemed);
		
		Set<UUID> uniqInviterIds = new HashSet<UUID>();
		
		for (UserFacebookInviteForSlot invite : idsToInvites.values()) {
			UUID inviterId = invite.getInviterUserId();
			uniqInviterIds.add(inviterId);
		}
		
		return uniqInviterIds;
	}
	
	@Override
	public Map<UUID, UserFacebookInviteForSlot> getUnredeemedInvites(UUID inviterId,
			List<UUID> userFbInviteIds) {
		boolean filterByAccepted = true;
		boolean isAccepted = true;
		boolean filterByRedeemed = true;
		boolean isRedeemed = false;
		Map<UUID, UserFacebookInviteForSlot> idsToAcceptedTemp =
				getSpecificOrAllInvitesForInviter(inviterId, userFbInviteIds,
						filterByAccepted, isAccepted, filterByRedeemed, isRedeemed);
		return idsToAcceptedTemp;
	}

	
	//INSERTING STUFF****************************************************************
	@Override
	public List<UserFacebookInviteForSlot> insertIntoUserFbInviteForSlot(UUID userId,
			List<String> facebookIds, Date curTime, Map<String, UUID> fbIdsToUserStructIds,
			Map<String, Integer> fbIdsToUserStructsFbLvl) {
		
		List<UserFacebookInviteForSlot> retVal = new ArrayList<UserFacebookInviteForSlot>();
		
		//creates new UserFacebookInviteForSlot
		for (String fbId : facebookIds) {
			UUID inviterUserStructId = fbIdsToUserStructIds.get(fbId);
			Integer userStructFbLvl = fbIdsToUserStructsFbLvl.get(fbId);
			
			UserFacebookInviteForSlot ufifs = createNewUserFbInviteForSlot(userId, fbId,
					curTime, inviterUserStructId, userStructFbLvl);
			
			retVal.add(ufifs);
		}
		
		return retVal;
	}
	
	private UserFacebookInviteForSlot createNewUserFbInviteForSlot(UUID inviterId,
			String recipientFbId, Date timeOfInvite, UUID inviterUserStructId, 
			int userStructFbLvl) {
		UserFacebookInviteForSlot ufifs = new UserFacebookInviteForSlot();
		ufifs.setInviterUserId(inviterId);
		ufifs.setRecipientFbId(recipientFbId);
		ufifs.setTimeOfInvite(timeOfInvite);
		ufifs.setUserStructId(inviterUserStructId);
		ufifs.setUserStructFbLvl(userStructFbLvl);
		
		return ufifs;
	}
	
	//SAVING STUFF****************************************************************
	
	@Override
	public void saveUserFacebookInvites(List<UserFacebookInviteForSlot> ufiList) {
		getUserFacebookInviteForSlotEntityManager().get().put(ufiList);
	}
	
	
	//UPDATING STUFF****************************************************************
	@Override
	public void updateUserFacebookInviteForSlotRedeemTime(Date redeemTime,
			List<UserFacebookInviteForSlot> redeemedInvites) {
		
		for (UserFacebookInviteForSlot ufifs : redeemedInvites) {
			ufifs.setTimeRedeemed(redeemTime);
		}
		saveUserFacebookInvites(redeemedInvites);
	}
	
	
	//DELETING STUFF****************************************************************
	@Override
	public void deleteUserFacebookInvite(UUID userFacebookInviteUuid) {
		getUserFacebookInviteForSlotEntityManager().get().delete(userFacebookInviteUuid);
	}
	
	@Override
	public void deleteUserFacebookInvitesForIds(List<UUID> userFacebookInviteList) {
		getUserFacebookInviteForSlotEntityManager().get().delete(userFacebookInviteList);
	}
	
	@Override
	public void deleteUserFacebookInvites(Collection<UserFacebookInviteForSlot> ufifsList) {
		List<UUID> userFacebookInviteList = new ArrayList<UUID>();
		
		for (UserFacebookInviteForSlot invite : ufifsList) {
			UUID inviteId = invite.getId();
			userFacebookInviteList.add(inviteId);
		}
		getUserFacebookInviteForSlotEntityManager().get().delete(userFacebookInviteList);
	}
	
	
	//for the setter dependency injection or something****************************************************************
	@Override
	public UserFacebookInviteForSlotEntityManager getUserFacebookInviteForSlotEntityManager() {
		return userFacebookInviteForSlotEntityManager;
	}
	@Override
	public void setUserFacebookInviteForSlotEntityManager(
			UserFacebookInviteForSlotEntityManager userFacebookInviteForSlotEntityManager) {
		this.userFacebookInviteForSlotEntityManager = userFacebookInviteForSlotEntityManager;
	}
	@Override
	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}
	@Override
	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}

}
