package com.lvl6.mobsters.services.clanchatpost;

import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.nonstaticdata.ClanChatPostEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.ClanChatPost;
import com.lvl6.mobsters.properties.MobstersDbTables;

@Component
public class ClanChatPostServiceImpl implements ClanChatPostService {
	
		
	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	private final String TABLE_NAME = MobstersDbTables.TABLE_CLAN_CHAT_POST;
	
	@Autowired
	protected ClanChatPostEntityManager clanChatPostEntityManager;
	

	//CONTROLLER LOGIC STUFF****************************************************************
	


	//RETRIEVING STUFF****************************************************************
	@Override
	public ClanChatPost getSpecificClanChatPost(UUID wallPostId) {
		log.debug("retrieving clan chat post with id " + wallPostId);
		
	    ClanChatPost ccp = getClanChatPostEntityManager().get().get(wallPostId);
	    return ccp;
	}

	//INSERTING STUFF****************************************************************
	@Override
	public ClanChatPost insertClanChatPost(UUID userId, UUID clanId, String content,
			Date timeOfPost) {
		
		ClanChatPost ccp = new ClanChatPost();
		ccp.setPosterId(userId);
		ccp.setClanId(clanId);
		ccp.setTimeOfPost(timeOfPost);
		ccp.setContent(content);
		
		saveClanChatPost(ccp);
		return ccp;
	}


	//SAVING STUFF****************************************************************
	@Override
	public void saveClanChatPost(ClanChatPost ccp) {
		getClanChatPostEntityManager().get().put(ccp);
	}

	//UPDATING STUFF****************************************************************

	//DELETING STUFF****************************************************************


	//for the setter dependency injection or something

	public ClanChatPostEntityManager getClanChatPostEntityManager() {
		return clanChatPostEntityManager;
	}


	public void setClanChatPostEntityManager(
			ClanChatPostEntityManager clanChatPostEntityManager) {
		this.clanChatPostEntityManager = clanChatPostEntityManager;
	}
	
}
