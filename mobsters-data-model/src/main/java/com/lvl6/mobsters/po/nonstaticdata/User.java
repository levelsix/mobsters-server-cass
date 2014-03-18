package com.lvl6.mobsters.po.nonstaticdata;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;

 
@Entity
public class User extends BasePersistentObject<UUID>{

	@Id
	protected UUID id = UUID.randomUUID();
	
	@Column(name="name")
	@Index
	protected String name = "";
	
	@Column(name="lvl")
	@Index
	protected int lvl = 0;
	
	//gems
	@Column(name="gems")
	@Index
	protected int gems = 0;

	@Column(name="cash")
	@Index
	protected int cash = 0;
	
	@Column(name="oil")
	protected int oil = 0;
	
	@Column(name="exp")
	@Index
	protected int exp = 0;
	
	@Column(name="tasks_completed")
	protected int tasksCompleted = 0;
	
	@Column(name="battles_won")
	@Index
	protected int battlesWon = 0;

	@Column(name="battles_lost")
	@Index
	protected int battlesLost = 0;
	
	@Column(name="flees")
	protected int flees = 0;
	
	@Column(name="referral_code")
	protected String referralCode = null;

	@Column(name="num_referrals")
	protected int numReferrals = 0;
	
	@Column(name="udid")
	@Index
	protected String udid = null;
	
	@Column(name="last_login")
	@Index
	protected Date lastLogin = null;
	
	@Column(name="last_logout")
	@Index
	protected Date lastLogout = null;
	
	@Column(name="device_token")
	protected String deviceToken = null;
	
	@Column(name="last_battle_notification_time")
	protected Date lastBattleNotificationTime = null;
	
	@Column(name="num_badges")
	protected int numBadges = 0;
	
	@Column(name="is_fake")
	protected boolean isFake;
	
	@Column(name="create_time")
	@Index
	protected Date createTime = null;
	
	@Column(name="is_admin")
	protected boolean isAdmin = false;
	
	@Column(name="apsalar_id")
	@Index
	protected String apsalarId = null;
	
	@Column(name="num_cash_retrieved_from_structs")
	protected int numCashRetrievedFromStructs = 0;
	
	@Column(name="num_oil_retrieved_from_structs")
	protected int numOilRetrievedFromStructs = 0;
	
	@Column(name="num_consecutive_days_played")
	protected int numConsecutiveDaysPlayed = 0;
	
	@Column(name="clan_id")
	@Index
	protected UUID clanId = null;
	
	//not sure if needed
	@Column(name="last_wall_post_notification_time")
	protected Date lastWallPostNotificationTime = null;
	
	@Column(name="kabam_naid")
	protected int kabamNaid = 0;
	
	@Column(name="has_received_fb_reward")
	protected boolean hasReceivedFbReward = false;
	
	@Column(name="num_additional_monster_slots")
	@Index
	protected int numAdditionalMonsterSlots = 0;
	
	//not sure if needed
	@Column(name="num_beginner_sales_purchased")
	protected int numBeginnerSalesPurchased = 0;
	
	//not really sure if needed, since pvp may be very later on
	@Column(name="has_active_shield")
	protected boolean hasActiveShield = true;
	
	@Column(name="shield_end_time")
	protected Date shieldEndTime = null;
	
	@Column(name="elo")
	protected int elo = 0;
	
	@Column(name="rank")
	protected String rank = null;
	
	//for pvp and not sure if needed
	@Column(name="last_time_queued")
	protected Date lastTimeQueued = null;

	//this summed with defenses won = battles won
	@Column(name="attacks_won")
	protected int attacksWon = 0;
	//this summed with attacks won = battles won
	@Column(name="defenses_won")
	protected int defensesWon = 0;
	
	//This summed with defenses lost = battles lost
	@Column(name="attacks_lost")
	protected int attacksLost = 0;
	//This summed with attacks lost = battles lost
	@Column(name="defenses_lost")
	protected int defensesLost = 0;
	
	@Column(name="facebook_id")
	protected String facebookId = null;
	
	@Column(name="nth_extra_slots_via_fb")
	protected int nthExtraSlotsViaFb = 0;
	
	@Column(name="game_center_id")
	@Index
	protected String gameCenterId = "";
	
	@Column(name="account_initialized")
	protected boolean accountInitialized = false;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLvl() {
		return lvl;
	}

