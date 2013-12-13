package com.lvl6.mobsters.po.staticdata;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class BoosterPack extends BasePersistentObject<Integer> implements Serializable{

	private static final long serialVersionUID = -1463361841004526021L;

	@Id
	protected Integer id = 0;
	
	@Column(name="name")
	@Index
	protected String name = "";
	
	@Column(name="gem_price")
	@Index
	protected int gemPrice = 0;
	
	//list background image name
	@Column(name="list_bg_img_name")
	@Index
	protected String listBgImgName = "";

	@Column(name="list_description")
	protected String listDescription = "";

	@Column(name="nav_bar_img_name")
	@Index
	protected String navBarImgName = "";
	
	@Column(name="nav_title_img_name")
	@Index
	protected String navTitleImgName = "";
	
	@Column(name="machine_img_name")
	@Index
	protected String machineImgName = "";

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

	public int getGemPrice() {
		return gemPrice;
	}

	public void setGemPrice(int gemPrice) {
		this.gemPrice = gemPrice;
	}

	public String getListBgImgName() {
		return listBgImgName;
	}

	public void setListBgImgName(String listBgImgName) {
		this.listBgImgName = listBgImgName;
	}

	public String getListDescription() {
		return listDescription;
	}

	public void setListDescription(String listDescription) {
		this.listDescription = listDescription;
	}

	public String getNavBarImgName() {
		return navBarImgName;
	}

	public void setNavBarImgName(String navBarImgName) {
		this.navBarImgName = navBarImgName;
	}

	public String getNavTitleImgName() {
		return navTitleImgName;
	}

	public void setNavTitleImgName(String navTitleImgName) {
		this.navTitleImgName = navTitleImgName;
	}

	public String getMachineImgName() {
		return machineImgName;
	}

	public void setMachineImgName(String machineImgName) {
		this.machineImgName = machineImgName;
	}

	@Override
	public String toString() {
		return "BoosterPack [id=" + id + ", name=" + name + ", gemPrice="
				+ gemPrice + ", listBgImgName=" + listBgImgName
				+ ", listDescription=" + listDescription + ", navBarImgName="
				+ navBarImgName + ", navTitleImgName=" + navTitleImgName
				+ ", machineImgName=" + machineImgName + "]";
	}

	
}
