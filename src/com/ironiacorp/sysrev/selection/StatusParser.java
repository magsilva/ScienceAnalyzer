/*
 * Copyright (C) 2011 Marco Aurélio Graciotto Silva <magsilva@ironiacorp.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.ironiacorp.sysrev.selection;

import java.util.ArrayList;
import java.util.List;

public class StatusParser
{
	private Status parseStatus(String text)
	{
		return Status.get(text);
	}

	private Reason parseReason(String text)
	{
		return Reason.get(text);
	}

	private List<String> parseDescription(String text)
	{
		String[] descriptions = text.split(",");
		List<String> result = new ArrayList<String>();
		for (String description : descriptions) {
			description = description.trim();
			if (description.charAt(0) == '\'' && description.charAt(description.length() - 1) == '\'') {
				description = description.substring(1, description.length() - 1);
			}
			result.add(description.trim());
		}
		return result;
	}
	
	private Argument parseArgument(String text)
	{
		Argument argument = new Argument();
		StringBuilder buffer = new StringBuilder();
		int i = 0;

		while (i < text.length() && ! Character.isWhitespace(text.charAt(i))) {
			buffer.append(text.charAt(i));
			i++;
		}
		argument.setStatus(parseStatus(buffer.toString().trim()));
		if (i == text.length()) {
			return argument;
		}
		buffer.setLength(0);
		
		
		while (Character.isWhitespace(text.charAt(i))) {
			i++;
		}
		if (text.charAt(i) != 'b' || text.charAt(i + 1) != 'y') {
			throw new IllegalArgumentException("Missing delimiter between status and reason");
		} else {
			i += 2;
		}
		while (Character.isWhitespace(text.charAt(i))) {
			i++;
		}
		
		while (i < text.length() && text.charAt(i) != '(') {
			buffer.append(text.charAt(i));
			i++;
		}
		argument.setReason(parseReason(buffer.toString().trim()));
		if (i == text.length()) {
			return argument;
		} else {
			i++;
		}
		buffer.setLength(0);
		
		while (text.charAt(i) != ')') {
			buffer.append(text.charAt(i));
			i++;
		}
		argument.setDescriptions(parseDescription(buffer.toString().trim()));
		return argument;
	}
	
	public List<Argument> parse(String text)
	{
		List<Argument> arguments = new ArrayList<Argument>();
		StringBuilder buffer = new StringBuilder();
		int openBrackets = 0;
		
		for (int i = 0; i < text.length(); i++) {
			if (openBrackets < 0) {
				throw new IllegalArgumentException("Error parsing string: unbalanced brackets");
			}
			char c = text.charAt(i); 
			if (c != ',') {
				buffer.append(c);
				if (c == '(') {
					openBrackets++;
				} else if (c == ')') {
					openBrackets--;
				}
			} else {
				if (openBrackets == 0) {
					arguments.add(parseArgument(buffer.toString().trim()));
					buffer.setLength(0);
				} else {
					buffer.append(c);
				}
			}
		}
		
		if (openBrackets != 0) {
			throw new IllegalArgumentException("Error parsing string: unbalanced brackets");
		}
		
		if (buffer.length() != 0) {
			parseArgument(buffer.toString().trim());
			arguments.add(parseArgument(buffer.toString().trim()));
		}
		
		return arguments;
	}
}
