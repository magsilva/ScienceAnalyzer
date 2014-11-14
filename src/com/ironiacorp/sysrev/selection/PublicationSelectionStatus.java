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

import java.util.List;

public class PublicationSelectionStatus
{
	private List<Argument> arguments;
	
	public static List<Argument> parseStatus(String text)
	{
		StatusParser parser = new StatusParser();
		return parser.parse(text);
	}

	public PublicationSelectionStatus(String status)
	{
		this(parseStatus(status));
	}
	
	public PublicationSelectionStatus(List<Argument> arguments)
	{
		this.arguments = arguments;
	}
	
	public List<Argument> getArguments()
	{
		return arguments;
	}
	
	public void addArgument(Argument argument)
	{
		arguments.add(argument);
	}
	
	public Status getDecision()
	{
		try {
			Argument argument = arguments.get(arguments.size() - 1);
			return argument.getStatus();
		} catch (Exception e) {
			return Status.UNDEFINED;
		}
	}
	
	public Argument getDecisionArgument()
	{
		try {
			Argument argument = arguments.get(arguments.size() - 1);
			return argument;
		} catch (Exception e) {
			return null;
		}
	}
}
