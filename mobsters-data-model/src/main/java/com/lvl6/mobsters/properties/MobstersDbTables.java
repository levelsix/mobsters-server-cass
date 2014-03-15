package com.lvl6.mobsters.properties;

//SPECIFYING COLUMNS OF STATIC DATA TABLES UNNECESSARY
public class MobstersDbTables {
	/* TABLENAMES*/
	public static final String TABLE_ALERT_ON_STARTUP = "alertonstartup";
	public static final String TABLE_BATTLE_HISTORY = "battlehistory";
	public static final String TABLE_BOOSTER_DISPLAY_ITEM = "boosterdisplayitem";
	public static final String TABLE_BOOSTER_ITEM = "boosteritem";
	public static final String TABLE_BOOSTER_PACK = "boosterpack";
	public static final String TABLE_BOOSTER_PACK_PURCHASE_HISTORY = "boosterpackpurchasehistory";
	public static final String TABLE_CITY = "city";
//	public static final String TABLE_CITY_BOSS = "city_boss";
//	public static final String TABLE_CITY_BOSS_SITE_FOR_USER = "city_boss_site_for_user";
	public static final String TABLE_CITY_ELEMENT = "cityelement";
	public static final String TABLE_CLAN = "clan";
	public static final String TABLE_CLAN_CHAT_POST = "clanchatpost";
	public static final String TABLE_CLAN_FOR_USER = "clanforuser";
	public static final String TABLE_EVENT_PERSISTENT = "eventpersistent";
	public static final String TABLE_EVENT_PERSISTENT_FOR_USER = "eventpersistentforuser";
	public static final String TABLE_EXPANSION_COST = "expansioncost";
	public static final String TABLE_EXPANSION_PURCHASE_FOR_USER = "expansionpurchaseforuser";
	public static final String TABLE_GOLD_SALE = "goldsale";	
	public static final String TABLE_IAP_HISTORY = "iaphistory";
	public static final String TABLE_ITEM = "item";
	public static final String TABLE_LOCK_BOX_EVENT = "lockboxevent";
	public static final String TABLE_LOCK_BOX_EVENT_FOR_USER = "lockboxeventforuser";
	public static final String TABLE_LOCK_BOX_ITEM = "lockboxitem";
	public static final String TABLE_LOGIN_HISTORY = "loginhistory";	
	public static final String TABLE_MONSTER = "monster";
	public static final String TABLE_MONSTER_BATTLE_DIALOGUE = "monsterbattledialogue";
	public static final String TABLE_MONSTER_ENHANCING_FOR_USER = "monsterenhancingforuser";
	public static final String TABLE_MONSTER_ENHANCING_HISTORY = "monsterenhancinghistory";
	public static final String TABLE_MONSTER_EVOLVING_FOR_USER = "monsterevolvingforuser";
	public static final String TABLE_MONSTER_EVOLVING_HISTORY = "monsterevolvinghistory";
	public static final String TABLE_MONSTER_FOR_USER = "monsterforuser";
	public static final String TABLE_MONSTER_FOR_USER_DELETED = "monsterforuserdeleted";
	public static final String TABLE_MONSTER_HEALING_FOR_USER = "monsterhealingforuser";
	public static final String TABLE_MONSTER_HEALING_HISTORY = "monsterhealinghistory";
	public static final String TABLE_MONSTER_LEVEL_INFO = "monsterlevelinfo";
	public static final String TABLE_PROFANITY = "profanity";
	public static final String TABLE_QUEST = "quest";
	public static final String TABLE_QUEST_FOR_USER = "questforuser";
	public static final String TABLE_QUEST_MONSTER_ITEM = "questmonsteritem";
	public static final String TABLE_REFERRAL = "referral";
	public static final String TABLE_REFERRAL_CODE_AVAILABLE = "referralcodeavailable";
	public static final String TABLE_REFERRAL_CODE_GENERATED = "referralcodegenerated";
	public static final String TABLE_STATIC_LEVEL_INFO = "staticlevelinfo";
	public static final String TABLE_STRUCTURE = "structure";
	public static final String TABLE_STRUCTURE_FOR_USER = "structureforuser";
	public static final String TABLE_STRUCTURE_HOSPITAL = "structurehospital";
	public static final String TABLE_STRUCTURE_LAB = "structurelab";
	public static final String TABLE_STRUCTURE_RESIDENCE = "structureresidence";
	public static final String TABLE_STRUCTURE_RESOURCE_GENERATOR = "structureresourcegenerator";
	public static final String TABLE_STRUCTURE_RESOURCE_STORAGE = "structureresourcestorage";
	public static final String TABLE_STRUCTURE_TOWN_HALL = "structuretownhall";
	public static final String TABLE_TASK = "task";
	public static final String TABLE_TASK_FOR_USER_COMPLETED = "taskforusercompleted";
	public static final String TABLE_TASK_FOR_USER_ONGOING = "taskforuserongoing";
	public static final String TABLE_TASK_HISTORY = "taskhistory";
	public static final String TABLE_TASK_STAGE = "taskstage";
	public static final String TABLE_TASK_STAGE_FOR_USER = "taskstageforuser";
	public static final String TABLE_TASK_STAGE_HISTORY = "taskstagehistory";
	public static final String TABLE_TASK_STAGE_MONSTER = "taskstagemonster";
	public static final String TABLE_TOURNAMENT_EVENT = "tournamentevent";	
	public static final String TABLE_TOURNAMENT_EVENT_FOR_USER = "tournamenteventforuser";
	public static final String TABLE_TOURNAMENT_REWARD = "tournamentreward";
	public static final String TABLE_USER = "user";
	public static final String TABLE_USER_BANNED = "userbanned";
	public static final String TABLE_USER_BEFORE_TUTORIAL_COMPLETION = "userbeforetutorialcompletion";
	public static final String TABLE_USER_CURRENCY_HISTORY = "usercurrencyhistory";
	public static final String TABLE_USER_FACEBOOK_INVITE_FOR_SLOT = "userfacebookinviteforslot";
//	public static final String TABLE_USER_FACEBOOK_INVITE_FOR_SLOT_ACCEPTED = "user_facebook_invite_for_slot_accepted";
	public static final String TABLE_USER_PRIVATE_CHAT_POST = "userprivatechatpost";
	public static final String TABLE_USER_SESSION = "usersession";

