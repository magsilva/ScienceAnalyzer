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

import com.ironiacorp.commons.http.HttpJobRunner;
import com.ironiacorp.commons.http.PostRequest3;

public class IEEEMetadataRetriever
{
	private HttpJobRunner runner;
	
	public static final String BASE_URL = "http://ieeexplore.ieee.org"; 

	public static final String FORM_ACTION = "/xpls/citationAct";

	public static final String CONTENT_NAME = "dlSelect";
	public static final String CONTENT_VALUE = "cite_abs";
	
	public static final String METADATA_FORMAT_NAME = "fileFormate";
	public static final String METADATA_FORMAT_VALUE = "BibTex";
	
	public static final String PUBLICATION_IDENTIFIER_NAME = "arnumber";
	public static final String PUBLICATION_IDENTIFIER_FORMAT = "";
	
	public static final String SUBMIT = "&submit=Download";
		

	protected Callable<File> getMethod(URI uri, NameValuePair[] data, HttpClient client)
	{
		return new PostRequest3(uri, data, client);
	}

	public NameValuePair[] getParameters()
	{
		int pubId = 4028642;
		NameValuePair[] data = {
			new NameValuePair(CONTENT_NAME, CONTENT_VALUE),
			new NameValuePair(METADATA_FORMAT_NAME, METADATA_FORMAT_VALUE),
			new NameValuePair(PUBLICATION_IDENTIFIER_NAME, "<arnumber>" + pubId + "</arnumber>")
		};
		//"<arnumber>4302684</arnumber><arnumber>1167159</arnumber><arnumber>1030811</arnumber><arnumber>794064</arnumber><arnumber>990766</arnumber><arnumber>847848</arnumber><arnumber>675755</arnumber><arnumber>1610546</arnumber><arnumber>1062678</arnumber><arnumber>1062169</arnumber><arnumber>1062134</arnumber><arnumber>4317840</arnumber><arnumber>4317810</arnumber><arnumber>4266041</arnumber><arnumber>4283061</arnumber><arnumber>4060716</arnumber><arnumber>4053943</arnumber><arnumber>1597708</arnumber><arnumber>1614504</arnumber><arnumber>1526375</arnumber><arnumber>1558125</arnumber><arnumber>1493720</arnumber><arnumber>1469420</arnumber><arnumber>1334958</arnumber><arnumber>1202253</arnumber>">
		return data;
	}

	public List<URI> getURIs()
	{
		List<URI> uris = new ArrayList<URI>();
		try {
			uris.add(new URI(BASE_URL + FORM_ACTION));
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Invalid IEEE search URI", e);
		}
		
		return uris;
	}

	public IEEEMetadataRetriever()
	{
	}
}
