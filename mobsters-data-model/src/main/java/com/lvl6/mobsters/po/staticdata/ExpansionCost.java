package com.lvl6.mobsters.po.staticdata;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class ExpansionCost extends BasePersistentObject<Integer> implements Serializable{

	private static final long serialVersionUID = 3799063741786243224L;

	@Id
	protected Integer id = 0;
	
	@Column(name="expansion_cost_cash")
	@Index
	protected int expansionCostCash = 0;
	
	@Column(name="num_minutes_to_expand")
	@Index
	protected int numMinutesToExpand = 0;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getExpansionCostCash() {
		return expansionCostCash;
	}

	public void setExpansionCostCash(int expansionCostCash) {
		this.expansionCostCash = expansionCostCash;
	}

	public int getNumMinutesToExpand() {
		return numMinutesToExpand;
	}

	public void setNumMinutesToExpand(int numMinutesToExpand) {
		this.numMinutesToExpand = numMinutesToExpand;
	}

	@Override
	public String toString() {
		return "ExpansionCost [id=" + id + ", expansionCostCash="
				+ expansionCostCash + ", numMinutesToExpand="
				+ numMinutesToExpand + "]";
	}
	
}
