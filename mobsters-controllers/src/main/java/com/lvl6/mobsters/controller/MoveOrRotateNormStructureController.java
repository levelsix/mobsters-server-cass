package com.lvl6.mobsters.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.eventprotos.EventStructureProto.MoveOrRotateNormStructureRequestProto;
import com.lvl6.mobsters.eventprotos.EventStructureProto.MoveOrRotateNormStructureRequestProto.MoveOrRotateNormStructType;
import com.lvl6.mobsters.eventprotos.EventStructureProto.MoveOrRotateNormStructureResponseProto;
import com.lvl6.mobsters.eventprotos.EventStructureProto.MoveOrRotateNormStructureResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventStructureProto.MoveOrRotateNormStructureResponseProto.MoveOrRotateNormStructureStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.MoveOrRotateNormStructureRequestEvent;
import com.lvl6.mobsters.events.response.MoveOrRotateNormStructureResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.po.nonstaticdata.StructureForUser;
import com.lvl6.mobsters.services.structureforuser.StructureForUserService;
import com.lvl6.mobsters.services.user.UserService;
import com.lvl6.mobsters.utils.CoordinatePair;

@Component
public class MoveOrRotateNormStructureController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected UserService userService;
	
	@Autowired
	protected StructureForUserService structureForUserService;
	


	public MoveOrRotateNormStructureController() {
		numAllocatedThreads = 3;
	}
	
	@Override
	public RequestEvent createRequestEvent() {
		return new MoveOrRotateNormStructureRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_MOVE_OR_ROTATE_NORM_STRUCTURE_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		MoveOrRotateNormStructureRequestProto reqProto = 
				((MoveOrRotateNormStructureRequestEvent) event).getMoveOrRotateNormStructureRequestProto();

		//get stuff client sent
	    MinimumUserProto senderProto = reqProto.getSender();
	    String userStructIdStr = reqProto.getUserStructUuid();
	    UUID userStructId = UUID.fromString(userStructIdStr);
	    MoveOrRotateNormStructType type = reqProto.getType();
	    String userIdStr = senderProto.getUserUuid();

	    CoordinatePair newCoords = null;
	    if (type == MoveOrRotateNormStructType.MOVE) {
	      newCoords = new CoordinatePair(reqProto.getCurStructCoordinates().getX(), reqProto.getCurStructCoordinates().getY());
	    }
	    
		//response to send back to client
		Builder responseBuilder = MoveOrRotateNormStructureResponseProto.newBuilder();
		responseBuilder.setStatus(MoveOrRotateNormStructureStatus.OTHER_FAIL);
		MoveOrRotateNormStructureResponseEvent resEvent =
				new MoveOrRotateNormStructureResponseEvent(userIdStr);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			
			StructureForUser userStruct = getStructureForUserService()
					.getSpecificUserStruct(userStructId);

			//validate request
			boolean validRequest = isValidRequest(responseBuilder, userStruct,
					newCoords, type);

			boolean successful = false;
			if (validRequest) { 
				successful = writeChangesToDb(userStruct, newCoords);
			}

			if (successful) {
				responseBuilder.setStatus(MoveOrRotateNormStructureStatus.SUCCESS);
			}

			//write to client
			resEvent.setMoveOrRotateNormStructureResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in MoveOrRotateNormStructureController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(MoveOrRotateNormStructureStatus.OTHER_FAIL);
				resEvent.setMoveOrRotateNormStructureResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in MoveOrRotateNormStructureController processRequestEvent", e2);
			}
		}
	}
	
	private boolean isValidRequest(Builder responseBuilder, StructureForUser sfu,
			CoordinatePair newCoords, MoveOrRotateNormStructType type) {
		if (null == sfu) {
			log.error("unexpected error: no sfu exists. userStruct=" + sfu);
			return false;
		}
		
		if (type == MoveOrRotateNormStructType.MOVE && null == newCoords) {
			log.error("coordinates supplied are null. coordinates=" + newCoords);
			return false;
		}
		
		responseBuilder.setStatus(MoveOrRotateNormStructureStatus.SUCCESS);
		return true;
	}

	private boolean writeChangesToDb(StructureForUser sfu,
			CoordinatePair coordinates) {
			
		try {
			getStructureForUserService().updateUserStructCoordinates(sfu, coordinates);
			
			return true;

		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
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
	
}
