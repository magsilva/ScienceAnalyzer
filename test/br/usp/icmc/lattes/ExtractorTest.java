package br.usp.icmc.lattes;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

public class ExtractorTest
{
	private Extractor extractor;

	@Before
	public void setUp() throws Exception
	{
		extractor = new Extractor();
	}

	@Test
	public void testRunConvert() {
		File file = extractor.runConvert(new File("/home/magsilva/teste.jpg"));
		assertEquals("/home/magsilva/teste.ppm", file.getAbsolutePath());
	}

	@Test
	public void testRunOCR() {
		String captcha = extractor.runOCR(new File("/home/magsilva/teste.ppm"));
		assertEquals("ID6N", captcha);
	}

	@Test
	public void testGuessCaptcha() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetLattesId() {
		extractor.getLattesId("Marco Aurélio Graciotto Silva");
	}

}
