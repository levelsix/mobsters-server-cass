package com.lvl6.mobsters.po.staticdata;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class MonsterLevelInfo extends BasePersistentObject<Integer> implements Serializable{
	
	private static final long serialVersionUID = -8084916065884193782L;

	@Id
	protected Integer id = 0;
	
	@Column(name="monster_id")
	@Index
	protected int monsterId = 0;
	
	@Column(name="lvl")
	protected int lvl = 0;
	
	@Column(name="hp")
	protected int hp = 0;
	
	@Column(name="cur_lvl_required_exp")
	protected int curLvlRequiredExp = 0;
	
	@Column(name="feeder_exp")
	protected int feederExp = 0;
	
	@Column(name="fire_dmg")
	protected int fireDmg = 0;
	
	@Column(name="grass_dmg")
	protected int grassDmg = 0;

	@Column(name="water_dmg")
	protected int waterDmg = 0;
	
	@Column(name="lightning_dmg")
	protected int lightningDmg = 0;

	@Column(name="darkness_dmg")
	protected int darknessDmg = 0;
	
	@Column(name="rock_dmg")
	protected int rockDmg = 0;

	@Column(name="speed")
	protected int speed = 0;
	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(int monsterId) {
		this.monsterId = monsterId;
	}

	public int getLvl() {
		return lvl;
	}

	public void setLvl(int lvl) {
		this.lvl = lvl;
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public int getCurLvlRequiredExp() {
		return curLvlRequiredExp;
	}

	public void setCurLvlRequiredExp(int curLvlRequiredExp) {
		this.curLvlRequiredExp = curLvlRequiredExp;
	}

	public int getFeederExp() {
		return feederExp;
	}

	public void setFeederExp(int feederExp) {
		this.feederExp = feederExp;
	}

	public int getFireDmg() {
		return fireDmg;
	}

	public void setFireDmg(int fireDmg) {
		this.fireDmg = fireDmg;
	}

	public int getGrassDmg() {
		return grassDmg;
	}

	public void setGrassDmg(int grassDmg) {
		this.grassDmg = grassDmg;
	}

	public int getWaterDmg() {
		return waterDmg;
	}

	public void setWaterDmg(int waterDmg) {
		this.waterDmg = waterDmg;
	}

	public int getLightningDmg() {
		return lightningDmg;
	}

	public void setLightningDmg(int lightningDmg) {
		this.lightningDmg = lightningDmg;
	}

	public int getDarknessDmg() {
		return darknessDmg;
	}

	public void setDarknessDmg(int darknessDmg) {
		this.darknessDmg = darknessDmg;
	}

	public int getRockDmg() {
		return rockDmg;
	}

	public void setRockDmg(int rockDmg) {
		this.rockDmg = rockDmg;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	@Override
	public String toString() {
		return "MonsterLevelInfo [id=" + id + ", monsterId=" + monsterId
				+ ", lvl=" + lvl + ", hp=" + hp + ", curLvlRequiredExp="
				+ curLvlRequiredExp + ", feederExp=" + feederExp + ", fireDmg="
				+ fireDmg + ", grassDmg=" + grassDmg + ", waterDmg=" + waterDmg
				+ ", lightningDmg=" + lightningDmg + ", darknessDmg="
				+ darknessDmg + ", rockDmg=" + rockDmg + ", speed=" + speed
				+ "]";
	}

}
