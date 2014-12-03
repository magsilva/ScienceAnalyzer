/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sf.sysrev.engines.pdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;

import net.sf.sysrev.engines.TextResource;
import net.sf.sysrev.engines.TextResourcePosition;
import net.sf.sysrev.engines.TextResourcePositionComparator;

import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.util.PDFStreamEngine;
import org.apache.pdfbox.util.ResourceLoader;
import org.apache.pdfbox.util.TextNormalize;
import org.apache.pdfbox.util.TextPosition;
import org.apache.pdfbox.util.TextPositionComparator;

/**
 * Take a pdf document and strip out all of the text (ignoring the formatting and such), figures and
 * any other important element.
 *
 * This class is derived from org.apache.pdfbox.util.PDFTextStripper, from <a
 * href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 */
public class PDFStripper extends PDFStreamEngine
{
	private PDDocument document;

	/**
	 * Page the processing will start in.
	 */
	private int startPage = 1;

	/**
	 * Last page to process.
	 */
	private int endPage = Integer.MAX_VALUE;

	/**
	 * Pages processed.
	 */
	private volatile int pagesProcessed = 0;

	/**
	 * When processing the text, should we suppress overlapping text found in the PDF?
	 */
	private boolean suppressDuplicateOverlappingText = true;

	/**
	 * When processing the text, should we sort the characters found by the position?
	 */
	private boolean sortByPosition = false;

	/**
	 * Size of a space (if the length between two chars is bigger than this, there is a space
	 * between the chars).
	 */
	private float spacingTolerance = .5f;

	/**
	 * Average size of a character.
	 */
	private float averageCharTolerance = .3f;

	protected List<TextResource> textList = new ArrayList<TextResource>();

	private Map characterListMapping = new HashMap();

	protected String lineSeparator = System.getProperty("line.separator");
	private String pageSeparator = System.getProperty("line.separator");
	private String wordSeparator = " ";

	/**
	 * Encoding that text will be written in (or null)
	 */
	protected String encoding;

	/**
	 * The normalizer is used to remove text ligatures/presentation forms and to correct the
	 * direction of right to left text, such as Arabic and Hebrew.
	 */
	private TextNormalize normalize = null;

	/**
	 * Instantiate a new PDFTextStripper object. This object will load properties from
	 * Resources/PDFTextStripper.properties and will not do anything special to convert the text to
	 * a more encoding-specific output.
	 *
	 * @throws IOException If there is an error loading the properties.
	 */
	public PDFStripper() throws IOException
	{
		this(ResourceLoader.loadProperties("Resources/PDFTextStripper.properties", true));
	}

	/**
	 * Instantiate a new PDFTextStripper object. Loading all of the operator mappings from the
	 * properties object that is passed in. Does not convert the text to more encoding-specific
	 * output.
	 *
	 * @param props The properties containing the mapping of operators to PDFOperator classes.
	 *
	 * @throws IOException If there is an error reading the properties.
	 */
	public PDFStripper(Properties props) throws IOException
	{
		this(props, null);
	}


	/**
	 * Instantiate a new PDFTextStripper object. This object will load properties from
	 * Resources/PDFTextStripper.properties and will apply encoding-specific conversions to the
	 * output text.
	 *
	 * @param encoding The encoding that the output will be written in.
	 *
	 * @throws IOException If there is an error reading the properties.
	 */
	public PDFStripper(Properties props, String encoding) throws IOException
	{
		super(props);
		this.encoding = encoding;
		normalize = new TextNormalize(this.encoding);
	}

