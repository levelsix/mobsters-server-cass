package com.lvl6.mobsters.services.userdungeonstatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.UserDungeonStatusEntityManager;
import com.lvl6.mobsters.po.UserDungeonStatus;


@Component
public class UserDungeonStatusServiceImpl implements UserDungeonStatusService {
	
	@Autowired
	protected UserDungeonStatusEntityManager userDungeonStatusEntityManager;

		
	
	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private  Map<UUID, UserDungeonStatus> idsToUserDungeonStatus;
	
	@Autowired
	protected UserDungeonStatusEntityManager UserDungeonStatusEntityManager;

@Override
	public  UserDungeonStatus getUserDungeonStatusForId(UUID id) {
		log.debug("retrieve UserDungeonStatus data for id " + id);
		if (idsToUserDungeonStatus == null) {
			setStaticIdsToUserDungeonStatuss();      
		}
		return idsToUserDungeonStatus.get(id);
	}

@Override
	public  Map<UUID, UserDungeonStatus> getUserDungeonStatussForIds(List<UUID> ids) {
		log.debug("retrieve UserDungeonStatuss data for ids " + ids);
		if (idsToUserDungeonStatus == null) {
			setStaticIdsToUserDungeonStatuss();      
		}
		Map<UUID, UserDungeonStatus> toreturn = new HashMap<UUID, UserDungeonStatus>();
		for (UUID id : ids) {
			toreturn.put(id,  idsToUserDungeonStatus.get(id));
		}
		return toreturn;
	}

	private  void setStaticIdsToUserDungeonStatuss() {
		log.debug("setting  map of UserDungeonStatusIds to UserDungeonStatuss");

		String cqlquery = "select * from user_dungeon_status;"; 
		List <UserDungeonStatus> list = getUserDungeonStatusEntityManager().get().find(cqlquery);
		idsToUserDungeonStatus = new HashMap<UUID, UserDungeonStatus>();
		for(UserDungeonStatus us : list) {
			UUID id= us.getId();
			idsToUserDungeonStatus.put(id, us);
		}
					
	}

	public UserDungeonStatusEntityManager getUserDungeonStatusEntityManager() {
		return UserDungeonStatusEntityManager;
	}

	public void setUserDungeonStatusEntityManager(
			UserDungeonStatusEntityManager userDungeonStatusEntityManager) {
		UserDungeonStatusEntityManager = userDungeonStatusEntityManager;
	}


	


	
}