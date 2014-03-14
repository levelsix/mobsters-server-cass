package com.lvl6.mobsters.services.clan;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.lvl6.mobsters.entitymanager.nonstaticdata.ClanEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.Clan;
import com.lvl6.mobsters.utils.QueryConstructionUtil;


public interface ClanService {
	//CONTROLLER LOGIC STUFF****************************************************************



	//RETRIEVING STUFF****************************************************************
	public abstract Clan getClanWithId(UUID wallPostId);
	
	public abstract Map<UUID, Clan> getClansByIds(List<UUID> clanIds);
	
	public abstract List<Clan> getClansWithSimilarNameOrTag(String name, String tag);
	
	public abstract Clan getClanWithNameOrTag(String name, String tag);
	

	//INSERTING STUFF****************************************************************
	public abstract Clan insertClan(String clanName, Date createTime, String description,
			String tag, boolean requestToJoinRequired);
	
	

	//SAVING STUFF****************************************************************
	public abstract void saveClan(Clan ccp);

	//UPDATING STUFF****************************************************************
	public abstract void updateUserClanStatus(Clan cfu, String newStatus);

	//DELETING STUFF****************************************************************


	//for the setter dependency injection or something
	public abstract ClanEntityManager getClanEntityManager();
	public abstract void setClanEntityManager(
			ClanEntityManager clanEntityManager);
	
	public abstract QueryConstructionUtil getQueryConstructionUtil();
	public abstract void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil);



}