package com.lvl6.mobsters.services.userequip;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.lvl6.mobsters.entitymanager.UserEquipEntityManager;
import com.lvl6.mobsters.po.Equipment;
import com.lvl6.mobsters.po.UserEquip;

public interface UserEquipService {
	
	public abstract Map<UUID, UserEquip> getUserEquipsByUserEquipIds (Collection<UUID> ids);
	
	public abstract void saveEquips(Collection<UserEquip> newEquips);
	
	public abstract void getEquippedUserEquips(List<UserEquip> allUserEquips, List<UserEquip> equippedUserEquips);
	
	public abstract UserEquipEntityManager getUserEquipEntityManager();
	
	public abstract void setUserEquipEntityManager(UserEquipEntityManager userEquipEntityManager);
	
	public abstract UserEquip getUserEquipForId(UUID id);
	
	public abstract Map<UUID, UserEquip> getUserEquipsForIds(List<UUID> ids);
	
	public abstract List<UserEquip> getAllUserEquipsForUser(UUID userId);
	
	public abstract Equipment getEquipmentCorrespondingToUserEquip(UserEquip ue);
	
	public abstract List<UserEquip> getAllEquippedUserEquipsForUser(UUID userId);
	
}