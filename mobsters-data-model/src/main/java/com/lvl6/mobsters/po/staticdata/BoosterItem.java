package com.lvl6.mobsters.po.staticdata;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class BoosterItem extends BasePersistentObject<Integer> implements Serializable{

	private static final long serialVersionUID = -5642227706963554577L;

	@Id
	protected Integer id = 0;
	
	@Column(name="booster_pack_id")
	@Index
	protected int boosterPackId = 0;
	
	@Column(name="monster_id")
	protected int monsterId = 0;
	
	@Column(name="num_pieces")
	protected int numPieces = 0;
	
	@Column(name="is_complete")
	@Index
	protected boolean isComplete = false;
	
	@Column(name="is_special")
	protected boolean isSpecial = false;

	@Column(name="gem_reward")
	@Index
	protected int gemReward = 0;
	
	@Column(name="cash_reward")
	@Index
	protected int cashReward = 0;
	
	@Column(name="chance_to_appear")
	@Index
	protected float chanceToAppear = 0F;

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

	public int getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(int monsterId) {
		this.monsterId = monsterId;
	}

	public int getNumPieces() {
		return numPieces;
	}

	public void setNumPieces(int numPieces) {
		this.numPieces = numPieces;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}

	public boolean isSpecial() {
		return isSpecial;
	}

	public void setSpecial(boolean isSpecial) {
		this.isSpecial = isSpecial;
	}

	public int getGemReward() {
		return gemReward;
	}

	public void setGemReward(int gemReward) {
		this.gemReward = gemReward;
	}

	public int getCashReward() {
		return cashReward;
	}

	public void setCashReward(int cashReward) {
		this.cashReward = cashReward;
	}

	public float getChanceToAppear() {
		return chanceToAppear;
	}

	public void setChanceToAppear(float chanceToAppear) {
		this.chanceToAppear = chanceToAppear;
	}

	@Override
	public String toString() {
		return "BoosterItem [id=" + id + ", boosterPackId=" + boosterPackId
				+ ", monsterId=" + monsterId + ", numPieces=" + numPieces
				+ ", isComplete=" + isComplete + ", isSpecial=" + isSpecial
				+ ", gemReward=" + gemReward + ", cashReward=" + cashReward
				+ ", chanceToAppear=" + chanceToAppear + "]";
	}

}
