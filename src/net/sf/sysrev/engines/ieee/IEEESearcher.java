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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;

import com.ironiacorp.commons.EncodingUtil;
import com.ironiacorp.commons.http.GetRequest3;
import com.ironiacorp.commons.http.HttpJobRunner;


public class IEEESearcher
{
	private HttpJobRunner runner;
	
	public static final String BASE_URL = "http://ieeexplore.ieee.org"; 

	public static final String SEARCH_URL = BASE_URL + "/search/searchresult.jsp?"; 

	public static final String QUERY_TEXT = "&queryText=";
	private String queryText;
	
	public static final String PUBLICATIONS = "&coll1=ieeejrns&coll2=ieejrns&coll3=ieeecnfs&coll4=ieecnfs&coll5=ieeestds&coll6=preprint&srchlist=publist"; 
	
	public static final String STANDARD_STATUS = "&std_status=all";
	
	public static final String DATE_RANGE = "&srchyr=allyr&py1=1950&py2=2007";
	
	public static final String RESULT_FORMAT = "&disp=cit";
	
	public static final String MAX_RESULTS = "&maxdoc=";
	public static final int MAX_RESULTS_LIMIT = 2000;
	private int maxResults;
	
	
	public static final String MAX_RESULTS_PER_PAGE = "&ResultCount=";
	public static final int MAX_RESULTS_PER_PAGE_LIMIT = 2000;
	private int maxResultsPerPage;
	
	public static final String RESULTS_SORT_FIELD = "&SortField=pyr";
	
	public static final String RESULTS_SORT_ORDER = "&SortOrder=desc";
	

	protected void processResult(File result)
	{
		System.out.println(result.toURI());
		
		// BASE_URL + <A href="/iel5/83/4130412/04130427.pdf?tp=&arnumber=4130427&isnumber=4130412">
	}
	
	public int getMaxResults()
	{
		return maxResults;
	}

	public String getMaxResultsArg()
	{
		return MAX_RESULTS + Integer.toString(maxResults); 
	}

	public void setMaxResults()
	{
		setMaxResults(MAX_RESULTS_LIMIT);
	}
	
	public void setMaxResults(int max)
	{
		if (max < 0) {
			throw new IllegalArgumentException("Cannot request a negative result count");
		}
		
		if (max > MAX_RESULTS_LIMIT) {
			max = MAX_RESULTS_LIMIT;
		}

		maxResults = max;
	}
	
	
	
	public int getMaxResultsPerPage()
	{
		return maxResultsPerPage;
	}
	
	public String getMaxResultsPerPageArg()
	{
		return MAX_RESULTS_PER_PAGE + maxResultsPerPage; 
	}
	
	public void setMaxResultsPerPage()
	{
		setMaxResultsPerPage(MAX_RESULTS_PER_PAGE_LIMIT);
	}
	
	public void setMaxResultsPerPage(int max)
	{
		if (max < 0) {
			throw new IllegalArgumentException("Cannot request a negative result count");
		}
		
		if (max > MAX_RESULTS_PER_PAGE_LIMIT) {
			max = MAX_RESULTS_PER_PAGE_LIMIT;
		}
		
		maxResultsPerPage = max;
	}
	
	
	
	public String getPublicationArg()
	{
		return PUBLICATIONS;
	}
	
	
	
	public String getQueryText()
	{
		return QUERY_TEXT + EncodingUtil.encodeToIEEE(queryText);
	}

	public String getQueryTextArg()
	{
		return QUERY_TEXT + EncodingUtil.encodeToIEEE(queryText);
	}
	
	public void setQueryText(String expression)
	{
		queryText = expression;
	}
	
	
	public String getDateRangeArg()
	{
		return DATE_RANGE;
	}
	
	public String getStandardStatusArg()
	{
		return STANDARD_STATUS;
	}
	
	public String getResultFormatArg()
	{
		return RESULT_FORMAT;
	}
	
	public String getResultSortArg()
	{
		return RESULTS_SORT_FIELD + RESULTS_SORT_ORDER;
	}
		
	public String compileSearchString()
	{
		return SEARCH_URL + getQueryTextArg() + getPublicationArg() + getStandardStatusArg() + getDateRangeArg() + getMaxResultsArg() + getMaxResultsPerPageArg() + getResultFormatArg() + getResultSortArg();
	}
	
	public List<URI> getURIs()
	{
		List<URI> uris = new ArrayList<URI>();
		try {
			uris.add(new URI(compileSearchString()));
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Invalid IEEE search URI", e);
		}
		
		return uris;
	}

	
	public IEEESearcher(String expression)
	{
		setQueryText(expression);
		setMaxResults();
		setMaxResultsPerPage();
	}
}
