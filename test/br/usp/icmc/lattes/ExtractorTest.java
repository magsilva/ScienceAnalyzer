package br.usp.icmc.lattes;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

public class ExtractorTest
{
	private Extractor extractor;

	@Ignore
	@Test
	public void testRunConvert() {
		extractor = new Extractor(false);
		File file = extractor.convertToPPM(new File("/home/magsilva/teste.jpg"));
		assertEquals("/home/magsilva/teste.ppm", file.getAbsolutePath());
	}

	@Ignore
	@Test
	public void testRunOCR() {
		extractor = new Extractor(false);
		String captcha = extractor.runOCR(new File("/home/magsilva/teste.ppm"));
		assertEquals("ID6N", captcha);
	}

	@Test
	public void testGuessCaptcha2() {
		extractor = new Extractor(false);
		String captcha = extractor.guessCaptcha(new File("/home/magsilva/te.ppm"));
		assertEquals("UA8R", captcha);
	}

	// http://lattes.cnpq.br/
	@Test
	public void testGetLattesId() {
		extractor = new Extractor();
		String id = extractor.getLattesId("Aretha Barbosa Alencar");
		assertEquals("3074461020062854", id);
	}

}
