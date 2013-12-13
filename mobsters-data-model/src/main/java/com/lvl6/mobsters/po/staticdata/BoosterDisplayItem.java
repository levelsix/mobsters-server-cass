package com.lvl6.mobsters.po.staticdata;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class BoosterDisplayItem extends BasePersistentObject<Integer> implements Serializable{

	private static final long serialVersionUID = 3923073441025256214L;

	@Id
	protected Integer id = 0;
	
	@Column(name="booster_pack_id")
	@Index
	protected int boosterPackId = 0;
	
	@Column(name="is_monster")
	@Index
	protected boolean isMonster = false;
	
	@Column(name="is_complete")
	@Index
	protected boolean isComplete = false;

	@Column(name="monster_quality")
	@Index
	protected String monsterQuality = "";

	@Column(name="gem_reward")
	@Index
	protected int gemReward = 0;
	
	@Column(name="quantity")
	@Index
	protected int quantity = 0;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getBoosterPackId() {
		return boosterPackId;
	}

	public void setBoosterPackId(int boosterPackId) {
		this.boosterPackId = boosterPackId;
	}

	public boolean isMonster() {
		return isMonster;
	}

	public void setMonster(boolean isMonster) {
		this.isMonster = isMonster;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}

	public String getMonsterQuality() {
		return monsterQuality;
	}

	public void setMonsterQuality(String monsterQuality) {
		this.monsterQuality = monsterQuality;
	}

	public int getGemReward() {
		return gemReward;
	}

	public void setGemReward(int gemReward) {
		this.gemReward = gemReward;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "BoosterDisplayItem [id=" + id + ", boosterPackId="
				+ boosterPackId + ", isMonster=" + isMonster + ", isComplete="
				+ isComplete + ", monsterQuality=" + monsterQuality
				+ ", gemReward=" + gemReward + ", quantity=" + quantity + "]";
	}

}
