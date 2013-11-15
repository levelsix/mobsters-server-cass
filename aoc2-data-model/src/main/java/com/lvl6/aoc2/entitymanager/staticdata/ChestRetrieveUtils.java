package com.lvl6.aoc2.entitymanager.staticdata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.aoc2.entitymanager.ChestEntityManager;
import com.lvl6.aoc2.po.Chest;

@Component public class ChestRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private  Map<UUID, Chest> idsToChests;

	//private  final String TABLE_NAME = DBConstants.CONSUMABLE;

	@Autowired
	protected ChestEntityManager chestEntityManager;

	public  Chest getChestForId(UUID id) {
		log.debug("retrieve chest data for id " + id);
		if (idsToChests == null) {
			setStaticIdsToChests();      
		}
		return idsToChests.get(id);
	}

	public  Map<UUID, Chest> getChestsForIds(List<UUID> ids) {
		log.debug("retrieve chests data for ids " + ids);
		if (idsToChests == null) {
			setStaticIdsToChests();      
		}
		Map<UUID, Chest> toreturn = new HashMap<UUID, Chest>();
		for (UUID id : ids) {
			toreturn.put(id,  idsToChests.get(id));
		}
		return toreturn;
	}

	private  void setStaticIdsToChests() {
		log.debug("setting  map of chestIds to chests");

		String cqlquery = "select * from chest;"; 
		List <Chest> list = getChestEntityManager().get().find(cqlquery);
		idsToChests = new HashMap<UUID, Chest>();
		for(Chest c : list) {
			UUID id= c.getId();
			idsToChests.put(id, c);
		}
	}



	public  void reload() {
		setStaticIdsToChests();
	}
	
	

	public ChestEntityManager getChestEntityManager() {
		return chestEntityManager;
	}

	public void setChestEntityManager(
			ChestEntityManager chestEntityManager) {
		this.chestEntityManager = chestEntityManager;
	}
}
