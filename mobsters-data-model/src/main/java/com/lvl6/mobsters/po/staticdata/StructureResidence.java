package com.lvl6.mobsters.po.staticdata;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class StructureResidence extends BasePersistentObject<Integer> implements Serializable {

	private static final long serialVersionUID = -7514248476784739606L;

	@Id
	protected Integer id = 0;
	
	@Column(name="num_monster_slots")
	@Index
	protected int numMonsterSlots = 0;
	
	@Column(name="num_bonus_monster_slots")
	protected int numBonusMonsterSlots = 0;
	
	@Column(name="num_gems_required")
	protected int numGemsRequired = 0;
	
	@Column(name="num_accepeted_fb_invites")
	protected int numAcceptedFbInvites = 0;

	
	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getNumMonsterSlots() {
		return numMonsterSlots;
	}

	public void setNumMonsterSlots(int numMonsterSlots) {
		this.numMonsterSlots = numMonsterSlots;
	}

	public int getNumBonusMonsterSlots() {
		return numBonusMonsterSlots;
	}

	public void setNumBonusMonsterSlots(int numBonusMonsterSlots) {
		this.numBonusMonsterSlots = numBonusMonsterSlots;
	}

	public int getNumGemsRequired() {
		return numGemsRequired;
	}

	public void setNumGemsRequired(int numGemsRequired) {
		this.numGemsRequired = numGemsRequired;
	}

	public int getNumAcceptedFbInvites() {
		return numAcceptedFbInvites;
	}

	public void setNumAcceptedFbInvites(int numAcceptedFbInvites) {
		this.numAcceptedFbInvites = numAcceptedFbInvites;
	}

	@Override
	public String toString() {
		return "StructureResidence [id=" + id + ", numMonsterSlots=" + numMonsterSlots
				+ ", numBonusMonsterSlots=" + numBonusMonsterSlots
				+ ", numGemsRequired=" + numGemsRequired
				+ ", numAcceptedFbInvites=" + numAcceptedFbInvites + "]";
	}


}
