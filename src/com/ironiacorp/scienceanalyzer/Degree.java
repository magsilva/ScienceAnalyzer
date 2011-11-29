package com.ironiacorp.scienceanalyzer;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.ironiacorp.scienceanalyzer.library.Dissertation;

@Entity
public class Degree
{
	public enum DegreeType {
		MSC,
		PHD
	}
	
	@Basic
	private String courseName;
	
	@Basic
	private DegreeType type;
	
	@ManyToOne
	private Person advisor;
	
	@ManyToOne
	private Person coadvisor;
	
	@Basic
	private Date date;
	
	@OneToOne
	private Dissertation dissertation;

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public DegreeType getType() {
		return type;
	}

	public void setType(DegreeType type) {
		this.type = type;
	}

	public Person getAdvisor() {
		return advisor;
	}

	public void setAdvisor(Person advisor) {
		this.advisor = advisor;
	}

	public Person getCoadvisor() {
		return coadvisor;
	}

	public void setCoadvisor(Person coadvisor) {
		this.coadvisor = coadvisor;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Dissertation getDissertation() {
		return dissertation;
	}

	public void setDissertation(Dissertation dissertation) {
		this.dissertation = dissertation;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((advisor == null) ? 0 : advisor.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result
				+ ((dissertation == null) ? 0 : dissertation.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Degree other = (Degree) obj;
		if (advisor == null) {
			if (other.advisor != null)
				return false;
		} else if (!advisor.equals(other.advisor))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (dissertation == null) {
			if (other.dissertation != null)
				return false;
		} else if (!dissertation.equals(other.dissertation))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		switch (type) {
			case MSC:
				sb.append("MSc.");
				break;
			case PHD:
				sb.append("Ph.D.");
				break;
		}
		sb.append(", ");
		sb.append(dissertation.getTitle());
		
		return sb.toString();
	}
}
