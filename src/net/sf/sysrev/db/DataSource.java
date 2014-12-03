package net.sf.sysrev.db;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="datasource")
public class DataSource
{
	@SuppressWarnings("unused")
	@Id
	@GeneratedValue
	private Long id;
	
	@Basic
	@Column(name="name")
	private String name;
	
	@Basic
	@Column(name="abstract")
	private String textAbstract;
	
	@Basic
	@Column(name="content")
	private String content;
	
	/**
	 * URI.
	 */
	@Basic
	@Column(name="source")
	private String source;

	public String getSource()
	{
		return source;
	}

	public void setSource(String source)
	{
		this.source = source;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getAbstract()
	{
		return textAbstract;
	}

	public void setAbstract(String textAbstract)
	{
		this.textAbstract = textAbstract;
	}

}
