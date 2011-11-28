package com.ironiacorp.scienceanalyzer;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;

@Entity
public class Job
{
	@Basic
	private String name;
	
	@Basic
	private Institution workplace;
	
	@Basic
	private PhoneNumber phone;
	
	@Basic
	private Date startDate;
	
	@Basic
	private Date endDate;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Institution getWorkplace() {
		return workplace;
	}

	public void setWorkplace(Institution workplace) {
		this.workplace = workplace;
	}

	public PhoneNumber getPhone() {
		return phone;
	}

	public void setPhone(PhoneNumber phone) {
		this.phone = phone;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
