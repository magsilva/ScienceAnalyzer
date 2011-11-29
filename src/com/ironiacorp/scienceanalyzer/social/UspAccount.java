package com.ironiacorp.scienceanalyzer.social;

import javax.persistence.Entity;

@Entity
public class UspAccount extends SocialAccount
{
	@Override
	public String getServiceName() {
		return "Usp";
	}

}
