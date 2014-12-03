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

package net.sf.sysrev.methods;

public class MethodCapability
{
	public static int TITLE;

	public static int AUTHORSHIP;
	
	public static int YEAR;

	public static int PAGES;
	
	public static int DOI;
	
	public static int PUBLISHER;
	
	public static int EDITOR;
	
	public static int KEYWORDS;
	
	static {
		int i = 1;
		TITLE = i;
		AUTHORSHIP = i << 2;
		YEAR = i << 2;
		PAGES = i << 2;
		DOI = i << 2;
		PUBLISHER = i << 2;
		EDITOR = i << 2;
		KEYWORDS = i << 2;
	}
}