	/*COLUMNNAMES*/
	public static final String GENERIC__USER_ID = "user_id";
	public static final String GENERIC__ID = "id";

	/*ALERT ON STARTUP*/
	public static final String ALERT_ON_STARTUP__ID = GENERIC__ID;
	public static final String ALERT_ON_STARTUP__MESSAGE = "message";
	public static final String ALERT_ON_STARTUP__IS_ACTIVE = "is_active";


	/*AVAILABLE REFERRAL CODES*/
	public static final String AVAILABLE_REFERRAL_CODES__ID = GENERIC__ID;
	public static final String AVAILABLE_REFERRAL_CODES__CODE = "code";

	/*BATTLE HISTORY*/
	public static final String BATTLE_HISTORY__ATTACKER_ID = "attacker_id";
	public static final String BATTLE_HISTORY__DEFENDER_ID = "defender_id";
	public static final String BATTLE_HISTORY__RESULT = "result";
	public static final String BATTLE_HISTORY__BATTLE_COMPLETE_TIME = "battle_complete_time";
	public static final String BATTLE_HISTORY__COINS_STOLEN = "coins_stolen";
	public static final String BATTLE_HISTORY__EQUIP_STOLEN = "equip_stolen";
	public static final String BATTLE_HISTORY__EXP_GAINED = "exp_gained";
	public static final String BATTLE_HISTORY__STOLEN_EQUIP_LEVEL = "stolen_equip_level";

	/*BOOSTER PACK PURCHASE HISTORY*/
	public static final String BOOSTER_PACK_PURCHASE_HISTORY__USER_ID = GENERIC__USER_ID;
	public static final String BOOSTER_PACK_PURCHASE_HISTORY__BOOSTER_PACK_ID = "booster_pack_id"; 
	public static final String BOOSTER_PACK_PURCHASE_HISTORY__TIME_OF_PURCHASE = "time_of_purchase";
	public static final String BOOSTER_PACK_PURCHASE_HISTORY__BOOSTER_ITEM_ID = "booster_item_id";
	public static final String BOOSTER_PACK_PURCHASE_HISTORY__MONSTER_ID = "monster_id";
	public static final String BOOSTER_PACK_PURCHASE_HISTORY__NUM_PIECES = "num_pieces";
	public static final String BOOSTER_PACK_PURCHASE_HISTORY__IS_COMPLETE = "is_complete";
	public static final String BOOSTER_PACK_PURCHASE_HISTORY__IS_SPECIAL = "is_special";
	public static final String BOOSTER_PACK_PURCHASE_HISTORY__GEM_REWARD = "gem_reward";
	public static final String BOOSTER_PACK_PURCHASE_HISTORY__CASH_REWARD = "cash_reward";
	public static final String BOOSTER_PACK_PURCHASE_HISTORY__CHANCE_TO_APPEAR = "chance_to_appear";
	public static final String BOOSTER_PACK_PURCHASE_HISTORY__CHANGED_MONSTER_FOR_USER_IDS = "changed_monster_for_user_ids";

