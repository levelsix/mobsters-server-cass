
package com.lvl6.mobsters.entitymanager.staticdata.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.staticdata.UserBannedEntityManager;
import com.lvl6.mobsters.po.staticdata.UserBanned;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

@Component public class UserBannedRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private Map<UUID, UserBanned> userIdsToBannedUsers;

	private final String TABLE_NAME = MobstersDbTables.TABLE_USER_BANNED;
	
	@Autowired
	private UserBannedEntityManager userBannedEntityManager;

	@Autowired
	private QueryConstructionUtil queryConstructionUtil;
	
	
	public Set<UUID> getAllBannedUserIds() {
		log.debug("setting  map of user ids to banned users");
		if (null == userIdsToBannedUsers) {
			setStaticBannedUsers();
		}
		return userIdsToBannedUsers.keySet();
	}

	private void setStaticBannedUsers() {
		log.debug("setting static set of banned users");

		//construct the search parameters
		Map<String, Object> equalityConditions = null;
		String conditionDelimiter = getQueryConstructionUtil().getAnd();
		
		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement) 
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		String cqlquery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
				TABLE_NAME, equalityConditions, conditionDelimiter, values, preparedStatement);
		List<UserBanned> userBannedList = getUserBannedEntityManager().get().find(cqlquery);

		//fill up the map
		userIdsToBannedUsers = new HashMap<UUID, UserBanned>();
		for(UserBanned ub : userBannedList) {
			UUID userId = ub.getUserId();
			userIdsToBannedUsers.put(userId, ub);
		}
	}


	public  void reload() {
		setStaticBannedUsers();
	}
	


	public UserBannedEntityManager getUserBannedEntityManager() {
		return userBannedEntityManager;
	}

	public void setUserBannedEntityManager(
			UserBannedEntityManager userBannedEntityManager) {
		this.userBannedEntityManager = userBannedEntityManager;
	}

	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}

	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}
	
}