	/**
	 * This will return the text of a document. See writeText. <br />
	 * NOTE: The document must not be encrypted when coming into this method.
	 *
	 * @param doc The document to get the text from.
	 *
	 * @return The text of the PDF document.
	 *
	 * @throws IOException if the doc state is invalid or it is encrypted.
	 */
	public String getText(PDDocument doc)
	{
		resetEngine();
		document = doc;
		pagesProcessed = 0;

		if (document.isEncrypted()) {
			throw new IllegalArgumentException(
							"Expected unencrypted or already decrypted documents");
		}
		processPages(document.getDocumentCatalog().getAllPages());

		StringBuilder sb = new StringBuilder();
		Iterator<TextResource> i = textList.iterator();
		while (i.hasNext()) {
			TextResource res = i.next();
			sb.append(res.getText());
		}
		return sb.toString();
	}

	/**
	 * Process all of the pages of the document.
	 *
	 * @param pages The pages object in the document.
	 */
	protected void processPages(List pages)
	{
		Iterator i = pages.iterator();
		while (i.hasNext()) {
			PDPage page = (PDPage) i.next();
			try {
				processPage(page);
			} catch (IOException e) {
				// Just ignore unprocessed pages. We can discover this
				// later by subtracting the pages processed from the total
				// pages.
			}
		}
	}

	/**
	 * Process a single page of the document.
	 *
	 * @param pages The page object.
	 *
	 * @throws IOException If any error is found when processing/extracting the text from the page.
	 */
	protected void processPage(PDPage page) throws IOException
	{
		PDStream contentStream = page.getContents();
		if (contentStream == null) {
			return;
		}

		COSStream contents = contentStream.getStream();
		characterListMapping.clear();

		processStream(page, page.findResources(), contents);
		writePage();
		pagesProcessed++;
	}

