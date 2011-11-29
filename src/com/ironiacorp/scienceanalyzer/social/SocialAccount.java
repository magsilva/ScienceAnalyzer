package com.ironiacorp.scienceanalyzer.social;

import javax.persistence.Basic;
import javax.persistence.Entity;

@Entity
public abstract class SocialAccount
{
	@Basic
	private String userName;
	
	public abstract String getServiceName();
	
	public void setUserName(String userName)
	{
		this.userName = userName;
	}
	
	public String getUserName() {
		return userName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((userName == null) ? 0 : userName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SocialAccount other = (SocialAccount) obj;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}
}
