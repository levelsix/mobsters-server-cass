package com.lvl6.aoc2.entitymanager.staticdata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.aoc2.entitymanager.ClassLevelInfoEntityManager;
import com.lvl6.aoc2.po.ClassLevelInfo;

@Component public class ClassLevelInfoRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private  Map<Integer, Map<Integer, ClassLevelInfo>> classAndLevelToInfo;

	//private  final String TABLE_NAME = DBConstants.CONSUMABLE;

	@Autowired
	protected ClassLevelInfoEntityManager classLevelInfoEntityManager;

	public ClassLevelInfo getClassLevelInfoForClassAndLevel(int classType, int lvl) {
		log.debug("retrieve classLevelInfo data for classType " + classType +
				" lvl " + lvl);
		if (classAndLevelToInfo == null) {
			setStaticClassAndLevelToInfo();      
		}
		
		if (!classAndLevelToInfo.containsKey(classType)) {
			log.error("unexpected error: no entry for classType=" + classType + 
					" and lvl=" + lvl);
			return null;
		}
		
		Map<Integer, ClassLevelInfo> levelToInfo = classAndLevelToInfo.get(classType);
		if (!levelToInfo.containsKey(lvl)) {
			log.error("unexpected error: no entry for lvl=" + lvl + 
					". classType=" + classType);
			return null;
		}
		
		return levelToInfo.get(lvl);
	}

//	public  Map<UUID, ClassLevelInfo> getClassLevelInfosForIds(List<UUID> ids) {
//		log.debug("retrieve classLevelInfos data for ids " + ids);
//		if (classAndLevelToInfo == null) {
//			setStaticClassAndLevelToInfo();      
//		}
//		Map<UUID, ClassLevelInfo> toreturn = new HashMap<UUID, ClassLevelInfo>();
//		for (UUID id : ids) {
//			toreturn.put(id,  classAndLevelToInfo.get(id));
//		}
//		return toreturn;
//	}

	private void setStaticClassAndLevelToInfo() {
		log.debug("setting  map of classLevelInfoIds to classLevelInfos");

		String cqlquery = "select * from classLevelInfo;"; 
		List <ClassLevelInfo> list = getClassLevelInfoEntityManager().get().find(cqlquery);
		classAndLevelToInfo = new HashMap<Integer, Map<Integer, ClassLevelInfo>>();
		for(ClassLevelInfo c : list) {
			int classType = c.getClassType();
			int lvl = c.getLvl();
			
			if (!classAndLevelToInfo.containsKey(classType)) {
				classAndLevelToInfo.put(classType,
						new HashMap<Integer, ClassLevelInfo>());
			}
			Map<Integer, ClassLevelInfo> levelToInfo =
					classAndLevelToInfo.get(classType);
			
			levelToInfo.put(lvl, c);
		}
	}



	public void reload() {
		setStaticClassAndLevelToInfo();
	}
	
	

	public ClassLevelInfoEntityManager getClassLevelInfoEntityManager() {
		return classLevelInfoEntityManager;
	}

	public void setClassLevelInfoEntityManager(
			ClassLevelInfoEntityManager classLevelInfoEntityManager) {
		this.classLevelInfoEntityManager = classLevelInfoEntityManager;
	}
}
