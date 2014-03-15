package com.lvl6.mobsters.controller.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.MinimumUserMonsterSellProto;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.UserEnhancementItemProto;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.UserMonsterCurrentHealthProto;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.UserMonsterHealingProto;
import com.lvl6.mobsters.po.nonstaticdata.MonsterEnhancingForUser;
import com.lvl6.mobsters.po.nonstaticdata.MonsterForUser;
import com.lvl6.mobsters.po.nonstaticdata.MonsterHealingForUser;

//utility class that messes with protos
@Component
public class MonsterStuffUtil {
	
//	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	//extract and return the ids from the UserMonsterCurrentHealthProtos, also
	//return mapping of userMonsterIdToExpectedHealth
  public List<UUID> getUserMonsterIds(List<UserMonsterCurrentHealthProto> umchpList,
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
  
  public List<UUID> getUserMonsterIds(List<FullUserMonsterProto> mfuList) {
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
  public Map<UUID, UserMonsterHealingProto> convertIntoUserMonsterIdToUmhpProtoMap(
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
  
  public Map<UUID, UserEnhancementItemProto> convertIntoUserMonsterIdToUeipProtoMap(
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
  

  //creates a new MonsterHealingForUser from a proto, or
  //updates existing MonsterHealingForUser
  public List<MonsterHealingForUser> convertToMonsterHealingForUser(UUID userId,
		  Map<UUID, MonsterHealingForUser> mhfuIdToMonsterHealingForUser,
		  Map<UUID, UserMonsterHealingProto> protos) {
  	
  	List<MonsterHealingForUser> nonProtos = new ArrayList<MonsterHealingForUser>();
  	
  	for(UUID monsterForUserId : protos.keySet()) {
  		UserMonsterHealingProto umhp = protos.get(monsterForUserId);
  		
  		MonsterHealingForUser mhfu;
  		if (!mhfuIdToMonsterHealingForUser.containsKey(monsterForUserId)) {
  			//create new MonsterHealingForUser object from proto
  			mhfu = new MonsterHealingForUser();
  			mhfu.setUserId(userId);
  			mhfu.setMonsterForUserId(monsterForUserId);
  		} else {
  			//update existing MonsterHealingForUser object
  			mhfu = mhfuIdToMonsterHealingForUser.get(monsterForUserId);
  		}
  		
  		//maybe client not supposed to always set this?
  		Date queuedTime = null;
  		if (umhp.hasQueuedTimeMillis() && umhp.getQueuedTimeMillis() > 0) {
  			queuedTime = new Date(umhp.getQueuedTimeMillis());
  		}
  		mhfu.setQueuedTime(queuedTime);
  		mhfu.setHealthProgress(umhp.getHealthProgress());
  		mhfu.setPriority(umhp.getPriority());
  		nonProtos.add(mhfu);
  	}
  	
  	return nonProtos;
  }
  
  public List<MonsterEnhancingForUser> convertToMonsterEnhancingForUser(
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
  		int enhancingCost = ueip.getEnhancingCost();
  		
  		MonsterEnhancingForUser mefu = new MonsterEnhancingForUser();
  		mefu.setUserId(userId);
  		mefu.setMonsterForUserId(monsterForUserId);
  		mefu.setExpectedStartTime(expectedStartTime);//, queuedTime);
  		mefu.setEnhancingCost(enhancingCost);
  		nonProtos.add(mefu);
  	}
  	
  	return nonProtos;
  }

  public Map<UUID, Integer> convertToMonsterForUserIdToCashAmount(
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
  
  //for given monster_for_user ids get corresponding monster_enhancing_for_users ids
  public List<UUID> getMonsterEnhancingForUserIds(Collection<UUID> finishedMfuIds,
		  Map<UUID, MonsterEnhancingForUser> inEnhancing) {
	  List<UUID> ids = new ArrayList<UUID>();
	  
	  for (UUID mfuId : finishedMfuIds) {
		  //for each monster_for_user_id get corresponding enhancing object in the map
		  
		  if (inEnhancing.containsKey(mfuId)) {
			  MonsterEnhancingForUser mefu = inEnhancing.get(mfuId);

			  //keep track of its monster_enhancing id
			  UUID id = mefu.getId();
			  ids.add(id);
		  }
	  }
	  return ids;
  }
  
  public Map<UUID, Integer> getHealths(Collection<UUID> mfuIds, 
		  Map<UUID, MonsterForUser> idsToUserMonsters) {
	  Map<UUID, Integer> curHps = new HashMap<UUID, Integer>();
	  //there is no prevHp for the user monsters that are deleted
	  for (UUID mfuId : mfuIds) {
		  MonsterForUser mfu = idsToUserMonsters.get(mfuId);
		  int prevHp = mfu.getCurrentHealth();
		  curHps.put(mfuId, prevHp);
	  }
	  
	  return curHps;
  }
  
}