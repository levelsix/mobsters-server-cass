package com.lvl6.mobsters.entitymanager.staticdata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.ConsumableEntityManager;
import com.lvl6.mobsters.po.Consumable;

@Component public class ConsumableRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private  Map<String, Consumable> namesToConsumables;

	//private  final String TABLE_NAME = DBConstants.CONSUMABLE;

	@Autowired
	protected ConsumableEntityManager consumableEntityManager;

	public  Consumable getConsumableForName(String name) {
		log.debug("retrieve consumable data for id " + name);
		if (namesToConsumables == null) {
			setStaticIdsToConsumables();      
		}
		return namesToConsumables.get(name);
	}

	public  Map<String, Consumable> getConsumablesForNames(List<String> names) {
		log.debug("retrieve consumables data for ids " + names);
		if (namesToConsumables == null) {
			setStaticIdsToConsumables();      
		}
		Map<String, Consumable> toreturn = new HashMap<String, Consumable>();
		for (String name : names) {
			toreturn.put(name,  namesToConsumables.get(name));
		}
		return toreturn;
	}

	private  void setStaticIdsToConsumables() {
		log.debug("setting  map of consumableIds to consumables");

		String cqlquery = "select * from consumable;"; 
		List <Consumable> list = getConsumableEntityManager().get().find(cqlquery);
		namesToConsumables = new HashMap<String, Consumable>();
		for(Consumable c : list) {
			String name= c.getName();
			namesToConsumables.put(name, c);
		}
	}



	public  void reload() {
		setStaticIdsToConsumables();
	}
	
	

	public ConsumableEntityManager getConsumableEntityManager() {
		return consumableEntityManager;
	}

	public void setConsumableEntityManager(
			ConsumableEntityManager consumableEntityManager) {
		this.consumableEntityManager = consumableEntityManager;
	}
}
