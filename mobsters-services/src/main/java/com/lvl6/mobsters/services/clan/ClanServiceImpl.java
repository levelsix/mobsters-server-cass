package com.lvl6.mobsters.services.clan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.nonstaticdata.ClanEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.Clan;
import com.lvl6.mobsters.po.nonstaticdata.ClanForUser;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.services.clanforuser.ClanForUserService;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

@Component
public class ClanServiceImpl implements ClanService {
	
		
	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	private final String TABLE_NAME = MobstersDbTables.TABLE_CLAN;
	
	@Autowired
	protected ClanEntityManager clanEntityManager;
	
	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;
	
	@Autowired
	protected ClanForUserService clanForUserService;

	//CONTROLLER LOGIC STUFF****************************************************************
	


	//RETRIEVING STUFF****************************************************************
	
	
	@Override
	public Clan getClanWithId(UUID clanId) {
		log.debug("retrieving clan with id " + clanId);
		if (null == clanId) {
			return null;
		}
		
	    Clan ccp = getClanEntityManager().get().get(clanId);
	    return ccp;
	}
	
	@Override
	public Map<UUID, Clan> getClansByIds(List<UUID> clanIds) {
		log.debug("retrieving clans with ids " + clanIds);
		Map<String, Object> equalityConditions = null;
		String equalityCondDelim = getQueryConstructionUtil().getAnd();

		Map<String, Object> greaterThanConditions = null;
		String greaterThanCondDelim = getQueryConstructionUtil().getAnd();

		Map<String, Collection<?>> inConditions = null;
		if(null != clanIds && !clanIds.isEmpty()) {
			inConditions = new HashMap<String, Collection<?>>();
			inConditions.put(MobstersDbTables.CLAN__ID, clanIds);
		}
		String inCondDelim = getQueryConstructionUtil().getAnd();

		Map<String, Collection<?>> isConditions = null;
		String isCondDelim = null;
		String delimAcrossConditions = getQueryConstructionUtil().getAnd();

		//query db, "values" is not used
		//(its purpose is to hold the values that were supposed to be put
		//into a prepared statement)
		List<Object> values = new ArrayList<Object>();
		String cqlQuery = getQueryConstructionUtil().selectRowsQueryAllConditions(
				TABLE_NAME, equalityConditions, equalityCondDelim, greaterThanConditions,
				greaterThanCondDelim, isConditions, isCondDelim, inConditions,
				inCondDelim, delimAcrossConditions, values);
		List<Clan> clanList = getClanEntityManager().get().find(cqlQuery);
		
		Map<UUID, Clan> clanIdToClans = new HashMap<UUID, Clan>();
		for (Clan c :clanList) {
			UUID clanId = c.getId();
			clanIdToClans.put(clanId, c);
		}
		return clanIdToClans;
	}
	
	@Override
	public List<Clan> getClansWithSimilarNameOrTag(String name, String tag) {
		name = name.replaceAll("\\s+","");
		tag = tag.replaceAll("\\s+","");
		log.debug("retrieving clans with similar name: " + name + "\t or tag: " + tag);
		
		//construct the search parameters
		Map<String, Object> beginsWith = null;
		String beginsWithCondDelim = null;
		
		Map<String, Object> beginsAndEndsWith = new HashMap<String, Object>();
		beginsAndEndsWith.put(MobstersDbTables.CLAN__NAME, name);
		beginsAndEndsWith.put(MobstersDbTables.CLAN__TAG, tag);
		String beginsAndEndsWithCondDelim = getQueryConstructionUtil().getOr();
		
		Map<String, Object> endsWith = null;
		String endsWithCondDelim = null;
		
		String overallDelimiter = getQueryConstructionUtil().getOr();
		//query db, "values" is not used
		//(its purpose is to hold the values that were supposed to be put
		//into a prepared statement)
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		String cqlQuery = getQueryConstructionUtil().selectRowsQueryLikeConditions(
				TABLE_NAME, beginsWith, beginsWithCondDelim, beginsAndEndsWith,
				beginsAndEndsWithCondDelim, endsWith, endsWithCondDelim,
				overallDelimiter, values, preparedStatement);
		List<Clan> clanList = getClanEntityManager().get().find(cqlQuery);
		
		return clanList;
	}
	
	@Override
	public Clan getClanWithNameOrTag(String name, String tag) {
		name = name.replaceAll("\\s+","");
		tag = tag.replaceAll("\\s+","");
		log.debug("retrieving clan with name: " + name + "\t or tag: " + tag);

		//construct the search parameters
		Map<String, Object> equalityConditions = new HashMap<String, Object>();
		equalityConditions.put(MobstersDbTables.CLAN__NAME, name);
		equalityConditions.put(MobstersDbTables.CLAN__TAG, tag);
		String conditionDelimiter = getQueryConstructionUtil().getOr();

		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement)
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		String cqlQuery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
				TABLE_NAME, equalityConditions, conditionDelimiter, values, preparedStatement);
		List<Clan> clanList = getClanEntityManager().get().find(cqlQuery);

		if (null == clanList || clanList.isEmpty()) {
			return null;
		}
		
		if (clanList.size() > 1) {
			log.error("multiple clans with same name or tag. clans=" + clanList);
		}
		
		return clanList.get(0);
		
	}

	//INSERTING STUFF****************************************************************
	@Override
	public Clan insertClan(String clanName, Date createTime, String description,
			String tag, boolean requestToJoinRequired) {

		Clan clan = new Clan();
		clan.setName(clanName);
		clan.setCreateTime(createTime);
		clan.setDescription(description);
		clan.setTag(tag);
		clan.setRequestToJoinRequired(requestToJoinRequired);
		
		saveClan(clan);
		
		return clan;
	}


	//SAVING STUFF****************************************************************
	@Override
	public void saveClan(Clan c) {
		getClanEntityManager().get().put(c);
	}

	//UPDATING STUFF****************************************************************
	public void updateUserClanStatus(Clan cfu, String newStatus) {
//		cfu.setStatus(newStatus);
		saveClan(cfu);
	}

	
	//DELETING STUFF****************************************************************
	public void deleteClan(Clan c, List<ClanForUser> clanPpl) {
		UUID clanId = c.getId();
		
		getClanEntityManager().get().delete(clanId);
		
		if (null != clanPpl && !clanPpl.isEmpty()) {
			//when deleting clan need to delete all the entries relating to it
			getClanForUserService().deleteUserClans(clanPpl);
		}
	}


	//for the setter dependency injection or something
	@Override
	public ClanEntityManager getClanEntityManager() {
		return clanEntityManager;
	}
	@Override
	public void setClanEntityManager(
			ClanEntityManager clanEntityManager) {
		this.clanEntityManager = clanEntityManager;
	}

	@Override
	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}
	@Override
	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}
	
	@Override
	public ClanForUserService getClanForUserService() {
		return clanForUserService;
	}
	@Override
	public void setClanForUserService(ClanForUserService clanForUserService) {
		this.clanForUserService = clanForUserService;
	}
	
}
