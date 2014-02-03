package com.lvl6.mobsters.services.monsterevolvingforuser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.nonstaticdata.MonsterEnhancingHistoryEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.MonsterEnhancingForUser;
import com.lvl6.mobsters.po.nonstaticdata.MonsterEnhancingHistory;
import com.lvl6.mobsters.po.nonstaticdata.MonsterForUser;


@Component
public class MonsterEnhancingHistoryServiceImpl implements MonsterEnhancingHistoryService {
	
//	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
//	
//	private final String TABLE_NAME = MobstersDbTables.TABLE_MONSTER_ENHANCING_HISTORY;
	
	@Autowired
	protected MonsterEnhancingHistoryEntityManager monsterEnhancingHistoryEntityManager;
	

	
	//CONTROLLER LOGIC STUFF****************************************************************
	

	//RETRIEVING STUFF****************************************************************
	
	
	//INSERTING STUFF****************************************************************
	@Override
	public void insertEnhancingHistory(UUID uId, Date timeOfEntry, int prevExp,
			Map<UUID, MonsterEnhancingForUser> inEnhancing, Collection<UUID> finishedMfuIds,
			Map<UUID, MonsterForUser> idsToUserMonsters, UUID enhancingBaseMfuId,
			boolean enhancingCancelled) {
		
		//for now (12/28/13) don't record cancelled enhancements
		if (enhancingCancelled) {
			return;
		}
		
		List<MonsterEnhancingHistory> saveMe = new ArrayList<MonsterEnhancingHistory>();
		
		MonsterForUser baseMonster = idsToUserMonsters.get(enhancingBaseMfuId);
		MonsterEnhancingForUser baseEnhancingMonster = inEnhancing.get(enhancingBaseMfuId);
		
		//create MonsterEnhancingHistory object for base monster
		UUID enhancingBaseMefuId = baseEnhancingMonster.getId();
		MonsterEnhancingHistory meh = createMonsterEnhancingHistory(baseMonster,
				baseEnhancingMonster, enhancingBaseMefuId, timeOfEntry, prevExp, uId,
				enhancingCancelled);
		saveMe.add(meh);
		
		//create MonserEnhancingHistory object for feeder monsters
		for (UUID mfuId : finishedMfuIds) {
			MonsterForUser mfu = idsToUserMonsters.get(mfuId);
			MonsterEnhancingForUser mefu = inEnhancing.get(mfuId);
			
			prevExp = mfu.getCurrentExp();
			
			meh = createMonsterEnhancingHistory(mfu, mefu, enhancingBaseMefuId,
					timeOfEntry, prevExp, uId, enhancingCancelled);
			saveMe.add(meh);
		}
		
		saveUserMonsters(saveMe);
	}
	
	
	//SAVING STUFF****************************************************************
	
	private MonsterEnhancingHistory createMonsterEnhancingHistory(MonsterForUser mfu,
			MonsterEnhancingForUser mefu, UUID enhancingBaseMefuId, Date timeOfEntry,
			int prevExp, UUID userId, boolean enhancingCancelled) {
		MonsterEnhancingHistory meh = new MonsterEnhancingHistory();
		meh.setUserId(userId);
		meh.setMonsterEnhancingForUserId(mefu.getId());
		meh.setBaseEnhancingId(enhancingBaseMefuId);
		meh.setTimeOfEntry(timeOfEntry);
		meh.setEnhancingStartTime(mefu.getExpectedStartTime());
		meh.setMonsterForUserId(mfu.getId());
		meh.setCurExp(mfu.getCurrentExp());
		meh.setPrevExp(prevExp);
		meh.setEnhancingCancelled(enhancingCancelled);
		
		return meh;
	}
	
	@Override
	public void saveUserMonsters(List<MonsterEnhancingHistory> mehList) {
		getMonsterEnhancingHistoryEntityManager().get().put(mehList);
	}
	
	
	//UPDATING STUFF****************************************************************
	
	
	// DELETING STUFF****************************************************************
	
	
	
	//for the setter dependency injection or something****************************************************************
	@Override
	public MonsterEnhancingHistoryEntityManager getMonsterEnhancingHistoryEntityManager() {
		return monsterEnhancingHistoryEntityManager;
	}
	@Override
	public void setMonsterEnhancingHistoryEntityManager(
			MonsterEnhancingHistoryEntityManager monsterEnhancingHistoryEntityManager) {
		this.monsterEnhancingHistoryEntityManager = monsterEnhancingHistoryEntityManager;
	}

}
