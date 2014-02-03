package com.lvl6.mobsters.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.controller.utils.CreateEventProtoUtil;
import com.lvl6.mobsters.controller.utils.CreateNoneventProtoUtil;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.EvolutionFinishedRequestProto;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.EvolutionFinishedResponseProto;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.EvolutionFinishedResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.EvolutionFinishedResponseProto.EvolutionFinishedStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.EvolutionFinishedRequestEvent;
import com.lvl6.mobsters.events.response.EvolutionFinishedResponseEvent;
import com.lvl6.mobsters.events.response.UpdateClientUserResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.po.nonstaticdata.MonsterEvolvingForUser;
import com.lvl6.mobsters.po.nonstaticdata.MonsterForUser;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.po.nonstaticdata.UserCurrencyHistory;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.properties.MobstersTableConstants;
import com.lvl6.mobsters.services.monsterevolvingforuser.MonsterEvolvingForUserService;
import com.lvl6.mobsters.services.monsterforuser.MonsterForUserService;
import com.lvl6.mobsters.services.monsterforuserdeleted.MonsterForUserDeletedService;
import com.lvl6.mobsters.services.user.UserService;
import com.lvl6.mobsters.services.usercurrencyhistory.UserCurrencyHistoryService;


@Component
public class EvolutionFinishedController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected UserService userService;

	@Autowired
	protected MonsterEvolvingForUserService monsterEvolvingForUserService;
	
	@Autowired
	protected MonsterForUserService monsterForUserService;
	
	@Autowired
	protected UserCurrencyHistoryService userCurrencyHistoryService;
	
	@Autowired
	protected CreateNoneventProtoUtil createNoneventProtoUtil;
	
	@Autowired
	protected CreateEventProtoUtil createEventProtoUtil;
	
	@Autowired
	protected MonsterForUserDeletedService monsterForUserDeletedService;

	public EvolutionFinishedController() {
		numAllocatedThreads = 4;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new EvolutionFinishedRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_ENHANCEMENT_WAIT_TIME_COMPLETE_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		EvolutionFinishedRequestProto reqProto = 
				((EvolutionFinishedRequestEvent) event).getEvolutionFinishedRequestProto();
		
		//get the values client sent
		MinimumUserProto senderProto = reqProto.getSender();
		//(positive number, server will convert it to negative)
		int gemsSpent = reqProto.getGemsSpent();
		Date clientTime = new Date();

		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = senderProto.getUserUuid();
		UUID userId = UUID.fromString(userIdString);
		
		//response to send back to client
		Builder responseBuilder = EvolutionFinishedResponseProto.newBuilder();
		responseBuilder.setStatus(EvolutionFinishedStatus.FAIL_OTHER);
		EvolutionFinishedResponseEvent resEvent =
				new EvolutionFinishedResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User aUser = getUserService().getUserWithId(userId);
			MonsterEvolvingForUser evolution = getMonsterEvolvingForUserService()
					.getEvolutionForUser(userId);
			
			//retrieve all the monsters used in evolution
			Map<UUID, MonsterForUser> existingUserMonsters = getMonsterForUserService()
	    			.getUserMonstersInEvolution(userId, evolution);
			
			//validate request
			//do check to make sure one monster has a null start time
			boolean legit = checkLegit(responseBuilder, aUser, userId, evolution,
					existingUserMonsters, gemsSpent);

			boolean successful = false;
			List<MonsterForUser> evolvedUserMonster = new ArrayList<MonsterForUser>();
			if (legit) {
				successful = writeChangesToDb(aUser, userId, clientTime, gemsSpent,
						evolution, existingUserMonsters, evolvedUserMonster);
			}

			if (successful) {
				MonsterForUser evolvedMfu = evolvedUserMonster.get(0);
				FullUserMonsterProto fump = getCreateNoneventProtoUtil()
						.createFullUserMonsterProtoFromUserMonster(evolvedMfu);
				responseBuilder.setEvolvedMonster(fump);
				responseBuilder.setStatus(EvolutionFinishedStatus.SUCCESS);
			}

			//write to client
			resEvent.setEvolutionFinishedResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);
			
			if (successful) {
				if (0 != gemsSpent) {
					UpdateClientUserResponseEvent resEventUpdate = getCreateEventProtoUtil()
							.createUpdateClientUserResponseEvent(aUser);
					resEventUpdate.setTag(event.getTag());
					getEventWriter().handleEvent(resEventUpdate);
				}
				
				MonsterForUser evolvedMfu = evolvedUserMonster.get(0);
				writeChangesToHistory(userId, clientTime, evolvedMfu, evolution,
						existingUserMonsters);
			}

		} catch (Exception e) {
			log.error("exception in EvolutionFinishedController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(EvolutionFinishedStatus.FAIL_OTHER);
				resEvent.setEvolutionFinishedResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in EvolutionFinishedController processRequestEvent", e2);
			}
		}
	}
	
	private boolean checkLegit(Builder resBuilder, User u, UUID userId,
			MonsterEvolvingForUser evolution,
			Map<UUID, MonsterForUser> existingUserMonsters, int gemsSpent) {

		if (null == u || null == evolution || null == existingUserMonsters ||
				existingUserMonsters.isEmpty()) {
			log.error("unexpected error: user, evolution, or existingMonsters is null. user=" +
					u + ",\t evolution="+ evolution + "\t existingMonsters=" + existingUserMonsters);
			return false;
		}
		
		UUID catalystUserMonsterId = evolution.getCatalystMonsterForUserId();
		UUID one = evolution.getMonsterForUserIdOne();
		UUID two = evolution.getMonsterForUserIdTwo();

		if (!existingUserMonsters.containsKey(catalystUserMonsterId) ||
				!existingUserMonsters.containsKey(one) || !existingUserMonsters.containsKey(two)) {
			log.error("one of the monsters in an evolution is missing. evolution=" + evolution +
					"\t existingUserMonsters=" + existingUserMonsters);
			resBuilder.setStatus(EvolutionFinishedStatus.FAIL_OTHER);
			return false;
		}
		
		//CHECK MONEY
		if (!hasEnoughGems(resBuilder, u, gemsSpent, evolution)) {
			return false;
		}

		return true;
	}

	//if gem cost is 0 and user gems is 0, then 0 !< 0 so no error issued
	private boolean hasEnoughGems(Builder resBuilder, User u, int gemsSpent,
			MonsterEvolvingForUser evolution) {
		int userGems = u.getGems();
		//if user's aggregate gems is < cost, don't allow transaction
		if (userGems < gemsSpent) {
			log.error("user error: user does not have enough gems. userGems=" + userGems +
					"\t gemsSpent=" + gemsSpent + "\t evolution=" + evolution);
			resBuilder.setStatus(EvolutionFinishedStatus.FAIL_INSUFFICIENT_GEMS);
			return false;
		}
		return true;
	}
	
	//List<MonsterForUser> evolved contains the evolved monster the user was making
	private boolean writeChangesToDb(User aUser, UUID uId, Date clientTime, int gemsSpent,
			MonsterEvolvingForUser mefu, Map<UUID, MonsterForUser> existingUserMonsters,
			List<MonsterForUser> evolved) {
		try {
			//CHARGE THE USER IF HE SPED UP THE EVOLUTION
			if (0 != gemsSpent) {
				//CHARGE THE USER
				int gemChange = -1 * gemsSpent;
				
				//create history first
				List<UserCurrencyHistory> uchList = createCurrencyHistory(aUser, clientTime,
						gemChange, mefu);
				int oilChange = 0;
				int cashChange = 0;
				getUserService().updateUserResources(aUser, gemChange, oilChange, cashChange);
				
				//keep track of currency stuff
				if (!uchList.isEmpty()) {
					getUserCurrencyHistoryService().saveCurrencyHistories(uchList);
				}
			}

			UUID catalystMfuId = mefu.getCatalystMonsterForUserId();
			UUID mfuIdOne = mefu.getMonsterForUserIdOne();
			UUID mfuIdTwo = mefu.getMonsterForUserIdTwo();

			//delete the monsters used in the evolution
			List<UUID> mfuIdsExpendedInEvolution = new ArrayList<UUID>();
			mfuIdsExpendedInEvolution.add(catalystMfuId);
			mfuIdsExpendedInEvolution.add(mfuIdOne);
			mfuIdsExpendedInEvolution.add(mfuIdTwo);
			getMonsterForUserService().deleteUserMonsters(mfuIdsExpendedInEvolution);
			
			//delete the evolution
			getMonsterEvolvingForUserService().deleteUserMonsterEvolving(mefu.getId());
			
			//doesn't matter if I used mfuIdTwo, either could work
			MonsterForUser mfu = existingUserMonsters.get(mfuIdOne);
			StringBuilder sourceOfPiecesSb = new StringBuilder();
			sourceOfPiecesSb.append("evolved from (catalystId, mfuIdOne, mfuIdTwo): (");
			sourceOfPiecesSb.append(catalystMfuId);
			sourceOfPiecesSb.append(",");
			sourceOfPiecesSb.append(mfuIdOne);
			sourceOfPiecesSb.append(",");
			sourceOfPiecesSb.append(mfuIdTwo);
			sourceOfPiecesSb.append(")");
			String sourceOfPieces = sourceOfPiecesSb.toString();
			
			//give the user the new evolved monster
			MonsterForUser evolvedMfu = getMonsterForUserService()
					.createEvolvedMonster(uId, mfu, mefu, clientTime, sourceOfPieces);
			
			evolved.add(evolvedMfu);
			
			return true;
		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
	}

	private List<UserCurrencyHistory> createCurrencyHistory(User aUser, Date clientTime,
			int gemChange, MonsterEvolvingForUser mefu) {
		String gemsStr = MobstersDbTables.USER__GEMS;
		String reasonForChange = MobstersTableConstants.UCHRFC__SPED_UP_EVOLVING;
		StringBuilder detailSb = new StringBuilder();
		
		detailSb.append("(catalystMfuId, mfuIdOne, mfuIdTwo, startTime): ");
		detailSb.append("(");
		detailSb.append(mefu.getCatalystMonsterForUserId());
		detailSb.append(", ");
		detailSb.append(mefu.getMonsterForUserIdOne());
		detailSb.append(", ");
		detailSb.append(mefu.getMonsterForUserIdTwo());
		detailSb.append(", ");
		detailSb.append(mefu.getStartTime());
		detailSb.append(") ");
		String details = detailSb.toString();

		boolean saveToDb = false;
		UserCurrencyHistory gems = getUserCurrencyHistoryService()
				.createNewUserCurrencyHistory(aUser, clientTime, gemsStr, gemChange,
						reasonForChange, details, saveToDb);

		List<UserCurrencyHistory> uchList = new ArrayList<UserCurrencyHistory>();
		if (null != gems) {
			uchList.add(gems);
		}
		return uchList;
	}
	
	private void writeChangesToHistory(UUID uId, Date deleteTime, MonsterForUser evolved,
			MonsterEvolvingForUser mefu, Map<UUID, MonsterForUser> idsToUserMonsters) {
		UUID catalystMfuId = mefu.getCatalystMonsterForUserId();
		UUID mfuIdOne = mefu.getMonsterForUserIdOne();
		UUID mfuIdTwo = mefu.getMonsterForUserIdTwo();
		
		String deleteReason = MobstersTableConstants.MFUDR__EVOLVING;
		StringBuilder detailSb = new StringBuilder();
		detailSb.append("expended in evolution to create mfuId=");
		detailSb.append(evolved.getId());
		String detail = detailSb.toString();
		
		Map<UUID, String> details = new HashMap<UUID, String>();
		details.put(catalystMfuId, detail);
		details.put(mfuIdOne, detail);
		details.put(mfuIdOne, detail);
		
		Map<UUID, MonsterForUser> idsToUserMonstersCopy = new HashMap<UUID, MonsterForUser>();
		MonsterForUser catalyst = idsToUserMonsters.get(catalystMfuId);
		MonsterForUser one = idsToUserMonsters.get(mfuIdOne);
		MonsterForUser two = idsToUserMonsters.get(mfuIdTwo);
		
		idsToUserMonstersCopy.put(catalystMfuId, catalyst);
		idsToUserMonstersCopy.put(mfuIdOne, one);
		idsToUserMonstersCopy.put(mfuIdTwo, two);
		
		//record in the delete history
		getMonsterForUserDeletedService().createUserMonsterDeletedFromUserMonsters(
				deleteReason, details, deleteTime, idsToUserMonstersCopy);
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public MonsterEvolvingForUserService getMonsterEvolvingForUserService() {
		return monsterEvolvingForUserService;
	}

	public void setMonsterEvolvingForUserService(
			MonsterEvolvingForUserService monsterEvolvingForUserService) {
		this.monsterEvolvingForUserService = monsterEvolvingForUserService;
	}

	public MonsterForUserService getMonsterForUserService() {
		return monsterForUserService;
	}

	public void setMonsterForUserService(MonsterForUserService monsterForUserService) {
		this.monsterForUserService = monsterForUserService;
	}

	public UserCurrencyHistoryService getUserCurrencyHistoryService() {
		return userCurrencyHistoryService;
	}

	public void setUserCurrencyHistoryService(
			UserCurrencyHistoryService userCurrencyHistoryService) {
		this.userCurrencyHistoryService = userCurrencyHistoryService;
	}

	public CreateNoneventProtoUtil getCreateNoneventProtoUtil() {
		return createNoneventProtoUtil;
	}

	public void setCreateNoneventProtoUtil(
			CreateNoneventProtoUtil createNoneventProtoUtil) {
		this.createNoneventProtoUtil = createNoneventProtoUtil;
	}

	public CreateEventProtoUtil getCreateEventProtoUtil() {
		return createEventProtoUtil;
	}

	public void setCreateEventProtoUtil(CreateEventProtoUtil createEventProtoUtil) {
		this.createEventProtoUtil = createEventProtoUtil;
	}

	public MonsterForUserDeletedService getMonsterForUserDeletedService() {
		return monsterForUserDeletedService;
	}

	public void setMonsterForUserDeletedService(
			MonsterForUserDeletedService monsterForUserDeletedService) {
		this.monsterForUserDeletedService = monsterForUserDeletedService;
	}

}
