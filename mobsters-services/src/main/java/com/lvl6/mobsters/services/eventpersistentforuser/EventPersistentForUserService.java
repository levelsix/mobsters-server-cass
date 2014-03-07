package com.lvl6.mobsters.services.eventpersistentforuser;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.lvl6.mobsters.entitymanager.nonstaticdata.EventPersistentForUserEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.EventPersistentForUser;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

public interface EventPersistentForUserService {
	
	//CONTROLLER LOGIC STUFF****************************************************************
	public void orderUserPersistentEvent(List<EventPersistentForUser> events);
		
	//RETRIEVING STUFF****************************************************************
	public abstract EventPersistentForUser getUserPersistentEventForUserAndEventId(
			UUID userId, int eventId);
	
	public abstract List<EventPersistentForUser> getUserPersistentEventsForUserId(
			UUID userId);
	
	//INSERTING STUFF****************************************************************
	public abstract EventPersistentForUser createOrUpdateUserEventPersistentForUser(UUID userId,
			int eventPersistentId, Date now);
	
	
	//SAVING STUFF****************************************************************
	public abstract void saveEventPersistentForUser(EventPersistentForUser qfu);
	
	public abstract void saveEventPersistentsForUser(Collection<EventPersistentForUser> qfuList);
	
	//UPDATING STUFF****************************************************************
	
	
	//DELETING STUFF****************************************************************
	public abstract void deleteEventPersistentsForUser(List<UUID> events);
	
	
	//for the setter dependency injection or something****************************************************************
	public abstract EventPersistentForUserEntityManager getEventPersistentForUserEntityManager();

	public abstract void setEventPersistentForUserEntityManager(
			EventPersistentForUserEntityManager expansionPurchaseForUserEntityManager);

	public abstract QueryConstructionUtil getQueryConstructionUtil(); 
	
	public abstract void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil);
	
}
