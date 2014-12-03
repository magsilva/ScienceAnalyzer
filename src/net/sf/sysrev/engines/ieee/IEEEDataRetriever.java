package net.sf.sysrev.engines.ieee;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.ironiacorp.commons.http.HttpJobRunner;

public class IEEEDataRetriever
{
	private HttpJobRunner runner;
	
	public static final String BASE_URL = "http://ieeexplore.ieee.org"; 

	public static final String REGEXP = "/iel.*\\.pdf.*";
	
	private File searchResultPage;
	
	public List<URI> getURIs()
	{
		List<URI> uris = new ArrayList<URI>();
		try {
			URI uri = null;
			Parser parser = new Parser(searchResultPage.getAbsolutePath());
			NodeFilter linkFilter = new NodeClassFilter(LinkTag.class);
			NodeFilter pdfFilter = new NodeFilter()
			{
				public boolean accept(Node node)
                {
					LinkTag linkNode = (LinkTag) node;
					String pattern = "^" + Pattern.quote("file://localhost") + REGEXP;
					boolean result = linkNode.extractLink().matches(pattern);
					return result;
                }
			};
			NodeFilter filter = new AndFilter(linkFilter, pdfFilter);
			NodeList list = parser.extractAllNodesThatMatch(filter);
						
			for (int i = 0; i < list.size (); i++) {
				LinkTag linkNode = (LinkTag) list.elementAt(i);
				String link = linkNode.extractLink();
				String pattern = Pattern.quote("file://localhost");
				link = link.replaceFirst(pattern, BASE_URL);
				uri = new URI(link);
				uris.add(uri);
			}		
		} catch (ParserException e) {
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Invalid IEEE search URI", e);
		}
		return uris;
	}

	protected void processResult(File result)
	{
		System.out.println(result.toURI());
	}
	
	public IEEEDataRetriever(File searchResultPage)
	{
		this.searchResultPage = searchResultPage;
	}
}
