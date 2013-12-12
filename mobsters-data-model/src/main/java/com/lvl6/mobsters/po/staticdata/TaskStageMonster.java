package com.lvl6.mobsters.po.staticdata;

import java.io.Serializable;
import java.util.Random;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class TaskStageMonster extends BasePersistentObject<Integer> implements Serializable{

	private static final long serialVersionUID = 4991947856341943944L;

	@Id
	protected Integer id = 0;
	
	//also task_stage_id
	@Column(name="stage_id")
	@Index
	protected int stageId = 0;
	
	@Column(name="monster_id")
	protected int monsterId = 0;
	
	@Column(name="monster_type")
	@Index
	protected String monsterType = null;
	
	@Column(name="exp_reward")
	@Index
	protected int expReward = 0;
	
	@Column(name="min_cash_drop")
	protected int minCashDrop = 0;
	
	@Column(name="max_cash_drop")
	protected int maxCashDrop = 1;
	
	@Column(name="puzzle_piece_drop_rate")
	protected float puzzlePieceDropRate = 0.1F;

	//enhancement level
	@Column(name="level")
	@Index
	protected int level = 0;
	
	@Column(name="chance_to_appear")
	protected float chanceToAppear = 0.1F;

	
	//not represented in db
	protected Random rand = null;
	//convenience method for logic regarding computing cash reward and if a piece dropped
	public int getCashDrop() {
		//example goal: [min,max]=[5, 10], transform range to start at 0.
		//[min-min, max-min] = [0,max-min] = [0,10-5] = [0,5]
		//this means there are (10-5)+1 possible numbers

		int minMaxDiff = getMaxCashDrop() - getMinCashDrop();
		int randCash = rand.nextInt(minMaxDiff + 1); 

		//number generated in [0, max-min] range, but need to transform
		//back to original range [min, max]. so add min. [0+min, max-min+min]
		return randCash + getMinCashDrop();
	}
	public boolean didPuzzlePieceDrop() {
		float randFloat = this.rand.nextFloat();

		if (randFloat < this.puzzlePieceDropRate) {
			return true;
		} else {
			return false;
		}
	}

	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getStageId() {
		return stageId;
	}

	public void setStageId(int stageId) {
		this.stageId = stageId;
	}

	public int getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(int monsterId) {
		this.monsterId = monsterId;
	}

	public String getMonsterType() {
		return monsterType;
	}

	public void setMonsterType(String monsterType) {
		this.monsterType = monsterType;
	}

	public int getExpReward() {
		return expReward;
	}

	public void setExpReward(int expReward) {
		this.expReward = expReward;
	}

	public int getMinCashDrop() {
		return minCashDrop;
	}

	public void setMinCashDrop(int minCashDrop) {
		this.minCashDrop = minCashDrop;
	}

	public int getMaxCashDrop() {
		return maxCashDrop;
	}

	public void setMaxCashDrop(int maxCashDrop) {
		this.maxCashDrop = maxCashDrop;
	}

	public float getPuzzlePieceDropRate() {
		return puzzlePieceDropRate;
	}

	public void setPuzzlePieceDropRate(float puzzlePieceDropRate) {
		this.puzzlePieceDropRate = puzzlePieceDropRate;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public float getChanceToAppear() {
		return chanceToAppear;
	}

	public void setChanceToAppear(float chanceToAppear) {
		this.chanceToAppear = chanceToAppear;
	}

	public Random getRand() {
		return rand;
	}
	public void setRand(Random rand) {
		this.rand = rand;
	}
	
	@Override
	public String toString() {
		return "TaskStageMonster [id=" + id + ", stageId=" + stageId
				+ ", monsterId=" + monsterId + ", monsterType=" + monsterType
				+ ", expReward=" + expReward + ", minCashDrop=" + minCashDrop
				+ ", maxCashDrop=" + maxCashDrop + ", puzzlePieceDropRate="
				+ puzzlePieceDropRate + ", level=" + level
				+ ", chanceToAppear=" + chanceToAppear + "]";
	}

}
