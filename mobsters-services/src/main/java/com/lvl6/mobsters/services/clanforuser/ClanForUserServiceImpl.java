package com.lvl6.mobsters.services.clanforuser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.nonstaticdata.ClanForUserEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.ClanForUser;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

@Component
public class ClanForUserServiceImpl implements ClanForUserService {
	
		
	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	private final String TABLE_NAME = MobstersDbTables.TABLE_CLAN_FOR_USER;
	
	@Autowired
	protected ClanForUserEntityManager clanForUserEntityManager;
	
	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;
	

	//CONTROLLER LOGIC STUFF****************************************************************
	@Override
	public Set<UUID> getUserIdsFromUserClans(Collection<ClanForUser> userClans) {
		Set<UUID> userIds = new HashSet<UUID>();
		
		for (ClanForUser cfu : userClans) {
			UUID userId = cfu.getUserId();
			userIds.add(userId);
		}
		
		return userIds;
	}
	
	@Override
	public Set<UUID> getUserIdsForStatuses(Collection<ClanForUser> userClans,
			Set<String> statuses) {
		Set<UUID> userIds = new HashSet<UUID>();
		
		for (ClanForUser uc : userClans) {
			UUID userId = uc.getUserId();
			
			if (!statuses.contains(uc.getStatus())) {
				continue;
			}
			userIds.add(userId);
		}
		
		return userIds;
	}
	
	@Override
	public ClanForUser getClanForUserForUserId(UUID userId, List<ClanForUser> cfuList) {
		for(ClanForUser cfu : cfuList) {
			UUID cfuUserId = cfu.getUserId();
			
			if (userId.equals(cfuUserId)) {
				return cfu;
			}
		}
		return null;
	}


	//RETRIEVING STUFF****************************************************************
	@Override
	public ClanForUser getSpecificClanForUserWithId(UUID userClanId) {
		log.debug("retrieving clan with id " + userClanId);
		
	    ClanForUser ccp = getClanForUserEntityManager().get().get(userClanId);
	    return ccp;
	}
	
