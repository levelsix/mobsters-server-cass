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
	public void updateUserResources(User u, int gemsChange, int oilChange, int cashChange) {

		if (gemsChange != 0) {
			int newGems = u.getGems() + gemsChange;
			u.setGems(newGems);
		}

		if (oilChange != 0) {
			int newOil = u.getOil() + oilChange;
			u.setOil(newOil);
		}

		if (cashChange != 0) {
			int newCash = u.getCash() + cashChange;
			u.setCash(newCash);
		}
		saveUser(u);
	}

	@Override
	public void updateDeviceToken(User u, String deviceToken) {
		u.setDeviceToken(deviceToken);
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