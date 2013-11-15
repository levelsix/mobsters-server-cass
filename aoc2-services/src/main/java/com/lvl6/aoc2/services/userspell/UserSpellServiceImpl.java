package com.lvl6.aoc2.services.userspell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.aoc2.entitymanager.SpellEntityManager;
import com.lvl6.aoc2.entitymanager.UserSpellEntityManager;
import com.lvl6.aoc2.entitymanager.staticdata.SpellRetrieveUtils;
import com.lvl6.aoc2.po.Spell;
import com.lvl6.aoc2.po.UserSpell;


@Component
public class UserSpellServiceImpl implements UserSpellService {
	
	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private  Map<UUID, UserSpell> idsToUserSpells;
	
	@Autowired
	protected SpellRetrieveUtils spellRetrieveUtils;

	@Autowired
	protected UserSpellEntityManager UserSpellEntityManager;

	@Autowired
	protected SpellEntityManager spellEntityManager;
	
	@Override
	public  UserSpell getUserSpellForId(UUID id) {
		log.debug("retrieve UserSpell data for id " + id);
		if (idsToUserSpells == null) {
			setStaticIdsToUserSpells();      
		}
		return idsToUserSpells.get(id);
	}

	@Override
	public  Map<UUID, UserSpell> getUserSpellsForIds(List<UUID> ids) {
		log.debug("retrieve UserSpells data for ids " + ids);
		if (idsToUserSpells == null) {
			setStaticIdsToUserSpells();      
		}
		Map<UUID, UserSpell> toreturn = new HashMap<UUID, UserSpell>();
		for (UUID id : ids) {
			toreturn.put(id,  idsToUserSpells.get(id));
		}
		return toreturn;
	}

	private  void setStaticIdsToUserSpells() {
		log.debug("setting  map of UserSpellIds to UserSpells");

		String cqlquery = "select * from user_spell;"; 
		List <UserSpell> list = getUserSpellEntityManager().get().find(cqlquery);
		idsToUserSpells = new HashMap<UUID, UserSpell>();
		for(UserSpell us : list) {
			UUID id= us.getId();
			idsToUserSpells.put(id, us);
		}
					
	}

	@Override
	public  List<UserSpell> getAllUserSpellsForUser(UUID userId) {
		String cqlquery = "select * from user_spell where user_id=" + userId + ";"; 
		List <UserSpell> list = getUserSpellEntityManager().get().find(cqlquery);
		return list;
	}

	@Override
	public Spell getSpellCorrespondingToUserSpell(UserSpell us) {
		UUID spellId = us.getSpellId();
		return getSpellRetrieveUtils().getSpellForId(spellId);
	}

	public UserSpellEntityManager getUserSpellEntityManager() {
		return UserSpellEntityManager;
	}

	public void setUserSpellEntityManager(
			UserSpellEntityManager userSpellEntityManager) {
		UserSpellEntityManager = userSpellEntityManager;
	}

	public SpellEntityManager getSpellEntityManager() {
		return spellEntityManager;
	}

	public void setSpellEntityManager(SpellEntityManager spellEntityManager) {
		this.spellEntityManager = spellEntityManager;
	}

	public SpellRetrieveUtils getSpellRetrieveUtils() {
		return spellRetrieveUtils;
	}

	public void setSpellRetrieveUtils(SpellRetrieveUtils spellRetrieveUtils) {
		this.spellRetrieveUtils = spellRetrieveUtils;
	}
	
	
	


	
}