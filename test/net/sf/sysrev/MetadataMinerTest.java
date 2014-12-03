/*
This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.
 
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
 
You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 
Copyright (C) 2007 Marco Aurelio Graciotto Silva <magsilva@gmail.com>
*/

package net.sf.sysrev;

import static org.junit.Assert.*;

import java.io.File;

import net.sf.sysrev.MetadataMiner;
import net.sf.sysrev.types.Article;
import net.sf.sysrev.types.Publication;

import org.junit.Before;
import org.junit.Test;

public class MetadataMinerTest
{
	private MetadataMiner miner; 
	
	private static String basedirEmpiricalSoftwareEngineering = "/home/magsilva/Projects/SystematicReviewer/resources/publications/Empirical Software Engineering/";
	private static String basedirSoftwarePracticeAndExperience = "/home/magsilva/Projects/SystematicReviewer/resources/publications/Software - Practice and Experience/";
	
	
	private static String article1 = "Protocols in the use of empirical software engineering artifacts.pdf";
	private static String article2 = "A highly modular and extensible architecture for an integrated IMS-based authoring system.pdf";
	
	@Before
	public void setUp()
	{
		miner = new MetadataMiner(); 
	}
	
	@Test
	public void testEmpiricalSoftwareEngineering()
	{
		String filename = basedirEmpiricalSoftwareEngineering + article1;
		File file = new File(filename);
		Publication pub = null;
		
		miner.load(filename);
		miner.process();
		pub = miner.getDocument(file.getAbsolutePath());
		if (pub instanceof Article) {
			Article article = (Article) pub;
			assertEquals(article.getTitle(), "Protocols in the use of empirical software engineering artifacts");
		} else {
			fail();
		}
		
	}
	
	// @Test
	public void testSoftwarePracticeAndExperience()
	{
		String filename = basedirSoftwarePracticeAndExperience + article2;
		File file = new File(filename);
		Publication pub = null;
		
		miner.load(filename);
		miner.process();
		pub = miner.getDocument(file.getAbsolutePath());
		if (pub instanceof Article) {
			Article article = (Article) pub;
			assertEquals(article.getTitle(), "A highly modular and extensible architecture for an integrated IMS-based authoring system: the <e-Aula> experience");
		} else {
			fail();
		}
		
	}
}
