package com.lvl6.mobsters.po.staticdata;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class StructureTownHall extends BasePersistentObject<Integer> implements Serializable {

	private static final long serialVersionUID = 6957003539671371981L;


	@Id
	protected Integer id = 0;
	
	
	@Column(name="num_resource_one_generators")
	@Index
	protected int numResourceOneGenerators = 0;
	
	@Column(name="num_resource_one_storages")
	@Index
	protected int numResourceOneStorages = 0;
	
	
	@Column(name="num_resource_two_generators")
	protected int numResourceTwoGenerators = 0;
	
	@Column(name="num_resource_two_storages")
	@Index
	protected int numResourceTwoStorages = 0;
	
	@Column(name="num_hospitals")
	protected int numHospitals = 0;
	
	@Column(name="num_residences")
	protected int numResidences = 0;

	//how many monster slots this town hall gives the user (absolute number)
	@Column(name="num_monster_slots")
	protected int numMonsterSlots = 0;
	
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getNumResourceOneGenerators() {
		return numResourceOneGenerators;
	}

	public void setNumResourceOneGenerators(int numResourceOneGenerators) {
		this.numResourceOneGenerators = numResourceOneGenerators;
	}

	public int getNumResourceOneStorages() {
		return numResourceOneStorages;
	}

	public void setNumResourceOneStorages(int numResourceOneStorages) {
		this.numResourceOneStorages = numResourceOneStorages;
	}

	public int getNumResourceTwoGenerators() {
		return numResourceTwoGenerators;
	}

	public void setNumResourceTwoGenerators(int numResourceTwoGenerators) {
		this.numResourceTwoGenerators = numResourceTwoGenerators;
	}

	public int getNumResourceTwoStorages() {
		return numResourceTwoStorages;
	}

	public void setNumResourceTwoStorages(int numResourceTwoStorages) {
		this.numResourceTwoStorages = numResourceTwoStorages;
	}

	public int getNumHospitals() {
		return numHospitals;
	}

	public void setNumHospitals(int numHospitals) {
		this.numHospitals = numHospitals;
	}

	public int getNumResidences() {
		return numResidences;
	}

	public void setNumResidences(int numResidences) {
		this.numResidences = numResidences;
	}

	public int getNumMonsterSlots() {
		return numMonsterSlots;
	}

	public void setNumMonsterSlots(int numMonsterSlots) {
		this.numMonsterSlots = numMonsterSlots;
	}

	@Override
	public String toString() {
		return "StructureTownHall [id=" + id + ", numResourceOneGenerators="
				+ numResourceOneGenerators + ", numResourceOneStorages="
				+ numResourceOneStorages + ", numResourceTwoGenerators="
				+ numResourceTwoGenerators + ", numResourceTwoStorages="
				+ numResourceTwoStorages + ", numHospitals=" + numHospitals
				+ ", numResidences=" + numResidences + ", numMonsterSlots="
				+ numMonsterSlots + "]";
	}

}
