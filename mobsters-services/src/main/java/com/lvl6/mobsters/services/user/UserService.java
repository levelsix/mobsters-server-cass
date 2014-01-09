package com.lvl6.mobsters.services.user;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.lvl6.mobsters.entitymanager.nonstaticdata.UserEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

public interface UserService {
	
	//RETRIEVE STUFF****************************************************************
	public abstract User getUserWithId(UUID userId);
	
	public abstract User getUserByGamcenterIdOrUserId(String gameCenterId, UUID userId);
	
	public abstract List<User> getUserByUDIDorFbId(String udid, String facebookId);
	
	public abstract List<User> getUsersByFbIds(Collection<String> fbIds);
	
	public abstract List<UUID> getUserIdsForFacebookIds(Collection<String> fbIds);
	
	public abstract Map<UUID, User> getUserIdsToUsersForIds(Collection<UUID> uIdList);
	
	//INSERT STUFF****************************************************************
	
	
	//SAVING STUFF****************************************************************
	public abstract void saveUser(User u);
	
	public abstract void saveUsers(Collection<User> uCollection);
	
	//UPDATING STUFF****************************************************************
	public abstract void updateUserResources(User u, int gemChange, int oilChange, int cashChange);
	
	public abstract void updateDeviceToken(User u, String deviceToken);

	
	//setters and getters for the Setter Dependency Injection (or something)****************************************************************
	public abstract UserEntityManager getUserEntityManager();

	public abstract void setUserEntityManager(UserEntityManager userEntityManager);
	
	public abstract QueryConstructionUtil getQueryConstructionUtil();

	public abstract void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil);
	
}
