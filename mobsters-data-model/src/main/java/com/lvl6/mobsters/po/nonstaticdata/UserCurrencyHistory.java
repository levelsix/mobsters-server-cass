package com.lvl6.mobsters.po.nonstaticdata;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class UserCurrencyHistory extends BasePersistentObject<UUID>{

	@Id
	protected UUID id = UUID.randomUUID();
	
	@Column(name="user_id")
	@Index
	protected UUID userId = null;
	
	@Column(name="date")
	@Index
	protected Date date = null;
	
	@Column(name="is_cash")
	@Index
	protected boolean isCash = false;
	
	@Column(name="currency_change")
	@Index
	protected int currencyChange = 0;
	
	@Column(name="currency_before_change")
	protected int currencyBeforeChange = 0;
	
	@Column(name="currency_after_change")
	protected int currencyAfterChange = 0;
	
	@Column(name="reason_for_change")
	protected String reasonForChange = "";
	
	@Column(name="details")
	protected String details = "";

	
	
	
	
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public boolean isCash() {
		return isCash;
	}

	public void setCash(boolean isCash) {
		this.isCash = isCash;
	}

	public int getCurrencyChange() {
		return currencyChange;
	}

	public void setCurrencyChange(int currencyChange) {
		this.currencyChange = currencyChange;
	}

	public int getCurrencyBeforeChange() {
		return currencyBeforeChange;
	}

	public void setCurrencyBeforeChange(int currencyBeforeChange) {
		this.currencyBeforeChange = currencyBeforeChange;
	}

	public int getCurrencyAfterChange() {
		return currencyAfterChange;
	}

	public void setCurrencyAfterChange(int currencyAfterChange) {
		this.currencyAfterChange = currencyAfterChange;
	}

	public String getReasonForChange() {
		return reasonForChange;
	}

	public void setReasonForChange(String reasonForChange) {
		this.reasonForChange = reasonForChange;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	@Override
	public String toString() {
		return "UserCurrencyHistory [id=" + id + ", userId=" + userId
				+ ", date=" + date + ", isCash=" + isCash + ", currencyChange="
				+ currencyChange + ", currencyBeforeChange="
				+ currencyBeforeChange + ", currencyAfterChange="
				+ currencyAfterChange + ", reasonForChange=" + reasonForChange
				+ ", details=" + details + "]";
	}
	
}
