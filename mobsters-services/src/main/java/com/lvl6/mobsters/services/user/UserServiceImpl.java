package com.lvl6.mobsters.services.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.UserDeviceEntityManager;
import com.lvl6.mobsters.entitymanager.UserEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.ClassLevelInfoRetrieveUtils;
import com.lvl6.mobsters.noneventprotos.FunctionalityTypeEnum.FunctionalityType;
import com.lvl6.mobsters.po.ClassLevelInfo;
import com.lvl6.mobsters.po.Structure;
import com.lvl6.mobsters.po.User;
import com.lvl6.mobsters.po.UserDevice;
import com.lvl6.mobsters.po.UserStructure;
import com.lvl6.mobsters.properties.MobstersTableConstants;
import com.lvl6.mobsters.services.userstructure.UserStructureService;


@Component
public class UserServiceImpl implements UserService {
	
	@Autowired
	protected ClassLevelInfoRetrieveUtils classLevelInfoRetrieveUtils;
	
	@Autowired
	protected UserDeviceEntityManager userDeviceEntityManager;
	
	@Autowired
	protected UserEntityManager userEntityManager;
	
	@Autowired
	protected UserStructureService userStructureService;

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	@Override
	public User createNewUser(String gameCenterId, DateTime loginTime, String udid) {
		Date d = loginTime.toDate();
		
		//create the new user
		User u = new User();
		getUserEntityManager().get().put(u);
		
		//create row in user_device
		UserDevice ud = new UserDevice();
		ud.setUserId(u.getId());
		ud.setDateLinked(d);
		ud.setLastLogin(d);
		getUserDeviceEntityManager().get().put(ud);
		
		return u;
	}
	
	//caller should save the user object
	@Override
	public void initializeUser(User u, Date now) {
		levelUpUser(u);
		
		u.setGems(MobstersTableConstants.USER__GEMS);
		u.setGold(MobstersTableConstants.USER__GOLD);
		u.setTonic(MobstersTableConstants.USER__TONIC);
		u.setLastTimeHpRegened(now);
		u.setLastTimeManaRegened(now);
		
		u.setAccountInitialized(true);
		
		getUserEntityManager().get().put(u);
	}
	
	//caller should save the user object
	@Override
	public void levelUpUser(User u) {
		int classType = u.getClassType();
		int newLvl = u.getLvl() + 1;
		
		ClassLevelInfo cli = getClassLevelInfoRetrieveUtils()
				.getClassLevelInfoForClassAndLevel(classType, newLvl);
		
		int maxHp = cli.getMaxHp();
		int maxMana = cli.getMaxMana();
		
		u.setLvl(newLvl);
		u.setMaxHp(maxHp);
		u.setMaxMana(maxMana);
		
		u.setHp(maxHp);
		u.setMana(maxMana);
	}
	
	//searches for a user based on game center or user id
	@Override
	public User retrieveUser(String gameCenterId, String userId) throws Exception {
		String cqlQuery = "select * from user u ";
		List<User> uList = new ArrayList<User>();
		if (null != gameCenterId) {
			cqlQuery += "where u.game_center_id = " + gameCenterId + ";";
			uList = getUserEntityManager().get().find(cqlQuery);
			
		} else if (null != userId){
			cqlQuery += "where u.user_id = " + userId + ";";
			uList = getUserEntityManager().get().find(cqlQuery);
			
		}
		
		if (uList == null || uList.isEmpty()) {
			return null;
		} else if (uList.size() > 1) {
			String msg = "multiple users exist. gameCenterId=" +
					gameCenterId + ", userId=" + userId +
					" uList=" + uList;
			log.error("unexpected error: " + msg);
			throw new Exception(msg);
		} else {
			return uList.get(0);
		}
		
	}
	
	@Override
	public User retrieveUserForUdid(String udid) throws Exception {
		String cqlQuery = "select * from user_device ud ";
		List<UserDevice> udList = null;
		
		if (null != udid && !udid.isEmpty()) {
			cqlQuery += "where ud.udid = " + udid + ";";
			
			udList = getUserDeviceEntityManager().
					get().find(cqlQuery);
		}
		
		if (null == udList || udList.isEmpty()) {
			//there is no user associated with this udid.
			return null;
		} else if (udList.size() > 1) {
			String msg = "User id tied to more than one udid. udidList="
					+ udList;
			log.error("unexpected error: " + msg);
			throw new Exception(msg);
		} else {
			UserDevice ud = udList.get(0);
			String userId = ud.getUserId().toString();
			//there is a user account tied to this udid
			return retrieveUser(null, userId);
			
		}
	}
	
