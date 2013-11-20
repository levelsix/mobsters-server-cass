package com.lvl6.mobsters.controller.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.MinimumUserMonsterSellProto;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.UserEnhancementItemProto;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.UserMonsterCurrentHealthProto;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.UserMonsterHealingProto;
import com.lvl6.mobsters.po.nonstaticdata.MonsterEnhancingForUser;
import com.lvl6.mobsters.po.nonstaticdata.MonsterHealingForUser;


public class MonsterStuffUtils {
	
	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	//extract and return the ids from the UserMonsterCurrentHealthProtos, also
	//return mapping of userMonsterIdToExpectedHealth
  public static List<UUID> getUserMonsterIds(List<UserMonsterCurrentHealthProto> umchpList,
  		Map<UUID, Integer> userMonsterIdToExpectedHealth) {
  	List<UUID> idList = new ArrayList<UUID>();
  	
  	if (null == umchpList) {
  		return idList;
  	}
  	
  	for(UserMonsterCurrentHealthProto umchp : umchpList) {
  		String idStr = umchp.getUserMonsterUuid();
  		UUID id = UUID.fromString(idStr);
  		
  		idList.add(id);
  		int health = umchp.getCurrentHealth();
  		userMonsterIdToExpectedHealth.put(id, health);
  	}
  	return idList;
  }
  
  public static List<UUID> getUserMonsterIds(List<FullUserMonsterProto> mfuList) {
  	List<UUID> idList = new ArrayList<UUID>();
  	
  	if (null == mfuList) {
  		return idList;
  	}
  	
  	for (FullUserMonsterProto fump : mfuList) {
  		String idStr = fump.getUserMonsterUuid();
  		UUID id = UUID.fromString(idStr);
  		
  		idList.add(id);
  	}
  	return idList;
  }
  
  //transforming list to map with key = monsterForUserId.
  public static Map<UUID, UserMonsterHealingProto> convertIntoUserMonsterIdToUmhpProtoMap(
  		List<UserMonsterHealingProto> umhpList) {
  	Map<UUID, UserMonsterHealingProto> returnMap = new HashMap<UUID, UserMonsterHealingProto>();
  	if (null == umhpList) {
  		return returnMap;
  	}
  	for (UserMonsterHealingProto umhp : umhpList) {
  		String idStr = umhp.getUserMonsterUuid();
  		UUID id = UUID.fromString(idStr);
  		returnMap.put(id, umhp);
  	}
  	
  	return returnMap;
  }
  
  public static Map<UUID, UserEnhancementItemProto> convertIntoUserMonsterIdToUeipProtoMap(
  		List<UserEnhancementItemProto> ueipList) {
  	Map<UUID, UserEnhancementItemProto> returnMap = new HashMap<UUID, UserEnhancementItemProto>();
  	if(null == ueipList) {
  		return returnMap;
  	}
  	for (UserEnhancementItemProto ueip : ueipList) {
  		String idStr = ueip.getUserMonsterUuid();
  		UUID id = UUID.fromString(idStr);
  		returnMap.put(id, ueip);
  	}
  	
  	return returnMap;
  }
  
  /*
   * selected monsters (the second argument) might be modified
   */
  public static void retainValidMonsters(Set<Long> domain,  Map<Long, ?> selectedMonsters,
  		boolean keepThingsInDomain, boolean keepThingsNotInDomain) {
  	Set<Long> selectedIds = selectedMonsters.keySet();
  	selectedIds = new HashSet<Long>(selectedIds);
  	
  	for (Long selectedId : selectedIds) {
  		if (domain.contains(selectedId) && keepThingsInDomain) {
  			continue;
  		}
  		if (!domain.contains(selectedId) && keepThingsNotInDomain) {
  			continue;
  		}
  		//since selectedId isn't in the domain and want to keep things in domain
  		//or is in the domain and want to keep things not in domain, remove it
  		Object umhp = selectedMonsters.remove(selectedId);
  		log.warn("Not retaining. object=" + umhp + "; keepThingsInDomain=" + keepThingsInDomain +
  				"; keepThingsNotInDomain=" + keepThingsNotInDomain);
  	}
  }
  