	@Override
	public List<ClanForUser> getAllUserClansForUser(UUID userId) {
		//construct the search parameters
		Map<String, Object> equalityConditions = new HashMap<String, Object>();
		equalityConditions.put(MobstersDbTables.CLAN_FOR_USER__USER_ID, userId);
		String conditionDelimiter = getQueryConstructionUtil().getOr();

		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement)
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		boolean allowFiltering = true; //need cassandra to query with non row keys
		String cqlQuery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
				TABLE_NAME, equalityConditions, conditionDelimiter, values,
				preparedStatement, allowFiltering);
		List<ClanForUser> cfuList = getClanForUserEntityManager().get().find(cqlQuery);
		
		return cfuList;
	}
	
	@Override
	public Map<UUID, ClanForUser> getSpecificOrAllUserClansForClan(UUID clanId, List<UUID> userIds) {
		log.debug("retrieving user clan info for clan " + clanId + "\t userIds=" + userIds);
		//construct the search parameters
		Map<String, Object> equalityConditions = new HashMap<String, Object>();
		equalityConditions.put(MobstersDbTables.CLAN_FOR_USER__CLAN_ID, clanId);
		String equalityCondDelim = getQueryConstructionUtil().getAnd();

		Map<String, Object> greaterThanConditions = null;
		String greaterThanCondDelim = getQueryConstructionUtil().getAnd();

		Map<String, Collection<?>> inConditions = null;
		if(null != userIds && !userIds.isEmpty()) {
			inConditions = new HashMap<String, Collection<?>>();
			inConditions.put(MobstersDbTables.CLAN_FOR_USER__USER_ID, userIds);
		}
		String inCondDelim = getQueryConstructionUtil().getAnd();

		Map<String, Collection<?>> isConditions = null;
		String isCondDelim = null;
		String delimAcrossConditions = getQueryConstructionUtil().getAnd();

		//query db, "values" is not used
		//(its purpose is to hold the values that were supposed to be put
		//into a prepared statement)
		List<Object> values = new ArrayList<Object>();
		boolean allowFiltering = true; //need cassandra to query with non row keys
		String cqlQuery = getQueryConstructionUtil().selectRowsQueryAllConditions(
				TABLE_NAME, equalityConditions, equalityCondDelim, greaterThanConditions,
				greaterThanCondDelim, inConditions, inCondDelim, isConditions, isCondDelim,
				delimAcrossConditions, values, allowFiltering);
		List<ClanForUser> cfuList = getClanForUserEntityManager().get().find(cqlQuery);
		
		Map<UUID, ClanForUser> userIdsToUserClans = new HashMap<UUID, ClanForUser>();
		for (ClanForUser cfu : cfuList) {
			UUID userId = cfu.getUserId();
			userIdsToUserClans.put(userId, cfu);
		}
		return userIdsToUserClans;
	}
	
	@Override
	public List<ClanForUser> getUserClansForStatuses(UUID clanId, Collection<String> statuses) {
		log.debug("retrieving user clan info for clan " + clanId + "\t statuses=" + statuses);
		//construct the search parameters
		Map<String, Object> equalityConditions = new HashMap<String, Object>();
		equalityConditions.put(MobstersDbTables.CLAN_FOR_USER__CLAN_ID, clanId);
		String equalityCondDelim = getQueryConstructionUtil().getAnd();

		Map<String, Object> greaterThanConditions = null;
		String greaterThanCondDelim = getQueryConstructionUtil().getAnd();

		Map<String, Collection<?>> inConditions = null;
		if(null != statuses && !statuses.isEmpty()) {
			inConditions = new HashMap<String, Collection<?>>();
			inConditions.put(MobstersDbTables.CLAN_FOR_USER__STATUS, statuses);
		}
		String inCondDelim = getQueryConstructionUtil().getAnd();

		Map<String, Collection<?>> isConditions = null;
		String isCondDelim = null;
		String delimAcrossConditions = getQueryConstructionUtil().getAnd();

		//query db, "values" is not used
		//(its purpose is to hold the values that were supposed to be put
		//into a prepared statement)
		List<Object> values = new ArrayList<Object>();
		boolean allowFiltering = true; //need cassandra to query with non row keys
		String cqlQuery = getQueryConstructionUtil().selectRowsQueryAllConditions(
				TABLE_NAME, equalityConditions, equalityCondDelim, greaterThanConditions,
				greaterThanCondDelim, inConditions, inCondDelim, isConditions, isCondDelim,
				delimAcrossConditions, values, allowFiltering);
		List<ClanForUser> cfuList = getClanForUserEntityManager().get().find(cqlQuery);
		
		return cfuList;
	}
	@Override
	public ClanForUser getUserClanForUserAndClanId(UUID userId, UUID clanId) {
		log.debug("retrieving user clan info for clan " + clanId + "\t userId=" + userId);
		//construct the search parameters
		Map<String, Object> equalityConditions = new HashMap<String, Object>();
		equalityConditions.put(MobstersDbTables.CLAN_FOR_USER__CLAN_ID, clanId);
		equalityConditions.put(MobstersDbTables.CLAN_FOR_USER__USER_ID, userId);
		String conditionDelimiter = getQueryConstructionUtil().getAnd();

		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement)
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		boolean allowFiltering = true; //need cassandra to query with non row keys
		String cqlQuery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
				TABLE_NAME, equalityConditions, conditionDelimiter, values,
				preparedStatement, allowFiltering);
		List<ClanForUser> cfuList = getClanForUserEntityManager().get().find(cqlQuery);

		if (null != cfuList && !cfuList.isEmpty()) {
			if (1 == cfuList.size()) {
				return cfuList.get(0);
			}
			log.error("user clan entries total more than one. entries=" + cfuList);
		}
		//no entries in this clan for this user
		return null;
	}
	

	//INSERTING STUFF****************************************************************
	@Override
	public ClanForUser insertClanForUser(UUID userId, UUID clanId, String status,
			Date timeOfEntry) {

		ClanForUser ccp = new ClanForUser();
		return ccp;
	}


	//SAVING STUFF****************************************************************
	@Override
	public void saveClanForUser(ClanForUser cfu) {
		getClanForUserEntityManager().get().put(cfu);
	}

	//UPDATING STUFF****************************************************************
	public void updateUserClanStatus(ClanForUser cfu, String newStatus) {
		cfu.setStatus(newStatus);
		saveClanForUser(cfu);
	}

	
	//DELETING STUFF****************************************************************
	@Override
	public void deleteUserClans(List<ClanForUser> cfuList) {
		List<UUID> cfuIdList = new ArrayList<UUID>();
		
		for (ClanForUser cfu : cfuList) {
			UUID cfuId = cfu.getId();
			cfuIdList.add(cfuId);
		}
		
		if (!cfuIdList.isEmpty()) {
			getClanForUserEntityManager().get().delete(cfuIdList);
		}
	}

	@Override
	public void deleteUserClan(ClanForUser cfu) {
		UUID id = cfu.getId();
		getClanForUserEntityManager().get().delete(id);
	}
	
	@Override
	public void deleteUserClansForUserProspective(UUID userId) {
		ClanForUser cfu = new ClanForUser();
		cfu.setId(null);
		cfu.setUserId(userId);
		getClanForUserEntityManager().get().remove(cfu);
	}
	

	//for the setter dependency injection or something
	@Override
	public ClanForUserEntityManager getClanForUserEntityManager() {
		return clanForUserEntityManager;
	}
	@Override
	public void setClanForUserEntityManager(
			ClanForUserEntityManager clanForUserEntityManager) {
		this.clanForUserEntityManager = clanForUserEntityManager;
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
