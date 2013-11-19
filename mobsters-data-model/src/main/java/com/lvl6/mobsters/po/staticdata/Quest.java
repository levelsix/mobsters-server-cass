package com.lvl6.mobsters.po.staticdata;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;
import com.lvl6.mobsters.utils.Dialogue;



@Entity
public class Quest extends BasePersistentObject<Integer> implements Serializable{

	private static final long serialVersionUID = 3876445184135296684L;

	@Id
	protected Integer id = 0;
	
	@Column(name="city_id")
	@Index
	protected int cityId = 0;
	
	@Column(name="name")
	@Index
	protected String name = null;
	
	@Column(name="description")
	protected String description = null;
	
	@Column(name="done_response")
	protected String doneResponse = null;
	
	@Column(name="accept_dialogue")
	protected String acceptDialogue = null;
	
	//for valid values look in FullQuestProto.QuestType in file QuestStuff.proto
	@Column(name="quest_type")
	@Index
	protected String questType = null;
	
	@Column(name="job_description")
	protected String jobDescription = null;

	//the primary key value in, say, monster or task 
	@Column(name="static_data_id")
	@Index
	protected int staticDataId = 0;

	@Column(name="static_data_quantity")
	protected int staticDataQuantity = 0;
	
	@Column(name="cash_reward")
	protected int cashReward = 0;
	
	@Column(name="gem_reward")
	protected int gemReward = 0;
	
	@Column(name="exp_reward")
	protected int expReward = 0;
	
	@Column(name="monster_id_reward")
	protected int monsterIdReward = 0;

	@Column(name="is_complete_monster")
	protected boolean isCompleteMonster = false;

	//this will later be converted into a List
	@Column(name="quests_required_for_this")
	protected String questsRequiredForThis = null;

	@Column(name="quest_giver_img_suffix")
	protected String questGiverImgSuffix = null;

	@Column(name="priority")
	protected int priority = 0;

	@Column(name="carrot_id")
	protected String carrotId = null;
	
	
	//will be ignored by cassandra except when I choose to modify this
	protected Dialogue dialogue = null;
	protected Set<Integer> questsRequiredForThisAsSet = null;
	


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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDoneResponse() {
		return doneResponse;
	}

	public void setDoneResponse(String doneResponse) {
		this.doneResponse = doneResponse;
	}

	public String getAcceptDialogue() {
		return acceptDialogue;
	}

	public void setAcceptDialogue(String acceptDialogue) {
		this.acceptDialogue = acceptDialogue;
	}

	public String getQuestType() {
		return questType;
	}

	public void setQuestType(String questType) {
		this.questType = questType;
	}

	public String getJobDescription() {
		return jobDescription;
	}

	public void setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}

	public int getStaticDataId() {
		return staticDataId;
	}

	public void setStaticDataId(int staticDataId) {
		this.staticDataId = staticDataId;
	}

	public int getStaticDataQuantity() {
		return staticDataQuantity;
	}

	public void setStaticDataQuantity(int staticDataQuantity) {
		this.staticDataQuantity = staticDataQuantity;
	}

	public int getCashReward() {
		return cashReward;
	}

	public void setCashReward(int cashReward) {
		this.cashReward = cashReward;
	}

	public int getGemReward() {
		return gemReward;
	}

	public void setGemReward(int gemReward) {
		this.gemReward = gemReward;
	}

	public int getExpReward() {
		return expReward;
	}

	public void setExpReward(int expReward) {
		this.expReward = expReward;
	}

	public int getMonsterIdReward() {
		return monsterIdReward;
	}

	public void setMonsterIdReward(int monsterIdReward) {
		this.monsterIdReward = monsterIdReward;
	}

	public boolean isCompleteMonster() {
		return isCompleteMonster;
	}

	public void setCompleteMonster(boolean isCompleteMonster) {
		this.isCompleteMonster = isCompleteMonster;
	}

	public String getQuestsRequiredForThis() {
		return questsRequiredForThis;
	}

	public void setQuestsRequiredForThis(String questsRequiredForThis) {
		this.questsRequiredForThis = questsRequiredForThis;
	}

	public String getQuestGiverImgSuffix() {
		return questGiverImgSuffix;
	}

	public void setQuestGiverImgSuffix(String questGiverImgSuffix) {
		this.questGiverImgSuffix = questGiverImgSuffix;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getCarrotId() {
		return carrotId;
	}

	public void setCarrotId(String carrotId) {
		this.carrotId = carrotId;
	}

	

	
	//IGNORED BY CASSANDRA
	public Dialogue getDialogue() {
		return dialogue;
	}

	public void setDialogue(Dialogue dialogue) {
		this.dialogue = dialogue;
	}

	public Set<Integer> getQuestsRequiredForThisAsSet() {
		return questsRequiredForThisAsSet;
	}

	public void setQuestsRequiredForThisAsSet(
			Set<Integer> questsRequiredForThisAsSet) {
		this.questsRequiredForThisAsSet = questsRequiredForThisAsSet;
	}
	
	

	

	@Override
	public String toString() {
		return "Quest [id=" + id + ", cityId=" + cityId + ", name=" + name
				+ ", description=" + description + ", doneResponse="
				+ doneResponse + ", acceptDialogue=" + acceptDialogue
				+ ", questType=" + questType + ", jobDescription="
				+ jobDescription + ", staticDataId=" + staticDataId
				+ ", staticDataQuantity=" + staticDataQuantity + ", cashReward=" + cashReward
				+ ", gemReward=" + gemReward + ", expReward=" + expReward
				+ ", monsterIdReward=" + monsterIdReward
				+ ", isCompleteMonster=" + isCompleteMonster
				+ ", questsRequiredForThis=" + questsRequiredForThis
				+ ", questGiverImgSuffix=" + questGiverImgSuffix
				+ ", priority=" + priority + ", carrotId=" + carrotId + "]";
	}

	
}
