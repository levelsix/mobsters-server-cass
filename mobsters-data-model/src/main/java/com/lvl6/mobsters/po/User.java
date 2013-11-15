package com.lvl6.mobsters.po;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.lvl6.mobsters.entitymanager.Index;


 
@Entity
public class User extends BasePersistentObject{

	@Id
	protected UUID id = UUID.randomUUID();
	
	@Column(name="name")
	@Index
	protected String name = "";
	
	@Column(name="lvl")
	@Index
	protected int lvl = 0;
	
	//total exp
	@Column(name="exp")
	@Index
	protected int exp = 0;

	@Column(name="gold")
	@Index
	protected int gold = 0;
	
	@Column(name="tonic")
	@Index
	protected int tonic = 0;
	
	//in game currency
	@Column(name="gems")
	@Index
	protected int gems = 0;
	
	@Column(name="class_type")
	@Index
	protected int classType = 0;

	@Column(name="max_hp")
	protected int maxHp = 0;
	
	@Column(name="hp")
	protected int hp = 0;
	
	@Column(name="last_time_hp_regened")
	protected Date lastTimeHpRegened = null;

	@Column(name="max_mana")
	protected int maxMana = 0;
	
	@Column(name="mana")
	protected int mana = 0;
	
	@Column(name="last_time_mana_regened")
	protected Date lastTimeManaRegened = null;
	
	//if has a gameCenterId use that id, else generate random string
	@Column(name="game_center_id")
	@Index
	protected String gameCenterId = "";
	
	//@Column(name="signup_date")
	//protected Date signupDate = null;
	
	@Column(name="clan_id")
	@Index
	protected String clanId = null;
	
	//@Column(name="last_login")
	//protected Date lastLogin = null;
	
	@Column(name="account_initialized")
	@Index
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

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public int getTonic() {
		return tonic;
	}

	public void setTonic(int tonic) {
		this.tonic = tonic;
	}

	public int getGems() {
		return gems;
	}

	public void setGems(int gems) {
		this.gems = gems;
	}

	public int getClassType() {
		return classType;
	}

	public void setClassType(int classType) {
		this.classType = classType;
	}

	public int getMaxHp() {
		return maxHp;
	}

	public void setMaxHp(int maxHp) {
		this.maxHp = maxHp;
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public Date getLastTimeHpRegened() {
		return lastTimeHpRegened;
	}

	public void setLastTimeHpRegened(Date lastTimeHpRegened) {
		this.lastTimeHpRegened = lastTimeHpRegened;
	}

	public int getMaxMana() {
		return maxMana;
	}

	public void setMaxMana(int maxMana) {
		this.maxMana = maxMana;
	}

	public int getMana() {
		return mana;
	}

	public void setMana(int mana) {
		this.mana = mana;
	}

	public Date getLastTimeManaRegened() {
		return lastTimeManaRegened;
	}

	public void setLastTimeManaRegened(Date lastTimeManaRegened) {
		this.lastTimeManaRegened = lastTimeManaRegened;
	}

	public String getGameCenterId() {
		return gameCenterId;
	}

	public void setGameCenterId(String gameCenterId) {
		this.gameCenterId = gameCenterId;
	}

//	public Date getSignupDate() {
//		return signupDate;
//	}
//
//	public void setSignupDate(Date signupDate) {
//		this.signupDate = signupDate;
//	}

	public String getClanId() {
		return clanId;
	}

	public void setClanId(String clanId) {
		this.clanId = clanId;
	}

//	public Date getLastLogin() {
//		return lastLogin;
//	}
//
//	public void setLastLogin(Date lastLogin) {
//		this.lastLogin = lastLogin;
//	}
	
	public boolean isAccountInitialized() {
		return accountInitialized;
	}
	
	public void setAccountInitialized(boolean accountInitialized) {
		this.accountInitialized = accountInitialized;
	}	


	
	
//	@Column(name="email")
//	protected String email = "";
	
	

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", lvl=" + lvl
				+ ", exp=" + exp + ", gold=" + gold + ", tonic="
				+ tonic + ", gems=" + gems + ", classType=" + classType
				+ ", maxHp=" + maxHp + ", hp=" + hp + ", lastTimeHpRegened="
				+ lastTimeHpRegened + ", maxMana=" + maxMana + ", mana=" + mana
				+ ", lastTimeManaRegened=" + lastTimeManaRegened
				+ ", gameCenterId=" + gameCenterId + ", clanId=" + clanId
				+ ", accountInitialized=" + accountInitialized + "]";
	}


	@Override
	public Set<String> getTableUpdateStatements() {
		Set<String> indexes = new HashSet<String>();
		
		return indexes;
	}
	
}
