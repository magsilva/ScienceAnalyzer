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

Copyright (C) 2007 Apache Software Foundation (ASF).
*/

package net.sf.sysrev.searchengine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

/**
 * How to send a request via proxy using {@link HttpClient HttpClient}.
 *
 * @author Roland Weber
 */
public class HttpEngine
{
	public static void main(String[] args) throws Exception
	{	
		// Create and initialize HTTP parameters
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "UTF-8");
		HttpProtocolParams.setUseExpectContinue(params, true);
		
		// Create and initialize scheme registry 
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

		// Create an HttpClient with the ThreadSafeClientConnManager.
		// This connection manager must be used if more than one thread will
		// be using the HttpClient.
		ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
		HttpClient httpClient = new DefaultHttpClient(cm, params);


		
		// create an array of URIs to perform GETs on
		String[] urisToGet = {
			"http://jakarta.apache.org/",
			"http://jakarta.apache.org/commons/",
			"http://jakarta.apache.org/commons/httpclient/",
			"http://svn.apache.org/viewvc/jakarta/httpcomponents/"
		};

		// create a thread for each URI
		List<Runnable> threads = new ArrayList<Runnable>(urisToGet.length);
		for (String uri : urisToGet) {
			HttpGet httpget = new HttpGet(uri);
			threads.add(new GetThread(httpClient, httpget));
		}

		// start the threads
		Iterator<Runnable> i = threads.iterator();
		while (i.hasNext()) {
			Runnable thread  = i.next();
			thread.run();
		}
	}


	/**
	* A thread that performs a GET.
	*/
	static class GetThread extends Thread
	{

		private HttpClient client;

		private HttpContext context;

		private HttpGet httpget;

		public GetThread(HttpClient httpClient, HttpGet httpget)
		{
			this.client = httpClient;
			this.context = new BasicHttpContext();
			this.httpget = httpget;
		}

		/**
		 * Executes the GetMethod and prints some status information.
		 */
		public void run()
		{
			HttpEntity entity = null;
			HttpResponse rsp = null;
			
			try {
				rsp = client.execute(httpget, context);
				// if (rsp.getStatusLine()) {}
				
				entity = rsp.getEntity();

				// If the response does not enclose an entity, there is no need
				// to bother about connection release
				if (entity != null) {
					/*
					if (entity != null) {
						byte[] bytes = EntityUtils.toByteArray(entity);
					}
					*/
					BufferedReader reader = null;
					
					try {
						reader = new BufferedReader(new InputStreamReader(entity.getContent()));
					} finally {
						// Closing the input stream will trigger connection release
						try {
							reader.close();
						} catch (IOException e) {
						}
					}
				}
				entity.consumeContent(); // release connection gracefully
			} catch (IOException e) {
				// In case of an IOException the connection will be released
				// back to the connection manager automatically
			} catch (RuntimeException ex) {
				// In case of an unexpected exception you may want to abort
				// the HTTP request in order to shut down the underlying 
				// connection and release it back to the connection manager.
				httpget.abort();
			} 
		}
	}
}
