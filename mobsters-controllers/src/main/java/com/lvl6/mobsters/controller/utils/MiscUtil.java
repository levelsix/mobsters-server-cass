package com.lvl6.mobsters.controller.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

import com.lvl6.mobsters.entitymanager.staticdata.utils.BoosterDisplayItemRetrieveUtils;
import com.lvl6.mobsters.entitymanager.staticdata.utils.BoosterItemRetrieveUtils;
import com.lvl6.mobsters.entitymanager.staticdata.utils.BoosterPackRetrieveUtils;
import com.lvl6.mobsters.entitymanager.staticdata.utils.CityRetrieveUtils;
import com.lvl6.mobsters.entitymanager.staticdata.utils.EventPersistentRetrieveUtils;
import com.lvl6.mobsters.entitymanager.staticdata.utils.ExpansionCostRetrieveUtils;
import com.lvl6.mobsters.entitymanager.staticdata.utils.MonsterLevelInfoRetrieveUtils;
import com.lvl6.mobsters.entitymanager.staticdata.utils.MonsterRetrieveUtils;
import com.lvl6.mobsters.entitymanager.staticdata.utils.QuestRetrieveUtils;
import com.lvl6.mobsters.entitymanager.staticdata.utils.StructureHospitalRetrieveUtils;
import com.lvl6.mobsters.entitymanager.staticdata.utils.StructureLabRetrieveUtils;
import com.lvl6.mobsters.entitymanager.staticdata.utils.StructureResidenceRetrieveUtils;
import com.lvl6.mobsters.entitymanager.staticdata.utils.StructureResourceGeneratorRetrieveUtils;
import com.lvl6.mobsters.entitymanager.staticdata.utils.StructureResourceStorageRetrieveUtils;
import com.lvl6.mobsters.entitymanager.staticdata.utils.StructureRetrieveUtils;
import com.lvl6.mobsters.entitymanager.staticdata.utils.StructureTownHallRetrieveUtils;
import com.lvl6.mobsters.entitymanager.staticdata.utils.TaskRetrieveUtils;
import com.lvl6.mobsters.eventprotos.EventStartupProto.StartupResponseProto.StartupConstants;
import com.lvl6.mobsters.noneventprotos.BoosterPackStuffProto.BoosterPackProto;
import com.lvl6.mobsters.noneventprotos.CityProto.CityExpansionCostProto;
import com.lvl6.mobsters.noneventprotos.QuestStuffProto.QuestProto;
import com.lvl6.mobsters.noneventprotos.StaticDataStuffProto.StaticDataProto;
import com.lvl6.mobsters.noneventprotos.StructureProto.HospitalProto;
import com.lvl6.mobsters.noneventprotos.StructureProto.LabProto;
import com.lvl6.mobsters.noneventprotos.StructureProto.ResidenceProto;
import com.lvl6.mobsters.noneventprotos.StructureProto.ResourceGeneratorProto;
import com.lvl6.mobsters.noneventprotos.StructureProto.ResourceStorageProto;
import com.lvl6.mobsters.noneventprotos.StructureProto.StructureInfoProto;
import com.lvl6.mobsters.noneventprotos.StructureProto.TownHallProto;
import com.lvl6.mobsters.noneventprotos.TaskProto.FullTaskProto;
import com.lvl6.mobsters.noneventprotos.TaskProto.PersistentEventProto;
import com.lvl6.mobsters.po.nonstaticdata.QuestForUser;
import com.lvl6.mobsters.po.staticdata.BoosterDisplayItem;
import com.lvl6.mobsters.po.staticdata.BoosterItem;
import com.lvl6.mobsters.po.staticdata.BoosterPack;
import com.lvl6.mobsters.po.staticdata.City;
import com.lvl6.mobsters.po.staticdata.EventPersistent;
import com.lvl6.mobsters.po.staticdata.ExpansionCost;
import com.lvl6.mobsters.po.staticdata.Monster;
import com.lvl6.mobsters.po.staticdata.MonsterLevelInfo;
import com.lvl6.mobsters.po.staticdata.Quest;
import com.lvl6.mobsters.po.staticdata.Structure;
import com.lvl6.mobsters.po.staticdata.StructureHospital;
import com.lvl6.mobsters.po.staticdata.StructureLab;
import com.lvl6.mobsters.po.staticdata.StructureResidence;
import com.lvl6.mobsters.po.staticdata.StructureResourceGenerator;
import com.lvl6.mobsters.po.staticdata.StructureResourceStorage;
import com.lvl6.mobsters.po.staticdata.StructureTownHall;
import com.lvl6.mobsters.po.staticdata.Task;
import com.lvl6.mobsters.properties.MDCKeys;
import com.lvl6.mobsters.properties.MobstersTableConstants;
import com.lvl6.mobsters.services.questforuser.QuestForUserService;

