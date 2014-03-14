package com.lvl6.mobsters.services.clanforuser;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.lvl6.mobsters.entitymanager.nonstaticdata.ClanForUserEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.ClanForUser;
import com.lvl6.mobsters.utils.QueryConstructionUtil;


public interface ClanForUserService {
	//CONTROLLER LOGIC STUFF****************************************************************



	//RETRIEVING STUFF****************************************************************
	public abstract ClanForUser getSpecificClanForUserWithId(UUID wallPostId);
	
	public abstract List<ClanForUser> getAllUserClansForUser(UUID userId);

	public abstract Map<UUID, ClanForUser> getSpecificOrAllUserClansForClan(UUID clanId, List<UUID> userIds);

	//INSERTING STUFF****************************************************************
	public abstract ClanForUser insertClanForUser(UUID userId, UUID clanId, String status,
			Date timeOfEntry);

	//SAVING STUFF****************************************************************
	public abstract void saveClanForUser(ClanForUser ccp);

	//UPDATING STUFF****************************************************************
	public abstract void updateUserClanStatus(ClanForUser cfu, String newStatus);

	//DELETING STUFF****************************************************************
	public abstract void deleteUserClans(List<ClanForUser> cfuList);

	//for the setter dependency injection or something
	public abstract ClanForUserEntityManager getClanForUserEntityManager();
	public abstract void setClanForUserEntityManager(
			ClanForUserEntityManager clanForUserEntityManager);
	
	public abstract QueryConstructionUtil getQueryConstructionUtil();
	public abstract void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil);



}