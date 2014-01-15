package com.lvl6.mobsters.entitymanager.staticdata.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.staticdata.AlertOnStartupEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.po.staticdata.AlertOnStartup;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

@Component public class AlertOnStartupRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private List<AlertOnStartup> notices;
	private User adminChatUser;

	private final String TABLE_NAME = MobstersDbTables.TABLE_ALERT_ON_STARTUP;

	@Autowired
	private AlertOnStartupEntityManager alertOnStartupEntityManager;

	@Autowired
	private QueryConstructionUtil queryConstructionUtil;


//	public User getAdminChatUser() {
//		log.debug("retrieving adminChatUserProto");
//		if (null == adminChatUser) {
//			setStaticAdminChatUser();
//		}
//
//		return adminChatUser;
//	}
//
//	private static void setStaticAdminChatUser() {
//		User adminChatUserTemp = RetrieveUtils.userRetrieveUtils().getUserById(
//				ControllerConstants.STARTUP__ADMIN_CHAT_USER_ID);
//		adminChatUser = adminChatUserTemp;
//
//	}

	public List<AlertOnStartup> getAllActiveAlerts() {
		log.debug("retrieving all alerts placed in a set");
		if (null == notices) {
			setStaticActiveAlerts();
		}
		return notices;
	}
	
	public void setStaticActiveAlerts() {

		//construct the search parameters
		Map<String, Object> equalityConditions = new HashMap<String, Object>();
		equalityConditions.put(MobstersDbTables.ALERT_ON_STARTUP__IS_ACTIVE, true);
		String conditionDelimiter = getQueryConstructionUtil().getAnd();

		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement) 
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		String cqlquery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
				TABLE_NAME, equalityConditions, conditionDelimiter, values, preparedStatement);
		notices = getAlertOnStartupEntityManager().get().find(cqlquery);
	}



	public  void reload() {
//		setStaticAdminChatUser
		setStaticActiveAlerts();
	}



	public AlertOnStartupEntityManager getAlertOnStartupEntityManager() {
		return alertOnStartupEntityManager;
	}

	public void setAlertOnStartupEntityManager(
			AlertOnStartupEntityManager alertOnStartupEntityManager) {
		this.alertOnStartupEntityManager = alertOnStartupEntityManager;
	}

	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}

	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}

}