public class MiscUtil {

	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected CreateNoneventProtoUtil createNoneventProtoUtil;
	
	@Autowired
	protected ExpansionCostRetrieveUtils expansionCostRetrieveUtils;
	
	@Autowired
	protected CityRetrieveUtils cityRetrieveUtils;
	
	@Autowired
	protected TaskRetrieveUtils taskRetrieveUtils;
	
	@Autowired
	protected MonsterRetrieveUtils monsterRetrieveUtils;
	
	@Autowired
	protected QuestForUserService questForUserService;
	
	@Autowired
	protected QuestRetrieveUtils questRetrieveUtils;
	
	@Autowired
	protected BoosterPackRetrieveUtils boosterPackRetrieveUtils;
	
	@Autowired
	protected BoosterItemRetrieveUtils boosterItemRetrieveUtils;
	
	@Autowired
	protected BoosterDisplayItemRetrieveUtils boosterDisplayItemRetrieveUtils;
	
	@Autowired
	protected StructureRetrieveUtils structureRetrieveUtils;
	
	@Autowired
	protected StructureResourceGeneratorRetrieveUtils structureResourceGeneratorRetrieveUtils;
	
	@Autowired
	protected StructureResourceStorageRetrieveUtils structureResourceStorageRetrieveUtils;
	
	@Autowired
	protected StructureHospitalRetrieveUtils structureHospitalRetrieveUtils;
	
	@Autowired
	protected StructureResidenceRetrieveUtils structureResidenceRetrieveUtils;
	
	@Autowired
	protected StructureTownHallRetrieveUtils structureTownHallRetrieveUtils;
	
	@Autowired
	protected StructureLabRetrieveUtils structureLabRetrieveUtils;
	
	@Autowired
	protected EventPersistentRetrieveUtils eventPersistentRetrieveUtils;
	
	@Autowired
	protected MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils;
	
	/*
	 * aMap (the second argument) might be modified
	 */
	public void retainValidMapEntries(Set<UUID> domain,  Map<UUID, ?> aMap,
			boolean keepThingsInDomain, boolean keepThingsNotInDomain) {
		Set<UUID> selectedIds = aMap.keySet();
		selectedIds = new HashSet<UUID>(selectedIds);

		for (UUID selectedId : selectedIds) {
			if (domain.contains(selectedId) && keepThingsInDomain) {
				continue;
			}
			if (!domain.contains(selectedId) && keepThingsNotInDomain) {
				continue;
			}
			//since selectedId isn't in the domain and want to keep things in domain
			//or is in the domain and want to keep things not in domain, remove it
			Object umhp = aMap.remove(selectedId);
			log.warn("Not retaining. object=" + umhp + "; keepThingsInDomain=" + keepThingsInDomain +
					"; keepThingsNotInDomain=" + keepThingsNotInDomain);
		}
	}

	/*
	 * ids (the second argument) might be modified
	 */
	public void retainValidListEntries(Set<UUID> existing, List<UUID> ids) {
		//	  	ids.add(123456789L);
		//	  	log.info("existing=" + existing + "\t ids=" + ids);

		List<UUID> copyIds = new ArrayList<UUID>(ids);
		// remove the invalid ids from ids client sent 
		// (modifying argument so calling function doesn't have to do it)
		ids.retainAll(existing);

		if (copyIds.size() != ids.size()) {
			//client asked for invalid ids
			log.warn("client asked for some invalid ids. asked for ids=" + copyIds + 
					"\t existingIds=" + existing + "\t remainingIds after purge =" + ids);
		}
	}

	//only the entries in the map that have their key in validIds will be kept  
	public Map<UUID, Integer> getValidMapEntries(List<UUID> validIds, 
			Map<UUID, Integer> idsToValues) {

		Map<UUID, Integer> returnMap = new HashMap<UUID, Integer>();

		for(UUID id : validIds) {
			int value = idsToValues.get(id);
			returnMap.put(id, value);
		}
		return returnMap;
	}
	
