package com.lvl6.mobsters.services.usercurrencyhistory;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.lvl6.mobsters.entitymanager.nonstaticdata.UserCurrencyHistoryEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.po.nonstaticdata.UserCurrencyHistory;

public interface UserCurrencyHistoryService {
	
	//CONTROLLER LOGIC****************************************************************
	
	
	//INSERTING STUFF****************************************************************
	public abstract UserCurrencyHistory createNewUserCurrencyHistory(User u, Date date,
			String resourceType, int delta, String reasonForChange, String details,
			boolean saveToDb);

	public abstract List<UserCurrencyHistory> createUserCurrencyHistories(UUID userId,
			Date date, Map<String,Integer> changeMap, Map<String, Integer> previousCurrencyMap,
	  		Map<String, Integer> currentCurrencyMap, Map<String, String> changeReasonsMap,
	  		Map<String, String> detailsMap, boolean saveToDb);
	
	
	//SAVING STUFF****************************************************************
	public abstract void saveCurrencyHistory(UserCurrencyHistory uch);
	
	public abstract void saveCurrencyHistories(Collection<UserCurrencyHistory> uchList);
	
	
	//for the setter dependency injection or something****************************************************************	
	public abstract UserCurrencyHistoryEntityManager getUserCurrencyHistoryEntityManager();
	
	public abstract void setUserCurrencyHistoryEntityManager(
			UserCurrencyHistoryEntityManager userCurrencyHistoryEntityManager);
}