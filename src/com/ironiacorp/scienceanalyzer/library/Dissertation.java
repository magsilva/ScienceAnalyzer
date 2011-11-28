package com.ironiacorp.scienceanalyzer.library;

import javax.persistence.Basic;
import javax.persistence.Entity;

import com.ironiacorp.scienceanalyzer.Institution;
import com.ironiacorp.scienceanalyzer.Person;

@Entity
public class Dissertation extends Publication
{
	@Basic
	private Person advisor;
	
	@Basic
	private Institution instituion;

	public Person getAdvisor() {
		return advisor;
	}

	public void setAdvisor(Person advisor) {
		this.advisor = advisor;
	}

	public Institution getInstituion() {
		return instituion;
	}

	public void setInstituion(Institution instituion) {
		this.instituion = instituion;
	}
}