	public List<UUID> createUUIDListFromStrings(List<String> strings) {
		if (strings == null) return null;

		ArrayList<UUID> uuids = new ArrayList<UUID>();
		for (String string : strings) {
			uuids.add(UUID.fromString(string));
		}
		return uuids;
	}

	public String censorInput(String content, Set<String> blackList) {
		StringBuilder toReturn = new StringBuilder(content.length());

		String[] words = content.split(" ");
		String space = " "; //split by space, need to add them back in
		String w = "";

		for(int i = 0; i < words.length; i++) {
			w = words[i];

			//if at the last word, don't add a space after "censoring" it
			if ((words.length - 1) == i) {
				space = "";
			}
			//get rid of all punctuation
			String wWithNoPunctuation = w.replaceAll("\\p{Punct}", "");

			//the profanity table only holds lower case one word profanities
			if(blackList.contains(wWithNoPunctuation.toLowerCase())) {
				toReturn.append(asteriskify(w) + space);
			} else {
				toReturn.append(w + space);
			}
		}

		return toReturn.toString();
	}

	public String asteriskify(String wordToAskerify) {
		int len = wordToAskerify.length();
		StringBuilder sb = new StringBuilder();

		for(int i = 0; i < len; i++) {
			sb.append("*");
		}
		return sb.toString();
	}
	
	public <T> Collection<T> getValuesForIds (List<UUID> ids, Map<UUID, T> idsToValues) {
		Collection<T> values = new ArrayList<T>();
		
		for (UUID id : ids) {
			T value = idsToValues.get(id);
			values.add(value);
		}
		return values;
	}
	
	public int sumMapValues(Map<?, Integer> idsToValues) {
		int sum = 0;
		for (int value : idsToValues.values()) {
			sum += value;
		}
		return sum;
	}

	//copy pasted from aoc's MiscMethods.java
	//commenting it out, 1) don't know how to properly get ip, 2) just cause don't know what
	//these are used for or how they are used
	public void purgeMDCProperties() {
		MDC.remove(MDCKeys.UDID);
		MDC.remove(MDCKeys.PLAYER_ID);
//		MDC.remove(MDCKeys.IP);
	}

	public void setMDCProperties(String udid, UUID playerId) {//, String ip) {
		purgeMDCProperties();
		if (udid != null) MDC.put(MDCKeys.UDID, udid);
//		if (ip != null) MDC.put(MDCKeys.IP, ip);
		if (playerId != null) MDC.put(MDCKeys.PLAYER_ID.toString(), playerId.toString());
	}
	
	public StaticDataProto getAllStaticData(UUID userId) {
		StaticDataProto.Builder sdpb = StaticDataProto.newBuilder();
		
		setPlayerCityExpansions(sdpb);
		setCities(sdpb);
		setTasks(sdpb);
		setMonsters(sdpb);
		setInProgressAndAvailableQuests(sdpb, userId);
		setBoosterPackStuff(sdpb);
		setStructures(sdpb);
		setEvents(sdpb);
		
		return sdpb.build();
	}

	private void setPlayerCityExpansions(StaticDataProto.Builder sdpb) {
		//Player city expansions
		Map<Integer, ExpansionCost> expansionCosts =
				getExpansionCostRetrieveUtils().getAllExpansionNumsToCosts();
		for (ExpansionCost cec : expansionCosts.values()) {
			CityExpansionCostProto cecp = getCreateNoneventProtoUtil()
					.createCityExpansionCostProtoFromCityExpansionCost(cec);
			sdpb.addExpansionCosts(cecp);
		}
	}
	
	private void setCities(StaticDataProto.Builder sdpb) {
		//Cities
	  	Map<Integer, City> cities = getCityRetrieveUtils().getCityIdsToCities();
	  	for (Integer cityId : cities.keySet()) {
	  		City city = cities.get(cityId);
	  		List<Task> allTasksForCity = getTaskRetrieveUtils().getAllTasksForCityId(cityId);
	  		sdpb.addAllCities(getCreateNoneventProtoUtil()
	  				.createFullCityProtoFromCity(city, allTasksForCity));
	  	}
	}
	
	private void setTasks(StaticDataProto.Builder sdpb) {
		//Tasks
	  	Map<Integer, Task> taskIdsToTasks = getTaskRetrieveUtils().getTaskIdsToTasks();
	  	for (Task aTask : taskIdsToTasks.values()) {
	  		FullTaskProto ftp = getCreateNoneventProtoUtil()
	  				.createFullTaskProtoFromTask(aTask);
	  		sdpb.addAllTasks(ftp);
	  	}
	}