	/*CITY BOSS SITE FOR USER*/
	public static final String CITY_BOSS_SITE_FOR_USER__CITY_ID = "city_id";
	public static final String CITY_BOSS_SITE_FOR_USER__USER_ID = "user_id";
	public static final String CITY_BOSS_SITE_FOR_USER__TASK_ID = "task_id";
	public static final String CITY_BOSS_SITE_FOR_USER__BOSS_ID = "boss_id";
	public static final String CITY_BOSS_SITE_FOR_USER__TIME_OF_ENTRY = "time_of_entry";

	/*CLANS*/
	public static final String CLAN__ID = "id";
	public static final String CLAN__NAME = "name";
	public static final String CLAN__CREATE_TIME = "create_time";
	public static final String CLAN__DESCRIPTION = "description";
	public static final String CLAN__TAG = "tag";
	public static final String CLAN__REQUEST_TO_JOIN_REQUIRED = "request_to_join_required";

	/*CLAN_CHAT_POSTS*/
	public static final String CLAN_CHAT_POST__ID = GENERIC__ID;
	public static final String CLAN_CHAT_POST__POSTER_ID = "poster_id";
	public static final String CLAN_CHAT_POST__CLAN_ID = "clan_id";
	public static final String CLAN_CHAT_POST__TIME_OF_POST = "time_of_post";
	public static final String CLAN_CHAT_POST__CONTENT = "content";

	/*USER CLANS*/
	public static final String CLAN_FOR_USER__USER_ID = "user_id";
	public static final String CLAN_FOR_USER__CLAN_ID = "clan_id";
	public static final String CLAN_FOR_USER__STATUS = "status";
	public static final String CLAN_FOR_USER__REQUEST_TIME = "request_time";

	/*EVENT PERSISTENT FOR USER*/
	public static final String EVENT_PERSISTENT_FOR_USER__USER_ID = "user_id";
	public static final String EVENT_PERSISTENT_FOR_USER__EVENT_PERSISTENT_ID = "event_persistent_id";
	public static final String EVENT_PERSISTENT_FOR_USER__TIME_OF_ENTRY = "time_of_entry";
	  
	/*EXPANSION PURCHASE FOR USER*/
	public static final String EXPANSION_PURCHASE_FOR_USER__USER_ID = GENERIC__USER_ID;
	public static final String EXPANSION_PURCHASE_FOR_USER__X_POSITION = "x_position";
	public static final String EXPANSION_PURCHASE_FOR_USER__Y_POSITION = "y_position";
	public static final String EXPANSION_PURCHASE_FOR_USER__IS_EXPANDING = "is_expanding";
	public static final String EXPANSION_PURCHASE_FOR_USER__EXPAND_START_TIME = "expand_start_time";

	/*IAP TABLE*/
	public static final String IAP_HISTORY__ID = GENERIC__ID;
	public static final String IAP_HISTORY__USER_ID = GENERIC__USER_ID;
	public static final String IAP_HISTORY__TRANSACTION_ID = "transaction_id";
	public static final String IAP_HISTORY__PURCHASE_DATE = "purchase_date";
	public static final String IAP_HISTORY__PREMIUMCUR_PURCHASED = "premiumcur_purchased";
	public static final String IAP_HISTORY__REGCUR_PURCHASED = "regcur_purchased";
	public static final String IAP_HISTORY__CASH_SPENT = "cash_spent";
	public static final String IAP_HISTORY__UDID = "udid";
	public static final String IAP_HISTORY__PRODUCT_ID = "product_id";
	public static final String IAP_HISTORY__QUANTITY = "quantity";
	public static final String IAP_HISTORY__BID = "bid";
	public static final String IAP_HISTORY__BVRS = "bvrs";
	public static final String IAP_HISTORY__APP_ITEM_ID = "app_item_id";

	/*LOCK BOX EVENT FOR USER*/
	public static final String LOCK_BOX_EVENT_FOR_USER__EVENT_ID = "lock_box_event_id";
	public static final String LOCK_BOX_EVENT_FOR_USER__USER_ID = "user_id";
	public static final String LOCK_BOX_EVENT_FOR_USER__NUM_BOXES = "num_boxes";
	public static final String LOCK_BOX_EVENT_FOR_USER__LAST_OPENING_TIME = "last_opening_time";
	public static final String LOCK_BOX_EVENT_FOR_USER__NUM_TIMES_COMPLETED = "num_times_completed";
	public static final String LOCK_BOX_EVENT_FOR_USER__HAS_BEEN_REDEEMED = "has_been_redeemed";

	/*LOGIN HISTORY*/
	public static final String LOGIN_HISTORY__ID = GENERIC__ID;
	public static final String LOGIN_HISTORY__UDID = "udid";
	public static final String LOGIN_HISTORY__USER_ID = GENERIC__USER_ID;
	public static final String LOGIN_HISTORY__DATE = "date";
	public static final String LOGIN_HISTORY__IS_LOGIN = "is_login";