	@Override
	public void updateUserResources(User u,
			Map<Integer, Integer> resourceTypeToChanges) {
		
		int gold = MobstersTableConstants.RESOURCE_TYPE__GOLD;
		int tonic = MobstersTableConstants.RESOURCE_TYPE__TONIC;
		if (resourceTypeToChanges.containsKey(gold)) {
			int totalGold = u.getGold();
			totalGold += resourceTypeToChanges.get(gold);
			u.setGold(totalGold);
		}
		
		if (resourceTypeToChanges.containsKey(tonic)) {
			int totalTonic = u.getTonic();
			totalTonic += resourceTypeToChanges.get(tonic);
			u.setTonic(totalTonic);
		}
		
		getUserEntityManager().get().put(u);
	}
	
	public int calculateGemCostForMissingResources(User u, int missingResources, int missingResourcesType) {
		List<UserStructure> usList = getUserStructureService().getAllUserStructuresForUser(u.getId());
		int maxStorage = 0;
		for(UserStructure us : usList) {
			Structure s = getUserStructureService().getStructureCorrespondingToUserStructure(us);
			if((s.getFunctionalityResourceType() == missingResourcesType) && (s.getFunctionalityType() == FunctionalityType.RESOURCE_STORAGE_VALUE)) {
				maxStorage = maxStorage + s.getFunctionalityCapacity();
			}
		}
		double percentage = (double)(missingResources)/(double)(maxStorage);
		int proportionalGemCost = calculateGemCostForPercentageOfResource(u, percentage, maxStorage);
		//multiply by some punishing constant here
		int inflatedGemCost = proportionalGemCost * 2; 
		return inflatedGemCost;
		
	}
	
	public int calculateGemCostForPercentageOfResource(User u, double percentage, int maxStorage) {
		//super cool logrithmic formula to calculate what cost should be using maxStorage and percentage values
		return 1000000;
	}
	
	public int calculateGemCostForSpeedUp(int minutes) {
		//TODO: LOG FORMULA FOR SPEEDINGUP BASED ON TIME
		return 10000000;
	}
	
	@Override
	public void updateUserGold(User u, int goldChange) {
		if (goldChange > 0) {
			int totalGold = u.getGold();
			totalGold += goldChange;
			u.setGold(totalGold);
		}
		getUserEntityManager().get().put(u);
	}

	@Override
	public void updateUserTonic(User u, int tonicChange) {
		if (tonicChange > 0) {
			int totalTonic = u.getTonic();
			totalTonic += tonicChange;
			u.setTonic(totalTonic);
		}
		getUserEntityManager().get().put(u);
	}
	
	
	
	@Override
	public ClassLevelInfoRetrieveUtils getClassLevelInfoRetrieveUtils() {
		return classLevelInfoRetrieveUtils;
	}

	@Override
	public void setClassLevelInfoRetrieveUtils(
			ClassLevelInfoRetrieveUtils classLevelInfoRetrieveUtils) {
		this.classLevelInfoRetrieveUtils = classLevelInfoRetrieveUtils;
	}

	@Override
	public UserDeviceEntityManager getUserDeviceEntityManager() {
		return userDeviceEntityManager;
	}
	
	@Override
	public void setUserDeviceEntityManager(
			UserDeviceEntityManager userDeviceEntityManager) {
		this.userDeviceEntityManager = userDeviceEntityManager;
	}
	
	@Override
	public UserEntityManager getUserEntityManager() {
		return userEntityManager;
	}

	@Override
	public void setUserEntityManager(UserEntityManager userEntityManager) {
		this.userEntityManager = userEntityManager;
	}

	public UserStructureService getUserStructureService() {
		return userStructureService;
	}

	public void setUserStructureService(UserStructureService userStructureService) {
		this.userStructureService = userStructureService;
	}


	
}