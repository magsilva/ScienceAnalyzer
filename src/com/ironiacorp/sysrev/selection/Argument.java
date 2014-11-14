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

public class Argument
{
	private Status status;
	
	private Reason reason;
	
	private List<String> descriptions;

	public Argument()
	{
		descriptions = new ArrayList<String>();
	}
	
	public Status getStatus()
	{
		return status;
	}

	public void setStatus(Status status)
	{
		this.status = status;
	}

	public String[] getDescriptions()
	{
		return descriptions.toArray(new String[descriptions.size()]);
	}

	public void addDescription(String description)
	{
		if (description == null) {
			throw new IllegalArgumentException(new NullPointerException());
		}
		
		descriptions.add(description);
	}

	public void setDescriptions(List<String> descriptions)
	{
		if (descriptions == null) {
			throw new IllegalArgumentException(new NullPointerException());
		}
		
		this.descriptions = descriptions;
	}
	
	public void setReason(Reason reason) {
		this.reason = reason;
	}

	public Reason getReason() {
		return reason;
	}
	
	
}
