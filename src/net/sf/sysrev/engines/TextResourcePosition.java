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

package net.sf.sysrev.engines;


import org.apache.pdfbox.util.Matrix;

public interface TextResourcePosition extends ResourcePosition
{
	boolean contains(TextResourcePosition textPosition);

	boolean equals(Object obj);

	float getDirection();

	Font getFont();

	float getFontSize();

	float getHeight();

	float getAdjustedHeight();

	float[] getIndividualWidths();

	Matrix getTextPosition();

	float getWidth();

	float getAdjustedWidth();

	float getWidthOfSpace();

	float getWordSpacing();

	float getX();

	float getAdjustedX();

	float getXScale();

	float getY();

	float getAdjustedY();

	float getYScale();
}
