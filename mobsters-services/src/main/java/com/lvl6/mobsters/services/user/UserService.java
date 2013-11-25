package com.lvl6.mobsters.services.user;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.joda.time.DateTime;

import com.lvl6.mobsters.entitymanager.UserDeviceEntityManager;
import com.lvl6.mobsters.entitymanager.nonstaticdata.UserEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.StructureResourceStorageRetrieveUtils;
import com.lvl6.mobsters.po.nonstaticdata.User;

public interface UserService {
	
	//RETRIEVE STUFF
	public abstract User getUserWithId(UUID userId);
	
	public abstract User retrieveUser(String gameCenterId, UUID userId);
	
	
	
	//INSERT STUFF
	
	
	//SAVING STUFF
	public abstract void saveUser(User u);
	
	public abstract void saveUsers(Collection<User> uCollection);
	
	
	//old aoc2 stuff
	public abstract User createNewUser(String gameCenterId,
			DateTime loginTime, UUID udid);
	
	public abstract void initializeUser(User u, Date now);
	
	public abstract void levelUpUser(User u);
	
	
	public abstract User retrieveUserForUdid(String udid) throws Exception;
	
	public abstract void updateUserResources(User u,
			Map<Integer, Integer> resourceTypeToChanges);
	
	public abstract void updateUserGold(User u, int goldChange);
	
	public abstract void updateUserTonic(User u, int tonicChange);
	
	public abstract int calculateGemCostForMissingResources(User u, int missingResources, int missingResourcesType);
	
	public abstract int calculateGemCostForPercentageOfResource(User u, double percentage, int maxStorage);
	
	public abstract int calculateGemCostForSpeedUp(int minutes);

	
	//setters and getters for the Setter Dependency Injection (or something)
	public abstract StructureResourceStorageRetrieveUtils getClassLevelInfoRetrieveUtils();	
	
	public abstract void setClassLevelInfoRetrieveUtils(StructureResourceStorageRetrieveUtils structureResourceStorageRetrieveUtils);
	
	public abstract UserDeviceEntityManager getUserDeviceEntityManager();
	
	public abstract void setUserDeviceEntityManager(UserDeviceEntityManager userDeviceEntityManager);
	
	public abstract UserEntityManager getUserEntityManager();

	public abstract void setUserEntityManager(UserEntityManager userEntityManager);
}