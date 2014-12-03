package net.sf.sysrev.methods;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ContractionReplacerTest
{
	private ContractionReplacer replacer;
	
	@Before
	public void setUp() throws Exception
	{
		replacer = new ContractionReplacer();
		replacer.setWordSplitter(new BlankWorkSplitter());
	}

	@Test
	public void testFilter()
	{
		String s1 = "I'm";
		String[] s = {"I", "am"};
		assertArrayEquals(s, replacer.filter(s1));
	}

}
