package com.lvl6.aoc2.po;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.lvl6.aoc2.entitymanager.Index;



@Entity
public class Equipment extends BasePersistentObject{

	@Id
	protected UUID id = UUID.randomUUID();
	
	@Column(name="name")
	@Index
	protected String name = "";
	
	@Column(name="level")
	protected int level = 0;
	
	@Column(name="type")
	@Index
	protected int type = 0;
	
	@Column(name="durability")
	protected double durability = 0.0;

	@Column(name="attack")
	protected int attack = 0;

	@Column(name="defense")
	protected int defense = 0;

	@Column(name="additional_hp")
	protected int additionalHp = 0;

	@Column(name="additional_mana")
	protected int additionalMana = 0;
	
	@Column(name="rarity")
	@Index
	protected int rarity = 0;

	@Column(name="class_required")
	@Index
	protected int classRequired = 0;
	
	//hero lvl required
	@Column(name="lvl_required")
	@Index
	protected int lvlRequired = 0;
	
	//fixing from 0(durability?) to 100%
	@Column(name="durability_fix_price")
	protected int durabilityFixPrice = 0;
	
	@Column(name="durability_fix_time_constant")
	protected int durabilityFixTimeConstant = 0;
	
	
	public UUID getId() {
		return id;
	}


	public void setId(UUID id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public int getLevel() {
		return level;
	}


	public void setLevel(int level) {
		this.level = level;
	}


	public int getType() {
		return type;
	}


	public void setType(int type) {
		this.type = type;
	}


	public double getDurability() {
		return durability;
	}


	public void setDurability(double durability) {
		this.durability = durability;
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


	public int getAdditionalHp() {
		return additionalHp;
	}


	public void setAdditionalHp(int additionalHp) {
		this.additionalHp = additionalHp;
	}


	public int getAdditionalMana() {
		return additionalMana;
	}


	public void setAdditionalMana(int additionalMana) {
		this.additionalMana = additionalMana;
	}


	public int getRarity() {
		return rarity;
	}


	public void setRarity(int rarity) {
		this.rarity = rarity;
	}


	public int getClassRequired() {
		return classRequired;
	}


	public void setClassRequired(int classRequired) {
		this.classRequired = classRequired;
	}


	public int getLvlRequired() {
		return lvlRequired;
	}


	public void setLvlRequired(int lvlRequired) {
		this.lvlRequired = lvlRequired;
	}


	public int getDurabilityFixPrice() {
		return durabilityFixPrice;
	}


	public void setDurabilityFixPrice(int durabilityFixPrice) {
		this.durabilityFixPrice = durabilityFixPrice;
	}
	

	public int getDurabilityFixTimeConstant() {
		return durabilityFixTimeConstant;
	}


	public void setDurabilityFixTimeConstant(int durabilityFixTimeConstant) {
		this.durabilityFixTimeConstant = durabilityFixTimeConstant;
	}


	@Override
	public String toString() {
		return "Equipment [id=" + id + ", name=" + name + ", level=" + level
				+ ", type=" + type + ", durability=" + durability + ", attack="
				+ attack + ", defense=" + defense + ", additionalHp="
				+ additionalHp + ", additionalMana=" + additionalMana
				+ ", rarity=" + rarity + ", classRequired=" + classRequired
				+ ", lvlRequired=" + lvlRequired + ", durabilityFixPrice="
				+ durabilityFixPrice + ", durabilityFixTimeConstant="
				+ durabilityFixTimeConstant + "]";
	}


	@Override
	public Set<String> getTableUpdateStatements() {
		Set<String> indexes = new HashSet<String>();
		
		return indexes;
	}
	
}