	public void setLvl(int lvl) {
		this.lvl = lvl;
	}

	public int getGems() {
		return gems;
	}

	public void setGems(int gems) {
		this.gems = gems;
	}

	public int getCash() {
		return cash;
	}

	public void setCash(int cash) {
		this.cash = cash;
	}

	public int getOil() {
		return oil;
	}

	public void setOil(int oil) {
		this.oil = oil;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getTasksCompleted() {
		return tasksCompleted;
	}

	public void setTasksCompleted(int tasksCompleted) {
		this.tasksCompleted = tasksCompleted;
	}

	public int getBattlesWon() {
		return battlesWon;
	}

	public void setBattlesWon(int battlesWon) {
		this.battlesWon = battlesWon;
	}

	public int getBattlesLost() {
		return battlesLost;
	}

	public void setBattlesLost(int battlesLost) {
		this.battlesLost = battlesLost;
	}

	public int getFlees() {
		return flees;
	}

	public void setFlees(int flees) {
		this.flees = flees;
	}

	public String getReferralCode() {
		return referralCode;
	}

	public void setReferralCode(String referralCode) {
		this.referralCode = referralCode;
	}

	public int getNumReferrals() {
		return numReferrals;
	}

	public void setNumReferrals(int numReferrals) {
		this.numReferrals = numReferrals;
	}

	public String getUdid() {
		return udid;
	}

	public void setUdid(String udid) {
		this.udid = udid;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Date getLastLogout() {
		return lastLogout;
	}

	public void setLastLogout(Date lastLogout) {
		this.lastLogout = lastLogout;
	}

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

	public Date getLastBattleNotificationTime() {
		return lastBattleNotificationTime;
	}

	public void setLastBattleNotificationTime(Date lastBattleNotificationTime) {
		this.lastBattleNotificationTime = lastBattleNotificationTime;
	}

	public int getNumBadges() {
		return numBadges;
	}

	public void setNumBadges(int numBadges) {
		this.numBadges = numBadges;
	}

	public boolean isFake() {
		return isFake;
	}

	public void setFake(boolean isFake) {
		this.isFake = isFake;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public String getApsalarId() {
		return apsalarId;
	}

	public void setApsalarId(String apsalarId) {
		this.apsalarId = apsalarId;
	}

	public int getNumCashRetrievedFromStructs() {
		return numCashRetrievedFromStructs;
	}

	public void setNumCashRetrievedFromStructs(int numCashRetrievedFromStructs) {
		this.numCashRetrievedFromStructs = numCashRetrievedFromStructs;
	}

	public int getNumOilRetrievedFromStructs() {
		return numOilRetrievedFromStructs;
	}

	public void setNumOilRetrievedFromStructs(int numOilRetrievedFromStructs) {
		this.numOilRetrievedFromStructs = numOilRetrievedFromStructs;
	}

	public int getNumConsecutiveDaysPlayed() {
		return numConsecutiveDaysPlayed;
	}

	public void setNumConsecutiveDaysPlayed(int numConsecutiveDaysPlayed) {
		this.numConsecutiveDaysPlayed = numConsecutiveDaysPlayed;
	}

	public UUID getClanId() {
		return clanId;
	}

	public void setClanId(UUID clanId) {
		this.clanId = clanId;
	}

	public Date getLastWallPostNotificationTime() {
		return lastWallPostNotificationTime;
	}

	public void setLastWallPostNotificationTime(Date lastWallPostNotificationTime) {
		this.lastWallPostNotificationTime = lastWallPostNotificationTime;
	}

	public int getKabamNaid() {
		return kabamNaid;
	}

	public void setKabamNaid(int kabamNaid) {
		this.kabamNaid = kabamNaid;
	}

	public boolean isHasReceivedFbReward() {
		return hasReceivedFbReward;
	}

	public void setHasReceivedFbReward(boolean hasReceivedFbReward) {
		this.hasReceivedFbReward = hasReceivedFbReward;
	}

	public int getNumAdditionalMonsterSlots() {
		return numAdditionalMonsterSlots;
	}

	public void setNumAdditionalMonsterSlots(int numAdditionalMonsterSlots) {
		this.numAdditionalMonsterSlots = numAdditionalMonsterSlots;
	}

	public int getNumBeginnerSalesPurchased() {
		return numBeginnerSalesPurchased;
	}

	public void setNumBeginnerSalesPurchased(int numBeginnerSalesPurchased) {
		this.numBeginnerSalesPurchased = numBeginnerSalesPurchased;
	}

	public boolean isHasActiveShield() {
		return hasActiveShield;
	}

	public void setHasActiveShield(boolean hasActiveShield) {
		this.hasActiveShield = hasActiveShield;
	}

	public Date getShieldEndTime() {
		return shieldEndTime;
	}

	public void setShieldEndTime(Date shieldEndTime) {
		this.shieldEndTime = shieldEndTime;
	}

	public int getElo() {
		return elo;
	}

	public void setElo(int elo) {
		this.elo = elo;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public Date getLastTimeQueued() {
		return lastTimeQueued;
	}

	public void setLastTimeQueued(Date lastTimeQueued) {
		this.lastTimeQueued = lastTimeQueued;
	}

	public int getAttacksWon() {
		return attacksWon;
	}

	public void setAttacksWon(int attacksWon) {
		this.attacksWon = attacksWon;
	}

	public int getDefensesWon() {
		return defensesWon;
	}

	public void setDefensesWon(int defensesWon) {
		this.defensesWon = defensesWon;
	}

	public int getAttacksLost() {
		return attacksLost;
	}

	public void setAttacksLost(int attacksLost) {
		this.attacksLost = attacksLost;
	}

	public int getDefensesLost() {
		return defensesLost;
	}

	public void setDefensesLost(int defensesLost) {
		this.defensesLost = defensesLost;
	}

	public String getFacebookId() {
		return facebookId;
	}

	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}

	public int getNthExtraSlotsViaFb() {
		return nthExtraSlotsViaFb;
	}

	public void setNthExtraSlotsViaFb(int nthExtraSlotsViaFb) {
		this.nthExtraSlotsViaFb = nthExtraSlotsViaFb;
	}

	public String getGameCenterId() {
		return gameCenterId;
	}

	public void setGameCenterId(String gameCenterId) {
		this.gameCenterId = gameCenterId;
	}

	public boolean isAccountInitialized() {
		return accountInitialized;
	}

	public void setAccountInitialized(boolean accountInitialized) {
		this.accountInitialized = accountInitialized;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", lvl=" + lvl + ", gems="
				+ gems + ", cash=" + cash + ", oil=" + oil + ", exp=" + exp
				+ ", tasksCompleted=" + tasksCompleted + ", battlesWon="
				+ battlesWon + ", battlesLost=" + battlesLost + ", flees="
				+ flees + ", referralCode=" + referralCode + ", numReferrals="
				+ numReferrals + ", udid=" + udid + ", lastLogin=" + lastLogin
				+ ", lastLogout=" + lastLogout + ", deviceToken=" + deviceToken
				+ ", lastBattleNotificationTime=" + lastBattleNotificationTime
				+ ", numBadges=" + numBadges + ", isFake=" + isFake
				+ ", createTime=" + createTime + ", isAdmin=" + isAdmin
				+ ", apsalarId=" + apsalarId
				+ ", numCashRetrievedFromStructs="
				+ numCashRetrievedFromStructs
				+ ", numOilRetrievedFromStructs=" + numOilRetrievedFromStructs
				+ ", numConsecutiveDaysPlayed=" + numConsecutiveDaysPlayed
				+ ", clanId=" + clanId + ", lastWallPostNotificationTime="
				+ lastWallPostNotificationTime + ", kabamNaid=" + kabamNaid
				+ ", hasReceivedFbReward=" + hasReceivedFbReward
				+ ", numAdditionalMonsterSlots=" + numAdditionalMonsterSlots
				+ ", numBeginnerSalesPurchased=" + numBeginnerSalesPurchased
				+ ", hasActiveShield=" + hasActiveShield + ", shieldEndTime="
				+ shieldEndTime + ", elo=" + elo + ", rank=" + rank
				+ ", lastTimeQueued=" + lastTimeQueued + ", attacksWon="
				+ attacksWon + ", defensesWon=" + defensesWon
				+ ", attacksLost=" + attacksLost + ", defensesLost="
				+ defensesLost + ", facebookId=" + facebookId
				+ ", nthExtraSlotsViaFb=" + nthExtraSlotsViaFb
				+ ", gameCenterId=" + gameCenterId + ", accountInitialized="
				+ accountInitialized + "]";
	}
	
//	@Column(name="email")
//	protected String email = "";

}
