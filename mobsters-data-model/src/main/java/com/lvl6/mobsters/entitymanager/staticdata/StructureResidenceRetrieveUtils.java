package com.lvl6.mobsters.entitymanager.staticdata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.po.staticdata.StructureResidence;
import com.lvl6.mobsters.properties.MobstersDbTables;

@Component public class StructureResidenceRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private  Map<Integer, StructureResidence> idsToItems;

	private  final String TABLE_NAME = MobstersDbTables.TABLE_STRUCTURE_RESIDENCE;

	@Autowired
	protected StructureResidenceEntityManager structureResidenceEntityManager;

	public  StructureResidence getItemForId(Integer id) {
		log.debug("retrieve item data for id " + id);
		if (idsToItems == null) {
			setStaticIdsToItems();      
		}
		return idsToItems.get(id);
	}

	public  Map<Integer, StructureResidence> getItemsForIds(List<Integer> ids) {
		log.debug("retrieve items data for ids " + ids);
		if (idsToItems == null) {
			setStaticIdsToItems();      
		}
		Map<Integer, StructureResidence> toreturn = new HashMap<Integer, StructureResidence>();
		for (Integer id : ids) {
			toreturn.put(id,  idsToItems.get(id));
		}
		return toreturn;
	}

	private  void setStaticIdsToItems() {
		log.debug("setting  map of itemIds to items");

		String cqlquery = "select * from item;"; 
		List <StructureResidence> list = getItemEntityManager().get().find(cqlquery);
		idsToItems = new HashMap<Integer, StructureResidence>();
		for(StructureResidence c : list) {
			Integer id= c.getId();
			idsToItems.put(id, c);
		}
	}

	public StructureResidence findMatchingKeyToChest(int chestType) {
//		String cqlquery = "select * from item;"; 
//		List <StructureResidence> list = getItemEntityManager().get().find(cqlquery);
		StructureResidence i2 = new StructureResidence();
//		for(StructureResidence i : list) {
//			if(i.getItemType() == chestType) {
//				i2 = i;
//			}
//			
//		}
		return i2;
	}
	
	public StructureResidence getItemAccordingToName(Integer itemId) {
		if(idsToItems == null) {
			setStaticIdsToItems();
		}
		return idsToItems.get(itemId);
	}
	
	

	public  void reload() {
		setStaticIdsToItems();
	}
	
	

	public StructureResidenceEntityManager getItemEntityManager() {
		return structureResidenceEntityManager;
	}

	public void setItemEntityManager(
			StructureResidenceEntityManager structureResidenceEntityManager) {
		this.structureResidenceEntityManager = structureResidenceEntityManager;
	}
}
