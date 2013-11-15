package com.lvl6.aoc2.controller;

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

import com.lvl6.aoc2.controller.utils.TimeUtils;
import com.lvl6.aoc2.entitymanager.UserEntityManager;
import com.lvl6.aoc2.entitymanager.UserEquipEntityManager;
import com.lvl6.aoc2.entitymanager.staticdata.EquipmentRetrieveUtils;
import com.lvl6.aoc2.eventprotos.RepairEquipEventProto.RepairEquipRequestProto;
import com.lvl6.aoc2.eventprotos.RepairEquipEventProto.RepairEquipResponseProto;
import com.lvl6.aoc2.eventprotos.RepairEquipEventProto.RepairEquipResponseProto.Builder;
import com.lvl6.aoc2.eventprotos.RepairEquipEventProto.RepairEquipResponseProto.RepairEquipStatus;
import com.lvl6.aoc2.events.RequestEvent;
import com.lvl6.aoc2.events.request.RepairEquipRequestEvent;
import com.lvl6.aoc2.events.response.RepairEquipResponseEvent;
import com.lvl6.aoc2.noneventprotos.AocTwoEventProtocolProto.AocTwoEventProtocolRequest;
import com.lvl6.aoc2.noneventprotos.FullUser.MinimumUserProto;
import com.lvl6.aoc2.noneventprotos.UserEquipRepair.UserEquipRepairProto;
import com.lvl6.aoc2.po.User;
import com.lvl6.aoc2.po.UserEquip;
import com.lvl6.aoc2.po.UserEquipRepair;
import com.lvl6.aoc2.properties.AocTwoTableConstants;
import com.lvl6.aoc2.services.user.UserService;
import com.lvl6.aoc2.services.userequip.UserEquipService;
import com.lvl6.aoc2.services.userequiprepair.UserEquipRepairService;


