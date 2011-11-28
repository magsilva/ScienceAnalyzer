package com.ironiacorp.scienceanalyzer;

import com.ironiacorp.scienceanalyzer.geo.Location;

public class Institution
{
	private Institution parent;
	
	private String name;
	
	private Location address;
	
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
