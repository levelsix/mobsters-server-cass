package com.lvl6.mobsters.po.staticdata;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class Profanity extends BasePersistentObject<Integer> implements Serializable{

	private static final long serialVersionUID = -4044083502502117675L;

	@Id
	protected Integer id = 0;
	
	@Column(name="profanity_term")
	@Index
	protected String profanityTerm = "";

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getProfanityTerm() {
		return profanityTerm;
	}

	public void setProfanityTerm(String profanityTerm) {
		this.profanityTerm = profanityTerm;
	}

	
	@Override
	public String toString() {
		return "Profanity [id=" + id + ", profanityTerm=" + profanityTerm + "]";
	}

}
