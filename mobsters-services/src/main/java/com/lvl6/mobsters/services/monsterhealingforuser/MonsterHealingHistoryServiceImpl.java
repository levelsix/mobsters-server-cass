package com.lvl6.mobsters.services.monsterhealingforuser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.nonstaticdata.MonsterHealingHistoryEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.MonsterForUser;
import com.lvl6.mobsters.po.nonstaticdata.MonsterHealingForUser;
import com.lvl6.mobsters.po.nonstaticdata.MonsterHealingHistory;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.utils.QueryConstructionUtil;


@Component
public class MonsterHealingHistoryServiceImpl implements MonsterHealingHistoryService {
	
	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	private final String TABLE_NAME = MobstersDbTables.TABLE_MONSTER_HEALING_HISTORY;
	
	@Autowired
	protected MonsterHealingHistoryEntityManager monsterHealingHistoryEntityManager;

	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;
	
	
	//CONTROLLER LOGIC STUFF****************************************************************
	

	//RETRIEVING STUFF****************************************************************
	
	//INSERTING STUFF****************************************************************
	@Override
	public void insertHealingHistory(UUID uId, Date timeOfEntry, Map<UUID, Integer> prevHps,
			Map<UUID, MonsterHealingForUser> inHealing, Collection<UUID> finishedMfuIds,
			Map<UUID, MonsterForUser> idsToUserMonsters, boolean healingCancelled) {
		
		//for now (12/28/13) don't record cancelled enhancements
		if (healingCancelled) {
			return;
		}
		
		List<MonsterHealingHistory> saveMe = new ArrayList<MonsterHealingHistory>();
		
		//create MonsterHealingHistory objects for monsters being healed
		for (UUID mfuId : finishedMfuIds) {
			MonsterForUser mfu = idsToUserMonsters.get(mfuId);
			MonsterHealingForUser mhfu = inHealing.get(mfuId);
			int prevHp = prevHps.get(mfuId);
			
			MonsterHealingHistory mhh = createMonsterHealingHistory(mfu, mhfu,
					timeOfEntry, prevHp, uId, healingCancelled);
			saveMe.add(mhh);
		}
		
		saveUserMonsterHealing(saveMe);
	}
	
	//SAVING STUFF****************************************************************
	private MonsterHealingHistory createMonsterHealingHistory (MonsterForUser mfu,
			MonsterHealingForUser mhfu, Date timeOfEntry, int prevHp, UUID userId,
			boolean healingCancelled) {
		MonsterHealingHistory mhh = new MonsterHealingHistory();
		mhh.setUserId(userId);
		UUID monsterForUserId = mfu.getId();
		mhh.setMonsterForUserId(monsterForUserId);
		
		int monsterId = mfu.getMonsterId();
		mhh.setMonsterId(monsterId);
		mhh.setTimeOfEntry(timeOfEntry);
		
		Date healingStartTime = mhfu.getExpectedStartTime();
		mhh.setHealingStartTime(healingStartTime);
		
		UUID userStructHospitalId = mhfu.getUserStructHospitalId();
		mhh.setUserStructHospitalId(userStructHospitalId);
		
		int curHealth = mfu.getCurrentHealth();
		mhh.setCurHealth(curHealth);
		mhh.setPrevHealth(prevHp);
		mhh.setHealingCancelled(healingCancelled);
		
		return mhh;
	}
	
	@Override
	public void saveUserMonsterHealing(Collection<MonsterHealingHistory> mhfuList) {
		getMonsterHealingHistoryEntityManager().get().put(mhfuList);
	}
	
	
	//UPDATING STUFF****************************************************************
	
	
	// DELETING STUFF****************************************************************
	
	
	
	//for the setter dependency injection or something****************************************************************
	@Override
	public MonsterHealingHistoryEntityManager getMonsterHealingHistoryEntityManager() {
		return monsterHealingHistoryEntityManager;
	}
	@Override
	public void setMonsterHealingHistoryEntityManager(
			MonsterHealingHistoryEntityManager monsterHealingHistoryEntityManager) {
		this.monsterHealingHistoryEntityManager = monsterHealingHistoryEntityManager;
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
