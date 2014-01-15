package com.lvl6.mobsters.services.user;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.lvl6.mobsters.entitymanager.nonstaticdata.UserEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

public interface UserService {
	
	//CONTROLLER LOGIC STUFF****************************************************************
	//priority of user returned is 
	//user with specified fbId
	//user with specified udid
	//null
	public abstract User selectivelyChooseUser(List<User> uList, String fbId, String udid);
	
	//given map of userIds to users, list of recipient facebook ids and list of inviter
	//user ids, separate the map of users into recipient and inviter
	public abstract void separateUsersIntoRecipientsAndInviters(Map<UUID, User> idsToUsers,
	  		List<String> recipientFacebookIds, List<UUID> inviterUserIds,
	  		List<User> recipients, List<User> inviters);
	
	
	//RETRIEVE STUFF****************************************************************
	public abstract User getUserWithId(UUID userId);
	
	public abstract User getUserByGamcenterIdOrUserId(String gameCenterId, UUID userId);
	
	public abstract List<User> getUserByUDIDorFbId(String udid, String facebookId);
	
	public abstract List<User> getUsersByFbIds(Collection<String> fbIds);
	
	public abstract List<UUID> getUserIdsForFacebookIds(Collection<String> fbIds);
	
	public abstract Map<UUID, User> getUserIdsToUsersForIds(Collection<UUID> uIdList);
	
	public abstract Map<UUID, User> getUsersForFacebookIdsOrUserIds(List<String> fbIds,
			List<UUID> uIdList);
	
	//INSERT STUFF****************************************************************
	
	
	//SAVING STUFF****************************************************************
	public abstract void saveUser(User u);
	
	public abstract void saveUsers(Collection<User> uCollection);
	
	//UPDATING STUFF****************************************************************
	public abstract void updateUserResources(User u, int gemChange, int oilChange, int cashChange);
	
	public abstract void updateDeviceToken(User u, String deviceToken);

	public abstract void updateFacebookId(User u, String fbId);
	
	//setters and getters for the Setter Dependency Injection (or something)****************************************************************
	public abstract UserEntityManager getUserEntityManager();

	public abstract void setUserEntityManager(UserEntityManager userEntityManager);
	
	public abstract QueryConstructionUtil getQueryConstructionUtil();

	public abstract void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil);
	
}
