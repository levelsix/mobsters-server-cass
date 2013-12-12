package com.lvl6.mobsters.po.staticdata;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class TaskStage extends BasePersistentObject<Integer> implements Serializable {

	private static final long serialVersionUID = 991811355776313516L;

	//explicit values are useless, I just made random values
	@Id
	protected Integer id = 0;
	
	@Column(name="task_id")
	@Index
	protected int taskId = 0;
	
	@Column(name="stage_num")
	@Index
	protected int stageNum = 0;
	
	//don't think this is used
//	@Column(name="equip_drop_rate")
//	protected double equipDropRate = 0.5;

	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public int getStageNum() {
		return stageNum;
	}

	public void setStageNum(int stageNum) {
		this.stageNum = stageNum;
	}

	@Override
	public String toString() {
		return "TaskStage [id=" + id + ", taskId=" + taskId + ", stageNum="
				+ stageNum + "]";
	}

//	public double getEquipDropRate() {
//		return equipDropRate;
//	}
//
//	public void setEquipDropRate(double equipDropRate) {
//		this.equipDropRate = equipDropRate;
//	}

}
