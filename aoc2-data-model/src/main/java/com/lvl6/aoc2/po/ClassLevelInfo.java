package com.lvl6.aoc2.po;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.lvl6.aoc2.entitymanager.Index;



@Entity
public class ClassLevelInfo extends BasePersistentObject{

	@Id
	protected UUID id = UUID.randomUUID();
	
	//we'll set this manually
	@Column(name="class_type")
	@Index
	protected int classType = 0;
	
	@Column(name="lvl")
	@Index
	protected int lvl = 0;
	
	@Column(name="max_hp")
	protected int maxHp = 0;
	
	@Column(name="max_mana")
	protected int maxMana = 0;
	
	//exp required to level up
	@Column(name="max_exp")
	protected int maxExp = 0;
	
	@Column(name="attack")
	protected int attack = 0;
	
	@Column(name="defense")
	protected int defense = 0;
	
	

	public UUID getId() {
		return id;
	}


	public void setId(UUID id) {
		this.id = id;
	}


	public int getClassType() {
		return classType;
	}


	public void setClassType(int classType) {
		this.classType = classType;
	}


	public int getLvl() {
		return lvl;
	}


	public void setLvl(int lvl) {
		this.lvl = lvl;
	}


	public int getMaxHp() {
		return maxHp;
	}


	public void setMaxHp(int maxHp) {
		this.maxHp = maxHp;
	}


	public int getMaxMana() {
		return maxMana;
	}


	public void setMaxMana(int maxMana) {
		this.maxMana = maxMana;
	}


	public int getMaxExp() {
		return maxExp;
	}


	public void setMaxExp(int maxExp) {
		this.maxExp = maxExp;
	}


	public int getAttack() {
		return attack;
	}


	public void setAttack(int attack) {
		this.attack = attack;
	}


	public int getDefense() {
		return defense;
	}


	public void setDefense(int defense) {
		this.defense = defense;
	}


	@Override
	public String toString() {
		return "ClassLevelInfo [id=" + id + ", classType=" + classType
				+ ", lvl=" + lvl + ", maxHp=" + maxHp + ", maxMana=" + maxMana
				+ ", maxExp=" + maxExp + ", attack=" + attack + ", defense="
				+ defense + "]";
	}

	
	@Override
	public Set<String> getTableUpdateStatements() {
		Set<String> indexes = new HashSet<String>();
		
		return indexes;
	}
	
}
