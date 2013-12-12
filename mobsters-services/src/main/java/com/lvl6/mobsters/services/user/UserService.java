package com.lvl6.mobsters.services.user;

import java.util.Collection;
import java.util.UUID;

import com.lvl6.mobsters.entitymanager.nonstaticdata.UserEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.User;

public interface UserService {
	
	//RETRIEVE STUFF****************************************************************
	public abstract User getUserWithId(UUID userId);
	
	public abstract User retrieveUser(String gameCenterId, UUID userId);
	
	
	
	//INSERT STUFF****************************************************************
	
	
	//SAVING STUFF****************************************************************
	public abstract void saveUser(User u);
	
	public abstract void saveUsers(Collection<User> uCollection);
	
	//UPDATING STUFF****************************************************************
	public abstract void updateUserResources(User u, int gemsChange, int oilChange, int cashChange);
	
	public abstract void updateDeviceToken(User u, String deviceToken);

	
	//setters and getters for the Setter Dependency Injection (or something)****************************************************************
	public abstract UserEntityManager getUserEntityManager();

	public abstract void setUserEntityManager(UserEntityManager userEntityManager);
	
}
