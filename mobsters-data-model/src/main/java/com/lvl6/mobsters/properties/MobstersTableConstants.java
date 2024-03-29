package com.lvl6.mobsters.properties;

public class MobstersTableConstants {
	//CONSTANTS PERTAINING TO THE DATABASE TABLES
	//MOBSTERS CONSTANTS
	
	//includes oil and cash, 1 gem per 1000 resource? 
	public static final float GEMS_PER_RESOURCE = 0.001F;
	public static final float GEMS_PER_DOLLAR = 10F;//client doesn't need this
	public static final float MINUTES_PER_GEM = 10F;
	
	//this multiplies with the cost to heal all monsters on user's battle team
	//BATTLE, DUNGEON
	public static final float BATTLE__CONTINUE_GEM_COST_MULTIPLIER = 1.2F; 
	
	//CLAN CONSTANTS
	public static final int CLAN__CREATE_CLAN_CASH_PRICE = 1000;
	public static final int CLAN__MAX_NAME_LENGTH = 15;
	public static final int CLAN__MAX_CLAN_TAG_LENGTH = 5;
	public static final int CLAN__MAX_NUM_MEMBERS = 50;

	public static final float MONSTER__CASH_PER_HEALTH_POINT = 0.5F;
	public static final float MONSTER__SECONDS_TO_HEAL_PER_HEALTH_POINT = 2F;
	public static final float MONSTER__ELEMENTAL_STRENGTH = 1.2F;
	public static final float MONSTER__ELEMENTAL_WEAKNESS = 0.8F;

	public static final int MONSTER_INVENTORY_SLOTS__INCREMENT_AMOUNT = 5;
	public static final int MONSTER_INVENTORY_SLOTS__GEM_PRICE_PER_SLOT = 2;
	public static final int MONSTER_INVENTORY_SLOTS__MIN_INVITES_TO_INCREASE_SLOTS = 3;

	public static final int MONSTER_FOR_USER__MAX_TEAM_SIZE = 3;
	public static final int MONSTER_FOR_USER__INITIAL_MAX_NUM_MONSTER_LIMIT = 10;

	//MFUSOP = monster_for_user_source_of_pieces
	public static final String MFUSOP__END_DUNGEON = "Task4UserId";
	public static final String MFUSOP__QUEST = "questId"; 
	public static final String MFUSOP__BOOSTER_PACK = "boosterPackId";

	//MFUDR = monster_for_user_delete_reasons
	public static final String MFUDR__ENHANCING = "enhancing";
	public static final String MFUDR__EVOLVING = "evolving";
	public static final String MFUDR__QUEST = "quest";
	public static final String MFUDR__SELL = "sold for cash";

	//chats
	public static final int RETRIEVE_PLAYER_WALL_POSTS__NUM_POSTS_CAP = 150;

	//pvp
	public static final int PVP__REQUIRED_MIN_LEVEL = 30;
	
	public static final int USER_CREATE__MAX_NAME_LENGTH = 15;
	public static final int USER_CREATE__MIN_NAME_LENGTH = 5;
	
	//USER CURRENCY HISTORY REASON FOR CHANGE     VALUES
	public static final String UCHRFC__CREATE_CLAN = "created clan";
	public static final String UCHRFC__CURRENCY_EXCHANGED = "currency exchanged";
	public static final String UCHRFC__EARN_FREE_DIAMONDS_FB_CONNECT = "connecting to facebook";
	public static final String UCHRFC__END_TASK = "end task";
	public static final String UCHRFC__ENHANCING = "enhancing user monsters";
	public static final String UCHRFC__EVOLVING = "evolving user monsters";
	public static final String UCHRFC__HEAL_MONSTER_OR_SPED_UP_HEALING = "healing or sped up healing user monsters";
	public static final String UCHRFC__INCREASE_MONSTER_INVENTORY = "increased user monster inventory";
	public static final String UCHRFC__PURHCASED_BOOSTER_PACK = "purchased booster pack";
	public static final String UCHRFC__PURHCASED_CITY_EXPANSION = "purchased_city_expansion";
	public static final String UCHRFC__PURCHASE_NORM_STRUCT = "purchased norm struct";
	public static final String UCHRFC__REDEEMED_QUEST = "redeemed quest";
	public static final String UCHRFC__RETRIEVE_CURRENCY_FROM_NORM_STRUCT = "retrieve currency from normal structures";
	public static final String UCHRFC__REVIVED_MONSTER = "revived monsters";
	public static final String UCHRFC__SOLD_USER_MONSTERS = "sold user monsters";
	public static final String UCHRFC__SPED_UP_CITY_EXPANSION = "sped up city expansion";
	public static final String UCHRFC__SPED_UP_COMBINING_MONSTER = "sped up combining user monsters";
	public static final String UCHRFC__SPED_UP_ENHANCING = "sped up enhancing user monsters";
	public static final String UCHRFC__SPED_UP_EVOLVING = "sped up evolving user monster";
//	public static final String UCHRFC__SPED_UP_HEALING = "sped up healing user monster";
	public static final String UCHRFC__SPED_UP_NORM_STRUCT = "sped up norm struct";
	public static final String UCHRFC__SPED_UP_PERSISTENT_EVENT_TIMER = "sped up peristent event timer";
	public static final String UCHRFC__UPGRADE_NORM_STRUCT = "upgrading norm struct";
	
	
	//SEND GROUP CHAT
	public static final int SEND_GROUP_CHAT__MAX_LENGTH_OF_CHAT_STRING = 200;
	
	//LEVEL UP
	public static final int LEVEL_UP__MAX_LEVEL_FOR_USER = 100; //add level up equipment for fake players if increasing

	//PURCHASE NORM STRUCTURE
	public static final int PURCHASE_NORM_STRUCTURE__MAX_NUM_OF_CERTAIN_STRUCTURE = 3;

	
	//AOC2 STUFF
	//user_equip_repair
	public static int USER_EQUIP_REPAIR__MAX_QUEUE_SIZE = 5;
	
	//user
	public static int USER__GOLD = 500;
	public static int USER__TONIC = 500;
	public static int USER__GEMS = 50;
	
}