	private void setMonsters(StaticDataProto.Builder sdpb) {
		//Monsters
		Map<Integer, Monster> monsters = getMonsterRetrieveUtils()
				.getMonsterIdsToMonsters();
		for (Monster monster : monsters.values()) {
			int monsterId = monster.getId();
			Map<Integer, MonsterLevelInfo> levelToInfo = getMonsterLevelInfoRetrieveUtils()
		  			.getMonsterLevelInfoForMonsterId(monsterId);
			
			sdpb.addAllMonsters(getCreateNoneventProtoUtil().createMonsterProto(monster,
					levelToInfo));
		}
	}

	private void setInProgressAndAvailableQuests(StaticDataProto.Builder sdpb, UUID userId) {
		//questIds to user quests
		Map<Integer, QuestForUser> inProgressAndRedeemedUserQuests = getQuestForUserService()
				.getQuestIdsToUserQuestsForUser(userId);

		List<Integer> inProgressQuestIds = new ArrayList<Integer>();
		List<Integer> redeemedQuestIds = new ArrayList<Integer>();

		Map<Integer, Quest> questIdToQuests = getQuestRetrieveUtils()
				.getQuestIdsToQuests();
		for (int questId : inProgressAndRedeemedUserQuests.keySet()) {
			QuestForUser uq = inProgressAndRedeemedUserQuests.get(questId);
			
			if (uq.isRedeemed()) {
				redeemedQuestIds.add(uq.getQuestId());

			} else {
				//unredeemed quest section
				Quest quest = questIdToQuests.get(questId);
				QuestProto questProto = getCreateNoneventProtoUtil().
						createQuestProtoFromQuest(quest);

				inProgressQuestIds.add(uq.getQuestId());
				if (uq.isComplete()) { 
					//complete and unredeemed userQuest, so quest goes in unredeemedQuest
					sdpb.addUnredeemedQuests(questProto);
				} else {
					//incomplete and unredeemed userQuest, so quest goes in inProgressQuest
					sdpb.addInProgressQuests(questProto);
				}
			}
		}

		List<Integer> availableQuestIds = getQuestForUserService()
				.getAvailableQuestIds(redeemedQuestIds, inProgressQuestIds);
		if (availableQuestIds == null) {
			return;
		}

		//from the available quest ids generate the available quest protos
		for (Integer questId : availableQuestIds) {
			QuestProto fqp = getCreateNoneventProtoUtil().createQuestProtoFromQuest(
					questIdToQuests.get(questId));
			sdpb.addAvailableQuests(fqp);
		}
	}
	
	private void setBoosterPackStuff(StaticDataProto.Builder sdpb) {
		//Booster pack stuff
		Map<Integer, BoosterPack> idsToBoosterPacks = getBoosterPackRetrieveUtils()
				.getBoosterPackIdsToBoosterPacks();
		Map<Integer, Map<Integer, BoosterItem>> packIdToItemIdsToItems =
				getBoosterItemRetrieveUtils()
				.getBoosterItemIdsToBoosterItemsForBoosterPackIds();
		Map<Integer, Map<Integer, BoosterDisplayItem>> packIdToDisplayIdsToDisplayItems =
				getBoosterDisplayItemRetrieveUtils()
				.getBoosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds();

		for (Integer bpackId : idsToBoosterPacks.keySet()) {
			BoosterPack bp = idsToBoosterPacks.get(bpackId);

			//get the booster items associated with this booster pack
			Map<Integer, BoosterItem> itemIdsToItems = packIdToItemIdsToItems.get(bpackId);
			Collection<BoosterItem> items = null;
			if (null != itemIdsToItems) {
				items = itemIdsToItems.values();
			}

			//get the booster display items for this booster pack
			Map<Integer, BoosterDisplayItem> displayIdsToDisplayItems = 
					packIdToDisplayIdsToDisplayItems.get(bpackId);
			Collection<BoosterDisplayItem> displayItems = null;
			if (null != displayIdsToDisplayItems) {
				displayItems = displayIdsToDisplayItems.values();
			}

			BoosterPackProto bpProto = getCreateNoneventProtoUtil().createBoosterPackProto(
					bp, items, displayItems);
			sdpb.addBoosterPacks(bpProto);
		}
	}
	
