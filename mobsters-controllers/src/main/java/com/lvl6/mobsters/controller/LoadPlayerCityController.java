package com.lvl6.mobsters.controller;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.controller.utils.CreateNoneventProtoUtil;
import com.lvl6.mobsters.eventprotos.EventCityProto.LoadPlayerCityRequestProto;
import com.lvl6.mobsters.eventprotos.EventCityProto.LoadPlayerCityResponseProto;
import com.lvl6.mobsters.eventprotos.EventCityProto.LoadPlayerCityResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventCityProto.LoadPlayerCityResponseProto.LoadPlayerCityStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.LoadPlayerCityRequestEvent;
import com.lvl6.mobsters.events.response.LoadPlayerCityResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.po.nonstaticdata.StructureForUser;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.services.structureforuser.StructureForUserService;
import com.lvl6.mobsters.services.user.UserService;



@Component
public class LoadPlayerCityController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected UserService userService;
	
	@Autowired
	protected StructureForUserService structureForUserService;
	
	@Autowired
	protected CreateNoneventProtoUtil createNoneventProtoUtil;
	
	@Override
	public RequestEvent createRequestEvent() {
		return new LoadPlayerCityRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_LOAD_PLAYER_CITY_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		LoadPlayerCityRequestProto reqProto = ((LoadPlayerCityRequestEvent) event)
				.getLoadPlayerCityRequestProto();

		//get the values client sent
		MinimumUserProto sender = reqProto.getSender();
		String cityOwnerIdStr = reqProto.getCityOwnerUuid();
		
		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdStr = sender.getUserUuid();
		UUID cityOwnerId = UUID.fromString(cityOwnerIdStr);

		//response to send back to client
		Builder responseBuilder = LoadPlayerCityResponseProto.newBuilder();
		responseBuilder.setStatus(LoadPlayerCityStatus.SUCCESS);
		responseBuilder.setSender(sender);
		LoadPlayerCityResponseEvent resEvent = new LoadPlayerCityResponseEvent(userIdStr);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User owner = getUserService().getUserWithId(cityOwnerId);
			List<UUID> userStructureIds = null;
			Map<UUID, StructureForUser> userStructs = getStructureForUserService()
					.getSpecificOrAllUserStructuresForUser(cityOwnerId, userStructureIds);
			setResponseUserStructs(responseBuilder, userStructs.values());
			//maybe need to retrieve the expansions
			if (owner == null) {
				log.error("owner is null for ownerId = "+cityOwnerId);
			} else {
				responseBuilder.setCityOwner(getCreateNoneventProtoUtils().createMinimumUserProtoFromUser(owner));
			}

			//write to client
			resEvent.setLoadPlayerCityResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in LoadPlayerCityController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(LoadPlayerCityStatus.FAIL_OTHER);
				resEvent.setLoadPlayerCityResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in LoadPlayerCityController processRequestEvent", e2);
			}
		}
	}

	private void setResponseUserStructs(Builder resBuilder,
			Collection<StructureForUser> userStructs) {
		if (userStructs != null) {
			for (StructureForUser userStruct : userStructs) {
				resBuilder.addOwnerNormStructs(getCreateNoneventProtoUtils()
						.createFullUserStructureProtoFromUserStruct(userStruct));
			}
		} else {
			resBuilder.setStatus(LoadPlayerCityStatus.FAIL_OTHER);
			log.error("user structs found for user is null");
		}
	}

	

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public StructureForUserService getStructureForUserService() {
		return structureForUserService;
	}

	public void setStructureForUserService(
			StructureForUserService structureForUserService) {
		this.structureForUserService = structureForUserService;
	}

	public CreateNoneventProtoUtil getCreateNoneventProtoUtils() {
		return createNoneventProtoUtil;
	}

	public void setCreateNoneventProtoUtils(
			CreateNoneventProtoUtil createNoneventProtoUtil) {
		this.createNoneventProtoUtil = createNoneventProtoUtil;
	}
	
}
