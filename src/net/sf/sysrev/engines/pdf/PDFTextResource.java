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

 Copyright (C) 2009 Marco Aurelio Graciotto Silva <magsilva@gmail.com>
 */

package net.sf.sysrev.engines.pdf;

import net.sf.sysrev.engines.ResourcePosition;
import net.sf.sysrev.engines.TextResource;

public class PDFTextResource implements TextResource
{
	private String text;

	private ResourcePosition position;

	public PDFTextResource(String text)
	{
		this(text, null);
	}

	public PDFTextResource(String text, ResourcePosition position)
	{
		this.text = text;
		this.position = position;
	}


	public ResourcePosition getPosition()
	{
		return position;
	}

	public void setPosition(ResourcePosition position)
	{
		this.position = position;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public String getText()
	{
		return text;
	}

}