	private void setStructures(StaticDataProto.Builder sdpb) {
		//Structures
		Map<Integer, Structure> structs = getStructureRetrieveUtils()
				.getStructIdsToStructs();
		Map<Integer, StructureInfoProto> structProtos = new HashMap<Integer, StructureInfoProto>();
		for (Integer structId : structs.keySet()) {
			Structure struct = structs.get(structId);
			StructureInfoProto sip = getCreateNoneventProtoUtil()
					.createStructureInfoProtoFromStructure(struct);
			structProtos.put(structId, sip);
		}

		setGenerators(sdpb, structs, structProtos);
		setStorages(sdpb, structs, structProtos);
		setHospitals(sdpb, structs, structProtos);
		setResidences(sdpb, structs, structProtos);
		setTownHalls(sdpb, structs, structProtos);
		setLabs(sdpb, structs, structProtos);
	}
	
	//resource generator
	private void setGenerators(StaticDataProto.Builder sdpb, Map<Integer, Structure> structs,
			Map<Integer, StructureInfoProto> structProtos) {
		Map<Integer, StructureResourceGenerator> idsToGenerators = 
				getStructureResourceGeneratorRetrieveUtils()
				.getStructIdsToResourceGenerators();
		for (Integer structId : idsToGenerators.keySet()) {
			Structure s = structs.get(structId);
			StructureInfoProto sip = structProtos.get(structId);
			StructureResourceGenerator srg = idsToGenerators.get(structId);

			ResourceGeneratorProto rgp = getCreateNoneventProtoUtil()
					.createResourceGeneratorProto(s, sip, srg);
			sdpb.addAllGenerators(rgp);
		}
	}
	//resource storage
	private void setStorages(StaticDataProto.Builder sdpb, Map<Integer, Structure> structs,
			Map<Integer, StructureInfoProto> structProtos) {
		Map<Integer, StructureResourceStorage> idsToStorages = 
				getStructureResourceStorageRetrieveUtils().getStructIdsToResourceStorages();
		for (Integer structId : idsToStorages.keySet()) {
			Structure s = structs.get(structId);
			StructureInfoProto sip = structProtos.get(structId);
			StructureResourceStorage srg = idsToStorages.get(structId);

			ResourceStorageProto rgp = getCreateNoneventProtoUtil().createResourceStorageProto(s, sip, srg);
			sdpb.addAllStorages(rgp);
		}
	}
	//hospitals
	private void setHospitals(StaticDataProto.Builder sdpb, Map<Integer, Structure> structs,
			Map<Integer, StructureInfoProto> structProtos) {
		Map<Integer, StructureHospital> idsToHospitals = 
				getStructureHospitalRetrieveUtils().getStructIdsToHospitals();
		for (Integer structId : idsToHospitals.keySet()) {
			Structure s = structs.get(structId);
			StructureInfoProto sip = structProtos.get(structId);
			StructureHospital srg = idsToHospitals.get(structId);

			HospitalProto rgp = getCreateNoneventProtoUtil().createHospitalProto(s, sip, srg);
			sdpb.addAllHospitals(rgp);
		}
	}
	//residences
	private void setResidences(StaticDataProto.Builder sdpb, Map<Integer, Structure> structs,
			Map<Integer, StructureInfoProto> structProtos) {
		Map<Integer, StructureResidence> idsToResidences = 
				getStructureResidenceRetrieveUtils().getStructIdsToResidences();
		for (Integer structId : idsToResidences.keySet()) {
			Structure s = structs.get(structId);
			StructureInfoProto sip = structProtos.get(structId);
			StructureResidence srg = idsToResidences.get(structId);

			ResidenceProto rgp = getCreateNoneventProtoUtil().createResidenceProto(s, sip, srg);
			sdpb.addAllResidences(rgp);
		}
	}
	//town hall
	private void setTownHalls(StaticDataProto.Builder sdpb, Map<Integer, Structure> structs,
			Map<Integer, StructureInfoProto> structProtos) {
		Map<Integer, StructureTownHall> idsToTownHalls = 
				getStructureTownHallRetrieveUtils().getStructIdsToTownHalls();
		for (Integer structId : idsToTownHalls.keySet()) {
			Structure s = structs.get(structId);
			StructureInfoProto sip = structProtos.get(structId);
			StructureTownHall srg = idsToTownHalls.get(structId);

			TownHallProto rgp = getCreateNoneventProtoUtil().createTownHallProto(s, sip, srg);
			sdpb.addAllTownHalls(rgp);
		}
	}
	//lab
	private void setLabs(StaticDataProto.Builder sdpb, Map<Integer, Structure> structs,
			Map<Integer, StructureInfoProto> structProtos) {
		Map<Integer, StructureLab> idsToLabs = getStructureLabRetrieveUtils()
				.getStructIdsToLabs();
		for (Integer structId : idsToLabs.keySet()) {
			Structure s = structs.get(structId);
			StructureInfoProto sip = structProtos.get(structId);
			StructureLab srg = idsToLabs.get(structId);

			LabProto rgp = getCreateNoneventProtoUtil().createLabProto(s, sip, srg);
			sdpb.addAllLabs(rgp);
		}		
	}
	
