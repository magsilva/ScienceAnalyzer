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

public enum Status 
{
	UNDEFINED(""),
	FOUND("found"),
	PENDING("pending"),
	SELECTED("selected"),
	EXCLUDED("excluded");
	
	private String identifier;
	
	Status(String identifier)
	{
		this.identifier = identifier;
	}
	
	public static Status get(String identifier)
	{
		if (identifier == null || identifier.trim().isEmpty()) {
			return UNDEFINED;
		}
		
		if (identifier.equalsIgnoreCase(FOUND.identifier)) {
			return FOUND;
		} else if (identifier.equalsIgnoreCase(SELECTED.identifier)) {
			return SELECTED;
		} else if (identifier.equalsIgnoreCase(EXCLUDED.identifier)) {
			return EXCLUDED;
		} else if (identifier.equalsIgnoreCase(PENDING.identifier)) {
			return PENDING;
		} else {
			throw new IllegalArgumentException("Invalid status identifier: " + identifier);
		}
    }
}
