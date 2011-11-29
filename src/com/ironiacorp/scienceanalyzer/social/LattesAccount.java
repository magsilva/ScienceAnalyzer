package com.ironiacorp.scienceanalyzer.social;

import javax.persistence.Entity;

@Entity
public class LattesAccount extends SocialAccount
{
	@Override
	public String getServiceName() {
		return "Lattes";
	}

}
