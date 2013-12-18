package com.lvl6.mobsters.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.nonstaticdata.UserEntityManager;
import com.lvl6.mobsters.entitymanager.nonstaticdata.StructureForUserEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.utils.StructureRetrieveUtils;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.services.structureforuser.StructureForUserService;


@Component
public class RetrieveResourceController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected StructureRetrieveUtils structureRetrieveUtils; 
	
	@Autowired
	protected StructureForUserService structureForUserService; 

	@Autowired
	protected StructureForUserEntityManager structureForUserEntityManager;

	@Autowired
	protected UserEntityManager userEntityManager;

	@Override
	public RequestEvent createRequestEvent() {
		return null;//new RetrieveResourceRequestEvent();
	}

	@Override
	public int getEventType() {
		return 1;//MobstersEventProtocolRequest.C_BUILD_OR_UPGRADE_STRUCTURE_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		/*RetrieveResourceRequestProto reqProto = 
				((RetrieveResourceRequestEvent) event).getRetrieveResourceRequestProto();

		//get the values client sent
		MinimumUserProto sender = reqProto.getMup();

		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = sender.getUserID();
		UUID userId = UUID.fromString(userIdString);
		Date clientDate = new Date();
		String userStructureIdString = reqProto.getStructureId();
		UUID userStructureId = UUID.fromString(userStructureIdString);

		//response to send back to client
		Builder responseBuilder = RetrieveResourceResponseProto.newBuilder();
		responseBuilder.setStatus(RetrieveResourceStatus.FAIL_OTHER);
		RetrieveResourceResponseEvent resEvent =
				new RetrieveResourceResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User inDb = getUserEntityManager().get().get(userId);
			StructureForUser us = getUserStructureEntityManager().get().get(userStructureId);
			Structure s = getUserStructureService().getStructureCorrespondingToUserStructure(us);

			//validate request
			boolean validRequest = isValidRequest(responseBuilder, sender, inDb,
					us, s, clientDate);

			boolean successful = false;
			if (validRequest) {
				successful = writeChangesToDb(inDb, us, s, clientDate);
			}

			if (successful) {
				responseBuilder.setStatus(RetrieveResourceStatus.SUCCESS);
			}

			//write to client
			resEvent.setRetrieveResourceResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in RetrieveResourceController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(RetrieveResourceStatus.FAIL_OTHER);
				resEvent.setRetrieveResourceResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in RetrieveResourceController processRequestEvent", e2);
			}
		}*/
	}
/*
	private boolean isValidRequest(Builder responseBuilder, MinimumUserProto sender,
			User inDb, StructureForUser us, Structure s, Date clientDate) throws ConnectionException {
		if (null == inDb || null == us) {
			log.error("unexpected error: no user exists. sender=" + sender +
					"\t inDb=" + inDb + "\t us=" + us);
			responseBuilder.setStatus(RetrieveResourceStatus.FAIL_NO_STRUCTURE_OR_USER_EXISTS);
			return false;
		}

		UUID structureId = us.getStructureId();
		
		if (null == s) {
			log.error("unexpected error: no structure with id exists. id=" + structureId);
			responseBuilder.setStatus(RetrieveResourceStatus.FAIL_NO_STRUCTURE_OR_USER_EXISTS);
			return false;
		}

		//check if user has waited long enough before collect, default is 5 min/300000 ms
		if(clientDate.getTime() - us.getLastCollectTime().getTime() < 300000) {
			log.error("Need to wait at least 5 min before collecting");
			responseBuilder.setStatus(RetrieveResourceStatus.FAIL_NOT_LONG_ENOUGH);
			return false;
		}
			
		responseBuilder.setStatus(RetrieveResourceStatus.SUCCESS);
		return true;
	}

	private boolean writeChangesToDb(User inDb, StructureForUser us, Structure s, Date clientDate) {
		try {
			int amountBuiltUp = AmountOfResourceRetrieved(us, s, clientDate);
			List<StructureForUser> usList = getUserStructureService().getAllUserStructuresForUser(inDb.getId());
			if(s.getFunctionalityResourceType() == ResourceType.GOLD_VALUE) {
				int goldCapacity = 0;
				for(StructureForUser us2 : usList) {
					Structure s2 = getUserStructureService().getStructureCorrespondingToUserStructure(us2);
					if(s2.getFunctionalityResourceType() == ResourceType.GOLD_VALUE) {
						goldCapacity = goldCapacity + s2.getFunctionalityCapacity();
					}
				}
				if(amountBuiltUp >= goldCapacity) {
					inDb.setGold(goldCapacity);
				}
				else inDb.setGold(inDb.getGold()+amountBuiltUp);
			}
			else {
				int tonicCapacity = 0;
				for(StructureForUser us2 : usList) {
					Structure s2 = getUserStructureService().getStructureCorrespondingToUserStructure(us2);
					if(s2.getFunctionalityResourceType() == ResourceType.TONIC_VALUE) {
						tonicCapacity = tonicCapacity + s2.getFunctionalityCapacity();
					}
				}
				if(amountBuiltUp >= tonicCapacity) {
					inDb.setTonic(tonicCapacity);
				}
				else inDb.setTonic(inDb.getTonic()+amountBuiltUp);
			}
			//update user and userstructure 
			getUserEntityManager().get().put(inDb);
			us.setLastCollectTime(clientDate);
			getUserStructureEntityManager().get().put(us);	
			
			return true;

		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
	}
		

	private int AmountOfResourceRetrieved(StructureForUser us, Structure s, Date clientDate) {
		int minutesSinceLastRetrieve = (int)(clientDate.getTime() - us.getLastCollectTime().getTime())/60000;
		int amountBuiltUp = minutesSinceLastRetrieve*s.getFunctionalityValue();
		return amountBuiltUp;
	}
	
	

	
	public StructureForUserService getUserStructureService() {
		return structureForUserService;
	}

	public void setUserStructureService(StructureForUserService structureForUserService) {
		this.userStructureService = structureForUserService;
	}

	public StructureRetrieveUtils getStructureRetrieveUtils() {
		return structureRetrieveUtils;
	}

	public void setStructureRetrieveUtils(
			StructureRetrieveUtils structureRetrieveUtils) {
		this.structureRetrieveUtils = structureRetrieveUtils;
	}

	public StructureForUserEntityManager getUserStructureEntityManager() {
		return structureForUserEntityManager;
	}

	public void setUserStructureEntityManager(
			StructureForUserEntityManager structureForUserEntityManager) {
		this.userStructureEntityManager = structureForUserEntityManager;
	}

	public UserEntityManager getUserEntityManager() {
		return userEntityManager;
	}

	public void setUserEntityManager(UserEntityManager userEntityManager) {
		this.userEntityManager = userEntityManager;
	}
	*/

}