	private void setEvents(StaticDataProto.Builder sdpb) {
		Map<Integer, EventPersistent> idsToEvents = getEventPersistentRetrieveUtils()
				.getAllEventIdsToEvents();
		for (Integer eventId: idsToEvents.keySet()) {
			EventPersistent event  = idsToEvents.get(eventId);
			PersistentEventProto eventProto = getCreateNoneventProtoUtil()
					.createPersistentEventProtoFromEvent(event);
			sdpb.addPersistentEvents(eventProto);
		}
	}
	

	//TODO: FINISH IMPLEMENTING THIS METHOD
	public StartupConstants createStartupConstantsProto() {
		StartupConstants.Builder scb = StartupConstants.newBuilder();
		//INAPP PURCHASE STUFF

		scb.setMaxLevelForUser(MobstersTableConstants.LEVEL_UP__MAX_LEVEL_FOR_USER);
	    scb.setMaxNumOfSingleStruct(MobstersTableConstants.PURCHASE_NORM_STRUCTURE__MAX_NUM_OF_CERTAIN_STRUCTURE);
	    
		scb.setMinNameLength(MobstersTableConstants.USER_CREATE__MIN_NAME_LENGTH);
	    scb.setMaxNameLength(MobstersTableConstants.USER_CREATE__MAX_NAME_LENGTH);
	    scb.setMaxLengthOfChatString(MobstersTableConstants.SEND_GROUP_CHAT__MAX_LENGTH_OF_CHAT_STRING);
	    
	    
	    return scb.build();
	}
	

	public ExpansionCostRetrieveUtils getExpansionCostRetrieveUtils() {
		return expansionCostRetrieveUtils;
	}

	public void setExpansionCostRetrieveUtils(
			ExpansionCostRetrieveUtils expansionCostRetrieveUtils) {
		this.expansionCostRetrieveUtils = expansionCostRetrieveUtils;
	}

	public CreateNoneventProtoUtil getCreateNoneventProtoUtil() {
		return createNoneventProtoUtil;
	}

	public void setCreateNoneventProtoUtil(
			CreateNoneventProtoUtil createNoneventProtoUtil) {
		this.createNoneventProtoUtil = createNoneventProtoUtil;
	}

	public CityRetrieveUtils getCityRetrieveUtils() {
		return cityRetrieveUtils;
	}

	public void setCityRetrieveUtils(CityRetrieveUtils cityRetrieveUtils) {
		this.cityRetrieveUtils = cityRetrieveUtils;
	}

	public TaskRetrieveUtils getTaskRetrieveUtils() {
		return taskRetrieveUtils;
	}

	public void setTaskRetrieveUtils(TaskRetrieveUtils taskRetrieveUtils) {
		this.taskRetrieveUtils = taskRetrieveUtils;
	}

	public MonsterRetrieveUtils getMonsterRetrieveUtils() {
		return monsterRetrieveUtils;
	}

	public void setMonsterRetrieveUtils(MonsterRetrieveUtils monsterRetrieveUtils) {
		this.monsterRetrieveUtils = monsterRetrieveUtils;
	}

	public QuestForUserService getQuestForUserService() {
		return questForUserService;
	}

	public void setQuestForUserService(QuestForUserService questForUserService) {
		this.questForUserService = questForUserService;
	}

	public QuestRetrieveUtils getQuestRetrieveUtils() {
		return questRetrieveUtils;
	}

	public void setQuestRetrieveUtils(QuestRetrieveUtils questRetrieveUtils) {
		this.questRetrieveUtils = questRetrieveUtils;
	}

