package com.lvl6.mobsters.entitymanager.staticdata.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.staticdata.EventPersistentEntityManager;
import com.lvl6.mobsters.po.staticdata.EventPersistent;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

@Component public class EventPersistentRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private static Map<Integer, EventPersistent> eventIdToEvent;
	
	private  final String TABLE_NAME = MobstersDbTables.TABLE_EVENT_PERSISTENT;

	@Autowired
	protected EventPersistentEntityManager eventPersistentEntityManager;

	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;
	

	public Map<Integer, EventPersistent> getAllEventIdsToEvents() {
		if (null == eventIdToEvent) {
			setStaticEventIdsToEvents();
		}

		return eventIdToEvent;
	}

	public EventPersistent getEventById(int id) {
		if (null == eventIdToEvent) {
			setStaticEventIdsToEvents();
		}
		EventPersistent ep = eventIdToEvent.get(id); 
		if (null == ep) {
			log.error("No EventPersistent for id=" + id);
		}
		return ep;
	}
	
	private void setStaticEventIdsToEvents() {
		log.debug("setting map of event ids to events");

		Map<String, Object> equalityConditions = null;
		String conditionDelimiter = getQueryConstructionUtil().getAnd();
		
		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement) 
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		String cqlquery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
				TABLE_NAME, equalityConditions, conditionDelimiter, values, preparedStatement);
		List<EventPersistent> list = getEventPersistentEntityManager().get().find(cqlquery);
		eventIdToEvent = new HashMap<Integer, EventPersistent>();
		for(EventPersistent ep : list) {
			Integer id= ep.getId();
			
			String dayOfWeek = ep.getDayOfWeek();
			String newDayOfWeek = dayOfWeek.trim().toUpperCase(Locale.ENGLISH);
			if (!dayOfWeek.equals(newDayOfWeek)) {
				log.error("DayOfWeek incorrectly set for EventPersistent. event=" + ep);
			}
			ep.setDayOfWeek(newDayOfWeek);
			
			String eventType = ep.getEventType();
			String newEventType = eventType.trim().toUpperCase();
			if (!eventType.equals(newEventType)) {
				log.error("EventType incorrectly set for EventPersistent. event=" + ep);
			}
			ep.setEventType(newEventType);
			
			String monsterElem = ep.getMonsterElement();
			String newMonsterElem = monsterElem.trim().toUpperCase();
			if (!monsterElem.equals(newMonsterElem)) {
				log.error("MonsterElement incorrectly set for EventPersistent. event=" + ep);
			}
			
			eventIdToEvent.put(id, ep);
		}		
	}
	
	public void reload() {
		setStaticEventIdsToEvents();
	}
	
	
	
	public EventPersistentEntityManager getEventPersistentEntityManager() {
		return eventPersistentEntityManager;
	}

	public void setEventPersistentEntityManager(
			EventPersistentEntityManager eventPersistentEntityManager) {
		this.eventPersistentEntityManager = eventPersistentEntityManager;
	}

	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}

	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}
}
