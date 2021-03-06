package com.ironiacorp.scienceanalyzer;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class Job
{
	@Basic
	private String name;
	
	@ManyToOne
	private Institution workplace;
	
	@OneToOne
	private PhoneNumber phone;
	
	@Basic
	private Date startDate;
	
	@Basic
	private Date endDate;
	
	@Basic
	private boolean retired;

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

	public boolean isRetired() {
		return retired;
	}

	public void setRetired(boolean retired) {
		this.retired = retired;
	}
}
