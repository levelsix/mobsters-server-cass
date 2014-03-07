package com.lvl6.mobsters.services.eventpersistentforuser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.nonstaticdata.EventPersistentForUserEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.EventPersistentForUser;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

@Component
public class EventPersistentForUserServiceImpl implements EventPersistentForUserService {
	
	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private final String TABLE_NAME = MobstersDbTables.TABLE_EXPANSION_PURCHASE_FOR_USER;
	  
	@Autowired
	protected EventPersistentForUserEntityManager eventPersistentForUserEntityManager;

	@Autowired
	private QueryConstructionUtil queryConstructionUtil;
	

	//CONTROLLER LOGIC STUFF****************************************************************
	@Override
	public void orderUserPersistentEvent(List<EventPersistentForUser> events) {
		Collections.sort(events, new Comparator<EventPersistentForUser>() {
	  		@Override
	  		public int compare(EventPersistentForUser lhs, EventPersistentForUser rhs) {
	  			//sorting by accept time, which should not be null
	  			Date lhsDate = lhs.getTimeOfEntry();
	  			Date rhsDate = rhs.getTimeOfEntry();

	  			if (null == lhsDate && null == rhsDate) 
	  				return 0;
	  			else if (null == lhsDate) 
	  				return -1;
	  			else if (null == rhsDate) 
	  				return 1;
	  			else if (lhsDate.getTime() < rhsDate.getTime())
	  				return -1;
	  			else if (lhsDate.getTime() == rhsDate.getTime())
	  				return 0;
	  			else
	  				return 1;
	  		}
	  	});
	}
	
	private List<UUID> getEventPersistentForUserIds(List<EventPersistentForUser> epfuList) {
		List<UUID> idList = new ArrayList<UUID>();
		
		for (EventPersistentForUser epfu : epfuList) {
			idList.add(epfu.getId());
		}
		return idList;
	}
	
	
	//RETRIEVING STUFF****************************************************************
	@Override
	public EventPersistentForUser getUserPersistentEventForUserAndEventId(UUID userId,
			int eventId) {
		log.debug("retrieving event persistent for userId " + userId);

		//construct the search parameters
		Map<String, Object> equalityConditions = new HashMap<String, Object>();
		equalityConditions.put(MobstersDbTables.EVENT_PERSISTENT_FOR_USER__USER_ID, userId);
		equalityConditions.put(MobstersDbTables.EVENT_PERSISTENT_FOR_USER__EVENT_PERSISTENT_ID, eventId);
		String conditionDelimiter = getQueryConstructionUtil().getAnd();

		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement)
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		String cqlquery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
				TABLE_NAME, equalityConditions, conditionDelimiter, values, preparedStatement); 
		List<EventPersistentForUser> epfuList = getEventPersistentForUserEntityManager()
				.get().find(cqlquery);
		EventPersistentForUser epfu = null;
		//should just be one
		if (null != epfuList && !epfuList.isEmpty()) {
			if (epfuList.size() > 1) {
				//shouldn't ever happen, but be safe
				log.error("EventPersistentForUser more than one. events=" + epfuList +
						"\t deleting all but most recent event");
				orderUserPersistentEvent(epfuList);
				
				int index = epfuList.size() - 1;
				epfu = epfuList.remove(index);
				
				//delete extra stuff
				List<UUID> idList = getEventPersistentForUserIds(epfuList);
				deleteEventPersistentsForUser(idList);
				
			} else {
				epfu = epfuList.get(0);
			}
		}
		return epfu;
	}
	
	@Override
	public List<EventPersistentForUser> getUserPersistentEventsForUserId(UUID userId) {
		log.debug("retrieving event persistents for userId: " + userId);
		
		//construct the search parameters
		Map<String, Object> equalityConditions = new HashMap<String, Object>();
		equalityConditions.put(MobstersDbTables.EVENT_PERSISTENT_FOR_USER__USER_ID, userId);
		String conditionDelimiter = getQueryConstructionUtil().getAnd();

		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement)
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		String cqlquery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
				TABLE_NAME, equalityConditions, conditionDelimiter, values, preparedStatement); 
		List<EventPersistentForUser> epfuList = getEventPersistentForUserEntityManager()
				.get().find(cqlquery);
		
		return epfuList;
	}
	
	
	//INSERTING STUFF****************************************************************
	@Override
	public EventPersistentForUser createOrUpdateUserEventPersistentForUser(UUID userId,
			int eventPersistentId, Date now) {
		EventPersistentForUser epfu =  getUserPersistentEventForUserAndEventId(userId,
				eventPersistentId);
		
		if (null == epfu) {
			epfu = new EventPersistentForUser();
			epfu.setUserId(userId);
		}
		epfu.setEventPersistentId(eventPersistentId);
		epfu.setTimeOfEntry(now);
		log.info("saving new eventPersistent for user. epfu=" + epfu);
		saveEventPersistentForUser(epfu);
		
		return epfu;
	}
	
	//SAVING STUFF****************************************************************
	@Override
	public void saveEventPersistentForUser(EventPersistentForUser qfu) {
		getEventPersistentForUserEntityManager().get().put(qfu);
	}
	
	@Override
	public void saveEventPersistentsForUser(Collection<EventPersistentForUser> qfuList) {
		getEventPersistentForUserEntityManager().get().put(qfuList);
	}

	//UPDATING STUFF****************************************************************
	
	
	//DELETING STUFF****************************************************************
	public void deleteEventPersistentsForUser(List<UUID> events) {
		
	}
	
	
	
	//for the setter dependency injection or something****************************************************************
	@Override
	public EventPersistentForUserEntityManager getEventPersistentForUserEntityManager() {
		return eventPersistentForUserEntityManager;
	}
	@Override
	public void setEventPersistentForUserEntityManager(
			EventPersistentForUserEntityManager eventPersistentForUserEntityManager) {
		this.eventPersistentForUserEntityManager = eventPersistentForUserEntityManager;
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
