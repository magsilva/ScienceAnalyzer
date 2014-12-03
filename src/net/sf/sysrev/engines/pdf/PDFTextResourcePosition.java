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

package net.sf.sysrev.engines.pdf;

import net.sf.sysrev.engines.Font;
import net.sf.sysrev.engines.PDFFont;
import net.sf.sysrev.engines.TextResourcePosition;

import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.TextNormalize;
import org.apache.pdfbox.util.TextPosition;

public class PDFTextResourcePosition implements TextResourcePosition
{
	private TextPosition position;


	private TextPosition getPosition()
	{
		return position;
	}

	public PDFTextResourcePosition(TextPosition position)
	{
		this.position = position;
	}

	public boolean contains(TextResourcePosition tp2)
	{
		double thisXstart = getAdjustedX();
		double thisXend = getAdjustedX() + getAdjustedWidth();

		double tp2Xstart = tp2.getAdjustedX();
		double tp2Xend = tp2.getAdjustedX() + tp2.getAdjustedWidth();

		/*
		 * No X overlap at all so return as soon as possible.
		 */
		if (tp2Xend <= thisXstart || tp2Xstart >= thisXend) {
			return false;
		}

		/*
		 * No Y overlap at all so return as soon as possible. Note: 0.0 is in the upper left and
		 * y-coordinate is top of TextPosition
		 */
		if ((tp2.getAdjustedY() + tp2.getAdjustedHeight() < getAdjustedY())
						|| (tp2.getAdjustedY() > getAdjustedY() + getAdjustedHeight())) {
			return false;
		}
		/*
		 * We're going to calculate the percentage of overlap. If its less than a 15% x-coordinate
		 * overlap then we'll return false because its negligible. .15 was determined by trial and
		 * error in the regression test files.
		 */
		else if ((tp2Xstart > thisXstart) && (tp2Xend > thisXend)) {
			double overlap = thisXend - tp2Xstart;
			double overlapPercent = overlap / getAdjustedWidth();
			return (overlapPercent > .15);
		} else if ((tp2Xstart < thisXstart) && (tp2Xend < thisXend)) {
			double overlap = tp2Xend - thisXstart;
			double overlapPercent = overlap / getAdjustedWidth();
			return (overlapPercent > .15);
		}
		return true;
	}

	public boolean equals(Object obj)
	{
		return position.equals(obj);
	}

	public String getText()
	{
		return position.getCharacter();
	}

	public float getDirection()
	{
		return position.getDir();
	}

	public Font getFont()
	{
		return new PDFFont(position.getFont());
	}

	public float getFontSize()
	{
		return position.getFontSize();
	}

	public float getFontSizeInPt()
	{
		return position.getFontSizeInPt();
	}

	public float getHeight()
	{
		return position.getHeight();
	}

	public float getAdjustedHeight()
	{
		return position.getHeightDir();
	}

	public float[] getIndividualWidths()
	{
		return position.getIndividualWidths();
	}

	public Matrix getTextPosition()
	{
		return position.getTextPos();
	}

	public float getWidth()
	{
		return position.getWidth();
	}

	public float getAdjustedWidth()
	{
		return position.getWidthDirAdj();
	}

	public float getWidthOfSpace()
	{
		return position.getWidthOfSpace();
	}

	public float getWordSpacing()
	{
		return position.getWordSpacing();
	}

	public float getX()
	{
		return position.getX();
	}

	public float getAdjustedX()
	{
		return position.getXDirAdj();
	}

	public float getXScale()
	{
		return position.getXScale();
	}

	public float getY()
	{
		return position.getY();
	}

	public float getAdjustedY()
	{
		return position.getYDirAdj();
	}

	public float getYScale()
	{
		return position.getYScale();
	}

	public int hashCode()
	{
		return position.hashCode();
	}

	public boolean isDiacritic()
	{
		return position.isDiacritic();
	}

	public void mergeDiacritic(TextPosition diacritic, TextNormalize normalize)
	{
		position.mergeDiacritic(diacritic, normalize);
	}

	public void mergeDiacritic(PDFTextResourcePosition diacritic, TextNormalize normalize)
	{
		mergeDiacritic(diacritic.getPosition(), normalize);
	}

	public String toString()
	{
		return position.toString();
	}
}