	/*MONSTER ENHANCING FOR USER*/
	public static final String MONSTER_ENHANCING_FOR_USER__ID = GENERIC__ID;
	public static final String MONSTER_ENHANCING_FOR_USER__USER_ID = GENERIC__USER_ID;
	public static final String MONSTER_ENHANCING_FOR_USER__MONSTER_FOR_USER_ID = "monster_for_user_id";
	public static final String MONSTER_ENHANCING_FOR_USER__EXPECTED_START_TIME = "expected_start_time";

	/*MONSTER ENHANCING HISTORY*/
	public static final String MONSTER_ENHANCING_HISTORY__ID = "id";
	public static final String MONSTER_ENHANCING_HISTORY__USER_ID = GENERIC__USER_ID;
	public static final String MONSTER_ENHANCING_HISTORY__MONSTER_ENHANCING_FOR_USER_ID = "monster_enhancing_for_user_id";
	//enhancing id of monster this monster was sacrificed for,
	//otherwise the enhancing id of the base monster (which is this monster)
	public static final String MONSTER_ENHANCING_HISTORY__BASE_ENHANCING_ID = "base_enhancing_id";
	public static final String MONSTER_ENHANCING_HISTORY__TIME_OF_ENTRY = "time_of_entry";
	public static final String MONSTER_ENHANCING_HISTORY__ENHANCING_START_TIME = "enhancing_start_time";
	public static final String MONSTER_ENHANCING_HISTORY__MONSTER_FOR_USER_ID = "monster_for_user_id";
	public static final String MONSTER_ENHANCING_HISTORY__MONSTER_ID = "monster_id";
	public static final String MONSTER_ENHANCING_HISTORY__CUR_EXPERIENCE = "cur_experience";
	public static final String MONSTER_ENHANCING_HISTORY__PREV_EXPERIENCE = "prev_experience";
	public static final String MONSTER_ENHANCING_HISTORY__ENHANCING_CANCELLED = "enhancing_cancelled";


	/*MONSTER EVOLVING FOR USER TABLE*/
	public static final String MONSTER_EVOLVING_FOR_USER__CATALYST_USER_MONSTER_ID = "catalyst_user_monster_id";
	public static final String MONSTER_EVOLVING_FOR_USER__USER_MONSTER_ID_ONE = "user_monster_id_one";
	public static final String MONSTER_EVOLVING_FOR_USER__USER_MONSTER_ID_TWO = "user_monster_id_two";
	public static final String MONSTER_EVOLVING_FOR_USER__USER_ID = GENERIC__USER_ID;
	public static final String MONSTER_EVOLVING_FOR_USER__START_TIME = "start_time";

	/*MONSTER EVOLVING HISTORY TABLE*/
	public static final String MONSTER_EVOLVING_HISTORY__ID = "id";
	public static final String MONSTER_EVOLVING_HISTORY__USER_ID = GENERIC__USER_ID;
	public static final String MONSTER_EVOLVING_HISTORY__USER_MONSTER_ID_ONE = "user_monster_id_one";
	public static final String MONSTER_EVOLVING_HISTORY__USER_MONSTER_ID_TWO = "user_monster_id_two";
	public static final String MONSTER_EVOLVING_HISTORY__CATALYST_USER_MONSTER_ID = "catalyst_user_monster_id";
	public static final String MONSTER_EVOLVING_HISTORY__START_TIME = "start_time";
	public static final String MONSTER_EVOLVING_HISTORY__TIME_OF_ENTRY = "time_of_entry";
	

	/*MONSTER FOR USER*/
	public static final String MONSTER_FOR_USER__ID = GENERIC__ID;
	public static final String MONSTER_FOR_USER__USER_ID = GENERIC__USER_ID;
	public static final String MONSTER_FOR_USER__MONSTER_ID = "monster_id";
	public static final String MONSTER_FOR_USER__CURRENT_EXPERIENCE = "current_experience";
	public static final String MONSTER_FOR_USER__CURRENT_LEVEL = "current_level";
	public static final String MONSTER_FOR_USER__CURRENT_HEALTH = "current_health";
	public static final String MONSTER_FOR_USER__NUM_PIECES = "num_pieces";
	public static final String MONSTER_FOR_USER__IS_COMPLETE = "is_complete";
	public static final String MONSTER_FOR_USER__COMBINE_START_TIME = "combine_start_time";
	public static final String MONSTER_FOR_USER__TEAM_SLOT_NUM = "team_slot_num";
	public static final String MONSTER_FOR_USER__SOURCE_OF_PIECES = "source_of_pieces";

