package com.lvl6.aoc2.controller.utils;

import com.lvl6.aoc2.noneventprotos.FullUser.FullUserProto;
import com.lvl6.aoc2.po.User;


public interface CreateNoneventProtoUtils {
	
	public abstract FullUserProto createFullUser(User u);
}