package com.lvl6.mobsters.services.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.nonstaticdata.UserEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.utils.QueryConstructionUtil;


@Component
public class UserServiceImpl implements UserService {
	
	@Autowired
	protected UserEntityManager userEntityManager;
	
	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;
	

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	private final String TABLE_NAME = MobstersDbTables.TABLE_USER;

	//RETRIEVE STUFF****************************************************************
	@Override
	public User getUserWithId(UUID userId) {
		return getUserEntityManager().get().get(userId);
	}
	
	//searches for a user based on game center or user id
	@Override
	public User getUserByGamcenterIdOrUserId(String gameCenterId, UUID userId) {
		//"values" is not used
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		String conditionDelimiter = getQueryConstructionUtil().getAnd();
		
		//construct the search parameters
		Map<String, Object> equalityConditions = new HashMap<String, Object>();
		
		List<User> uList = new ArrayList<User>();
		if (null != gameCenterId) {
			equalityConditions.put(MobstersDbTables.USER__GAME_CENTER_ID, gameCenterId);
			
			String cqlQuery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
					TABLE_NAME, equalityConditions, conditionDelimiter, values, preparedStatement);
			uList = getUserEntityManager().get().find(cqlQuery);

		} else if (null != userId){
			equalityConditions.put(MobstersDbTables.USER__ID, userId);
			
			String cqlQuery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
					TABLE_NAME, equalityConditions, conditionDelimiter, values, preparedStatement);
			uList = getUserEntityManager().get().find(cqlQuery);

		}

		if (uList == null || uList.isEmpty()) {
			return null;
		} else if (uList.size() > 1) {
			String msg = "multiple users exist. gameCenterId=" + gameCenterId +
					", userId=" + userId + " uList=" + uList;
			log.error("unexpected error: " + msg);
			return null;
		} else {
			return uList.get(0);
		}

	}
	
	public List<User> getUserByUDIDorFbId(String udid, String facebookId) {
		//"values" is not used
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;

		//construct the search parameters
		Map<String, Object> equalityConditions = new HashMap<String, Object>();

		if (null != udid) {
			equalityConditions.put(MobstersDbTables.USER__UDID, udid);
		}
		if (null != facebookId){
			equalityConditions.put(MobstersDbTables.USER__FACEBOOK_ID, facebookId);
		}
		String conditionDelimiter = getQueryConstructionUtil().getOr();
		String cqlQuery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
				TABLE_NAME, equalityConditions, conditionDelimiter, values, preparedStatement);
		List<User> uList = getUserEntityManager().get().find(cqlQuery);
		return uList;
	}
	
	@Override
	public List<User> getUsersByFbIds(Collection<String> fbIds) {
		//construct the search parameters
		Map<String, Object> equalityConditions = null;
		String equalityCondDelim = getQueryConstructionUtil().getAnd();
		
		Map<String, Object> greaterThanConditions = null;
		String greaterThanCondDelim = getQueryConstructionUtil().getAnd();
		
		Map<String, Collection<?>> inConditions = new HashMap<String, Collection<?>>();
		inConditions.put(MobstersDbTables.USER__FACEBOOK_ID, fbIds);
		String inCondDelim = getQueryConstructionUtil().getAnd();

		Map<String, Collection<?>> isConditions = null;
		String isCondDelim = null;
		String delimAcrossConditions = getQueryConstructionUtil().getAnd();

		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement)
		List<Object> values = new ArrayList<Object>();
		String cqlQuery = getQueryConstructionUtil().selectRowsQueryAllConditions(
				TABLE_NAME, equalityConditions, equalityCondDelim, greaterThanConditions,
				greaterThanCondDelim, isConditions, isCondDelim, inConditions,
				inCondDelim, delimAcrossConditions, values);
		List<User> uList = getUserEntityManager().get().find(cqlQuery);
		
		return uList;
	}
	
	@Override
	public List<UUID> getUserIdsForFacebookIds(Collection<String> fbIds) {
		List<User> uList = getUsersByFbIds(fbIds);
		
		List<UUID> retVal = new ArrayList<UUID>();
		
		for (User u : uList) {
			UUID uId = u.getId();
			retVal.add(uId);
		}
		return retVal;
	}
	
	@Override
	public Map<UUID, User> getUserIdsToUsersForIds(Collection<UUID> uIdList) {
		//construct the search parameters
		Map<String, Object> equalityConditions = null;
		String equalityCondDelim = getQueryConstructionUtil().getAnd();

		Map<String, Object> greaterThanConditions = null;
		String greaterThanCondDelim = getQueryConstructionUtil().getAnd();

		Map<String, Collection<?>> inConditions = new HashMap<String, Collection<?>>();
		inConditions.put(MobstersDbTables.USER__ID, uIdList);
		String inCondDelim = getQueryConstructionUtil().getAnd();

		Map<String, Collection<?>> isConditions = null;
		String isCondDelim = null;
		String delimAcrossConditions = getQueryConstructionUtil().getAnd();

		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement)
		List<Object> values = new ArrayList<Object>();
		String cqlQuery = getQueryConstructionUtil().selectRowsQueryAllConditions(
				TABLE_NAME, equalityConditions, equalityCondDelim, greaterThanConditions,
				greaterThanCondDelim, isConditions, isCondDelim, inConditions,
				inCondDelim, delimAcrossConditions, values);
		List<User> uList = getUserEntityManager().get().find(cqlQuery);
		
		//mapify the list of users with the key being the userId
		Map<UUID, User> retVal = new HashMap<UUID, User>();
		for (User u : uList) {
			UUID uId = u.getId();
			retVal.put(uId, u);
		}
		return retVal;
	}
	
	//INSERT STUFF****************************************************************
	
	
	//SAVING STUFF****************************************************************
	@Override
	public void saveUser(User u) {
		getUserEntityManager().get().put(u);
	}
	
	@Override
	public void saveUsers(Collection<User> uCollection) {
		getUserEntityManager().get().put(uCollection);
	}

	//UPDATING STUFF****************************************************************
	@Override
	public void updateUserResources(User u, int gemChange, int oilChange, int cashChange) {
		//if no change in resources, do nothing
		if (0 == gemChange && 0 == oilChange && 0 == cashChange) {
			return;
		}

		if (gemChange != 0) {
			int newGems = u.getGems() + gemChange;
			if (newGems < 0) {
				log.error("gemChange is more than what the user has. gemChange=" +
						gemChange + "\t userGems=" + u.getGems() +
						"\t will set user's gems to 0.");
				newGems = 0;
			}
			u.setGems(newGems);
		}

		if (oilChange != 0) {
			int newOil = u.getOil() + oilChange;
			if (newOil < 0) {
				log.error("oilChange is more than what the user has. oilChange=" +
						oilChange + "\t userOil=" + u.getOil() +
						"\t will set user's oil to 0.");
				newOil = 0;
			}
			u.setOil(newOil);
		}

		if (cashChange != 0) {
			int newCash = u.getCash() + cashChange;
			if (newCash < 0) {
				log.error("cashChange is more than what the user has. cashChange=" +
						cashChange + "\t userCash=" + u.getCash() +
						"\t will set user's cash to 0.");
				newCash = 0;
			}
			u.setCash(newCash);
		}
		saveUser(u);
	}

	@Override
	public void updateDeviceToken(User u, String deviceToken) {
		u.setDeviceToken(deviceToken);
		saveUser(u);
	}
	
	@Override
	public void updateFacebookId(User u, String fbId) {
		u.setFacebookId(fbId);
		saveUser(u);
	}
	
	
	//setters and getters for the Setter Dependency Injection (or something)****************************************************************
	@Override
	public UserEntityManager getUserEntityManager() {
		return userEntityManager;
	}

	@Override
	public void setUserEntityManager(UserEntityManager userEntityManager) {
		this.userEntityManager = userEntityManager;
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