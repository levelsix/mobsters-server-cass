package com.lvl6.mobsters.services.clanforuser;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.lvl6.mobsters.entitymanager.nonstaticdata.ClanForUserEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.ClanForUser;
import com.lvl6.mobsters.utils.QueryConstructionUtil;


public interface ClanForUserService {
	
	//CONTROLLER LOGIC STUFF****************************************************************
	public abstract Set<UUID> getUserIdsFromUserClans(Collection<ClanForUser> userClans);
	
	public abstract Set<UUID> getUserIdsForStatuses(Collection<ClanForUser> userClans, Set<String> statuses);
	
	public abstract ClanForUser getClanForUserForUserId(UUID userId, List<ClanForUser> cfuList);

	//RETRIEVING STUFF****************************************************************
	public abstract ClanForUser getSpecificClanForUserWithId(UUID cfuId);
	
	public abstract List<ClanForUser> getAllUserClansForUser(UUID userId);

	public abstract Map<UUID, ClanForUser> getSpecificOrAllUserClansForClan(UUID clanId, List<UUID> userIds);
	
	public abstract List<ClanForUser> getUserClansForStatuses(UUID clanId, Collection<String> statues);
	
	public abstract ClanForUser getUserClanForUserAndClanId(UUID userId, UUID clanId);

	//INSERTING STUFF****************************************************************
	public abstract ClanForUser insertClanForUser(UUID userId, UUID clanId, String status,
			Date timeOfEntry);

	//SAVING STUFF****************************************************************
	public abstract void saveClanForUser(ClanForUser cfu);

	//UPDATING STUFF****************************************************************
	public abstract void updateUserClanStatus(ClanForUser cfu, String newStatus);

	//DELETING STUFF****************************************************************
	public abstract void deleteUserClans(List<ClanForUser> cfuList);
	
	public abstract void deleteUserClan(ClanForUser cfu);
	
	public abstract void deleteUserClansForUserProspective(UUID userId);
	
	//for the setter dependency injection or something
	public abstract ClanForUserEntityManager getClanForUserEntityManager();
	public abstract void setClanForUserEntityManager(
			ClanForUserEntityManager clanForUserEntityManager);
	
	public abstract QueryConstructionUtil getQueryConstructionUtil();
	public abstract void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil);



}