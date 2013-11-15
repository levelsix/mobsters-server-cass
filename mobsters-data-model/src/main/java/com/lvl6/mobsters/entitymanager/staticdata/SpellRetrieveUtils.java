package com.lvl6.mobsters.entitymanager.staticdata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.SpellEntityManager;
import com.lvl6.mobsters.po.Spell;

@Component public class SpellRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private  Map<UUID, Spell> idsToSpells;
	
	//private  final String TABLE_NAME = DBConstants.CONSUMABLE;

	@Autowired
	protected SpellEntityManager spellEntityManager;

	public  Spell getSpellForId(UUID id) {
		log.debug("retrieve spell data for id " + id);
		if (idsToSpells == null) {
			setStaticIdsToSpells();      
		}
		return idsToSpells.get(id);
	}

	public  Map<UUID, Spell> getSpellsForIds(List<UUID> ids) {
		log.debug("retrieve spells data for ids " + ids);
		if (idsToSpells == null) {
			setStaticIdsToSpells();      
		}
		Map<UUID, Spell> toreturn = new HashMap<UUID, Spell>();
		for (UUID id : ids) {
			toreturn.put(id,  idsToSpells.get(id));
		}
		return toreturn;
	}

	private  void setStaticIdsToSpells() {
		log.debug("setting  map of consumableIds to consumables");

		String cqlquery = "select * from spell;"; 
		List <Spell> list = getSpellEntityManager().get().find(cqlquery);
		idsToSpells = new HashMap<UUID, Spell>();
		for(Spell s : list) {
			UUID id= s.getId();
			idsToSpells.put(id, s);
		}		
	}
	
	public Spell getUpgradedSpell(Spell s) {
		if(idsToSpells == null) {
			setStaticIdsToSpells();
		}
		for(Spell value : idsToSpells.values()) {
			if((value.getName() == s.getName()) && (value.getLvl() == s.getLvl()+1))
				return value;	
		}
		return null;
		
	}
	
	public Spell getSpellAccordingToNameAndLevel(String name, int level) {
		if(idsToSpells == null) {
			setStaticIdsToSpells();
		}
		for(Spell value : idsToSpells.values()) {
			if((value.getName() == name) && (value.getLvl() == level))
				return value;
		}
		return null;
	}
	

	public  void reload() {
		setStaticIdsToSpells();
	}
	
	

	public SpellEntityManager getSpellEntityManager() {
		return spellEntityManager;
	}

	public void setSpellEntityManager(
			SpellEntityManager spellEntityManager) {
		this.spellEntityManager = spellEntityManager;
	}
}
