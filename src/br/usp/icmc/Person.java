package br.usp.icmc;

import java.util.HashSet;
import java.util.Set;

public class Person implements Comparable
{
	private String name;

	private Set<Degree> degrees;
	
	private Set<String> emails;
	
	public Person()
	{
		degrees = new HashSet<Degree>();
		emails = new HashSet<String>();
	}
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
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
