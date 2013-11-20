package com.lvl6.mobsters.po.staticdata;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class TaskStageMonster extends BasePersistentObject<UUID>{

	@Id
	protected UUID id = UUID.randomUUID();
	
	//room type = dungeon, specifies to which dungeon this room belongs
	@Column(name="type")
	@Index
	protected int type = 1;
	
	//if this room comes first, second, ... in this dungeon
	@Column(name="ordering")
	protected int ordering = 1;
	
	@Column(name="lvl_required")
	@Index
	protected int lvlRequired = 0;
	
	//flavor text for the user
	@Column(name="room_name")
	@Index
	protected String roomName = "Inferno";
	
	//for all stars
	@Column(name="time_millis_one")
	protected int timeMillisOne = 120000;
	
	//for two stars
	@Column(name="time_millis_two")
	protected int timeMillisTwo = 180000;
	
	//for one star
	@Column(name="time_millis_three")
	protected int timeMillisThree = 240000;

	// links a map-a-user-plays-in to a room
	// Using AoC terms,
	// world map (tapping on map button) = a dungeon
	// a city (kirin village, venetia,...) = a combat room
	// With this column, instead of each city/combat room correlating
	// to only one map, two or three cities/rooms can use the same map.
	@Column(name="map_index")
	@Index
	protected int mapIndex = 0;



	public UUID getId() {
		return id;
	}


	public void setId(UUID id) {
		this.id = id;
	}


	public int getType() {
		return type;
	}


	public void setType(int type) {
		this.type = type;
	}


	public int getOrdering() {
		return ordering;
	}


	public void setOrdering(int ordering) {
		this.ordering = ordering;
	}


	public int getLvlRequired() {
		return lvlRequired;
	}


	public void setLvlRequired(int lvlRequired) {
		this.lvlRequired = lvlRequired;
	}


	public String getRoomName() {
		return roomName;
	}


	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}


	public int getTimeMillisOne() {
		return timeMillisOne;
	}


	public void setTimeMillisOne(int timeMillisOne) {
		this.timeMillisOne = timeMillisOne;
	}


	public int getTimeMillisTwo() {
		return timeMillisTwo;
	}


	public void setTimeMillisTwo(int timeMillisTwo) {
		this.timeMillisTwo = timeMillisTwo;
	}


	public int getTimeMillisThree() {
		return timeMillisThree;
	}


	public void setTimeMillisThree(int timeMillisThree) {
		this.timeMillisThree = timeMillisThree;
	}


	public int getMapIndex() {
		return mapIndex;
	}


	public void setMapIndex(int mapIndex) {
		this.mapIndex = mapIndex;
	}


	@Override
	public String toString() {
		return "TaskStageMonster [id=" + id + ", type=" + type + ", ordering="
				+ ordering + ", lvlRequired=" + lvlRequired + ", roomName="
				+ roomName + ", timeMillisOne=" + timeMillisOne
				+ ", timeMillisTwo=" + timeMillisTwo + ", timeMillisThree="
				+ timeMillisThree + ", mapIndex=" + mapIndex + "]";
	}


	
}
