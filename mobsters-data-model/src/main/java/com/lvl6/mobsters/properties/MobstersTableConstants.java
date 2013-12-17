package com.lvl6.mobsters.properties;

public class MobstersTableConstants {
	//CONSTANTS PERTAINING TO THE DATABASE TABLES
	//MOBSTERS CONSTANTS
	public static final float GEMS_PER_DOLLAR = 10f;//client doesn't need this
	public static final float MINUTES_PER_GEM = 10f;

	public static final float MONSTER__CASH_PER_HEALTH_POINT = 0.5f;
	public static final float MONSTER__SECONDS_TO_HEAL_PER_HEALTH_POINT = 2f;
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
	public static final String MFUDR__QUEST = "quest";
	public static final String MFUDR__SELL = "sold for cash";

	//chats
	public static final int RETRIEVE_PLAYER_WALL_POSTS__NUM_POSTS_CAP = 150;

	//pvp
	public static final int PVP__REQUIRED_MIN_LEVEL = 30;
	
	//USER CURRENCY HISTORY REASON FOR CHANGE     VALUES
	public static final String UCHRFC__CREATE_CLAN = "created clan";
	public static final String UCHRFC__EARN_FREE_DIAMONDS_FB_CONNECT = "connecting to facebook";
	public static final String UCHRFC__END_TASK = "end task";
	public static final String UCHRFC__ENHANCING = "enhancing user monsters";
	public static final String UCHRFC__HEAL_MONSTER = "healed user monsters";
	public static final String UCHRFC__INCREASE_MONSTER_INVENTORY = "increased user monster inventory";
	public static final String UCHRFC__PURHCASED_BOOSTER_PACK = "purchased booster pack";
	public static final String UCHRFC__PURCHASE_NORM_STRUCT = "purchased norm struct";
	public static final String UCHRFC__RETRIEVE_CURRENCY_FROM_NORM_STRUCT = "retrieve currency from normal structures";
	public static final String UCHRFC__SPED_UP_NORM_STRUCT = "sped up norm struct";
	public static final String UCHRFC__UPGRADE_NORM_STRUCT = "upgrading norm struct";
	
	public static final String UCHRFC__REDEEMED_QUEST = "redeemed quest";
	public static final String UCHRFC__SOLD_USER_MONSTERS = "sold user monsters";
	public static final String UCHRFC__SPED_UP_COMBINING_MONSTER = "sped up combining user monster";
	public static final String UCHRFC__SPED_UP_ENHANCING = "sped up enhancing user monster";
	
	

	//SEND GROUP CHAT
	public static final int SEND_GROUP_CHAT__MAX_LENGTH_OF_CHAT_STRING = 200;
	
	//AOC2 STUFF
	//user_equip_repair
	public static int USER_EQUIP_REPAIR__MAX_QUEUE_SIZE = 5;
	
	//user
	public static int USER__GOLD = 500;
	public static int USER__TONIC = 500;
	public static int USER__GEMS = 50;
	
}