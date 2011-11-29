package com.ironiacorp.scienceanalyzer.social;

import javax.persistence.Entity;

@Entity
public class FacebookAccount extends SocialAccount
{
	@Override
	public String getServiceName() {
		return "Facebook";
	}

}
