package com.lvl6.mobsters.po.staticdata;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class StructureLab extends BasePersistentObject<Integer> implements Serializable {

	private static final long serialVersionUID = 1448690497935803206L;

	@Id
	protected Integer id = 0;
	
	@Column(name="queue_size")
	protected int queueSize = 0;
	
	@Column(name="points_per_second")
	@Index
	protected float pointsPerSecond = 0F;

	
	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getQueueSize() {
		return queueSize;
	}

	public void setQueueSize(int queueSize) {
		this.queueSize = queueSize;
	}

	public float getPointsPerSecond() {
		return pointsPerSecond;
	}

	public void setPointsPerSecond(float pointsPerSecond) {
		this.pointsPerSecond = pointsPerSecond;
	}

	@Override
	public String toString() {
		return "StructureLab [id=" + id + ", queueSize=" + queueSize
				+ ", pointsPerSecond=" + pointsPerSecond + "]";
	}
	
}