	public BoosterPackRetrieveUtils getBoosterPackRetrieveUtils() {
		return boosterPackRetrieveUtils;
	}

	public void setBoosterPackRetrieveUtils(
			BoosterPackRetrieveUtils boosterPackRetrieveUtils) {
		this.boosterPackRetrieveUtils = boosterPackRetrieveUtils;
	}

	public BoosterItemRetrieveUtils getBoosterItemRetrieveUtils() {
		return boosterItemRetrieveUtils;
	}

	public void setBoosterItemRetrieveUtils(
			BoosterItemRetrieveUtils boosterItemRetrieveUtils) {
		this.boosterItemRetrieveUtils = boosterItemRetrieveUtils;
	}

	public BoosterDisplayItemRetrieveUtils getBoosterDisplayItemRetrieveUtils() {
		return boosterDisplayItemRetrieveUtils;
	}

	public void setBoosterDisplayItemRetrieveUtils(
			BoosterDisplayItemRetrieveUtils boosterDisplayItemRetrieveUtils) {
		this.boosterDisplayItemRetrieveUtils = boosterDisplayItemRetrieveUtils;
	}

	public StructureRetrieveUtils getStructureRetrieveUtils() {
		return structureRetrieveUtils;
	}

	public void setStructureRetrieveUtils(
			StructureRetrieveUtils structureRetrieveUtils) {
		this.structureRetrieveUtils = structureRetrieveUtils;
	}

	public StructureResourceGeneratorRetrieveUtils getStructureResourceGeneratorRetrieveUtils() {
		return structureResourceGeneratorRetrieveUtils;
	}

	public void setStructureResourceGeneratorRetrieveUtils(
			StructureResourceGeneratorRetrieveUtils structureResourceGeneratorRetrieveUtils) {
		this.structureResourceGeneratorRetrieveUtils = structureResourceGeneratorRetrieveUtils;
	}

	public StructureResourceStorageRetrieveUtils getStructureResourceStorageRetrieveUtils() {
		return structureResourceStorageRetrieveUtils;
	}

	public void setStructureResourceStorageRetrieveUtils(
			StructureResourceStorageRetrieveUtils structureResourceStorageRetrieveUtils) {
		this.structureResourceStorageRetrieveUtils = structureResourceStorageRetrieveUtils;
	}

	public StructureHospitalRetrieveUtils getStructureHospitalRetrieveUtils() {
		return structureHospitalRetrieveUtils;
	}

	public void setStructureHospitalRetrieveUtils(
			StructureHospitalRetrieveUtils structureHospitalRetrieveUtils) {
		this.structureHospitalRetrieveUtils = structureHospitalRetrieveUtils;
	}

	public StructureResidenceRetrieveUtils getStructureResidenceRetrieveUtils() {
		return structureResidenceRetrieveUtils;
	}

	public void setStructureResidenceRetrieveUtils(
			StructureResidenceRetrieveUtils structureResidenceRetrieveUtils) {
		this.structureResidenceRetrieveUtils = structureResidenceRetrieveUtils;
	}

	public StructureTownHallRetrieveUtils getStructureTownHallRetrieveUtils() {
		return structureTownHallRetrieveUtils;
	}

	public void setStructureTownHallRetrieveUtils(
			StructureTownHallRetrieveUtils structureTownHallRetrieveUtils) {
		this.structureTownHallRetrieveUtils = structureTownHallRetrieveUtils;
	}

	public StructureLabRetrieveUtils getStructureLabRetrieveUtils() {
		return structureLabRetrieveUtils;
	}

	public void setStructureLabRetrieveUtils(
			StructureLabRetrieveUtils structureLabRetrieveUtils) {
		this.structureLabRetrieveUtils = structureLabRetrieveUtils;
	}

	public EventPersistentRetrieveUtils getEventPersistentRetrieveUtils() {
		return eventPersistentRetrieveUtils;
	}

	public void setEventPersistentRetrieveUtils(
			EventPersistentRetrieveUtils eventPersistentRetrieveUtils) {
		this.eventPersistentRetrieveUtils = eventPersistentRetrieveUtils;
	}

	public MonsterLevelInfoRetrieveUtils getMonsterLevelInfoRetrieveUtils() {
		return monsterLevelInfoRetrieveUtils;
	}

	public void setMonsterLevelInfoRetrieveUtils(
			MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils) {
		this.monsterLevelInfoRetrieveUtils = monsterLevelInfoRetrieveUtils;
	}
	
}
