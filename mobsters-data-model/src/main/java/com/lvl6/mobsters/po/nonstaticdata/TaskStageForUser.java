package com.lvl6.mobsters.po.nonstaticdata;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class TaskStageForUser extends BasePersistentObject<UUID>{

	@Id
	protected UUID id = UUID.randomUUID();
	
	@Column(name="user_task_id")
	@Index
	protected UUID userTaskId = null;
	
	@Column(name="stage_num")
	@Index
	protected int stageNum = 0;
	
	@Column(name="task_stage_monster_id")
	protected int taskStageMonsterId = 0;
	
	@Column(name="monster_type")
	protected String monsterType = "";
	
	@Column(name="exp_gained")
	@Index
	protected int expGained = 0;
	
	@Column(name="cash_gained")
	protected int cashGained = 0;
	
	@Column(name="monster_piece_dropped")
	protected boolean monsterPieceDropped = false;
	
	//not stored in db
	protected int taskStageId = 0;
	protected int monsterId = 0;
	protected int monsterLvl = 0;
	//will always be false if monsterPieceDropped == true;
	protected boolean itemDropped = false;
	protected int itemId = -1;
	
	
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getUserTaskId() {
		return userTaskId;
	}

	public void setUserTaskId(UUID userTaskId) {
		this.userTaskId = userTaskId;
	}

	public int getStageNum() {
		return stageNum;
	}

	public void setStageNum(int stageNum) {
		this.stageNum = stageNum;
	}

	public int getTaskStageMonsterId() {
		return taskStageMonsterId;
	}

	public void setTaskStageMonsterId(int taskStageMonsterId) {
		this.taskStageMonsterId = taskStageMonsterId;
	}

	public String getMonsterType() {
		return monsterType;
	}

	public void setMonsterType(String monsterType) {
		this.monsterType = monsterType;
	}

	public int getExpGained() {
		return expGained;
	}

	public void setExpGained(int expGained) {
		this.expGained = expGained;
	}

	public int getCashGained() {
		return cashGained;
	}

	public void setCashGained(int cashGained) {
		this.cashGained = cashGained;
	}

	public boolean isMonsterPieceDropped() {
		return monsterPieceDropped;
	}

	public void setMonsterPieceDropped(boolean monsterPieceDropped) {
		this.monsterPieceDropped = monsterPieceDropped;
	}
	
	public int getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(int monsterId) {
		this.monsterId = monsterId;
	}

	public int getTaskStageId() {
		return taskStageId;
	}

	public void setTaskStageId(int taskStageId) {
		this.taskStageId = taskStageId;
	}

	public int getMonsterLvl() {
		return monsterLvl;
	}

	public void setMonsterLvl(int monsterLvl) {
		this.monsterLvl = monsterLvl;
	}

	public boolean isItemDropped() {
		return itemDropped;
	}

	public void setItemDropped(boolean itemDropped) {
		this.itemDropped = itemDropped;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	@Override
	public String toString() {
		return "TaskStageForUser [id=" + id + ", userTaskId=" + userTaskId
				+ ", stageNum=" + stageNum + ", taskStageMonsterId="
				+ taskStageMonsterId + ", monsterType=" + monsterType
				+ ", expGained=" + expGained + ", cashGained=" + cashGained
				+ ", monsterPieceDropped=" + monsterPieceDropped
				+ ", taskStageId=" + taskStageId + ", monsterId=" + monsterId
				+ ", monsterLvl=" + monsterLvl + "]";
	}

}
