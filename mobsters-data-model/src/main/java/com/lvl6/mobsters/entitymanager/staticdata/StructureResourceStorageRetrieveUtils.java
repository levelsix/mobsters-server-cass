package com.lvl6.mobsters.entitymanager.staticdata;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.po.staticdata.StructureResourceStorage;
import com.lvl6.mobsters.properties.MobstersDbTables;

@Component public class StructureResourceStorageRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private  Map<Integer, Map<Integer, StructureResourceStorage>> classAndLevelToInfo;

	private  final String TABLE_NAME = MobstersDbTables.TABLE_STRUCTURE_RESOURCE_STORAGE;

	@Autowired
	protected StructureResourceStorageEntityManager structureResourceStorageEntityManager;

	public StructureResourceStorage getClassLevelInfoForClassAndLevel(int classType, int lvl) {
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
		
		Map<Integer, StructureResourceStorage> levelToInfo = classAndLevelToInfo.get(classType);
		if (!levelToInfo.containsKey(lvl)) {
			log.error("unexpected error: no entry for lvl=" + lvl + 
					". classType=" + classType);
			return null;
		}
		
		return levelToInfo.get(lvl);
	}

//	public  Map<UUID, StructureResourceStorage> getClassLevelInfosForIds(List<UUID> ids) {
//		log.debug("retrieve classLevelInfos data for ids " + ids);
//		if (classAndLevelToInfo == null) {
//			setStaticClassAndLevelToInfo();      
//		}
//		Map<UUID, StructureResourceStorage> toreturn = new HashMap<UUID, StructureResourceStorage>();
//		for (UUID id : ids) {
//			toreturn.put(id,  classAndLevelToInfo.get(id));
//		}
//		return toreturn;
//	}

	private void setStaticClassAndLevelToInfo() {
		log.debug("setting  map of classLevelInfoIds to classLevelInfos");

//		String cqlquery = "select * from classLevelInfo;"; 
//		List <StructureResourceStorage> list = getClassLevelInfoEntityManager().get().find(cqlquery);
//		classAndLevelToInfo = new HashMap<Integer, Map<Integer, StructureResourceStorage>>();
//		for(StructureResourceStorage c : list) {
//			int classType = c.getClassType();
//			int lvl = c.getLvl();
//			
//			if (!classAndLevelToInfo.containsKey(classType)) {
//				classAndLevelToInfo.put(classType,
//						new HashMap<Integer, StructureResourceStorage>());
//			}
//			Map<Integer, StructureResourceStorage> levelToInfo =
//					classAndLevelToInfo.get(classType);
//			
//			levelToInfo.put(lvl, c);
//		}
	}



	public void reload() {
		setStaticClassAndLevelToInfo();
	}
	
	

	public StructureResourceStorageEntityManager getClassLevelInfoEntityManager() {
		return structureResourceStorageEntityManager;
	}

	public void setClassLevelInfoEntityManager(
			StructureResourceStorageEntityManager structureResourceStorageEntityManager) {
		this.structureResourceStorageEntityManager = structureResourceStorageEntityManager;
	}
}
