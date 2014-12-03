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

package net.sf.sysrev.methods;


public class InvalidCharRemover implements Filter
{
	private static final char[] chars = { '\'', '\"', '@', '#', '$', '%', '&', '*', '(', ')',
					'-', '_', '+', '=', '§', '\'', '`', '{', '[', 'ª', '^', '~', ']', '}', 'º',
					'|', '\\', '<', '>', '/', '°' };

	@Override
	public String filter(String s)
	{
		StringBuilder sb = new StringBuilder(s);
		
		for (int i = 0; i < sb.length(); i++) {
			for (char c : chars) {
				if (sb.charAt(i) == c) {
					/*
					sb.deleteCharAt(i);
					sb.insert(i, " ");
					*/
					sb.replace(i, i + 1, " ");
				}
			}
		}
		return sb.toString();
	}
}
