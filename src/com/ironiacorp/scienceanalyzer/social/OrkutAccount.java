package com.ironiacorp.scienceanalyzer.social;

import javax.persistence.Entity;

@Entity
public class OrkutAccount extends SocialAccount
{
	@Override
	public String getServiceName() {
		return "Orkut";
	}

}
