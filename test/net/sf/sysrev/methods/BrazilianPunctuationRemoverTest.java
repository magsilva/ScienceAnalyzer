package net.sf.sysrev.methods;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class BrazilianPunctuationRemoverTest
{
	private BrazilianPunctuationRemover remover;
	
	@Before
	public void setUp() throws Exception
	{
		remover = new BrazilianPunctuationRemover();
	}

	@Test
	public void testFilter1()
	{
		assertEquals("e ", remover.filter("e."));
	}

	@Test
	public void testFilter2()
	{
		assertEquals(" e be", remover.filter("!e.be"));
	}

	@Test
	public void testFilter3()
	{
		assertEquals(" e be ", remover.filter("!e.be?"));
	}

	@Test
	public void testFilter4()
	{
		assertEquals(" e be  ", remover.filter("!e.be? "));
	}
}