	/**
	 * This will print the text of the processed page to "output". It will estimate, based on the
	 * coordinates of the text, where newlines and word spacings should be placed. The text will be
	 * sorted only if that feature was enabled.
	 *
	 * @throws IOException If there is an error writing the text.
	 */
	protected void writePage() throws IOException
	{
		float maxYForLine = -1;
		float minYTopForLine = Float.MAX_VALUE;
		float endOfLastTextX = -1;
		float lastWordSpacing = -1;
		float maxHeightForLine = -1;

		if (sortByPosition) {
			Comparator comparator = new TextResourcePositionComparator();
			Collections.sort(textList, comparator);
		}

		ListIterator i = textList.listIterator();

		/*
		 * Before we can display the text, we need to do some normalizing. Arabic and Hebrew text is
		 * right to left and is typically stored in its logical format, which means that the
		 * rightmost character is stored first, followed by the second character from the right etc.
		 * However, PDF stores the text in presentation form, which is left to right. We need to do
		 * some normalization to convert the PDF data to the proper logical output format.
		 *
		 * Note that if we did not sort the text, then the output of reversing the text is undefined
		 * and can sometimes produce worse output than not trying to reverse the order. Sorting
		 * should be done for these languages.
		 */

		/*
		 * First step is to determine if we have any right to left text, and if so, is it dominant.
		 */
		int ltrCnt = 0;
		int rtlCnt = 0;

		while (i.hasNext()) {
			TextResource res = (TextResource) i.next();
			String text = res.getText();

			for (int a = 0; a < text.length(); a++) {
				byte dir = Character.getDirectionality(text.charAt(a));
				switch (dir) {
					case Character.DIRECTIONALITY_LEFT_TO_RIGHT:
					case Character.DIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING:
					case Character.DIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE:
						ltrCnt++;
						break;
					case Character.DIRECTIONALITY_RIGHT_TO_LEFT:
					case Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC:
					case Character.DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING:
					case Character.DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE:
						rtlCnt++;
						break;
					default:
						ltrCnt++;
				}
			}
		}

		// choose the dominant direction
		boolean isRtlDominant = false;
		if (rtlCnt > ltrCnt) {
			isRtlDominant = true;
		}

		// we will later use this to skip reordering
		boolean hasRtl = false;
		if (rtlCnt > 0) {
			hasRtl = true;
		}

		/*
		 * Now cycle through to extract the text.
		 */
		String lineStr = "";
		i = textList.listIterator();

		/*
		 * PDF files don't always store spaces. We will need to guess where we should add spaces
		 * based on the distances between TextPositions. Historically, this was done based on the
		 * size of the space character provided by the font. In general, this worked but there were
		 * cases where it did not work. Calculating the average character width and using that as a
		 * metric works better in some cases but fails in some cases where the spacing worked. So we
		 * use both.
		 */

		// Keeps track of the previous average character width
		float previousAveCharWidth = -1;
		PDFTextResource lastResource = null;
		while (i.hasNext()) {
			PDFTextResource res = (PDFTextResource) i.next();
			TextResourcePosition position = (TextResourcePosition) res.getPosition();
			String text = res.getText();

			if (position == null) {
				continue;
			}

			// Resets the average character width when we see a change in font or a change in the
			// font size
			if (lastResource != null) {
				if (position.getFont() != ((TextResourcePosition) lastResource.getPosition())
								.getFont()
								|| position.getFontSize() != ((TextResourcePosition) lastResource
												.getPosition()).getFontSize()) {
					previousAveCharWidth = -1;
				}
			}

			float positionX;
			float positionY;
			float positionWidth;
			float positionHeight;
			/*
			 * If we are sorting, then we need to use the text direction adjusted coordinates,
			 * because they were used in the sorting.
			 */
			if (sortByPosition) {
				positionX = position.getAdjustedX();
				positionY = position.getAdjustedY();
				positionWidth = position.getAdjustedWidth();
				positionHeight = position.getAdjustedHeight();
			} else {
				positionX = position.getX();
				positionY = position.getY();
				positionWidth = position.getWidth();
				positionHeight = position.getHeight();
			}

			// The current amount of characters in a word
			int wordCharCount = position.getIndividualWidths().length;

			/*
			 * Estimate the expected width of the space based on the space character with some
			 * margin.
			 */
			float wordSpacing = position.getWidthOfSpace();
			float deltaSpace = 0;
			if ((wordSpacing == 0) || (wordSpacing == Float.NaN)) {
				deltaSpace = Float.MAX_VALUE;
			} else {
				if (lastWordSpacing < 0) {
					deltaSpace = (wordSpacing * spacingTolerance);
				} else {
					deltaSpace = (((wordSpacing + lastWordSpacing) / 2f) * spacingTolerance);
				}
			}

			/*
			 * Estimate the expected width of the space based on the average character width with
			 * some margin. This calculation does not make a true average (average of averages) but
			 * we found that it gave the best results after numerous experiments. Based on
			 * experiments we also found that .3 worked well.
			 */
			float averageCharWidth = -1;
			if (previousAveCharWidth < 0) {
				averageCharWidth = (positionWidth / wordCharCount);
			} else {
				averageCharWidth = (previousAveCharWidth + (positionWidth / wordCharCount)) / 2f;
			}
			float deltaCharWidth = (averageCharWidth * averageCharTolerance);

			// Compares the values obtained by the average method and the wordSpacing method and
			// picks
			// the smallest number.
			float expectedStartOfNextWordX = -1;
			if (endOfLastTextX != -1) {
				if (deltaCharWidth > deltaSpace) {
					expectedStartOfNextWordX = endOfLastTextX + deltaSpace;
				} else {
					expectedStartOfNextWordX = endOfLastTextX + deltaCharWidth;
				}
			}

			if (lastResource != null) {
				// RDD - Here we determine whether this text object is on the current line. We use
				// the lastBaselineFontSize to handle the superscript case, and the size of the
				// current font to handle the subscript case. Text must overlap with the last
				// rendered baseline text by at least a small amount in order to be considered as
				// being on the same line.

				/*
				 * XXX BC: In theory, this check should really check if the next char is in full
				 * range seen in this line. This is what I tried to do with minYTopForLine, but this
				 * caused a lot of regression test failures. So, I'm leaving it be for now.
				 */
				if (!overlap(positionY, positionHeight, maxYForLine, maxHeightForLine)) {
					// If we have RTL text on the page, change the direction
					if (hasRtl) {
						lineStr = normalize.makeLineLogicalOrder(lineStr, isRtlDominant);
					}

					/*
					 * normalize string to remove presentation forms. Note that this must come after
					 * the line direction conversion because the process looks ahead to the next
					 * logical character.
					 */
					lineStr = normalize.normalizePres(lineStr);

					// textList.add(new PDFTextResource(lineStr));
					i.add(new PDFTextResource(lineStr));
					lineStr = "";

					// textList.add(new PDFTextResource(lineSeparator));
					i.add(new PDFTextResource(lineSeparator));

					endOfLastTextX = -1;
					expectedStartOfNextWordX = -1;
					maxYForLine = -1;
					maxHeightForLine = -1;
					minYTopForLine = Float.MAX_VALUE;
				}

				// Test if our TextPosition starts after a new word would be
				// expected to start.
				if (expectedStartOfNextWordX != -1
								&& expectedStartOfNextWordX < positionX
								&&
								// only bother adding a space if the last character
								// was not a space
								lastResource.getText() != null
								&& !lastResource.getText().endsWith(wordSeparator)) {
					lineStr += wordSeparator;
				}
			}

			if (positionY >= maxYForLine) {
				maxYForLine = positionY;
			}

			// RDD - endX is what PDF considers to be the x coordinate of
			// the
			// end position of the text. We use it in computing our metrics
			// below.
			endOfLastTextX = positionX + positionWidth;

			// add it to the list
			if (text != null) {
				lineStr += text;
			}
			maxHeightForLine = Math.max(maxHeightForLine, positionHeight);
			minYTopForLine = Math.min(minYTopForLine, positionY - positionHeight);
			lastResource = res;
			lastWordSpacing = wordSpacing;
			previousAveCharWidth = averageCharWidth;
		}

		// print the final line
		if (lineStr.length() > 0) {
			if (hasRtl)
				lineStr = normalize.makeLineLogicalOrder(lineStr, isRtlDominant);

			// normalize string to remove presentation forms
			lineStr = normalize.normalizePres(lineStr);

			// textList.add(new PDFTextResource(lineStr));
			i.add(new PDFTextResource(lineStr));
		}

		textList.add(new PDFTextResource(pageSeparator));
	}

