package com.lvl6.mobsters.services.userdevice;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import com.lvl6.mobsters.entitymanager.UserDeviceEntityManager;
import com.lvl6.mobsters.po.UserDevice;

public interface UserDeviceService {
	
	
	public abstract Map<String, UserDevice> getUdidsToDevicesForUser(UUID userId);
	
	public abstract void saveUserDevices(Collection<UserDevice> devices);
	
	
	
	public abstract UserDeviceEntityManager getUserDeviceEntityManager();

	public abstract void setUserDeviceEntityManager(UserDeviceEntityManager userDeviceEntityManager);
	
//	public abstract UserService getUserService();

//	public abstract void setUserService(UserService userService);
}