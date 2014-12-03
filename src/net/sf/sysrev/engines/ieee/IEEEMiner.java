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

package net.sf.sysrev.engines.ieee;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;
import java.util.prefs.Preferences;

import net.sf.sysrev.Miner;
import net.sf.sysrev.types.Publication;


public class IEEEMiner implements Miner
{

	public Set<Publication> getPublications(String searchString)
	{
		IEEESearcher op1 = new IEEESearcher(searchString);
		// op1.run();
		// op1.getResult();
		
		IEEEMetadataRetriever op2 = new IEEEMetadataRetriever();
		// op2.run();
		// op2.getResult();

		
		IEEEDataRetriever op3 = new IEEEDataRetriever(new File("/tmp/sysrev-get-55726.html"));
		// op3.run();
		// op3.getResult();
		
		return null;
	}

	public Set<Publication> getPublications(String searchString, Preferences preferences)
	{
		return getPublications(searchString);
	}

}
