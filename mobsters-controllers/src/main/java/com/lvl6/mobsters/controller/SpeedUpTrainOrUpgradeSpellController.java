package com.lvl6.mobsters.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.nonstaticdata.UserEntityManager;
import com.lvl6.mobsters.entitymanager.nonstaticdata.TaskStageHistoryEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.StructureResourceGeneratorRetrieveUtils;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.services.taskstagehistory.TaskStageHistoryService;
import com.lvl6.mobsters.services.user.UserService;


@Component
public class SpeedUpTrainOrUpgradeSpellController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected StructureResourceGeneratorRetrieveUtils structureResourceGeneratorRetrieveUtils; 
	
	@Autowired
	protected UserEntityManager userEntityManager; 
	
	@Autowired
	protected UserService userService; 

	@Autowired
	protected TaskStageHistoryService taskStageHistoryService; 
	
	@Autowired
	protected TaskStageHistoryEntityManager taskStageHistoryEntityManager;

	@Override
	public RequestEvent createRequestEvent() {
		return  null;//new SpeedUpTrainOrUpgradeSpellRequestEvent();
	}

	@Override
	public int getEventType() {
		return 1;//MobstersEventProtocolRequest.C_TRAIN_OR_UPGRADE_SPELL_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		/*//stuff client sent
		SpeedUpTrainOrUpgradeSpellRequestProto reqProto = 
				((SpeedUpTrainOrUpgradeSpellRequestEvent) event).getSpeedUpTrainOrUpgradeSpellRequestProto();

		//get the values client sent
		MinimumUserProto sender = reqProto.getMup();

		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = sender.getUserID();
		UUID userId = UUID.fromString(userIdString);
		Date clientDate = new Date();
		String userSpellIdString = reqProto.getSpellId();
		UUID userSpellId = UUID.fromString(userSpellIdString);

		//response to send back to client
		Builder responseBuilder = SpeedUpTrainOrUpgradeSpellResponseProto.newBuilder();
		responseBuilder.setStatus(SpeedUpTrainOrUpgradeSpellStatus.FAIL_OTHER);
		SpeedUpTrainOrUpgradeSpellResponseEvent resEvent =
				new SpeedUpTrainOrUpgradeSpellResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User inDb = getUserEntityManager().get().get(userId);
			TaskStageHistory us = getUserSpellEntityManager().get().get(userSpellId);
			StructureResourceGenerator s = getUserSpellService().getSpellCorrespondingToUserSpell(us);;

			//validate request
			boolean validRequest = isValidRequest(responseBuilder, sender, inDb,
					us, s, clientDate);

			boolean successful = false;
			if (validRequest) {
				successful = writeChangesToDb(inDb, us, s, clientDate);
			}

			if (successful) {
				responseBuilder.setStatus(SpeedUpTrainOrUpgradeSpellStatus.SUCCESS);
			}

			//write to client
			resEvent.setSpeedUpTrainOrUpgradeSpellResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in SpeedUpTrainOrUpgradeSpellController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(SpeedUpTrainOrUpgradeSpellStatus.FAIL_OTHER);
				resEvent.setSpeedUpTrainOrUpgradeSpellResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in SpeedUpTrainOrUpgradeSpellController processRequestEvent", e2);
			}
		} */
	}
/*
	private boolean isValidRequest(Builder responseBuilder, MinimumUserProto sender,
			User inDb, TaskStageHistory us, StructureResourceGenerator s, Date clientDate) throws ConnectionException {
		if (null == inDb || null == us) {
			log.error("unexpected error: no user exists. sender=" + sender +
					"\t inDb=" + inDb + "\t us=" + us);
			responseBuilder.setStatus(SpeedUpTrainOrUpgradeSpellStatus.FAIL_OTHER);
			return false;
		}

		int millisPassed = (int)(clientDate.getTime() - us.getTimeAcquired().getTime());
		int minutesRemaining = (s.getResearchTimeMillis() - millisPassed)/60000;
		int gemSpeedUpCost = getUserService().calculateGemCostForSpeedUp(minutesRemaining);
		
		if(inDb.getGems() < gemSpeedUpCost) {
			log.error("user doesnt have enough gems to speed up");
			responseBuilder.setStatus(SpeedUpTrainOrUpgradeSpellStatus.FAIL_NOT_ENOUGH_GEMS);
			return false;
		}
		
		responseBuilder.setStatus(SpeedUpTrainOrUpgradeSpellStatus.SUCCESS);
		return true;
	}

	private boolean writeChangesToDb(User inDb, TaskStageHistory us,
			StructureResourceGenerator s, Date clientDate) {
		try {
			int millisPassed = (int)(clientDate.getTime() - us.getTimeAcquired().getTime());
			int minutesRemaining = (s.getResearchTimeMillis() - millisPassed)/60000;
			int gemSpeedUpCost = getUserService().calculateGemCostForSpeedUp(minutesRemaining);

			inDb.setGems(inDb.getGems()-gemSpeedUpCost);
			getUserEntityManager().get().put(inDb);
			
			//update user spell
			us.setIsTraining(false);
			getUserSpellEntityManager().get().put(us);
		
			return true;

		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
	}
		
	
	
	

	
	public TaskStageHistoryService getUserSpellService() {
		return taskStageHistoryService;
	}

	public void setUserSpellService(TaskStageHistoryService taskStageHistoryService) {
		this.userSpellService = taskStageHistoryService;
	}

	public StructureResourceGeneratorRetrieveUtils getSpellRetrieveUtils() {
		return structureResourceGeneratorRetrieveUtils;
	}

	public void setSpellRetrieveUtils(
			StructureResourceGeneratorRetrieveUtils structureResourceGeneratorRetrieveUtils) {
		this.spellRetrieveUtils = structureResourceGeneratorRetrieveUtils;
	}

	public TaskStageHistoryEntityManager getUserSpellEntityManager() {
		return taskStageHistoryEntityManager;
	}

	public void setUserSpellEntityManager(
			TaskStageHistoryEntityManager taskStageHistoryEntityManager) {
		this.userSpellEntityManager = taskStageHistoryEntityManager;
	}

	public UserEntityManager getUserEntityManager() {
		return userEntityManager;
	}

	public void setUserEntityManager(UserEntityManager userEntityManager) {
		this.userEntityManager = userEntityManager;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

*/

}
