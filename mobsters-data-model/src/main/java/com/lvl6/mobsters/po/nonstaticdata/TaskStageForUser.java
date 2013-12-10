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

	@Override
	public String toString() {
		return "TaskStageForUser [id=" + id + ", userTaskId=" + userTaskId
				+ ", stageNum=" + stageNum + ", taskStageMonsterId="
				+ taskStageMonsterId + ", monsterType=" + monsterType
				+ ", expGained=" + expGained + ", cashGained=" + cashGained
				+ ", monsterPieceDropped=" + monsterPieceDropped + "]";
	}

}
