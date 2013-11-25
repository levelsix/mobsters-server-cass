package com.lvl6.mobsters.entitymanager.staticdata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.po.staticdata.StructureHospital;
import com.lvl6.mobsters.properties.MobstersDbTables;

@Component public class StructureHospitalRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private  Map<String, StructureHospital> namesToConsumables;

	private  final String TABLE_NAME = MobstersDbTables.TABLE_STRUCTURE_HOSPITAL;

	@Autowired
	protected StructureHospitalEntityManager structureHospitalEntityManager;

	public  StructureHospital getConsumableForName(String name) {
		log.debug("retrieve consumable data for id " + name);
		if (namesToConsumables == null) {
			setStaticIdsToConsumables();      
		}
		return namesToConsumables.get(name);
	}

	public  Map<String, StructureHospital> getConsumablesForNames(List<String> names) {
		log.debug("retrieve consumables data for ids " + names);
		if (namesToConsumables == null) {
			setStaticIdsToConsumables();      
		}
		Map<String, StructureHospital> toreturn = new HashMap<String, StructureHospital>();
		for (String name : names) {
			toreturn.put(name,  namesToConsumables.get(name));
		}
		return toreturn;
	}

	private  void setStaticIdsToConsumables() {
		log.debug("setting  map of consumableIds to consumables");

//		String cqlquery = "select * from consumable;"; 
//		List <StructureHospital> list = getConsumableEntityManager().get().find(cqlquery);
//		namesToConsumables = new HashMap<String, StructureHospital>();
//		for(StructureHospital c : list) {
//			String name= c.getName();
//			namesToConsumables.put(name, c);
//		}
	}



	public  void reload() {
		setStaticIdsToConsumables();
	}
	
	

	public StructureHospitalEntityManager getConsumableEntityManager() {
		return structureHospitalEntityManager;
	}

	public void setConsumableEntityManager(
			StructureHospitalEntityManager structureHospitalEntityManager) {
		this.structureHospitalEntityManager = structureHospitalEntityManager;
	}
}