	/*MONSTER FOR USER DELETED*/
	public static final String MONSTER_FOR_USER_DELETED__ID = "monster_for_user_id";
	public static final String MONSTER_FOR_USER_DELETED__USER_ID = GENERIC__USER_ID;
	public static final String MONSTER_FOR_USER_DELETED__MONSTER_ID = "monster_id";
	public static final String MONSTER_FOR_USER_DELETED__EXP = "exp";
	public static final String MONSTER_FOR_USER_DELETED__LVL = "lvl";
	public static final String MONSTER_FOR_USER_DELETED__HEALTH = "health";
	public static final String MONSTER_FOR_USER_DELETED__NUM_PIECES = "num_pieces";
	public static final String MONSTER_FOR_USER_DELETED__IS_COMPLETE = "is_complete";
	public static final String MONSTER_FOR_USER_DELETED__COMBINE_START_TIME = "combine_start_time";
	public static final String MONSTER_FOR_USER_DELETED__TEAM_SLOT_NUM = "team_slot_num";
	public static final String MONSTER_FOR_USER_DELETED__SOURCE_OF_PIECES = "source_of_pieces";
	public static final String MONSTER_FOR_USER_DELETED__DELETED_REASON = "deleted_reason";
	public static final String MONSTER_FOR_USER_DELETED__DETAILS = "details";
	public static final String MONSTER_FOR_USER_DELETED__DELETED_TIME = "deleted_time";

	/*MONSTER HEALING FOR USER*/
	public static final String MONSTER_HEALING_FOR_USER__ID = GENERIC__ID;
	public static final String MONSTER_HEALING_FOR_USER__USER_ID = GENERIC__USER_ID;
	public static final String MONSTER_HEALING_FOR_USER__MONSTER_FOR_USER_ID = "monster_for_user_id";
	public static final String MONSTER_HEALING_FOR_USER__QUEUED_TIME = "queued_time";
	//  public static final String MONSTER_HEALING_FOR_USER__USER_STRUCT_HOSPITAL_ID = "user_struct_hospital_id";
	public static final String MONSTER_HEALING_FOR_USER__HEALTH_PROGRESS = "health_progress";
	public static final String MONSTER_HEALING_FOR_USER__PRIORITY = "priority";


	/*MONSTER HEALING HISTORY*/
	public static final String MONSTER_HEALING_HISTORY__USER_ID = "user_id";
	public static final String MONSTER_HEALING_HISTORY__MONSTER_FOR_USER_ID = "monster_for_user_id";
	public static final String MONSTER_HEALING_HISTORY__MONSTER_ID = "monster_id";
	public static final String MONSTER_HEALING_HISTORY__TIME_OF_ENTRY = "time_of_entry";
	public static final String MONSTER_HEALING_HISTORY__QUEUED_TIME = "queued_time";
//	public static final String MONSTER_HEALING_HISTORY__USER_STRUCT_HOSPITAL_ID = "user_struct_hospital_id";
	public static final String MONSTER_HEALING_HISTORY__CUR_HEALTH = "cur_health";
	public static final String MONSTER_HEALING_HISTORY__PREV_HEALTH = "prev_health";
	public static final String MONSTER_HEALING_HISTORY__HEALING_CANCELLED = "healing_cancelled";

	/*QUEST TABLE*/
	public static final String QUEST__INT_ID = "int_id";

	/*USER QUESTS TABLE*/
	public static final String QUEST_FOR_USER__USER_ID = GENERIC__USER_ID;
	public static final String QUEST_FOR_USER__QUEST_ID = "quest_id";
	public static final String QUEST_FOR_USER__IS_REDEEMED = "is_redeemed"; 
	public static final String QUEST_FOR_USER__IS_COMPLETE = "is_complete";
	public static final String QUEST_FOR_USER__PROGRESS = "progress"; 

	/*REFERRALS*/
	public static final String REFERRALS__REFERRER_ID = "referrer_id";
	public static final String REFERRALS__NEWLY_REFERRED_ID = "newly_referred_id";
	public static final String REFERRALS__TIME_OF_REFERRAL = "time_of_referral";
	public static final String REFERRALS__COINS_GIVEN_TO_REFERRER = "coins_given_to_referrer";

	/*USER STRUCTS TABLE*/
	public static final String STRUCTURE_FOR_USER__ID = GENERIC__ID;
	public static final String STRUCTURE_FOR_USER__USER_ID = GENERIC__USER_ID;
	public static final String STRUCTURE_FOR_USER__STRUCT_ID = "struct_id";
	public static final String STRUCTURE_FOR_USER__LAST_RETRIEVED = "last_retrieved";
	public static final String STRUCTURE_FOR_USER__X_COORD = "xcoord";
	public static final String STRUCTURE_FOR_USER__Y_COORD = "ycoord";
	public static final String STRUCTURE_FOR_USER__PURCHASE_TIME = "purchase_time";
	public static final String STRUCTURE_FOR_USER__IS_COMPLETE = "is_complete";
	public static final String STRUCTURE_FOR_USER__ORIENTATION = "orientation";
	public static final String STRUCTURE_FOR_USER__UPGRADE_START_TIME = "upgrade_start_time";

