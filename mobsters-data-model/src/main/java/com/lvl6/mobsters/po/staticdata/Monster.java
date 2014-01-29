package com.lvl6.mobsters.po.staticdata;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class Monster extends BasePersistentObject<Integer> implements Serializable {

	private static final long serialVersionUID = -4535334715807617293L;

	@Id
	protected Integer id = 0;
	
	@Column(name="name")
	@Index
	protected String name = null;
	
	@Column(name="monster_group")
	protected String monsterGroup = null;
	
	@Column(name="monster_quality")
	@Index
	protected String monsterQuality = null;
	
	@Column(name="evolution_lvl")
	@Index
	protected int evolutionLvl = 0; //does this start off at 1?
	
	@Column(name="display_name")
	protected String displayName = null;
	
	@Column(name="monster_element")
	protected String monsterElement = null;
	
	@Column(name="image_prefix")
	protected String imagePrefix = null;
	
	@Column(name="num_puzzle_pieces")
	protected int numPuzzlePieces = 0;
	
	@Column(name="minutes_to_combine_pieces")
	protected int minutesToCombinePieces = 0;
	
	//aka max enhancing level
	@Column(name="max_lvl")
	protected int maxLvl = 0;
	
	//monster id this monster becomes after evolving
	@Column(name="evolution_monster_id")
	protected int evolutionMonsterId = 0;
	
	@Column(name="evolution_catalyst_monster_id")
	protected int evolutionCatalystMonsterId = 0;
	
	@Column(name="minutes_to_evolve")
	protected int minutesToEvolve = 0;
	
	//most likely will not be changed to something other than 1;
	@Column(name="num_catalysts_required")
	protected int numCatalystsRequired = 1;
	
	//monster id this monster was before evolving
	@Column(name="devolution_monster_id")
	protected int devolutionMonsterId = 0;
	
	@Column(name="evolution_cost")
	protected int evolutionCost = 0;
	
	@Column(name="carrot_recruited")
	protected String carrotRecruited = null;
	
	@Column(name="carrot_defeated")
	protected String carrotDefeated = null;
	
	@Column(name="carrot_evolved")
	protected String carrotEvolved = null;
	
	@Column(name="description")
	protected String description = null;
	
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMonsterGroup() {
		return monsterGroup;
	}

	public void setMonsterGroup(String monsterGroup) {
		this.monsterGroup = monsterGroup;
	}

	public String getMonsterQuality() {
		return monsterQuality;
	}

	public void setMonsterQuality(String monsterQuality) {
		this.monsterQuality = monsterQuality;
	}

	public int getEvolutionLvl() {
		return evolutionLvl;
	}

	public void setEvolutionLvl(int evolutionLvl) {
		this.evolutionLvl = evolutionLvl;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getMonsterElement() {
		return monsterElement;
	}

	public void setMonsterElement(String monsterElement) {
		this.monsterElement = monsterElement;
	}

	public String getImagePrefix() {
		return imagePrefix;
	}

	public void setImagePrefix(String imagePrefix) {
		this.imagePrefix = imagePrefix;
	}

	public int getNumPuzzlePieces() {
		return numPuzzlePieces;
	}

	public void setNumPuzzlePieces(int numPuzzlePieces) {
		this.numPuzzlePieces = numPuzzlePieces;
	}

	public int getMinutesToCombinePieces() {
		return minutesToCombinePieces;
	}

	public void setMinutesToCombinePieces(int minutesToCombinePieces) {
		this.minutesToCombinePieces = minutesToCombinePieces;
	}

	public int getMaxLvl() {
		return maxLvl;
	}

	public void setMaxLvl(int maxLvl) {
		this.maxLvl = maxLvl;
	}

	public int getEvolutionMonsterId() {
		return evolutionMonsterId;
	}

	public void setEvolutionMonsterId(int evolutionMonsterId) {
		this.evolutionMonsterId = evolutionMonsterId;
	}

	public int getEvolutionCatalystMonsterId() {
		return evolutionCatalystMonsterId;
	}

	public void setEvolutionCatalystMonsterId(int evolutionCatalystMonsterId) {
		this.evolutionCatalystMonsterId = evolutionCatalystMonsterId;
	}

	public int getMinutesToEvolve() {
		return minutesToEvolve;
	}

	public void setMinutesToEvolve(int minutesToEvolve) {
		this.minutesToEvolve = minutesToEvolve;
	}

	public int getNumCatalystsRequired() {
		return numCatalystsRequired;
	}

	public void setNumCatalystsRequired(int numCatalystsRequired) {
		this.numCatalystsRequired = numCatalystsRequired;
	}

	public int getDevolutionMonsterId() {
		return devolutionMonsterId;
	}

	public void setDevolutionMonsterId(int devolutionMonsterId) {
		this.devolutionMonsterId = devolutionMonsterId;
	}

	public int getEvolutionCost() {
		return evolutionCost;
	}

	public void setEvolutionCost(int evolutionCost) {
		this.evolutionCost = evolutionCost;
	}

	public String getCarrotRecruited() {
		return carrotRecruited;
	}

	public void setCarrotRecruited(String carrotRecruited) {
		this.carrotRecruited = carrotRecruited;
	}

	public String getCarrotDefeated() {
		return carrotDefeated;
	}

	public void setCarrotDefeated(String carrotDefeated) {
		this.carrotDefeated = carrotDefeated;
	}

	public String getCarrotEvolved() {
		return carrotEvolved;
	}

	public void setCarrotEvolved(String carrotEvolved) {
		this.carrotEvolved = carrotEvolved;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Monster [id=" + id + ", name=" + name + ", monsterGroup="
				+ monsterGroup + ", monsterQuality=" + monsterQuality
				+ ", evolutionLvl=" + evolutionLvl + ", displayName="
				+ displayName + ", monsterElement=" + monsterElement
				+ ", imagePrefix=" + imagePrefix + ", numPuzzlePieces="
				+ numPuzzlePieces + ", minutesToCombinePieces="
				+ minutesToCombinePieces + ", maxLvl=" + maxLvl
				+ ", evolutionMonsterId=" + evolutionMonsterId
				+ ", evolutionCatalystMonsterId=" + evolutionCatalystMonsterId
				+ ", minutesToEvolve=" + minutesToEvolve
				+ ", numCatalystsRequired=" + numCatalystsRequired
				+ ", devolutionMonsterId=" + devolutionMonsterId
				+ ", evolutionCost=" + evolutionCost + ", carrotRecruited="
				+ carrotRecruited + ", carrotDefeated=" + carrotDefeated
				+ ", carrotEvolved=" + carrotEvolved + ", description="
				+ description + "]";
	}
	
}
