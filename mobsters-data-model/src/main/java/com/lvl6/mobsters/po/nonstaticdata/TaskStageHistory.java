package com.lvl6.mobsters.po.nonstaticdata;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class TaskStageHistory extends BasePersistentObject<UUID>{

	//id in taskStageForUser class/table
	@Id
	protected UUID id = UUID.randomUUID();
	
	@Column(name="task_for_user_id")
	@Index
	protected UUID taskForUserId = null;
	
	@Column(name="stage_num")
	@Index
	protected int stageNum = 0;
	
	@Column(name="task_stage_monster_id")
	@Index
	protected int taskStageMonsterId = 0;

	@Column(name="exp_gained")
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

	public UUID getTaskForUserId() {
		return taskForUserId;
	}

	public void setTaskForUserId(UUID taskForUserId) {
		this.taskForUserId = taskForUserId;
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
		return "TaskStageHistory [id=" + id + ", taskForUserId="
				+ taskForUserId + ", stageNum=" + stageNum
				+ ", taskStageMonsterId=" + taskStageMonsterId + ", expGained="
				+ expGained + ", cashGained=" + cashGained
				+ ", monsterPieceDropped=" + monsterPieceDropped + "]";
	}

}
