package com.lvl6.mobsters.services.usercurrencyhistory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.nonstaticdata.UserCurrencyHistoryEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.po.nonstaticdata.UserCurrencyHistory;
import com.lvl6.mobsters.properties.MobstersDbTables;

@Component
public class UserCurrencyHistoryServiceImpl implements UserCurrencyHistoryService {
	
	@Autowired
	protected UserCurrencyHistoryEntityManager userCurrencyHistoryEntityManager;
	
	//CONTROLLER LOGIC****************************************************************

	
	
	//INSERTING STUFF****************************************************************
	@Override
	public UserCurrencyHistory createNewUserCurrencyHistory(User u, Date date,
			String resourceType, int delta, String reasonForChange, String details,
			boolean saveToDb) {
		
		if (0 == delta) {
			return null;
		}
		
		int before = 0;
		int after = 0;
		
		if (MobstersDbTables.USER__CASH.equals(resourceType)) {
			before = u.getCash();
			after = before + delta;
		} else if (MobstersDbTables.USER__OIL.equals(resourceType)) {
			before = u.getOil();
			after = before + delta;
		} else if (MobstersDbTables.USER__GEMS.equals(resourceType)) {
			before = u.getGems();
			after = before + delta;
		}
		
		UserCurrencyHistory uch = new UserCurrencyHistory();
		uch.setUserId(u.getId());
		uch.setDate(date);
		uch.setResourceType(resourceType);
		uch.setCurrencyChange(delta);
		uch.setCurrencyBeforeChange(before);
		uch.setCurrencyAfterChange(after);
		uch.setReasonForChange(reasonForChange);
		uch.setDetails(details);
		
		if (saveToDb) {
			saveCurrencyHistory(uch);
		}
		return uch;
	}
	
	@Override
	public List<UserCurrencyHistory> createUserCurrencyHistories(UUID userId, Date date,
			Map<String,Integer> moneyChange, Map<String, Integer> previousCurrencyMap,
	  		Map<String, Integer> currentCurrencyMap, Map<String, String> changeReasonsMap,
	  		Map<String, String> detailsMap, boolean saveToDb) {
		List<UserCurrencyHistory> newHistories = new ArrayList<UserCurrencyHistory>();

		//getting rid of changes that are 0
		Set<String> keys = new HashSet<String>(moneyChange.keySet());
		for (String key : keys) {
			Integer change = moneyChange.get(key);
			int currencyBeforeChange = previousCurrencyMap.get(key);
			int currencyAfterChange = currentCurrencyMap.get(key);
			String reasonForChange = changeReasonsMap.get(key);
			String details = detailsMap.get(key);
			if (0 == change) {
				moneyChange.remove(key);
				previousCurrencyMap.remove(key);
				currentCurrencyMap.remove(key);
				changeReasonsMap.remove(key);
				detailsMap.remove(key);
			} else {
				UserCurrencyHistory uch = new UserCurrencyHistory();
				uch.setUserId(userId);
				uch.setDate(date);
				uch.setResourceType(key);
				uch.setCurrencyChange(change);
				uch.setCurrencyBeforeChange(currencyBeforeChange);
				uch.setCurrencyAfterChange(currencyAfterChange);
				uch.setReasonForChange(reasonForChange);
				uch.setDetails(details);
				
				newHistories.add(uch);
			}
		}

		if (saveToDb) {
			saveCurrencyHistories(newHistories);
		}
		
		return newHistories;
	}
	
	
	//SAVING STUFF****************************************************************
	@Override
	public void saveCurrencyHistory(UserCurrencyHistory uch) {
		getUserCurrencyHistoryEntityManager().get().put(uch);
	}
	@Override
	public void saveCurrencyHistories(Collection<UserCurrencyHistory> uchList) {
		getUserCurrencyHistoryEntityManager().get().put(uchList);
	}
	

	//for the setter dependency injection or something****************************************************************	
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