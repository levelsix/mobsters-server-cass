package com.lvl6.mobsters.po.staticdata;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class StructureHospital extends BasePersistentObject<Integer> implements Serializable{

	private static final long serialVersionUID = 6531253938335942919L;

	@Id
	protected Integer id = 0;
	
	@Column(name="queue_size")
	@Index
	protected int queueSize = 0;

	@Column(name="health_per_second")
	protected float healthPerSecond = 0F;

	
	
	
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

	public float getHealthPerSecond() {
		return healthPerSecond;
	}

	public void setHealthPerSecond(float healthPerSecond) {
		this.healthPerSecond = healthPerSecond;
	}

	@Override
	public String toString() {
		return "StructureHospital [id=" + id + ", queueSize=" + queueSize
				+ ", healthPerSecond=" + healthPerSecond + "]";
	}
}