@Component
public class RepairEquipController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected EquipmentRetrieveUtils equipmentRetrieveUtils; 

	@Autowired
	protected UserEquipRepairService userEquipRepairService;

	@Autowired
	protected UserEquipService userEquipService;

	@Autowired
	protected UserEntityManager userEntityManager;
	
	@Autowired
	protected UserEquipEntityManager userEquipEntityManager;

	@Autowired
	protected TimeUtils timeUtils;

	@Autowired
	protected UserService userService;




	@Override
	public RequestEvent createRequestEvent() {
		return new RepairEquipRequestEvent();
	}

	@Override
	public int getEventType() {
		return AocTwoEventProtocolRequest.C_REPAIR_EQUIP_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		RepairEquipRequestProto reqProto = 
				((RepairEquipRequestEvent) event).getRepairEquipRequestProto();

		//get the values client sent
		MinimumUserProto sender = reqProto.getMup();
		List<UserEquipRepairProto> uerpDelete = reqProto.getUerpDeleteList();
		List<UserEquipRepairProto> uerpUpdate = reqProto.getUerpUpdateList();
		List<UserEquipRepairProto> uerpNew = reqProto.getUerpNewList();
		List<String> equipStringBeingRepaired = reqProto.getEquipsBeingRepairedIdList();
		List<UUID> equipIdsBeingRepaired = new ArrayList<>();
		boolean usingGems = reqProto.getUsingGems();
		for(String s : equipStringBeingRepaired) {
			UUID equipId = UUID.fromString(s);
			equipIdsBeingRepaired.add(equipId);
		}
		
		Date clientDate = new Date(reqProto.getClientTimeMillis());

		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = sender.getUserID();
		UUID userId = UUID.fromString(userIdString);

		//response to send back to client
		Builder responseBuilder = RepairEquipResponseProto.newBuilder();
		responseBuilder.setStatus(RepairEquipStatus.FAIL_OTHER);
		RepairEquipResponseEvent resEvent =
				new RepairEquipResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User existingU = getUserEntityManager().get().get(userId);
			Map<UUID, UserEquipRepair> repairsInDb = 
					getUserEquipRepairService().getEquipsBeingRepaired(userIdString);

			//will be populated in isValidRequest
			Map<UUID, UserEquip> prospectiveRepairs = new HashMap<UUID, UserEquip>(); 

			//validate request
			boolean validRequest = isValidRequest(responseBuilder, sender,
					existingU, userIdString, uerpDelete, uerpUpdate, uerpNew,
					repairsInDb, prospectiveRepairs, usingGems, clientDate);

			boolean successful = false;
			if (validRequest) {
				successful = writeChangesToDb(existingU, uerpDelete,
						uerpUpdate, uerpNew, repairsInDb, equipIdsBeingRepaired);
			}

			if (successful) {
				responseBuilder.setStatus(RepairEquipStatus.SUCCESS);
			}

			//write to client
			resEvent.setRepairEquipResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in RepairEquipController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(RepairEquipStatus.FAIL_OTHER);
				resEvent.setRepairEquipResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in RepairEquipController processRequestEvent", e2);
			}
		}
	}


	private boolean isValidRequest(Builder responseBuilder, MinimumUserProto sender,
			User existingU, String userIdString, List<UserEquipRepairProto> uerpDelete,
			List<UserEquipRepairProto> uerpUpdate, List<UserEquipRepairProto> uerpNew,
			Map<UUID, UserEquipRepair> repairsInDb,  Map<UUID, UserEquip> prospectiveRepairs, 
			boolean usingGems, Date clientDate) throws Exception {
		boolean inDbEmpty = (null == repairsInDb || repairsInDb.isEmpty());
		boolean uerpDeleteNonEmpty = (null != uerpDelete && !uerpDelete.isEmpty());
		boolean uerpUpdateNonEmpty = null != uerpUpdate && !uerpUpdate.isEmpty(); 

		//mismatch between what's recorded on the server and what client
		//thinks is recorded
		if ((uerpDeleteNonEmpty || uerpUpdateNonEmpty) && inDbEmpty) {
			log.error("unexpected error: either server did not save equips-to-" +
					"be-repaired, or client is sending incorrectly.");
			return false;
		}

		Set<UUID> inDbKeys = repairsInDb.keySet();

		//check if what client deletes/updates are in the db.
		Set<UUID> uerDeleteIds = getUerIds(uerpDelete);
		if (!inDbKeys.containsAll(uerDeleteIds)) {
			log.error("unexpected error: either server did not modify" +
					"equips-to-be-repaired, or client is sending incorrectly. " +
					"uerDeleteIds=" + uerDeleteIds);
			return false;
		}
		Set<UUID> uerUpdateIds = getUerIds(uerpUpdate);
		if (!inDbKeys.containsAll(uerUpdateIds)) {
			log.error("unexpected error: either server did not modify" +
					"equips-to-be-repaired, or client is sending incorrectly. " +
					"uerUpdateIds=" + uerUpdateIds);
			return false;
		}

		//CHECK IF TIMES ARE IN SYNC
		if (!getTimeUtils().isSynchronizedWithServerTime(clientDate)) {
			log.error("user error: client time diverges from server time. clientTime="
					+ clientDate + ", approximateServerTime=" + new Date());
			responseBuilder.setStatus(RepairEquipStatus.FAIL_UNSYNCHRONIZED_TIMES);
			return false;
		}


		Set<UUID> newIds = getUerIds(uerpNew);
		Map<UUID, UserEquip> newEquipsToRepair = 
				getUserEquipService().getUserEquipsByUserEquipIds(newIds);

		
		if (!hasEnoughFunds(uerpDelete, newEquipsToRepair, existingU, repairsInDb)) {
			log.error("unexpected error: user has not enough funds to repair " +
					"equip. user="+ userIdString + "\t uerpDelete=" + uerpDelete +
					"\t uerpNew=" + uerpNew);
			responseBuilder.setStatus(RepairEquipStatus.FAIL_NOT_ENOUGH_RESOURCES);
			return false;
		}

		prospectiveRepairs.putAll(newEquipsToRepair);

		return true;
	}

	//get user equip repair ids (the primary key of the table)
	private Set<UUID> getUerIds(List<UserEquipRepairProto> aList) throws Exception {
		Set<UUID> ids = new HashSet<UUID>();

		for (UserEquipRepairProto uerp : aList) {
			String uerIdString = uerp.getUserEquipRepairID();
			UUID uerId = UUID.fromString(uerIdString);

			if (ids.contains(uerIdString)) {
				String msg = "client error: duplicate id=" + uerIdString; 
				log.error(msg);
				throw new Exception(msg);
			}
			ids.add(uerId);
		}
		return ids;
	}

	private boolean hasEnoughFunds(List<UserEquipRepairProto> uerpDelete,
			Map<UUID, UserEquip> newEquipsToRepair, User existingU,
			Map<UUID, UserEquipRepair> repairsInDb) {

		//get the equips that the user cancelled from being repaired
		Set<UUID> idsDelete = getEquipIds(uerpDelete);
		List<UserEquipRepair> deleteRepairs = new ArrayList<UserEquipRepair>();
		for (UUID id : idsDelete) {
			deleteRepairs.add(repairsInDb.get(id));
		}

		int goldLeft = existingU.getGold();
		int tonicLeft = existingU.getTonic();
		//user might have deleted things from the queue, refund him, before
		//charging the new equips to repair
		Map<Integer, Integer> totalRefund =
				getUserEquipRepairService().calculateRepairCost(null, deleteRepairs, -1);
		if (totalRefund.containsKey(AocTwoTableConstants.RESOURCE_TYPE__GOLD)) {
			goldLeft += totalRefund.get(AocTwoTableConstants.RESOURCE_TYPE__GOLD);
		}
		if (totalRefund.containsKey(AocTwoTableConstants.RESOURCE_TYPE__TONIC)) {
			tonicLeft += totalRefund.get(AocTwoTableConstants.RESOURCE_TYPE__TONIC);
		}

		//calculate how much to charge for new equips
		List<UserEquip> newEquips = new ArrayList<UserEquip>(newEquipsToRepair.values());
		Map<Integer, Integer> totalCost =
				getUserEquipRepairService().calculateRepairCost(newEquips, null, 1);
		if (totalCost.containsKey(AocTwoTableConstants.RESOURCE_TYPE__GOLD)) {
			goldLeft -= totalCost.get(AocTwoTableConstants.RESOURCE_TYPE__GOLD);
		}
		if (totalCost.containsKey(AocTwoTableConstants.RESOURCE_TYPE__TONIC)) {
			tonicLeft -= totalCost.get(AocTwoTableConstants.RESOURCE_TYPE__TONIC);
		}

		if (goldLeft < 0 || tonicLeft < 0) {
			return false;
		} else {
			return false;
		}
	}

	private Set<UUID> getEquipIds(List<UserEquipRepairProto> uerpList) {
		Set<UUID> uniqIds = new HashSet<UUID>();
		for (UserEquipRepairProto uerp : uerpList) {
			String equipIdStr = uerp.getEquipId();
			UUID equipId = UUID.fromString(equipIdStr);
			uniqIds.add(equipId);
		}

		return uniqIds;
	}


	private boolean writeChangesToDb(User u,
			List<UserEquipRepairProto> uerpDelete, List<UserEquipRepairProto> uerpUpdate,
			List<UserEquipRepairProto> uerpNew, Map<UUID, UserEquipRepair> repairsInDb, List<UUID> equipIdsBeingRepaired) {
		try {
			//determine the refund
			List<UserEquip> unrepaired = deleteExisting(uerpDelete, repairsInDb);
			Map<Integer, Integer> refund = getUserEquipRepairService()
					.calculateRepairCost(unrepaired, null, -1); 

			updateExisting(uerpUpdate, repairsInDb);

			//determine the costs
			List<UserEquipRepair> newRepairs = addNew(uerpNew, equipIdsBeingRepaired);
			Map<Integer, Integer> cost = getUserEquipRepairService()
					.calculateRepairCost(null, newRepairs, 1);


			modifyUserResources(u, refund, cost);
			return true;

		} catch (Exception e) {
			log.error("unexpected error: problem with saving to db.", e);
		}
		return false;
	}

	//GET THE EQUIPS-TO-DELETE AND DELETE THEM, PUTTING THEM BACK INTO
	//USER EQUIP
	private List<UserEquip> deleteExisting(List<UserEquipRepairProto> uerp,
			Map<UUID, UserEquipRepair> repairsInDb) throws Exception {

		Set<UUID> idsToBeDeleted = getUerIds(uerp);
		getUserEquipRepairService().deleteUserEquipRepairs(idsToBeDeleted);

		//equips to add to user equips
		List<UserEquip> unrepaired = new ArrayList<UserEquip>();

		for(UUID id : idsToBeDeleted) {
			UserEquipRepair uer = repairsInDb.remove(id);

			UserEquip ue = new UserEquip();

			ue.setUserId(uer.getUserId());
			ue.setEquipId(uer.getEquipId());
//			ue.setName(uer.getName());
//			ue.setEquipLevel(uer.getEquipLevel());
			ue.setDurability(uer.getDurability());
			ue.setEquipped(false);

			unrepaired.add(ue);
		}

		//put back into user equips
		getUserEquipService().saveEquips(unrepaired);

		return unrepaired;
	}

	//UPDATE THE ONES THAT CLIENT WANTS UPDATED
	private void updateExisting(List<UserEquipRepairProto> uerpList, 
			Map<UUID, UserEquipRepair> repairsInDb) throws Exception {
		List<UserEquipRepair> updated = new ArrayList<UserEquipRepair>();

		//Summary: go though inDb objects and replace its values with the
		//proto values (only expectedStartMillis should change)
		for (UserEquipRepairProto uer : uerpList) {
			long millis = uer.getExpectedStartMillis();
			Date newDate = new Date(millis);

			String idString = uer.getUserEquipRepairID();
			UUID id = UUID.fromString(idString);

			UserEquipRepair inDb = repairsInDb.get(id);
			inDb.setExpectedStart(newDate);

			updated.add(inDb);
		}

		getUserEquipRepairService().saveUserEquipRepairs(updated);
	}

	private List<UserEquipRepair> addNew(List<UserEquipRepairProto> uerpList,  List<UUID> equipIdsBeingRepaired) {
		List<UserEquipRepair> newStuff = new ArrayList<UserEquipRepair>();

		for(UserEquipRepairProto uerp : uerpList) {
			UserEquipRepair uer = new UserEquipRepair();
			UUID userId = UUID.fromString(uerp.getUserID());
			uer.setUserId(userId);

//			String equipName = uerp.getEquipName();
//			uer.setName(equipName);
			String equipIdStr = uerp.getEquipId();
			UUID equipId = UUID.fromString(equipIdStr);
			uer.setEquipId(equipId);
//			uer.setEquipLevel(uerp.getEquipLevel());
			uer.setDurability(uerp.getDurability());
			Date expectedStart = new Date(uerp.getExpectedStartMillis());
			uer.setExpectedStart(expectedStart);
			Date enteredQueue = new Date(uerp.getQueuedTimeMillis());
			uer.setEnteredQueue(enteredQueue);
			uer.setDungeonRoomOrChestAcquiredFrom(uerp.getDungeonRoomOrChestAcquiredFrom());
			uer.setLevelOfUserWhenAcquired(uerp.getLevelOfUserWhenAcquired());
			Date d = new Date(uerp.getTimeAcquired());
			uer.setTimeAcquired(d);

			newStuff.add(uer);
		}
		getUserEquipRepairService().saveUserEquipRepairs(newStuff);
		
		//delete equips from user equips
		for(UUID equipId : equipIdsBeingRepaired) {
			getUserEquipEntityManager().get().delete(equipId);
		}
		

		return newStuff;
	}

	//aggregate refund and cost and then upate user's resources
	private void modifyUserResources(User u, Map<Integer, Integer> refund,
			Map<Integer, Integer> cost) {
		Map<Integer, Integer> total = new HashMap<Integer, Integer>();
		
		int gold = AocTwoTableConstants.RESOURCE_TYPE__GOLD;
		int tonic = AocTwoTableConstants.RESOURCE_TYPE__TONIC;

		int goldChange = 0;
		int tonicChange = 0;
		if (refund.containsKey(tonic)) {
			tonicChange += refund.get(tonic);
		}
		if (cost.containsKey(tonic)) {
			tonicChange += cost.get(tonic);
		}

		if (refund.containsKey(gold)) {
			goldChange += refund.get(gold);
		}
		if (cost.containsKey(gold)) {
			goldChange += cost.get(gold);
		}

		total.put(goldChange, tonicChange);
		getUserService().updateUserResources(u, total);
	}






	public EquipmentRetrieveUtils getEquipmentRetrieveUtils() {
		return equipmentRetrieveUtils;
	}

	public void setEquipmentRetrieveUtils(
			EquipmentRetrieveUtils equipmentRetrieveUtils) {
		this.equipmentRetrieveUtils = equipmentRetrieveUtils;
	}

	public UserEquipRepairService getUserEquipRepairService() {
		return userEquipRepairService;
	}

	public void setUserEquipRepairService(
			UserEquipRepairService userEquipRepairService) {
		this.userEquipRepairService = userEquipRepairService;
	}

	public UserEquipService getUserEquipService() {
		return userEquipService;
	}

	public void setUserEquipService(UserEquipService userEquipService) {
		this.userEquipService = userEquipService;
	}

	public UserEntityManager getUserEntityManager() {
		return userEntityManager;
	}

	public void setUserEntityManager(UserEntityManager userEntityManager) {
		this.userEntityManager = userEntityManager;
	}

	public TimeUtils getTimeUtils() {
		return timeUtils;
	}

	public void setTimeUtils(TimeUtils timeUtils) {
		this.timeUtils = timeUtils;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public UserEquipEntityManager getUserEquipEntityManager() {
		return userEquipEntityManager;
	}

	public void setUserEquipEntityManager(
			UserEquipEntityManager userEquipEntityManager) {
		this.userEquipEntityManager = userEquipEntityManager;
	}

}

