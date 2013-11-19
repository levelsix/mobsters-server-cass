package com.lvl6.mobsters.po;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class Spell extends BasePersistentObject<UUID>{

	@Id
	protected UUID id = UUID.randomUUID();
	
	@Column(name="name")
	@Index
	protected String name = "";
	
	@Column(name="class_type")
	@Index
	protected int classType = 0;
	
	@Column(name="function_type")
	@Index
	protected int functionType = 0;
	
	@Column(name="function_constant")
	protected int functionConstant = 0;
	
	@Column(name="mana_cost")
	@Index
	protected int manaCost = 0;
	
	@Column(name="animation_effect")
	protected int animationEffect = 0;
	
	//animation duration?
	@Column(name="duration_millis")
	protected int durationMillis = 0;

	//we'll just leave this for now...
	@Column(name="elemental_type")
	@Index
	protected int elementalType = 0;
	
	@Column(name="speed")
	protected int speed = 0;
	
	@Column(name="targetted")
	protected boolean targetted = true;
	
	@Column(name="size")
	protected int size = 0;

	@Column(name="cast_time_millis")
	protected int castTimeMillis = 0;
	
	@Column(name="cooldown_millis")
	protected int cooldownMillis = 0;
	
	@Column(name="range")
	protected int range = 0;
	
	@Column(name="user_level_required")
	@Index
	protected int userLevelRequired = 0;
	
	@Column(name="lvl")
	protected int lvl = 0;
	
	@Column(name="research_cost")
	@Index
	protected int researchCost = 0;
	
	@Column(name="research_cost_resource")
	@Index
	protected int researchCostResource = 0;
	
	@Column(name="research_time_millis")
	@Index
	protected int researchTimeMillis = 0;
	
	@Column(name="research_speedup_base_cost")
	protected int researchSpeedupBaseCost = 0;

	


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


	public int getClassType() {
		return classType;
	}


	public void setClassType(int classType) {
		this.classType = classType;
	}


	public int getFunctionType() {
		return functionType;
	}


	public void setFunctionType(int functionType) {
		this.functionType = functionType;
	}


	public int getFunctionConstant() {
		return functionConstant;
	}


	public void setFunctionConstant(int functionConstant) {
		this.functionConstant = functionConstant;
	}


	public int getManaCost() {
		return manaCost;
	}


	public void setManaCost(int manaCost) {
		this.manaCost = manaCost;
	}


	public int getAnimationEffect() {
		return animationEffect;
	}


	public void setAnimationEffect(int animationEffect) {
		this.animationEffect = animationEffect;
	}


	public int getDurationMillis() {
		return durationMillis;
	}


	public void setDurationMillis(int durationMillis) {
		this.durationMillis = durationMillis;
	}


	public int getElementalType() {
		return elementalType;
	}


	public void setElementalType(int elementalType) {
		this.elementalType = elementalType;
	}


	public int getSpeed() {
		return speed;
	}


	public void setSpeed(int speed) {
		this.speed = speed;
	}


	public boolean isTargetted() {
		return targetted;
	}


	public void setTargetted(boolean targetted) {
		this.targetted = targetted;
	}


	public int getSize() {
		return size;
	}


	public void setSize(int size) {
		this.size = size;
	}


	public int getCastTimeMillis() {
		return castTimeMillis;
	}


	public void setCastTimeMillis(int castTimeMillis) {
		this.castTimeMillis = castTimeMillis;
	}


	public int getCooldownMillis() {
		return cooldownMillis;
	}


	public void setCooldownMillis(int cooldownMillis) {
		this.cooldownMillis = cooldownMillis;
	}


	public int getRange() {
		return range;
	}


	public void setRange(int range) {
		this.range = range;
	}


	public int getUserLevelRequired() {
		return userLevelRequired;
	}


	public void setUserLevelRequired(int userLevelRequired) {
		this.userLevelRequired = userLevelRequired;
	}


	public int getLvl() {
		return lvl;
	}


	public void setLevel(int lvl) {
		this.lvl = lvl;
	}


	public int getResearchCost() {
		return researchCost;
	}


	public void setResearchCost(int researchCost) {
		this.researchCost = researchCost;
	}


	public int getResearchCostResource() {
		return researchCostResource;
	}


	public void setResearchCostResource(int researchCostResource) {
		this.researchCostResource = researchCostResource;
	}


	public int getResearchTimeMillis() {
		return researchTimeMillis;
	}


	public void setResearchTimeMillis(int researchTimeMillis) {
		this.researchTimeMillis = researchTimeMillis;
	}


	public int getResearchSpeedupBaseCost() {
		return researchSpeedupBaseCost;
	}


	public void setResearchSpeedupBaseCost(int researchSpeedupBaseCost) {
		this.researchSpeedupBaseCost = researchSpeedupBaseCost;
	}



	@Override
	public String toString() {
		return "Spell [id=" + id + ", name=" + name + ", classType="
				+ classType + ", functionType=" + functionType
				+ ", functionConstant=" + functionConstant + ", manaCost="
				+ manaCost + ", animationEffect=" + animationEffect
				+ ", durationMillis=" + durationMillis + ", elementalType="
				+ elementalType + ", speed=" + speed + ", targetted="
				+ targetted + ", size=" + size + ", castTimeMillis="
				+ castTimeMillis + ", cooldownMillis=" + cooldownMillis
				+ ", range=" + range + ", userLevelRequired="
				+ userLevelRequired + ", lvl=" + lvl + ", researchCost="
				+ researchCost + ", researchCostResource="
				+ researchCostResource + ", researchTimeMillis="
				+ researchTimeMillis + ", researchSpeedupBaseCost="
				+ researchSpeedupBaseCost + "]";
	}

	
	
	
}
