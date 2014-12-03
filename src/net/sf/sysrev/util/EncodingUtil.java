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


package net.sf.sysrev.util;

import java.util.HashMap;
import java.util.Map;

public final class EncodingUtil
{
	private EncodingUtil()
	{
	}
	
	/**
	 * Mapping of chars to UTF strings.
	 */
	private static Map<String, String> translateChars;
	static {
		translateChars = new HashMap<String, String>();
		translateChars.put("´e", "é");
		translateChars.put("´a", "á");
		translateChars.put("´ı", "í");
		translateChars.put("´o", "ó");
	}

	public static String translate(String str)
	{
		for (String key : translateChars.keySet()) {
			str = str.replace(key, translateChars.get(key));
		}
		return str;
	}
}
