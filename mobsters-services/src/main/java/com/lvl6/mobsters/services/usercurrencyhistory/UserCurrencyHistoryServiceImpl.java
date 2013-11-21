package com.lvl6.mobsters.services.usercurrencyhistory;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.nonstaticdata.UserCurrencyHistoryEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.po.nonstaticdata.UserCurrencyHistory;

@Component
public class UserCurrencyHistoryServiceImpl implements UserCurrencyHistoryService {
	
	@Autowired
	protected UserCurrencyHistoryEntityManager userCurrencyHistoryEntityManager;
	
	//CONTROLLER LOGIC

	@Override
	public UserCurrencyHistory createNewUserCurrencyHistory(User u, Date date,
			boolean isCash, int delta, String reasonForChange, String details) {
		
		if (0 == delta) {
			return null;
		}
		
		int before = 0;
		int after = 0;
		
		if (isCash) {
			before = u.getCash();
			after = before + delta;
		} else {
			before = u.getGems();
			after = before + delta;
		}
		
		UserCurrencyHistory uch = new UserCurrencyHistory();
		uch.setUserId(u.getId());
		uch.setDate(date);
		uch.setCash(isCash);
		uch.setCurrencyChange(delta);
		uch.setCurrencyBeforeChange(before);
		uch.setCurrencyAfterChange(after);
		uch.setReasonForChange(reasonForChange);
		uch.setDetails(details);
		return uch;
	}
	
	
	
	//INSERTING STUFF
	
	
	//SAVING STUFF
	@Override
	public void saveCurrencyHistory(UserCurrencyHistory uch) {
		getUserCurrencyHistoryEntityManager().get().put(uch);
	}
	@Override
	public void saveCurrencyHistories(Collection<UserCurrencyHistory> uchList) {
		getUserCurrencyHistoryEntityManager().get().put(uchList);
	}
	

	
	
	@Override
	public UserCurrencyHistoryEntityManager getUserCurrencyHistoryEntityManager() {
		return userCurrencyHistoryEntityManager;
	}
	@Override
	public void setUserCurrencyHistoryEntityManager(
			UserCurrencyHistoryEntityManager userCurrencyHistoryEntityManager) {
		this.userCurrencyHistoryEntityManager = userCurrencyHistoryEntityManager;
	}
	
}