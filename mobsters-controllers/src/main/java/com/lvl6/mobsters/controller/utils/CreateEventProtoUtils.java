package com.lvl6.mobsters.controller.utils;

import com.lvl6.mobsters.events.response.UpdateClientUserResponseEvent;
import com.lvl6.mobsters.noneventprotos.CityProto.UserCityExpansionDataProto;
import com.lvl6.mobsters.po.nonstaticdata.ExpansionPurchaseForUser;
import com.lvl6.mobsters.po.nonstaticdata.User;


public interface CreateEventProtoUtils {
	
	//CITY PROTO****************************************************************
	public abstract UserCityExpansionDataProto createUserCityExpansionDataProtoFromUserExpansion(
			ExpansionPurchaseForUser uced);
	
	//USER PROTO****************************************************************
	public abstract UpdateClientUserResponseEvent createUpdateClientUserResponseEvent(User u);
	
	
	//for the setter dependency injection or something****************************************************************
	public abstract CreateNoneventProtoUtils getCreateNoneventProtoUtils();
	
	public abstract void setCreateNoneventProtoUtils(
			CreateNoneventProtoUtils createNoneventProtoUtils);
}