package com.ironiacorp.scienceanalyzer.social;

public abstract class BasicAccount implements SocialAccount
{
	private String userName;
	
	public void setUserName(String userName)
	{
		this.userName = userName;
	}
	
	@Override
	public String getUserName() {
		return userName;
	}

}
