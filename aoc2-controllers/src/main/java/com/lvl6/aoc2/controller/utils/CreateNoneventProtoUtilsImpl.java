package com.lvl6.aoc2.controller.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.lvl6.aoc2.noneventprotos.ClassEnum.ClassType;
import com.lvl6.aoc2.noneventprotos.FullUser.FullUserProto;
import com.lvl6.aoc2.po.User;

@Component
public class CreateNoneventProtoUtilsImpl implements CreateNoneventProtoUtils {

	//static initializing this map because didn't know how else to initialize
	private static Map<Integer, ClassType> classTypeNumToClassType =
			new HashMap<Integer, ClassType>();
    static {
    	classTypeNumToClassType.put(ClassType.ALL_VALUE, ClassType.ALL);
    	classTypeNumToClassType.put(ClassType.ARCHER_VALUE, ClassType.ARCHER);
    	classTypeNumToClassType.put(ClassType.NOOB_VALUE, ClassType.NOOB);
    	classTypeNumToClassType.put(ClassType.WARRIOR_VALUE, ClassType.WARRIOR);
    	classTypeNumToClassType.put(ClassType.WIZARD_VALUE, ClassType.WIZARD);
    }
			
    public boolean getClassType(int classTypeInt, List<ClassType> retVal) {
    	//ugh, sanity check
    	if (null == retVal) {
    		return false;
    	}
    	
    	//replacing "if, else" checks with map
    	if (classTypeNumToClassType.containsKey(classTypeInt)) {
    		ClassType ct = classTypeNumToClassType.get(classTypeInt);
    		retVal.add(ct);
    		return true;
    	} else {
    		return false;
    	}
    }
	
	@Override
	public FullUserProto createFullUser(User u) {
		// TODO Auto-generated method stub
		FullUserProto.Builder fupb = FullUserProto.newBuilder();
		
		String uId = u.getId().toString();
		int level = u.getLvl();
		String name = u.getName();
		int exp = u.getExp();
		int gold = u.getGold();
		int tonic = u.getTonic();
		int gems = u.getGems();
		int classTypeInt = u.getClassType();
		int lastHealth = u.getHp();
		int maxHealth = u.getMaxHp();
		Date lastHealthRegen = u.getLastTimeHpRegened();
		int lastMana = u.getMana();
		int maxMana = u.getMaxMana();
		Date lastManaRecovery = u.getLastTimeManaRegened();
		String gameCenterId = u.getGameCenterId();
		//Date dateCreated = u.getDateCreated();
		String clanId = u.getClanId();
		
		fupb.setUserID(uId);
		fupb.setLevel(level);
		fupb.setName(name);
		fupb.setExp(exp);
		fupb.setGold(gold);
		fupb.setTonic(tonic);
		fupb.setGems(gems);
		List<ClassType> ctList = new ArrayList<ClassType>();
		boolean validClassType = getClassType(classTypeInt, ctList);
		if (validClassType) {
			ClassType ct = ctList.get(0);
			fupb.setClassType(ct);
		}
		fupb.setLastHealth(lastHealth);
		fupb.setMaxHealth(maxHealth);
		fupb.setLastHealthRegen(lastHealthRegen.getTime());
		fupb.setLastMana(lastMana);
		fupb.setMaxMana(maxMana);
		fupb.setLastManaRecovery(lastManaRecovery.getTime());
		fupb.setGameCenterID(gameCenterId);
		fupb.setClanID(clanId);
		return fupb.build();
	}


}
