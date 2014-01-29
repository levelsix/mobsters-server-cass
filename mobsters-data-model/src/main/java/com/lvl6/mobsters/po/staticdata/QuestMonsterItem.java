package com.lvl6.mobsters.po.staticdata;

import java.io.Serializable;
import java.util.Random;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;

//only one item exists for questId 
//maybe a monsterId can have multiple items associated with it
//questId should only appear in this table once

@Entity
public class QuestMonsterItem extends BasePersistentObject<Integer> implements Serializable {

	private static final long serialVersionUID = 3427836206792960071L;

	//explicit values are useless, I just made random values
	@Id
	protected Integer id = 0;
	
	@Column(name="quest_id")
	@Index
	protected int questId = 0;
	
	@Column(name="monster_id")
	@Index
	protected int monsterId = 0;
	
	@Column(name="item_id")
	protected int itemId = 0;
	
	@Column(name="item_drop_rate")
	protected float itemDropRate;
	

	//convenience object
	private Random rand;

	//only one item exists for questId 
	//maybe a monsterId can have multiple items associated with it
	//questId should only appear in this table once

	//covenience methods--------------------------------------------------------
	public Random getRand() {
		return rand;
	}

	public void setRand(Random rand) {
		this.rand = rand;
	}

	public boolean didItemDrop() {
		float randFloat = getRand().nextFloat();

		if (randFloat < getItemDropRate()) {
			return true;
		} else {
			return false;
		}
	}

	//end covenience methods--------------------------------------------------------

	


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public int getQuestId() {
		return questId;
	}


	public void setQuestId(int questId) {
		this.questId = questId;
	}


	public int getMonsterId() {
		return monsterId;
	}


	public void setMonsterId(int monsterId) {
		this.monsterId = monsterId;
	}


	public int getItemId() {
		return itemId;
	}


	public void setItemId(int itemId) {
		this.itemId = itemId;
	}


	public float getItemDropRate() {
		return itemDropRate;
	}


	public void setItemDropRate(float itemDropRate) {
		this.itemDropRate = itemDropRate;
	}

	@Override
	public String toString() {
		return "QuestMonsterItem [id=" + id + ", questId=" + questId
				+ ", monsterId=" + monsterId + ", itemId=" + itemId
				+ ", itemDropRate=" + itemDropRate + "]";
	}

}
