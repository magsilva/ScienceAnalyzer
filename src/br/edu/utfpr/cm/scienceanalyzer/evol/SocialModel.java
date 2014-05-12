package br.edu.utfpr.cm.scienceanalyzer.evol;

import java.io.File;

public class SocialModel
{
	public enum Type {
		GUEPHI(".gdf");
		
		String extension;
		
		private Type(String extension) {
			this.extension = extension;
		}
	}
	
	private Type type;
	
	private File basedir;
	
	private String name;
	
	public void setBasedir(File basedir) {
		this.basedir = basedir;
	}

	public File getBasedir() {
		return basedir;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