	/*TASK FOR USER ONGOING TABLE*/
	public static final String TASK_FOR_USER_ONGOING__ID = GENERIC__ID;
	public static final String TASK_FOR_USER_ONGOING__USER_ID = GENERIC__USER_ID;
	public static final String TASK_FOR_USER_ONGOING__TASK_ID = "task_id";
	public static final String TASK_FOR_USER_ONGOING__EXP_GAINED = "exp_gained";
	public static final String TASK_FOR_USER_ONGOING__SILVER_GAINED = "silver_gained";
	public static final String TASK_FOR_USER_ONGOING__NUM_REVIVES = "num_revives";
	public static final String TASK_FOR_USER_ONGOING__START_TIME = "start_time";

	/*TASK FOR USER COMPLETED TABLE*/
	public static final String TASK_FOR_USER_COMPLETED__USER_ID = GENERIC__USER_ID;
	public static final String TASK_FOR_USER_COMPLETED__TASK_ID = "task_id";
	public static final String TASK_FOR_USER_COMPLETED__TIME_OF_ENTRY = "time_of_entry";

	/*TASK HISTORY TABLE*/
	public static final String TASK_HISTORY__TASK_FOR_USER_ID = "task_for_user_id";
	public static final String TASK_HISTORY__USER_ID = GENERIC__USER_ID;
	public static final String TASK_HISTORY__TASK_ID = "task_id";
	public static final String TASK_HISTORY__EXP_GAINED = "exp_gained";
	public static final String TASK_HISTORY__CASH_GAINED = "cash_gained";
	public static final String TASK_HISTORY__NUM_REVIVES = "num_revives";
	public static final String TASK_HISTORY__START_TIME = "start_time";
	public static final String TASK_HISTORY__END_TIME = "end_time";
	public static final String TASK_HISTORY__USER_WON = "user_won";
	public static final String TASK_HISTORY__CANCELLED = "cancelled";

	/*TASK STAGE FOR USER TABLE*/
	public static final String TASK_STAGE_FOR_USER__ID = GENERIC__ID;
	public static final String TASK_STAGE_FOR_USER__TASK_FOR_USER_ID = "task_for_user_id";
	public static final String TASK_STAGE_FOR_USER__STAGE_NUM = "stage_num";
	public static final String TASK_STAGE_FOR_USER__TASK_STAGE_MONSTER_ID = "task_stage_monster_id";
	public static final String TASK_STAGE_FOR_USER__MONSTER_TYPE = "monster_type";
	public static final String TASK_STAGE_FOR_USER__EXP_GAINED = "exp_gained";
	public static final String TASK_STAGE_FOR_USER__SILVER_GAINED = "silver_gained";
	public static final String TASK_STAGE_FOR_USER__MONSTER_PIECE_DROPPED = "monster_piece_dropped";

	/*USER TASK STAGE HISTORY TABLE*/
	public static final String TASK_STAGE_HISTORY__ID = "task_stage_for_user_id";
	public static final String TASK_STAGE_HISTORY__TASK_FOR_USER_ID = "task_for_user_id";
	public static final String TASK_STAGE_HISTORY__STAGE_NUM = "stage_num";
	public static final String TASK_STAGE_HISTORY__TASK_STAGE_MONSTER_ID = "task_stage_monster_id";
	//not needed since task stage monster now has monster_type
	public static final String TASK_STAGE_HISTORY__MONSTER_TYPE = "monster_type";
	public static final String TASK_STAGE_HISTORY__EXP_GAINED = "exp_gained";
	public static final String TASK_STAGE_HISTORY__CASH_GAINED = "cash_gained";
	public static final String TASK_STAGE_HISTORY__MONSTER_PIECE_DROPPED = "monster_piece_dropped";

	/*TOURNAMENT EVENT*/
	public static final String TOURNAMENT_EVENT__ID = GENERIC__ID;
	public static final String TOURNAMENT_EVENT__START_TIME = "start_time";
	public static final String TOURNAMENT_EVENT__END_TIME = "end_time";
	public static final String TOURNAMENT_EVENT__EVENT_NAME = "event_time";
	public static final String TOURNAMENT_EVENT__REWARDS_GIVEN_OUT = "rewards_given_out";

