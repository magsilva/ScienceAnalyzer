package br.usp.icmc.library;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Publication
{
	@Id
	@GeneratedValue
	private int id;
	

	public int getId()
	{
		return id;
	}
}
