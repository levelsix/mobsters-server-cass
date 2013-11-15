package com.lvl6.mobsters.services.user;

import java.util.Date;
import java.util.Map;

import org.joda.time.DateTime;

import com.lvl6.mobsters.entitymanager.UserDeviceEntityManager;
import com.lvl6.mobsters.entitymanager.UserEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.ClassLevelInfoRetrieveUtils;
import com.lvl6.mobsters.po.User;

public interface UserService {
	
	public abstract User createNewUser(String gameCenterId,
			DateTime loginTime, String udid);
	
	public abstract void initializeUser(User u, Date now);
	
	public abstract void levelUpUser(User u);
	
	public abstract User retrieveUser(String gameCenterId,
			String userId) throws Exception;
	
	public abstract User retrieveUserForUdid(String udid) throws Exception;
	
	public abstract void updateUserResources(User u,
			Map<Integer, Integer> resourceTypeToChanges);
	
	public abstract void updateUserGold(User u, int goldChange);
	
	public abstract void updateUserTonic(User u, int tonicChange);
	
	public abstract int calculateGemCostForMissingResources(User u, int missingResources, int missingResourcesType);
	
	public abstract int calculateGemCostForPercentageOfResource(User u, double percentage, int maxStorage);
	
	public abstract int calculateGemCostForSpeedUp(int minutes);

	public ClassLevelInfoRetrieveUtils getClassLevelInfoRetrieveUtils();	
	
	public void setClassLevelInfoRetrieveUtils(ClassLevelInfoRetrieveUtils classLevelInfoRetrieveUtils);
	
	public abstract UserDeviceEntityManager getUserDeviceEntityManager();
	
	public abstract void setUserDeviceEntityManager(UserDeviceEntityManager userDeviceEntityManager);
	
	public abstract UserEntityManager getUserEntityManager();

	public abstract void setUserEntityManager(UserEntityManager userEntityManager);
}