	/*TOURNAMENT EVENT FOR USER*/
	public static final String TOURNAMENT_EVENT_FOR_USER__TOURNAMENT_EVENT_ID = "tournament_event_id";
	public static final String TOURNAMENT_EVENT_FOR_USER__USER_ID = GENERIC__USER_ID;
	public static final String TOURNAMENT_EVENT_FOR_USER__BATTLES_WON = "battles_won";
	public static final String TOURNAMENT_EVENT_FOR_USER__BATTLES_LOST = "battles_lost";
	public static final String TOURNAMENT_EVENT_FOR_USER__BATTLES_FLED = "battles_fled";

	/*TOURNAMENT REWARD*/
	public static final String TOURNAMENT_REWARD__ID = "tournament_event_id";
	public static final String TOURNAMENT_REWARD__MIN_RANK = "min_rank";
	public static final String TOURNAMENT_REWARD__MAX_RANK = "max_rank";
	public static final String TOURNAMENT_REWARD__GOLD_REWARDED = "gold_rewarded";
	public static final String TOURNAMENT_REWARD__BACKGROUND_IMAGE_NAME = "background_image_name";
	public static final String TOURNAMENT_REWARD__PRIZE_IMAGE_NAME = "prize_image_name";
	public static final String TOURNAMENT_REWARD__BLUE = "blue";
	public static final String TOURNAMENT_REWARD__GREEN = "green";
	public static final String TOURNAMENT_REWARD__RED = "red";

	/*USER TABLE*/
	public static final String USER__ID = GENERIC__ID;
	public static final String USER__NAME = "name";
	public static final String USER__LEVEL = "level";
	public static final String USER__GEMS = "gems";
	public static final String USER__CASH = "cash";
	public static final String USER__OIL = "oil";
	public static final String USER__EXPERIENCE = "experience";
	public static final String USER__TASKS_COMPLETED = "tasks_completed";
	public static final String USER__BATTLES_WON = "battles_won";
	public static final String USER__BATTLES_LOST = "battles_lost";
	public static final String USER__FLEES = "flees";
	public static final String USER__REFERRAL_CODE = "referral_code";
	public static final String USER__NUM_REFERRALS = "num_referrals";
	public static final String USER__UDID = "udid";
	public static final String USER__LAST_LOGIN = "last_login";
	public static final String USER__LAST_LOGOUT = "last_logout";
	public static final String USER__DEVICE_TOKEN = "device_token";
	public static final String USER__LAST_BATTLE_NOTIFICATION_TIME = "last_battle_notification_time";
	public static final String USER__NUM_BADGES = "num_badges";
	public static final String USER__IS_FAKE = "is_fake";
	public static final String USER__CREATE_TIME = "create_time";
	public static final String USER__IS_ADMIN = "is_admin";
	public static final String USER__APSALAR_ID = "apsalar_id";
	public static final String USER__NUM_CASH_RETRIEVED_FROM_STRUCTS = "num_cash_retrieved_from_structs";
	public static final String USER__NUM_OIL_RETRIEVED_FROM_STRUCTS = "num_oil_retrieved_from_structs";
	public static final String USER__NUM_CONSECUTIVE_DAYS_PLAYED = "num_consecutive_days_played";
	public static final String USER__CLAN_ID = "clan_id";
	public static final String USER__LAST_WALL_POST_NOTIFICATION_TIME = "last_wall_post_notification_time";
	public static final String USER__KABAM_NAID = "kabam_naid";
	public static final String USER__HAS_RECEIVED_FB_REWARD = "has_received_fb_reward";
	public static final String USER__NUM_ADDITIONAL_MONSTER_SLOTS = "num_additional_monster_slots";
	public static final String USER__NUM_BEGINNER_SALES_PURCHASED = "num_beginner_sales_purchased";
	public static final String USER__HAS_ACTIVE_SHIELD = "has_active_shield";
	public static final String USER__SHIELD_END_TIME = "shield_end_time";
	public static final String USER__ELO = "elo";
	public static final String USER__RANK = "rank";
	public static final String USER__LAST_TIME_QUEUED = "last_time_queued";
	public static final String USER__ATTACKS_WON = "attacks_won";
	public static final String USER__DEFENSES_WON = "defenses_won";
	public static final String USER__ATTACKS_LOST = "attacks_lost";
	public static final String USER__DEFENSES_LOST = "defenses_lost";
	public static final String USER__FACEBOOK_ID = "facebook_id";
	public static final String USER__NTH_EXTRA_SLOTS_VIA_FB = "nth_extra_slots_via_fb";
	//might not be used and not implemented yet
	public static final String USER__GAME_CENTER_ID = "game_center_id";

	/* USER BANNED */
	public static final String USER_BANNED__ID = GENERIC__ID;
	public static final String USER_BANNED__USER_ID = GENERIC__USER_ID;
	public static final String USER_BANNED__TIME_BANNED = "time_banned";
	public static final String USER_BANNED__REASON = "reason";