	private boolean overlap(float y1, float height1, float y2, float height2)
	{
		return within(y1, y2, .1f) || (y2 <= y1 && y2 >= y1 - height1)
						|| (y1 <= y2 && y1 >= y2 - height2);
	}

	/**
	 * This will determine of two floating point numbers are within a specified variance.
	 *
	 * @param first The first number to compare to.
	 * @param second The second number to compare to.
	 * @param variance The allowed variance.
	 */
	private boolean within(float first, float second, float variance)
	{
		return second > first - variance && second < first + variance;
	}

	/**
	 * This will process a TextPosition object and add the text to the list of characters on a page.
	 * It takes care of overlapping text.
	 *
	 * @param text The text to process.
	 */
	protected void processTextPosition(TextPosition text)
	{
		boolean showCharacter = true;
		if (suppressDuplicateOverlappingText) {
			showCharacter = false;
			String textCharacter = text.getCharacter();
			float textX = text.getX();
			float textY = text.getY();
			List sameTextCharacters = (List) characterListMapping.get(textCharacter);
			if (sameTextCharacters == null) {
				sameTextCharacters = new ArrayList();
				characterListMapping.put(textCharacter, sameTextCharacters);
			}

			// RDD - Here we compute the value that represents the end of the rendered text. This
			// value is used to determine whether subsequent text rendered on the same line
			// overwrites the current text.
			//
			// We subtract any positive padding to handle cases where extreme amounts of padding are
			// applied, then backed off (not sure why this is done, but there are cases where the
			// padding is on the order of 10x the character width, and the TJ just backs up to
			// compensate after each character). Also, we subtract an amount to allow for kerning (a
			// percentage of the width of the last character).
			//
			boolean suppressCharacter = false;
			float tolerance = (text.getWidth() / textCharacter.length()) / 3.0f;
			for (int i = 0; i < sameTextCharacters.size() && textCharacter != null; i++) {
				TextPosition character = (TextPosition) sameTextCharacters.get(i);
				String charCharacter = character.getCharacter();
				float charX = character.getX();
				float charY = character.getY();
				// only want to suppress

				if (charCharacter != null &&
				// charCharacter.equals( textCharacter ) &&
								within(charX, textX, tolerance) && within(charY, textY, tolerance)) {
					suppressCharacter = true;
				}
			}
			if (!suppressCharacter) {
				sameTextCharacters.add(text);
				showCharacter = true;
			}
		}

		if (showCharacter) {
			float x = text.getX();
			float y = text.getY();

			/*
			 * In the wild, some PDF encoded documents put diacritics (accents on top of characters)
			 * into a separate Tj element. When displaying them graphically, the two chunks get
			 * overlayed. With text output though, we need to do the overlay. This code recombines
			 * the diacritic with its associated character if the two are consecutive.
			 */
			if (textList.isEmpty()) {
				textList.add(new PDFTextResource(text.getCharacter(), new PDFTextResourcePosition(
								text)));
			} else {
				/*
				 * test if we overlap the previous entry. Note that we are making an assumption that
				 * we need to only look back one TextPosition to find what we are overlapping. This
				 * may not always be true.
				 */
				TextResource previousText = textList.get(textList.size() - 1);
				PDFTextResourcePosition previousTextPosition = (PDFTextResourcePosition) previousText.getPosition();
				PDFTextResourcePosition textPosition = new PDFTextResourcePosition(text);
				if (text.isDiacritic() && previousTextPosition != null && previousTextPosition.contains(textPosition)) {
					previousTextPosition.mergeDiacritic(text, normalize);
				}
				/*
				 * If the previous TextPosition was the diacritic, merge it into this one and remove
				 * it from the list.
				 */
				else if (previousTextPosition != null && previousTextPosition.isDiacritic() && textPosition.contains(previousTextPosition)) {
					textPosition.mergeDiacritic(previousTextPosition, normalize);
					textList.remove(textList.size() - 1);
					textList.add(new PDFTextResource(text.getCharacter(),
									new PDFTextResourcePosition(text)));
				} else {
					textList.add(new PDFTextResource(text.getCharacter(),
									new PDFTextResourcePosition(text)));
				}
			}
		}
	}

