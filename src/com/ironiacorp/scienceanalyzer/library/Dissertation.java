package com.ironiacorp.scienceanalyzer.library;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.ironiacorp.scienceanalyzer.Institution;
import com.ironiacorp.scienceanalyzer.Person;

@Entity
public class Dissertation extends Publication
{
	@ManyToOne
	private Person advisor;
	
	@ManyToOne
	private Institution institution;

	public Person getAdvisor() {
		return advisor;
	}

	public void setAdvisor(Person advisor) {
		this.advisor = advisor;
	}

	public Institution getInstitution() {
		return institution;
	}

	public void setInstitution(Institution institution) {
		this.institution = institution;
	}
}