	/* USER BEFORE TUTORIAL COMPLETION*/
	public static final String USER_BEFORE_TUTORIAL_COMPLETION__ID = GENERIC__ID;
	public static final String USER_BEFORE_TUTORIAL_COMPLETION__OPEN_UDID = "open_udid";
	public static final String USER_BEFORE_TUTORIAL_COMPLETION__UDID = "udid";
	public static final String USER_BEFORE_TUTORIAL_COMPLETION__MAC = "mac";
	public static final String USER_BEFORE_TUTORIAL_COMPLETION__ADVERTISER_ID = "advertiser_id";
	public static final String USER_BEFORE_TUTORIAL_COMPLETION__CREATE_TIME = "create_time";

	/*USER CURRENCY HISTORY*/
	public static final String USER_CURRENCY_HISTORY__USER_ID = GENERIC__USER_ID;
	public static final String USER_CURRENCY_HISTORY__DATE = "date";
	public static final String USER_CURRENCY_HISTORY__RESOURCE_TYPE = "resource_type";
	public static final String USER_CURRENCY_HISTORY__CURRENCY_CHANGE = "currency_change";
	public static final String USER_CURRENCY_HISTORY__CURRENCY_BEFORE_CHANGE = "currency_before_change";
	public static final String USER_CURRENCY_HISTORY__CURRENCY_AFTER_CHANGE = "currency_after_change";
	public static final String USER_CURRENCY_HISTORY__REASON_FOR_CHANGE = "reason_for_change";
	public static final String USER_CURRENCY_HISTORY__DETAILS = "details";

	/*USER FACEBOOK INVITE*/
	public static final String USER_FACEBOOK_INVITE_FOR_SLOT__ID = GENERIC__ID;
	public static final String USER_FACEBOOK_INVITE_FOR_SLOT__INVITER_USER_ID = "inviter_user_id";
	public static final String USER_FACEBOOK_INVITE_FOR_SLOT__RECIPIENT_FACEBOOK_ID = "recipient_facebook_id";
	public static final String USER_FACEBOOK_INVITE_FOR_SLOT__TIME_OF_INVITE = "time_of_invite";
	public static final String USER_FACEBOOK_INVITE_FOR_SLOT__TIME_ACCEPTED = "time_accepted";
	public static final String USER_FACEBOOK_INVITE_FOR_SLOT__USER_STRUCT_ID = "user_struct_id";
	public static final String USER_FACEBOOK_INVITE_FOR_SLOT__USER_STRUCT_FB_LVL = "user_struct_fb_lvl";
	public static final String USER_FACEBOOK_INVITE_FOR_SLOT__TIME_REDEEMED = "time_redeemed";

	/*USER FACEBOOK INVITE ACCEPTED
	public static final String USER_FACEBOOK_INVITE_FOR_SLOT_ACCEPTED__ID = GENERIC__ID;
	public static final String USER_FACEBOOK_INVITE_FOR_SLOT_ACCEPTED__INVITER_USER_ID = "inviter_user_id";
	public static final String USER_FACEBOOK_INVITE_FOR_SLOT_ACCEPTED__RECIPIENT_FACEBOOK_ID = "recipient_facebook_id";
	public static final String USER_FACEBOOK_INVITE_FOR_SLOT_ACCEPTED__TIME_OF_INVITE = "time_of_invite";
	public static final String USER_FACEBOOK_INVITE_FOR_SLOT_ACCEPTED__TIME_ACCEPTED = "time_accepted";
	public static final String USER_FACEBOOK_INVITE_FOR_SLOT_ACCEPTED__NTH_EXTRA_SLOTS_VIA_FB = "nth_extra_slots_via_fb";
	public static final String USER_FACEBOOK_INVITE_FOR_SLOT_ACCEPTED__TIME_OF_ENTRY = "time_of_entry";
	*/

	/*USER PRIVATE CHAT POSTS*/
	public static final String USER_PRIVATE_CHAT_POSTS__ID = GENERIC__ID;
	public static final String USER_PRIVATE_CHAT_POSTS__POSTER_ID = "poster_id";
	public static final String USER_PRIVATE_CHAT_POSTS__RECIPIENT_ID = "recipient_id";
	public static final String USER_PRIVATE_CHAT_POSTS__TIME_OF_POST = "time_of_post";
	public static final String USER_PRIVATE_CHAT_POSTS__CONTENT = "content";

	/*USER SESSIONS*/
	public static final String USER_SESSIONS__USER_ID = GENERIC__USER_ID;
	public static final String USER_SESSIONS__LOGIN_TIME = "login_time";
	public static final String USER_SESSIONS__LOGOUT_TIME = "logout_time";


}