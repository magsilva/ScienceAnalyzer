package br.usp.icmc.library;

import javax.persistence.Basic;
import javax.persistence.Entity;

@Entity
public class Issn
{
	@Basic
	private String value;
	
	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	@Override
	public int hashCode()
	{
		return value.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Issn) {
			return value.equals(((Issn) obj).value);
		}
		else {
			return false;
		}
	}
}
