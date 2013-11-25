package com.lvl6.mobsters.services.userspell;

import java.util.List;
import java.util.Map;
import java.util.UUID;


import com.lvl6.mobsters.po.UserSpell;
import com.lvl6.mobsters.po.staticdata.StructureResourceGenerator;

public interface UserSpellService {
	
	public abstract UserSpell getUserSpellForId(UUID id);

	public abstract Map<UUID, UserSpell> getUserSpellsForIds(List<UUID> ids);
	
	public abstract List<UserSpell> getAllUserSpellsForUser(UUID userId);
	
	public abstract StructureResourceGenerator getSpellCorrespondingToUserSpell(UserSpell us);
	
	
}