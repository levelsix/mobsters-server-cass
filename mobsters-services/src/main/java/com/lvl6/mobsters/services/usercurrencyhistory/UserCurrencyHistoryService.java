package com.lvl6.mobsters.services.usercurrencyhistory;

import java.util.Collection;
import java.util.Date;

import com.lvl6.mobsters.entitymanager.nonstaticdata.UserCurrencyHistoryEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.po.nonstaticdata.UserCurrencyHistory;

public interface UserCurrencyHistoryService {
	
	//CONTROLLER LOGIC
	
	public abstract UserCurrencyHistory createNewUserCurrencyHistory(User u, Date date,
			boolean isCash, int delta, String reasonForChange, String details);
	
	//INSERTING STUFF

	
	//SAVING STUFF
	public abstract void saveCurrencyHistory(UserCurrencyHistory uch);
	
	public abstract void saveCurrencyHistories(Collection<UserCurrencyHistory> uchList);
	
	
		
	public abstract UserCurrencyHistoryEntityManager getUserCurrencyHistoryEntityManager();
	
	public abstract void setUserCurrencyHistoryEntityManager(
			UserCurrencyHistoryEntityManager userCurrencyHistoryEntityManager);
}