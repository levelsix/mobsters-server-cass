package com.lvl6.mobsters.controller.utils;

import com.lvl6.mobsters.noneventprotos.FullUser.FullUserProto;
import com.lvl6.mobsters.po.nonstaticdata.User;


public interface CreateNoneventProtoUtils {
	
	public abstract FullUserProto createFullUser(User u);
}