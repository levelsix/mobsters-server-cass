package com.lvl6.mobsters.services.clanchatpost;

import java.util.Date;
import java.util.UUID;

import com.lvl6.mobsters.po.nonstaticdata.ClanChatPost;


public interface ClanChatPostService {
	//CONTROLLER LOGIC STUFF****************************************************************



	//RETRIEVING STUFF****************************************************************
	public abstract ClanChatPost getSpecificClanChatPost(UUID wallPostId);


	//INSERTING STUFF****************************************************************
	public abstract ClanChatPost insertClanChatPost(UUID userId, UUID clanId, String content,
			Date timeOfPost);

	//SAVING STUFF****************************************************************
	public abstract void saveClanChatPost(ClanChatPost ccp);

	//UPDATING STUFF****************************************************************

	//DELETING STUFF****************************************************************


	//for the setter dependency injection or something


}