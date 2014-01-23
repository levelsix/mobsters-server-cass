package com.lvl6.mobsters.po.staticdata;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class EventPersistent extends BasePersistentObject<Integer> implements Serializable{

	private static final long serialVersionUID = 7106269179205034827L;

	@Id
	protected Integer id = 0;
	
	@Column(name="day_of_week")
	@Index
	protected String dayOfWeek = "";
	
	@Column(name="start_hour")
	@Index
	protected int startHour = 0;
	
	@Column(name="event_duration_minutes")
	protected int eventDurationMinutes = 0;
	
	@Column(name="task_id")
	protected int taskId = 0;
	
	@Column(name="cool_down_minutes")
	protected int coolDownMinutes = 0;
	
	@Column(name="event_type")
	protected String eventType = "";
	
	@Column(name="monster_element")
	protected String monsterElement = "";
	
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public int getStartHour() {
		return startHour;
	}

	public void setStartHour(int startHour) {
		this.startHour = startHour;
	}

	public int getEventDurationMinutes() {
		return eventDurationMinutes;
	}

	public void setEventDurationMinutes(int eventDurationMinutes) {
		this.eventDurationMinutes = eventDurationMinutes;
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public int getCoolDownMinutes() {
		return coolDownMinutes;
	}

	public void setCoolDownMinutes(int coolDownMinutes) {
		this.coolDownMinutes = coolDownMinutes;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getMonsterElement() {
		return monsterElement;
	}

	public void setMonsterElement(String monsterElement) {
		this.monsterElement = monsterElement;
	}

	@Override
	public String toString() {
		return "EventPersistent [id=" + id + ", dayOfWeek=" + dayOfWeek
				+ ", startHour=" + startHour + ", eventDurationMinutes="
				+ eventDurationMinutes + ", taskId=" + taskId
				+ ", coolDownMinutes=" + coolDownMinutes + ", eventType="
				+ eventType + ", monsterElement=" + monsterElement + "]";
	}
	
}
