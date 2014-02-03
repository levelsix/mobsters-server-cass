package com.lvl6.mobsters.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.controller.utils.CreateEventProtoUtil;
import com.lvl6.mobsters.controller.utils.MiscUtil;
import com.lvl6.mobsters.controller.utils.MonsterStuffUtil;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.EvolveMonsterRequestProto;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.EvolveMonsterResponseProto;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.EvolveMonsterResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.EvolveMonsterResponseProto.EvolveMonsterStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.EvolveMonsterRequestEvent;
import com.lvl6.mobsters.events.response.EvolveMonsterResponseEvent;
import com.lvl6.mobsters.events.response.UpdateClientUserResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.UserMonsterEvolutionProto;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.po.nonstaticdata.MonsterEnhancingForUser;
import com.lvl6.mobsters.po.nonstaticdata.MonsterEvolvingForUser;
import com.lvl6.mobsters.po.nonstaticdata.MonsterForUser;
import com.lvl6.mobsters.po.nonstaticdata.MonsterHealingForUser;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.po.nonstaticdata.UserCurrencyHistory;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.properties.MobstersTableConstants;
import com.lvl6.mobsters.services.monsterenhancingforuser.MonsterEnhancingForUserService;
import com.lvl6.mobsters.services.monsterenhancingforuser.MonsterEnhancingHistoryService;
import com.lvl6.mobsters.services.monsterevolvingforuser.MonsterEvolvingForUserService;
import com.lvl6.mobsters.services.monsterforuser.MonsterForUserService;
import com.lvl6.mobsters.services.monsterhealingforuser.MonsterHealingForUserService;
import com.lvl6.mobsters.services.user.UserService;
import com.lvl6.mobsters.services.usercurrencyhistory.UserCurrencyHistoryService;
import com.lvl6.mobsters.utils.QueryConstructionUtil;


