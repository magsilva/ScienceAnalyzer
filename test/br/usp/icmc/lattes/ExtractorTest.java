package br.usp.icmc.lattes;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class ExtractorTest
{
	private Extractor extractor;

	@Before
	public void setUp() throws Exception
	{
		extractor = new Extractor();
	}

	@Ignore
	@Test
	public void testRunConvert() {
		File file = extractor.runConvert(new File("/home/magsilva/teste.jpg"));
		assertEquals("/home/magsilva/teste.ppm", file.getAbsolutePath());
	}

	@Ignore
	@Test
	public void testRunOCR() {
		String captcha = extractor.runOCR(new File("/home/magsilva/teste.ppm"));
		assertEquals("ID6N", captcha);
	}

	@Ignore
	@Test
	public void testGuessCaptcha() {
		fail("Not yet implemented");
	}

	// http://lattes.cnpq.br/
	@Test
	public void testGetLattesId() {
		String id = extractor.getLattesId("Aretha Barbosa Alencar");
		System.out.println(id);
	}

}
