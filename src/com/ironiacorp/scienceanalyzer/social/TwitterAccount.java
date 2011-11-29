package com.ironiacorp.scienceanalyzer.social;

import javax.persistence.Entity;

@Entity
public class TwitterAccount extends SocialAccount
{
	@Override
	public String getServiceName() {
		return "Twitter";
	}

}
