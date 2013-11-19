package com.lvl6.mobsters.po.staticdata;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class Monster extends BasePersistentObject<Integer> implements Serializable {

	private static final long serialVersionUID = -8302301850028521702L;

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
	
	@Column(name="bash_hp")
	protected int baseHp = 0;
	
	@Column(name="image_prefix")
	protected String imagePrefix = null;
	
	@Column(name="num_puzzle_pieces")
	protected int numPuzzlePieces = 0;
	
	@Column(name="minutes_to_combine_pieces")
	protected int minutesToCombinePieces = 0;
	
	@Column(name="element_one_dmg")
	protected int elementOneDmg = 0;
	
	@Column(name="element_two_dmg")
	protected int elementTwoDmg = 0;
	
	@Column(name="element_three_dmg")
	protected int elementThreeDmg = 0;
	
	@Column(name="element_four_dmg")
	protected int elementFourDmg = 0;
	
	@Column(name="element_five_dmg")
	protected int elementFiveDmg = 0;
	
	@Column(name="hp_lvl_multiplier")
	protected float hpLvlMultiplier = 0;
	
	@Column(name="attack_lvl_multiplier")
	protected float attackLvlMultiplier = 0;
	
	@Column(name="max_lvl")
	protected int maxLvl = 0;
	
	//monster id this monster becomes after evolving
	@Column(name="evolution_monster_id")
	protected int evolutionMonsterId = 0;
	
	//monster id this monster was before evolving
	@Column(name="devolution_monster_id")
	protected int devolutionMonsterId = 0;
	
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

	public int getBaseHp() {
		return baseHp;
	}

	public void setBaseHp(int baseHp) {
		this.baseHp = baseHp;
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

	public int getElementOneDmg() {
		return elementOneDmg;
	}

	public void setElementOneDmg(int elementOneDmg) {
		this.elementOneDmg = elementOneDmg;
	}

	public int getElementTwoDmg() {
		return elementTwoDmg;
	}

	public void setElementTwoDmg(int elementTwoDmg) {
		this.elementTwoDmg = elementTwoDmg;
	}

	public int getElementThreeDmg() {
		return elementThreeDmg;
	}

	public void setElementThreeDmg(int elementThreeDmg) {
		this.elementThreeDmg = elementThreeDmg;
	}

	public int getElementFourDmg() {
		return elementFourDmg;
	}

	public void setElementFourDmg(int elementFourDmg) {
		this.elementFourDmg = elementFourDmg;
	}

	public int getElementFiveDmg() {
		return elementFiveDmg;
	}

	public void setElementFiveDmg(int elementFiveDmg) {
		this.elementFiveDmg = elementFiveDmg;
	}

	public float getHpLvlMultiplier() {
		return hpLvlMultiplier;
	}

	public void setHpLvlMultiplier(float hpLvlMultiplier) {
		this.hpLvlMultiplier = hpLvlMultiplier;
	}

	public float getAttackLvlMultiplier() {
		return attackLvlMultiplier;
	}

	public void setAttackLvlMultiplier(float attackLvlMultiplier) {
		this.attackLvlMultiplier = attackLvlMultiplier;
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

	public int getDevolutionMonsterId() {
		return devolutionMonsterId;
	}

	public void setDevolutionMonsterId(int devolutionMonsterId) {
		this.devolutionMonsterId = devolutionMonsterId;
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
				+ ", baseHp=" + baseHp + ", imagePrefix=" + imagePrefix
				+ ", numPuzzlePieces=" + numPuzzlePieces
				+ ", minutesToCombinePieces=" + minutesToCombinePieces
				+ ", elementOneDmg=" + elementOneDmg + ", elementTwoDmg="
				+ elementTwoDmg + ", elementThreeDmg=" + elementThreeDmg
				+ ", elementFourDmg=" + elementFourDmg + ", elementFiveDmg="
				+ elementFiveDmg + ", hpLvlMultiplier=" + hpLvlMultiplier
				+ ", attackLvlMultiplier=" + attackLvlMultiplier + ", maxLvl="
				+ maxLvl + ", evolutionMonsterId=" + evolutionMonsterId
				+ ", devolutionMonsterId=" + devolutionMonsterId
				+ ", carrotRecruited=" + carrotRecruited + ", carrotDefeated="
				+ carrotDefeated + ", carrotEvolved=" + carrotEvolved
				+ ", description=" + description + "]";
	}
}
