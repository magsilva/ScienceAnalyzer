package com.ironiacorp.scienceanalyzer;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.ironiacorp.scienceanalyzer.geo.Location;

@Entity
public class Institution
{
	@ManyToOne
	private Institution parent;
	
	@Basic
	private String name;
	
	@ManyToOne
	private Location address;
	
	@ManyToOne
	private PhoneNumber phone;

	public Institution getParent() {
		return parent;
	}

	public void setParent(Institution parent) {
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Location getAddress() {
		return address;
	}

	public void setAddress(Location address) {
		this.address = address;
	}

	public PhoneNumber getPhone() {
		return phone;
	}

	public void setPhone(PhoneNumber phone) {
		this.phone = phone;
	}	
}
