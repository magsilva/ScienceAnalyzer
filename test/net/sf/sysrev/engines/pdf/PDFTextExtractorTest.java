package net.sf.sysrev.engines.pdf;

import static org.junit.Assert.*;

import net.sf.sysrev.engines.pdf.PDFTextExtractor;

import org.junit.Before;
import org.junit.Test;

public class PDFTextExtractorTest
{
	private PDFTextExtractor extractor;
	
	@Before
	public void setUp() throws Exception
	{
		extractor = new PDFTextExtractor();
	}

	@Test
	public void testExtractWordsM1() throws Exception
	{
		// getClass().getResource("/config.xml")
		String file = Thread.currentThread().getContextClassLoader().getResource("TokyoIsBurning.pdf").getFile();
		PDFDocument document = new PDFDocument(file);
		extractor.setPDFDocument(document);
		String[] words = extractor.extractWordsM1();
		assertEquals(words[0], "Tokyo");
	}
	
	@Test
	public void testExtractWordsM2() throws Exception
	{
		// getClass().getResource("/config.xml")
		String file = Thread.currentThread().getContextClassLoader().getResource("TokyoIsBurning.pdf").getFile();
		PDFDocument document = new PDFDocument(file);
		extractor.setPDFDocument(document);
		String[] words = extractor.extractWordsM2();
		assertEquals(words[0], "Tokyo");
	}
}
