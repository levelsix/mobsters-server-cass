package com.lvl6.mobsters.services.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.nonstaticdata.UserEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.ProfanityEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.utils.StructureResourceStorageRetrieveUtils;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.services.structureforuser.StructureForUserService;


@Component
public class UserServiceImpl implements UserService {
	
	@Autowired
	protected StructureResourceStorageRetrieveUtils classLevelInfoRetrieveUtils;
	
	@Autowired
	protected ProfanityEntityManager profanityEntityManager;
	
	@Autowired
	protected UserEntityManager userEntityManager;
	
	@Autowired
	protected StructureForUserService userStructureService;

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	

	//RETRIEVE STUFF****************************************************************
	@Override
	public User getUserWithId(UUID userId) {
		return getUserEntityManager().get().get(userId);
	}
	
	//searches for a user based on game center or user id
	@Override
	public User retrieveUser(String gameCenterId, UUID userId) {
		String cqlQuery = "select * from user u ";
		List<User> uList = new ArrayList<User>();
		if (null != gameCenterId) {
			cqlQuery += "where u.game_center_id = " + gameCenterId + ";";
			uList = getUserEntityManager().get().find(cqlQuery);

		} else if (null != userId){
			cqlQuery += "where u.user_id = " + userId + ";";
			uList = getUserEntityManager().get().find(cqlQuery);

		}

		if (uList == null || uList.isEmpty()) {
			return null;
		} else if (uList.size() > 1) {
			String msg = "multiple users exist. gameCenterId=" +
					gameCenterId + ", userId=" + userId +
					" uList=" + uList;
			log.error("unexpected error: " + msg);
			return null;
		} else {
			return uList.get(0);
		}

	}
	
	
	//INSERT STUFF****************************************************************
	
	
	//SAVING STUFF****************************************************************
	@Override
	public void saveUser(User u) {
		getUserEntityManager().get().put(u);
	}
	
	@Override
	public void saveUsers(Collection<User> uCollection) {
		getUserEntityManager().get().put(uCollection);
	}

	//UPDATING STUFF****************************************************************
	@Override
	public void updateUserResources(User u, int gemsChange, int oilChange, int cashChange) {

		if (gemsChange != 0) {
			int newGems = u.getGems() + gemsChange;
			u.setGems(newGems);
		}

		if (oilChange != 0) {
			int newOil = u.getOil() + oilChange;
			u.setOil(newOil);
		}

		if (cashChange != 0) {
			int newCash = u.getCash() + cashChange;
			u.setCash(newCash);
		}
		saveUser(u);
	}

	@Override
	public void updateDeviceToken(User u, String deviceToken) {
		u.setDeviceToken(deviceToken);
		saveUser(u);
	}
	
	
	//setters and getters for the Setter Dependency Injection (or something)****************************************************************
	@Override
	public UserEntityManager getUserEntityManager() {
		return userEntityManager;
	}

	@Override
	public void setUserEntityManager(UserEntityManager userEntityManager) {
		this.userEntityManager = userEntityManager;
	}
	
}