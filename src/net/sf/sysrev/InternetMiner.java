/*
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Copyright (C) 2007 Marco Aurélio Graciotto Silva <magsilva@ironiacorp.com>
*/

package net.sf.sysrev;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.prefs.Preferences;

import net.sf.sysrev.engines.ieee.IEEEMiner;
import net.sf.sysrev.types.Publication;


public class InternetMiner implements Miner
{
	private List<Miner> miners;
	
	public InternetMiner()
	{
		miners = new ArrayList<Miner>();
		miners.add(new IEEEMiner());
	}
	
	public Set<Publication> getPublications(String searchString)
	{
		Set<Publication> pubs = new TreeSet<Publication>();
		Iterator<Miner> i = miners.iterator();
		while (i.hasNext()) {
			Miner miner = i.next();
			Set<Publication> pubsSubset = miner.getPublications(searchString);
			pubs.addAll(pubsSubset);
		}
		return pubs;
	}

	public Set<Publication> getPublications(String searchString, Preferences preferences)
	{
		return getPublications(searchString);
	}
}
