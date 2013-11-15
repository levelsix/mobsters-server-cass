package com.lvl6.aoc2.services.userdevice;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.aoc2.entitymanager.UserDeviceEntityManager;
import com.lvl6.aoc2.po.UserDevice;


@Component
public class UserDeviceServiceImpl implements UserDeviceService {
	
	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	
	@Autowired
	protected UserDeviceEntityManager userDeviceEntityManager;
	

	@Override
	public Map<String, UserDevice> getUdidsToDevicesForUser(UUID userId) {
		Map<String, UserDevice> udidsToDevices =
				new HashMap<String, UserDevice>();
		
		String cqlQuery = "select * from user_device where user_id = " + userId;
		List<UserDevice> devices = getUserDeviceEntityManager().get().find(cqlQuery);
		
		for (UserDevice ud : devices) {
			String udid = ud.getUdid();
			
			udidsToDevices.put(udid, ud);
		}
		
		return udidsToDevices;
	}
	
	@Override
	public void saveUserDevices(Collection<UserDevice> devices) {
		getUserDeviceEntityManager().get().put(devices);
	}
	

	@Override
	public UserDeviceEntityManager getUserDeviceEntityManager() {
		return userDeviceEntityManager;
	}

	@Override
	public void setUserDeviceEntityManager(UserDeviceEntityManager userDeviceEntityManager) {
		this.userDeviceEntityManager = userDeviceEntityManager;
	}
	
//	@Override
//	public UserService getUserService() {
//		return userService;
//	}
//	
//	@Override
//	public void setUserService(UserService userService) {
//		this.userService = userService;
//	}
	
}