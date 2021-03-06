package com.ironiacorp.scienceanalyzer;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.ironiacorp.scienceanalyzer.education.Degree;
import com.ironiacorp.scienceanalyzer.social.SocialAccount;

@Entity
public class Person implements Comparable
{
	@Id
	@GeneratedValue
	private int id;
	
	@Basic
	private String name;
	
	@Basic
	private Date birthDay;
	
	@Basic
	private Date deathDate;
	
	@ElementCollection
	private Set<String> names;

	@OneToMany(cascade=CascadeType.PERSIST)
	private Set<Degree> degrees;
	
	@ElementCollection
	private Set<String> emails;
	
	@OneToMany(cascade=CascadeType.PERSIST)
	private Set<Job> jobs;
	
	@OneToMany(cascade=CascadeType.PERSIST)
	private Set<SocialAccount> socialAccounts;
	
	public Person()
	{
		degrees = new HashSet<Degree>();
		emails = new TreeSet<String>();
		names = new TreeSet<String>();
		socialAccounts = new HashSet<SocialAccount>();
		jobs = new HashSet<Job>();
	}
	
	public int getId()
	{
		return id;
	}
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
		names.add(name);
	}
	
	public void addName(String name)
	{
		names.add(name);
		if (this.name == null) {
			this.name = name;
		}
	}
	
	public void removeName(String name)
	{
		names.remove(name);
		if (this.name.equals(name)) {
			this.name = names.iterator().next();
		}
	}
	
	public Date getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(Date birthDay) {
		this.birthDay = birthDay;
	}

	public Date getDeathDate() {
		return deathDate;
	}

	public void setDeathDate(Date deathDate) {
		this.deathDate = deathDate;
	}

	public Set<Degree> getDegrees()
	{
		return degrees;
	}

	public void addDegree(Degree degree)
	{
		degrees.add(degree);
	}

	public Set<String> getEmails()
	{
		return emails;
	}
	
	public void addEmail(String email)
	{
		if (email != null && ! email.trim().isEmpty()) {
			emails.add(email);
		}
	}
		
	public void addSocialAccount(SocialAccount account)
	{
		socialAccounts.add(account);
	}
	
	public void removeSocialAccount(SocialAccount account)
	{
		socialAccounts.remove(account);
	}
	
	public Set<SocialAccount> getSocialAccounts()
	{
		return socialAccounts;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Person other = (Person) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public int compareTo(Object o)
	{
		Person p = (Person) o;
		return name.compareTo(p.name);
	}
	
	
}