  /*
   * selected monsters (the second argument) might be modified
   */
  public static void retainValidMonsterIds(Set<Long> existing, List<Long> ids) {
//  	ids.add(123456789L);
//  	log.info("existing=" + existing + "\t ids=" + ids);
  	
  	List<Long> copyIds = new ArrayList<Long>(ids);
  	// remove the invalid ids from ids client sent 
  	// (modifying argument so calling function doesn't have to do it)
  	ids.retainAll(existing);
  	
  	if (copyIds.size() != ids.size()) {
  		//client asked for invalid ids
  		log.warn("client asked for some invalid ids. asked for ids=" + copyIds + 
  				"\t existingIds=" + existing + "\t remainingIds after purge =" + ids);
  	}
  }

  public static List<MonsterHealingForUser> convertToMonsterHealingForUser(
  		UUID userId, Map<UUID, UserMonsterHealingProto> protos) {
  	
  	List<MonsterHealingForUser> nonProtos = new ArrayList<MonsterHealingForUser>();
  	
  	for(UserMonsterHealingProto umhp: protos.values()) {
  		String monsterForUserIdStr = umhp.getUserMonsterUuid();
  		UUID monsterForUserId = UUID.fromString(monsterForUserIdStr);
  		
  		Date expectedStartTime = new Date(umhp.getExpectedStartTimeMillis());
//  		Date queuedTime = new Date(umhp.getQueuedTimeMillis());
  		MonsterHealingForUser mhfu = new MonsterHealingForUser();
  		mhfu.setUserId(userId);
  		mhfu.setMonsterForUserId(monsterForUserId);
  		mhfu.setExpectedStartTime(expectedStartTime);//, queuedTime);
  		nonProtos.add(mhfu);
  	}
  	
  	return nonProtos;
  }
  
  public static List<MonsterEnhancingForUser> convertToMonsterEnhancingForUser(
  		UUID userId, Map<UUID, UserEnhancementItemProto> protos) {
  	
  	List<MonsterEnhancingForUser> nonProtos = new ArrayList<MonsterEnhancingForUser>();
  	
  	for(UserEnhancementItemProto ueip: protos.values()) {
  		String monsterForUserIdStr = ueip.getUserMonsterUuid();
  		UUID monsterForUserId = UUID.fromString(monsterForUserIdStr);
  		
  		long startTimeMillis = ueip.getExpectedStartTimeMillis();
  		Date expectedStartTime;
  		
  		if (!ueip.hasExpectedStartTimeMillis() || startTimeMillis <= 0) {
  			expectedStartTime = null;
  		} else {
  			expectedStartTime = new Date(startTimeMillis);
  		}
//  		Date queuedTime = new Date(umhp.getQueuedTimeMillis());
  		MonsterEnhancingForUser mefu = new MonsterEnhancingForUser();
  		mefu.setUserId(userId);
  		mefu.setMonsterForUserId(monsterForUserId);
  		mefu.setExpectedStartTime(expectedStartTime);//, queuedTime);
  		nonProtos.add(mefu);
  	}
  	
  	return nonProtos;
  }

  public static Map<UUID, Integer> convertToMonsterForUserIdToCashAmount(
  		List<MinimumUserMonsterSellProto> userMonsters) {
  	Map<UUID, Integer> idToCashAmount = new HashMap<UUID, Integer>();
  	
  	for (MinimumUserMonsterSellProto mumsp : userMonsters) {
  		String userMonsterIdStr = mumsp.getUserMonsterUuid();
  		UUID userMonsterId = UUID.fromString(userMonsterIdStr);
  		
  		int cashAmount = mumsp.getCashAmount();
  		
  		idToCashAmount.put(userMonsterId, cashAmount);
  	}
  	
  	return idToCashAmount;
  }

//  //non cassandra mobsters stuff
//  public static MonsterForUser createNewUserMonster(int userId, int numPieces,
//  		Monster monster, Date now, boolean isComplete) {
//  	int id = 0;
//  	int monsterId = monster.getId();
//  	int currentExp = 0;
//  	int currentLvl = 1;
//  	int currentHealth = monster.getBaseHp();
//  	int teamSlotNum = 0;
//  	String sourceOfPieces = "";
//  	MonsterForUser mfu = new MonsterForUser(id, userId, monsterId, currentExp,
//  			currentLvl, currentHealth, numPieces, isComplete, now,
//  			teamSlotNum, sourceOfPieces);
//  	
//  	return mfu;
//  }
}