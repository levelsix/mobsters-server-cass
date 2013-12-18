package com.lvl6.mobsters.po.staticdata;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;
import com.lvl6.mobsters.utils.CoordinatePair;



@Entity
public class City extends BasePersistentObject<Integer> implements Serializable{


	private static final long serialVersionUID = -3760579411770974016L;

	@Id
	protected Integer id = 0;
	
	@Column(name="name")
	@Index
	protected String name = "";
	
	@Column(name="map_img_name")
	@Index
	protected String mapImgName = "";
	
	//this and 'y' are used together to indicate the center
	@Column(name="center_coord_x")
	protected float centerCoordX = 0;
	
	@Column(name="center_coord_y")
	protected float centerCoordY = 0;

	@Column(name="road_img_name")
	protected String roadImgName = "";

	@Column(name="map_tmx_name")
	@Index
	protected String mapTmxName = "";
	
	//this and 'y' used to indicate the coordinates for the road image
	@Column(name="road_img_coord_x")
	protected float roadImgCoordX = 0;
	
	@Column(name="road_img_coord_y")
	protected float roadImgCoordY = 0;
	
	@Column(name="attack_map_label_img_name")
	@Index
	protected String attackMapLabelImgName = "";

	
	
	//convenience methods
	public CoordinatePair getCenterCoords() {
		return new CoordinatePair(getCenterCoordX(), getCenterCoordY());
	}
	public CoordinatePair getRoadImgCoords() {
		return new CoordinatePair(getRoadImgCoordX(), getRoadImgCoordY());
	}
	
	
	
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

	public String getMapImgName() {
		return mapImgName;
	}

	public void setMapImgName(String mapImgName) {
		this.mapImgName = mapImgName;
	}

	public float getCenterCoordX() {
		return centerCoordX;
	}

	public void setCenterCoordX(float centerCoordX) {
		this.centerCoordX = centerCoordX;
	}

	public float getCenterCoordY() {
		return centerCoordY;
	}

	public void setCenterCoordY(float centerCoordY) {
		this.centerCoordY = centerCoordY;
	}

	public String getRoadImgName() {
		return roadImgName;
	}

	public void setRoadImgName(String roadImgName) {
		this.roadImgName = roadImgName;
	}

	public String getMapTmxName() {
		return mapTmxName;
	}

	public void setMapTmxName(String mapTmxName) {
		this.mapTmxName = mapTmxName;
	}

	public float getRoadImgCoordX() {
		return roadImgCoordX;
	}

	public void setRoadImgCoordX(float roadImgCoordX) {
		this.roadImgCoordX = roadImgCoordX;
	}

	public float getRoadImgCoordY() {
		return roadImgCoordY;
	}

	public void setRoadImgCoordY(float roadImgCoordY) {
		this.roadImgCoordY = roadImgCoordY;
	}

	public String getAttackMapLabelImgName() {
		return attackMapLabelImgName;
	}

	public void setAttackMapLabelImgName(String attackMapLabelImgName) {
		this.attackMapLabelImgName = attackMapLabelImgName;
	}

	@Override
	public String toString() {
		return "City [id=" + id + ", name=" + name + ", mapImgName="
				+ mapImgName + ", centerCoordX=" + centerCoordX
				+ ", centerCoordY=" + centerCoordY + ", roadImgName="
				+ roadImgName + ", mapTmxName=" + mapTmxName
				+ ", roadImgCoordX=" + roadImgCoordX + ", roadImgCoordY="
				+ roadImgCoordY + ", attackMapLabelImgName="
				+ attackMapLabelImgName + "]";
	}
	
}
