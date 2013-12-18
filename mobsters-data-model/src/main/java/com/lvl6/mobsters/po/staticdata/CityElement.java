package com.lvl6.mobsters.po.staticdata;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;
import com.lvl6.mobsters.utils.CoordinatePair;



@Entity
public class CityElement extends BasePersistentObject<Integer> implements Serializable{


	private static final long serialVersionUID = 4830238683524637339L;

	@Id
	protected Integer id = 0;
	
	@Column(name="city_id")
	@Index
	protected int cityId = 0;
	
	@Column(name="asset_id")
	protected int assetId = 0;
	
	@Column(name="city_elem_type")
	protected String cityElemType = "";
	
	//this and 'y' are used together to indicate the center
	@Column(name="coord_x")
	protected float coordX = 0F;
	
	@Column(name="coord_y")
	protected float coordY = 0F;
	
	@Column(name="x_length")
	protected float xLength = 0F;

	@Column(name="y_length")
	protected float yLength = 0F;
	
	@Column(name="img_good")
	protected String imgGood = "";

	@Column(name="struct_orientation")
	@Index
	protected String structOrientation = "";
	
	@Column(name="sprite_coords_x")
	@Index
	protected float spriteCoordsX = 0F;
	
	@Column(name="sprite_coords_y")
	@Index
	protected float spriteCoordsY = 0F;

	//convenience methods
	
	public CoordinatePair getCoords() {
		return new CoordinatePair(getCoordX(), getCoordY());
	}
	
	public CoordinatePair getSpriteCoords() {
		return new CoordinatePair(getSpriteCoordsX(), getSpriteCoordsY());
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public int getAssetId() {
		return assetId;
	}

	public void setAssetId(int assetId) {
		this.assetId = assetId;
	}

	public String getCityElemType() {
		return cityElemType;
	}

	public void setCityElemType(String cityElemType) {
		this.cityElemType = cityElemType;
	}

	public float getCoordX() {
		return coordX;
	}

	public void setCoordX(float coordX) {
		this.coordX = coordX;
	}

	public float getCoordY() {
		return coordY;
	}

	public void setCoordY(float coordY) {
		this.coordY = coordY;
	}

	public float getxLength() {
		return xLength;
	}

	public void setxLength(float xLength) {
		this.xLength = xLength;
	}

	public float getyLength() {
		return yLength;
	}

	public void setyLength(float yLength) {
		this.yLength = yLength;
	}

	public String getImgGood() {
		return imgGood;
	}

	public void setImgGood(String imgGood) {
		this.imgGood = imgGood;
	}

	public String getStructOrientation() {
		return structOrientation;
	}

	public void setStructOrientation(String structOrientation) {
		this.structOrientation = structOrientation;
	}

	public float getSpriteCoordsX() {
		return spriteCoordsX;
	}

	public void setSpriteCoordsX(float spriteCoordsX) {
		this.spriteCoordsX = spriteCoordsX;
	}

	public float getSpriteCoordsY() {
		return spriteCoordsY;
	}

	public void setSpriteCoordsY(float spriteCoordsY) {
		this.spriteCoordsY = spriteCoordsY;
	}

	@Override
	public String toString() {
		return "CityElement [id=" + id + ", cityId=" + cityId + ", assetId="
				+ assetId + ", cityElemType=" + cityElemType + ", coordX="
				+ coordX + ", coordY=" + coordY + ", xLength=" + xLength
				+ ", yLength=" + yLength + ", imgGood=" + imgGood
				+ ", structOrientation=" + structOrientation
				+ ", spriteCoordsX=" + spriteCoordsX + ", spriteCoordsY="
				+ spriteCoordsY + "]";
	}
	
}
