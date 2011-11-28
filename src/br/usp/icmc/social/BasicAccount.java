package br.usp.icmc.social;

public abstract class BasicAccount implements Account
{
	private String userName;
	
	private void setUserName(String userName)
	{
		this.userName = userName;
	}
	
	@Override
	public String getUserName() {
		return userName;
	}

}
