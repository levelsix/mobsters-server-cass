package com.lvl6.mobsters.po.staticdata;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class MonsterBattleDialogue extends BasePersistentObject<Integer> implements Serializable {

	private static final long serialVersionUID = -1344600430538783384L;

	//explicit values are useless, I just made random values
	@Id
	protected Integer id = 0;
	
	@Column(name="monster_id")
	@Index
	protected int monsterId = 0;
	
	@Column(name="dialogue_type")
	protected String dialogueType = "";
	
	@Column(name="dialogue")
	protected String dialogue = "";
	
	@Column(name="probability_uttered")
	protected float probabilityUttered = 0.0F;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(int monsterId) {
		this.monsterId = monsterId;
	}

	public String getDialogueType() {
		return dialogueType;
	}

	public void setDialogueType(String dialogueType) {
		this.dialogueType = dialogueType;
	}

	public String getDialogue() {
		return dialogue;
	}

	public void setDialogue(String dialogue) {
		this.dialogue = dialogue;
	}

	public float getProbabilityUttered() {
		return probabilityUttered;
	}

	public void setProbabilityUttered(float probabilityUttered) {
		this.probabilityUttered = probabilityUttered;
	}

	@Override
	public String toString() {
		return "MonsterBattleDialogue [id=" + id + ", monsterId=" + monsterId
				+ ", dialogueType=" + dialogueType + ", dialogue=" + dialogue
				+ ", probabilityUttered=" + probabilityUttered + "]";
	}
	
}
