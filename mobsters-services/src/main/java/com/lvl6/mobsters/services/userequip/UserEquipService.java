package com.lvl6.mobsters.services.userequip;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.lvl6.mobsters.entitymanager.nonstaticdata.MonsterForUserEntityManager;
import com.lvl6.mobsters.po.Equipment;
import com.lvl6.mobsters.po.nonstaticdata.MonsterForUser;

public interface UserEquipService {
	
	public abstract Map<UUID, MonsterForUser> getUserEquipsByUserEquipIds (Collection<UUID> ids);
	
	public abstract void saveEquips(Collection<MonsterForUser> newEquips);
	
	public abstract void getEquippedUserEquips(List<MonsterForUser> allUserEquips, List<MonsterForUser> equippedUserEquips);
	
	public abstract MonsterForUserEntityManager getUserEquipEntityManager();
	
	public abstract void setUserEquipEntityManager(MonsterForUserEntityManager monsterForUserEntityManager);
	
	public abstract MonsterForUser getUserEquipForId(UUID id);
	
	public abstract Map<UUID, MonsterForUser> getUserEquipsForIds(List<UUID> ids);
	
	public abstract List<MonsterForUser> getAllUserEquipsForUser(UUID userId);
	
	public abstract Equipment getEquipmentCorrespondingToUserEquip(MonsterForUser ue);
	
	public abstract List<MonsterForUser> getAllEquippedUserEquipsForUser(UUID userId);
	
}