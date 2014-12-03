package net.sf.sysrev.methods;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class BlankWorkSplitterTest
{
	private BlankWorkSplitter splitter;
	
	private static final String[] s = {"test", "1", "2", "3"};

	@Before
	public void setUp() throws Exception
	{
		splitter = new BlankWorkSplitter();
	}

	@Test
	public void testFilter1()
	{
		String s1 = "test 1 2 3";
		assertArrayEquals(s, splitter.filter(s1));
	}

	@Test
	public void testFilter2()
	{
		String s1 = "test\r1 2 3";
		assertArrayEquals(s, splitter.filter(s1));
	}

	@Test
	public void testFilter3()
	{
		String s1 = "test\r1 2\t3";
		assertArrayEquals(s, splitter.filter(s1));
	}

	@Test
	public void testFilter4()
	{
		String s1 = "test\r1\n2\t3";
		assertArrayEquals(s, splitter.filter(s1));
	}

}