	/**
	 * This is the page that the text extraction will start on. The pages start at page 1. For
	 * example in a 5 page PDF document, if the start page is 1 then all pages will be extracted. If
	 * the start page is 4 then pages 4 and 5 will be extracted. The default value is 1.
	 *
	 * @return Value of property startPage.
	 */
	public int getStartPage()
	{
		return startPage;
	}

	/**
	 * This will set the first page to be extracted by this class.
	 *
	 * @param startPageValue New value of property startPage.
	 */
	public void setStartPage(int startPageValue)
	{
		startPage = startPageValue;
	}

	/**
	 * This will get the last page that will be extracted. This is inclusive, for example if a 5
	 * page PDF an endPage value of 5 would extract the entire document, an end page of 2 would
	 * extract pages 1 and 2. This defaults to Integer.MAX_VALUE such that all pages of the pdf will
	 * be extracted.
	 *
	 * @return Value of property endPage.
	 */
	public int getEndPage()
	{
		return endPage;
	}

	/**
	 * This will set the last page to be extracted by this class.
	 *
	 * @param endPageValue New value of property endPage.
	 */
	public void setEndPage(int endPageValue)
	{
		endPage = endPageValue;
	}

	/**
	 * Set the desired page separator for output text. The line.separator system property is used if
	 * the page separator preference is not set explicitly using this method.
	 *
	 * @param separator The desired page separator string.
	 */
	public void setPageSeparator(String separator)
	{
		pageSeparator = separator;
	}

