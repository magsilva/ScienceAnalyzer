package net.sf.sysrev.methods;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ContractionReplacer implements ComplexFilter
{
	private static Map<String, String> map = new HashMap<String, String>();
	
	private ComplexFilter wordSplitter; 
	
	static {
		map.put("I'm", "I am");
	}
	
	public void setWordSplitter(WordSpliter splitter)
	{
		wordSplitter = splitter;
	}
	
	@Override
	public String[] filter(String s)
	{
		StringBuilder sb = new StringBuilder(s);
		
		Iterator<String> i = map.keySet().iterator();
		while (i.hasNext()) {
			String key = i.next();
			for (int strIndex = sb.indexOf(key); strIndex != -1; strIndex = sb.indexOf(key)) {
				String value = map.get(key);
				sb.replace(strIndex, key.length(), value);
			}
		}
		
		return wordSplitter.filter(sb.toString());
	}
}
