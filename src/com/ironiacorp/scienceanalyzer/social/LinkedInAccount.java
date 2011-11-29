package com.ironiacorp.scienceanalyzer.social;

import javax.persistence.Entity;

@Entity
public class LinkedInAccount extends SocialAccount
{
	@Override
	public String getServiceName() {
		return "LinkedIn";
	}

}