@Component
public class EvolveMonsterController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	@Autowired
	protected MiscUtil miscUtil;

	@Autowired
	protected UserService userService;
	
	@Autowired
	protected MonsterStuffUtil monsterStuffUtil;

	@Autowired
	protected MonsterEnhancingForUserService monsterEnhancingForUserService;
	
	@Autowired
	protected MonsterHealingForUserService monsterHealingForUserService;
	
	@Autowired
	protected MonsterEvolvingForUserService monsterEvolvingForUserService;
	
	@Autowired
	protected MonsterForUserService monsterForUserService;
	
	@Autowired
	protected UserCurrencyHistoryService userCurrencyHistoryService;
	
	@Autowired
	protected CreateEventProtoUtil createEventProtoUtil;

	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;
	
	@Autowired
	protected MonsterEnhancingHistoryService monsterEnhancingHistoryService;
	
	

	public EvolveMonsterController() {
		numAllocatedThreads = 3;
	}
	
	@Override
	public RequestEvent createRequestEvent() {
		return new EvolveMonsterRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_EVOLVE_MONSTER_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		EvolveMonsterRequestProto reqProto = 
				((EvolveMonsterRequestEvent) event).getEvolveMonsterRequestProto();
		
		//get the values client sent
		MinimumUserProto senderProto = reqProto.getSender();
		UserMonsterEvolutionProto uep = reqProto.getEvolution();
		
		int gemsSpent = reqProto.getGemsSpent();
		//positive means refund, negative means charge user
		int oilChange = reqProto.getOilChange();

		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = senderProto.getUserUuid();
		UUID userId = UUID.fromString(userIdString);
		
		UUID catalystUserMonsterId = null;
		List<UUID> evolvingUserMonsterIds = new ArrayList<UUID>();
		Date clientTime = null;

		if (null != uep && reqProto.hasEvolution()) {
			log.info("uep is not null");
			String catalystIdStr = uep.getCatalystUserMonsterId();
			catalystUserMonsterId = UUID.fromString(catalystIdStr);
			List<String> userMonsterIdStrList = uep.getUserMonsterIdsList();
			evolvingUserMonsterIds = getMiscUtil().createUUIDListFromStrings(userMonsterIdStrList);
			clientTime = new Date(uep.getStartTime());
		}
		
		
		//response to send back to client
		Builder responseBuilder = EvolveMonsterResponseProto.newBuilder();
		responseBuilder.setSender(senderProto);
		responseBuilder.setStatus(EvolveMonsterStatus.FAIL_OTHER);
		EvolveMonsterResponseEvent resEvent = new EvolveMonsterResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User aUser = getUserService().getUserWithId(userId);
			Map<UUID, MonsterEnhancingForUser> alreadyEnhancing = getMonsterEnhancingForUserService()
					.getMonstersEnhancingForUser(userId);
			Map<UUID, MonsterHealingForUser> alreadyHealing = getMonsterHealingForUserService()
					.getUserMonsterIdsToUserMonstersHealingForUser(userId);
			Map<UUID, MonsterEvolvingForUser> alreadyEvolving = getMonsterEvolvingForUserService()
					.getCatalystIdsToEvolutionsForUser(userId);

			//retrieve all the new monsters
			Map<UUID, MonsterForUser> existingUserMonsters = new HashMap<UUID, MonsterForUser>();

			//just in case uep is null, but most likely not. retrieve all the monsters used
			//in evolution, just to make sure they exist
			if (null != uep && reqProto.hasEvolution()) {
				Set<UUID> idsForEvolution = new HashSet<UUID>();
				idsForEvolution.add(catalystUserMonsterId);
				idsForEvolution.addAll(evolvingUserMonsterIds);
				existingUserMonsters = getMonsterForUserService()
						.getSpecificOrAllUserMonstersForUser(userId, idsForEvolution);
				log.info("retrieved user monsters. existingUserMonsters=" + existingUserMonsters);
			}

			
			//validate request
			boolean validRequest = checkLegit(responseBuilder, aUser, userId, existingUserMonsters, 
					alreadyEnhancing, alreadyHealing, alreadyEvolving, catalystUserMonsterId,
					evolvingUserMonsterIds, gemsSpent, oilChange);

			boolean successful = false;
			if (validRequest) {
				successful = writeChangesToDb(aUser, userId, gemsSpent, oilChange,
						catalystUserMonsterId, evolvingUserMonsterIds, clientTime);
			}

			if (successful) {
				responseBuilder.setStatus(EvolveMonsterStatus.SUCCESS);
			}

			//write to client
			resEvent.setEvolveMonsterResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);
			
			if (successful) {
				//since modified user's resources need to send update client user event
				UpdateClientUserResponseEvent resEventUpdate = getCreateEventProtoUtil()
						.createUpdateClientUserResponseEvent(aUser);
				resEventUpdate.setTag(event.getTag());
				getEventWriter().handleEvent(resEventUpdate);
			}

		} catch (Exception e) {
			log.error("exception in EvolveMonsterController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(EvolveMonsterStatus.FAIL_OTHER);
				resEvent.setEvolveMonsterResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in EvolveMonsterController processRequestEvent", e2);
			}
		}
	}
	
	private boolean checkLegit(Builder resBuilder, User u, UUID userId,
			Map<UUID, MonsterForUser> existingUserMonsters,
			Map<UUID, MonsterEnhancingForUser> alreadyEnhancing,
			Map<UUID, MonsterHealingForUser> alreadyHealing,
			Map<UUID, MonsterEvolvingForUser> alreadyEvolving, UUID catalystUserMonsterId,
			List<UUID> userMonsterIds, int gemsSpent, int oilChange) {
		if (null == u ) {
			log.error("unexpected error: user is null. user=" + u + "\t catalystUserMonsterId="+
					catalystUserMonsterId + "\t userMonsterIds=" + userMonsterIds);
			return false;
		}

		//at the moment only 3 are required to evolve a monster
		if (null == existingUserMonsters || existingUserMonsters.isEmpty() ||
				3 != existingUserMonsters.size()) {
			log.error("user trying to user nonexistent monster in evolution. existing=" +
					existingUserMonsters + "\t catalyst=" + catalystUserMonsterId + "\t others=" +
					userMonsterIds);
			resBuilder.setStatus(EvolveMonsterStatus.FAIL_NONEXISTENT_MONSTERS);
			return false;
		}


		//at the moment only one evolution is allowed going on at any one time
		if (null != alreadyEvolving && !alreadyEvolving.isEmpty()) {
			log.error("user already evolving monsters. monsters=" + alreadyEvolving);
			resBuilder.setStatus(EvolveMonsterStatus.FAIL_MAX_NUM_EVOLUTIONS_REACHED);
			return false;
		}

		//don't allow this transaction through because at least one of these monsters is
		//used in enhancing or is being healed
		if ((null != alreadyEnhancing && !alreadyEnhancing.isEmpty()) ||
				(null != alreadyHealing && !alreadyHealing.isEmpty())) {
			log.error("the monsters provided are in healing or enhancing. enhancing=" +
					alreadyEnhancing + "\t healing=" + alreadyHealing + "\t catalyst=" +
					catalystUserMonsterId + "\t others=" + userMonsterIds);
			return false;
		}
		
		//CHECK MONEY
		if (!hasEnoughGems(resBuilder, u, gemsSpent, oilChange, catalystUserMonsterId, userMonsterIds)) {
			return false;
		}

		if (!hasEnoughOil(resBuilder, u, gemsSpent, oilChange, catalystUserMonsterId, userMonsterIds)) {
			return false;
		}

		if (0 == gemsSpent && 0 == oilChange) {
			log.error("gemsSpent=" + gemsSpent + "\t oilChange=" + oilChange + "\t Not evolving.");
			return false;
		}
		
		return true;
	}
	
	//if gem cost is 0 and user gems is 0, then 0 !< 0 so no error issued
	private boolean hasEnoughGems(Builder resBuilder, User u, int gemsSpent,
			int oilChange, UUID catalyst, List<UUID> userMonsterIds) {
		int userGems = u.getGems();
		//if user's aggregate gems is < cost, don't allow transaction
		if (userGems < gemsSpent) {
			log.error("user error: user does not have enough gems. userGems=" + userGems +
					"\t gemsSpent=" + gemsSpent + "\t oilChange=" + oilChange + "\t catalyst=" +
					catalyst + "\t userMonsterIds=" + userMonsterIds);
			resBuilder.setStatus(EvolveMonsterStatus.FAIL_INSUFFICIENT_GEMS);
			return false;
		}
		return true;
	}

	private boolean hasEnoughOil(Builder resBuilder, User u, int gemsSpent,
			int oilChange, UUID catalyst, List<UUID> userMonsterIds) {
		int userOil = u.getOil(); 
		//positive 'cashChange' means refund, negative means charge user
		int cost = -1 * oilChange;

		//if user not spending gems check if user has enough oil
		if (0 == gemsSpent && userOil < cost) {
			log.error("user error: user does not have enough cash. cost=" + cost +
					"\t oilChange=" + oilChange + "\t catalyst=" + catalyst + 
					"\t userMonsterIds=" + userMonsterIds);
			resBuilder.setStatus(EvolveMonsterStatus.FAIL_INSUFFICIENT_RESOURCES);
			return false;
		}
		return true;
	}
	
	

	private boolean writeChangesToDb(User user, UUID uId, int gemsSpent, int oilChange,
			UUID catalystUserMonsterId, List<UUID> userMonsterIds, Date clientTime) {
		try {
			//CHARGE THE USER
			int gemChange = -1 * gemsSpent;
			
			//create history first
			List<UserCurrencyHistory> uchList = createCurrencyHistory(user, clientTime,
					oilChange, gemChange, catalystUserMonsterId, userMonsterIds);
			int cashChange = 0;
			getUserService().updateUserResources(user, gemChange, oilChange, cashChange);

			//keep track of currency stuff
			if (!uchList.isEmpty()) {
				getUserCurrencyHistoryService().saveCurrencyHistories(uchList);
			}
			
			//insert into monster_evolving_for_user table
			getMonsterEvolvingForUserService().insertIntoMonsterEvolvingForUser(uId,
					clientTime, catalystUserMonsterId, userMonsterIds);
			
			
			return true;
		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
	}

	private List<UserCurrencyHistory> createCurrencyHistory(User aUser, Date clientTime,
			int oilChange, int gemChange, UUID catalystId, List<UUID> userMonsterIds) {
		String oilStr = MobstersDbTables.USER__OIL;
		String gemsStr = MobstersDbTables.USER__GEMS;
		String reasonForChange = MobstersTableConstants.UCHRFC__EVOLVING;
		if (gemChange > 0) {
			reasonForChange = MobstersTableConstants.UCHRFC__SPED_UP_EVOLVING;
		}
		
		StringBuilder detailSb = new StringBuilder();
		detailSb.append("(catalystId, userMonsterId, userMonsterId");
		//maybe shouldn't keep track...oh well, more info hopefully is better than none
		detailSb.append("(");
		detailSb.append(catalystId);
		detailSb.append(",");
		UUID one = userMonsterIds.get(0);
		detailSb.append(one);
		UUID two = userMonsterIds.get(1);
		detailSb.append(two);
		
		String details = detailSb.toString();

		boolean saveToDb = false;
		UserCurrencyHistory oil = getUserCurrencyHistoryService()
				.createNewUserCurrencyHistory(aUser, clientTime, oilStr, oilChange,
						reasonForChange, details, saveToDb);
		UserCurrencyHistory gems = getUserCurrencyHistoryService()
				.createNewUserCurrencyHistory(aUser, clientTime, gemsStr, gemChange,
						reasonForChange, details, saveToDb);

		List<UserCurrencyHistory> uchList = new ArrayList<UserCurrencyHistory>();
		if (null != oil) {
			uchList.add(oil);
		}
		if (null != gems) {
			uchList.add(gems);
		}
		return uchList;
	}

	public MiscUtil getMiscUtil() {
		return miscUtil;
	}

	public void setMiscUtil(MiscUtil miscUtil) {
		this.miscUtil = miscUtil;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public MonsterStuffUtil getMonsterStuffUtil() {
		return monsterStuffUtil;
	}

	public void setMonsterStuffUtil(MonsterStuffUtil monsterStuffUtil) {
		this.monsterStuffUtil = monsterStuffUtil;
	}

	public MonsterEnhancingForUserService getMonsterEnhancingForUserService() {
		return monsterEnhancingForUserService;
	}

	public void setMonsterEnhancingForUserService(
			MonsterEnhancingForUserService monsterEnhancingForUserService) {
		this.monsterEnhancingForUserService = monsterEnhancingForUserService;
	}

	public MonsterHealingForUserService getMonsterHealingForUserService() {
		return monsterHealingForUserService;
	}

	public void setMonsterHealingForUserService(
			MonsterHealingForUserService monsterHealingForUserService) {
		this.monsterHealingForUserService = monsterHealingForUserService;
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

	public CreateEventProtoUtil getCreateEventProtoUtil() {
		return createEventProtoUtil;
	}

	public void setCreateEventProtoUtil(CreateEventProtoUtil createEventProtoUtil) {
		this.createEventProtoUtil = createEventProtoUtil;
	}

	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}

	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}

	public MonsterEnhancingHistoryService getMonsterEnhancingHistoryService() {
		return monsterEnhancingHistoryService;
	}

	public void setMonsterEnhancingHistoryService(
			MonsterEnhancingHistoryService monsterEnhancingHistoryService) {
		this.monsterEnhancingHistoryService = monsterEnhancingHistoryService;
	}
	
}
