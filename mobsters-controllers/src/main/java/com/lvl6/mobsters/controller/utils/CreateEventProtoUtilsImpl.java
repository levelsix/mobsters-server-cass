package com.lvl6.mobsters.controller.utils;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.eventprotos.EventUserProto.UpdateClientUserResponseProto;
import com.lvl6.mobsters.events.response.UpdateClientUserResponseEvent;
import com.lvl6.mobsters.noneventprotos.CityProto.UserCityExpansionDataProto;
import com.lvl6.mobsters.noneventprotos.UserProto.FullUserProto;
import com.lvl6.mobsters.po.nonstaticdata.ExpansionPurchaseForUser;
import com.lvl6.mobsters.po.nonstaticdata.User;

@Component
public class CreateEventProtoUtilsImpl implements CreateEventProtoUtils {

	/*//static initializing this map because didn't know how else to initialize
	private static Map<Integer, ClassType> classTypeNumToClassType =
			new HashMap<Integer, ClassType>();
    static {
    	classTypeNumToClassType.put(ClassType.ALL_VALUE, ClassType.ALL);
    	classTypeNumToClassType.put(ClassType.ARCHER_VALUE, ClassType.ARCHER);
    	classTypeNumToClassType.put(ClassType.NOOB_VALUE, ClassType.NOOB);
    	classTypeNumToClassType.put(ClassType.WARRIOR_VALUE, ClassType.WARRIOR);
    	classTypeNumToClassType.put(ClassType.WIZARD_VALUE, ClassType.WIZARD);
    }*/
	
	@Autowired
	protected CreateNoneventProtoUtils createNoneventProtoUtils;
	
	//CITY PROTO****************************************************************
	@Override
	public UserCityExpansionDataProto createUserCityExpansionDataProtoFromUserExpansion(
			ExpansionPurchaseForUser epfu) {
		UserCityExpansionDataProto.Builder builder = UserCityExpansionDataProto.newBuilder();
		
		UUID userId = epfu.getUserId();
		String userIdStr = userId.toString();
		builder.setUserUuid(userIdStr);
		builder.setXPosition(epfu.getxPosition());
		builder.setYPosition(epfu.getyPosition());
		builder.setIsExpanding(epfu.isExpanding());
		
		if (epfu.getExpandStartTime() != null) {
			builder.setExpandStartTime(epfu.getExpandStartTime().getTime());
		}
		return builder.build();
	}
	
	

	//USER PROTO****************************************************************
	@Override
	public UpdateClientUserResponseEvent createUpdateClientUserResponseEvent(User u) {
		String userIdStr = u.getId().toString();
		UpdateClientUserResponseEvent resEvent = new UpdateClientUserResponseEvent(userIdStr);
		
	    UpdateClientUserResponseProto.Builder ucurpb = UpdateClientUserResponseProto.newBuilder();
	    FullUserProto fup = getCreateNoneventProtoUtils().createFullUserProtoFromUser(u); 
	    ucurpb.setSender(fup);
	    ucurpb.setTimeOfUserUpdate(new Date().getTime()).build();
	    resEvent.setUpdateClientUserResponseProto(ucurpb.build());
	    return resEvent;
	}
	

	//for the setter dependency injection or something****************************************************************
	@Override
	public CreateNoneventProtoUtils getCreateNoneventProtoUtils() {
		return createNoneventProtoUtils;
	}
	@Override
	public void setCreateNoneventProtoUtils(
			CreateNoneventProtoUtils createNoneventProtoUtils) {
		this.createNoneventProtoUtils = createNoneventProtoUtils;
	}
	
	

}