	/**
	 * Set the desired word separator for output text. The PDFBox text extraction algorithm will
	 * output a space character if there is enough space between two words. By default a space
	 * character is used. If you need and accurate count of characters that are found in a PDF
	 * document then you might want to set the word separator to the empty string.
	 *
	 * @param separator The desired page separator string.
	 */
	public void setWordSeparator(String separator)
	{
		wordSeparator = separator;
	}

	/**
	 * @return Returns the suppressDuplicateOverlappingText.
	 */
	public boolean shouldSuppressDuplicateOverlappingText()
	{
		return suppressDuplicateOverlappingText;
	}

	/**
	 * By default the text stripper will attempt to remove text that overlapps each other. Word
	 * paints the same character several times in order to make it look bold. By setting this to
	 * false all text will be extracted, which means that certain sections will be duplicated, but
	 * better performance will be noticed.
	 *
	 * @param suppressDuplicateOverlappingTextValue The suppressDuplicateOverlappingText to set.
	 */
	public void setSuppressDuplicateOverlappingText(boolean suppressDuplicateOverlappingTextValue)
	{
		this.suppressDuplicateOverlappingText = suppressDuplicateOverlappingTextValue;
	}

	/**
	 * This will tell if the text stripper should sort the text tokens before writing to the stream.
	 *
	 * @return true If the text tokens will be sorted before being written.
	 */
	public boolean shouldSortByPosition()
	{
		return sortByPosition;
	}

	/**
	 * The order of the text tokens in a PDF file may not be in the same as they appear visually on
	 * the screen. For example, a PDF writer may write out all text by font, so all bold or larger
	 * text, then make a second pass and write out the normal text.<br/>
	 * The default is to <b>not</b> sort by position.<br/>
	 * <br/>
	 * A PDF writer could choose to write each character in a different order. By default PDFBox
	 * does <b>not</b> sort the text tokens before processing them due to performance reasons.
	 *
	 * @param newSortByPosition Tell PDFBox to sort the text positions.
	 */
	public void setSortByPosition(boolean newSortByPosition)
	{
		sortByPosition = newSortByPosition;
	}

	/**
	 * Get the current space width-based tolerance value that is being used to estimate where spaces
	 * in text should be added. Note that the default value for this has been determined from trial
	 * and error.
	 *
	 * @return The current tolerance / scaling factor
	 */
	public float getSpacingTolerance()
	{
		return spacingTolerance;
	}

	/**
	 * Set the space width-based tolerance value that is used to estimate where spaces in text
	 * should be added. Note that the default value for this has been determined from trial and
	 * error. Setting this value larger will reduce the number of spaces added.
	 *
	 * @param spacingTolerance tolerance / scaling factor to use
	 */
	public void setSpacingTolerance(float spacingTolerance)
	{
		this.spacingTolerance = spacingTolerance;
	}

	/**
	 * Get the current character width-based tolerance value that is being used to estimate where
	 * spaces in text should be added. Note that the default value for this has been determined from
	 * trial and error.
	 *
	 * @return The current tolerance / scaling factor
	 */
	public float getAverageCharTolerance()
	{
		return averageCharTolerance;
	}

	/**
	 * Set the character width-based tolerance value that is used to estimate where spaces in text
	 * should be added. Note that the default value for this has been determined from trial and
	 * error. Setting this value larger will reduce the number of spaces added.
	 *
	 * @param spacingTolerance tolerance / scaling factor to use
	 */
	public void setAverageCharTolerance(float averageCharTolerance)
	{
		this.averageCharTolerance = averageCharTolerance;
	}

	public int getPagesProcessed()
	{
		return pagesProcessed;
	}

}
