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


package com.ironiacorp.sysrev.lint;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lode.model.publication.Event;
import lode.model.publication.EventArticle;
import lode.model.publication.Journal;
import lode.model.publication.JournalArticle;
import lode.model.publication.Publication;

public class PublisherMerger
{
	private static Map<String, String> synonymous;
	static {
		synonymous = new HashMap<String, String>();
		synonymous.put("ACM Annual Conference", "ACM Annual Conference");
		synonymous.put("ACM Transactions on Computer-Human Interaction (TOCHI)", "Transactions on Computer-Human Interaction");
		synonymous.put("ACM Transactions on Applied Perception (TAP)", "Transactions on Applied Perception");
		synonymous.put("Annual Conference on Innovation and Technology in Computer Science Education", "Conference on Innovation and Technology in Computer Science Education");
		synonymous.put("Computers in Entertainment (CIE)", "Computers in Entertainment");
		synonymous.put("Conference on Computer Support for Collaborative Learning: Foundations for a CSCL Community", "Conference on Computer Support for Collaborative Learning");
		synonymous.put("Conference on Human Factors in Computing Systems -- Extended abstracts on Human factors in computing systems", "Conference on Human Factors in Computing Systems");
		synonymous.put("Conference on Human Factors in Computing Systems -- CHI Extended Abstracts on Human Factors in Computing Systems", "Conference on Human Factors in Computing Systems");
		synonymous.put("Conference on Interaction Design and Children: building a community", "Conference on Interaction Design and Children");
		synonymous.put("Conference on the Teaching of Computing and Conference on Integrating Technology into Computer Science Education: Changing the delivery of computer science education", "Conference on the Teaching of Computing and Conference on Integrating Technology into Computer Science Education");
		synonymous.put("European Conference on Cognitive ergonomics: invent! explore!", "European Conference on Cognitive ergonomics");
		synonymous.put("Hawaii International Conference on System Sciences (HICSS-34)", "Hawaii International Conference on System Sciences");
		synonymous.put("Journal on Educational Resources in Computing (JERIC)", "Journal on Educational Resources in Computing");
		synonymous.put("IEEE Trans. Learn. Technol.", "Transactions on Learning Technologies");
		synonymous.put("International Conference on Computational Linguistics and Annual meeting of the Association for Computational Linguistics", "International Conference on Computational Linguistics");
		synonymous.put("International Conference on Computer Graphics and Interactive Techniques -- ACM SIGGRAPH 2006 Educators program", "International Conference on Computer Graphics and Interactive Techniques");
		synonymous.put("International Conference on Computer Graphics and Interactive Techniques -- ACM SIGGRAPH 2007 educators program", "International Conference on Computer Graphics and Interactive Techniques");
		synonymous.put("International Conference on Computer Graphics and Interactive Techniques -- SIGGRAPH 2009: Talks", "International Conference on Computer Graphics and Interactive Techniques");
		synonymous.put("International Conference on Interaction Sciences: Information Technology, Culture and Human", "International Conference on Interaction Sciences"); 
		synonymous.put("International Conference On The Foundations Of Digital Games -- Intelligent Narrative Technologies III Workshop", "Intelligent Narrative Technologies Workshop");
		synonymous.put("International Symposium on Computer Architecture -- Workshop on Computer architecture Education", "Workshop on Computer architecture Education");
		synonymous.put("International Workshop on Contextualized Attention Metadata: Collecting, Managing and Exploiting of Rich Usage Information", "International Workshop on Contextualized Attention Metadata");
		synonymous.put("Nordic Conference on Human-Computer Interaction: changing roles", "Nordic Conference on Human-Computer Interaction");
		synonymous.put("SIGCSE Symposium on Computer Science Education", "SIGCSE Technical Symposium on Computer Science Education");
		synonymous.put("SIGCHI New Zealand Chapter's International Conference on Human-Computer Interaction: Design Centered HCI", "New Zealand International Conference on Human-Computer Interaction");
		synonymous.put("Annual ACM SIGUCCS fall conference: moving mountains, blazing trails", "Annual ACM SIGUCCS fall conference");
	}
	
	private static Pattern[] commonPrefixes = {
		Pattern.compile("^Proceedings of the \\d{4} \\d+\\D{2}", Pattern.CASE_INSENSITIVE),
		Pattern.compile("^Proceedings of the \\d{4}", Pattern.CASE_INSENSITIVE),
		Pattern.compile("^Proceedings of the", Pattern.CASE_INSENSITIVE),
		Pattern.compile("^ACM SIGGRAPH --", Pattern.CASE_INSENSITIVE),
		Pattern.compile("^ACM SIGGRAPH", Pattern.CASE_INSENSITIVE),
		Pattern.compile("^ACM/IEEE-CS", Pattern.CASE_INSENSITIVE),
		Pattern.compile("^ACM/IEEE", Pattern.CASE_INSENSITIVE),
		Pattern.compile("^SIGCSE/SIGCUE", Pattern.CASE_INSENSITIVE),
		Pattern.compile("^ACM", Pattern.CASE_INSENSITIVE),
		Pattern.compile("^IEEE", Pattern.CASE_INSENSITIVE),
		Pattern.compile("^\\d{4}", Pattern.CASE_INSENSITIVE),
		Pattern.compile("^\\d+\\D{2} ", Pattern.CASE_INSENSITIVE),
		Pattern.compile("^spring", Pattern.CASE_INSENSITIVE),
		Pattern.compile("^summer", Pattern.CASE_INSENSITIVE),
		Pattern.compile("^winter", Pattern.CASE_INSENSITIVE),
		Pattern.compile("^autumn", Pattern.CASE_INSENSITIVE),
		Pattern.compile("^first", Pattern.CASE_INSENSITIVE),
		Pattern.compile("^second", Pattern.CASE_INSENSITIVE),
		Pattern.compile("^third", Pattern.CASE_INSENSITIVE),
		Pattern.compile("^fourth", Pattern.CASE_INSENSITIVE),
		Pattern.compile("^fifth", Pattern.CASE_INSENSITIVE),
		Pattern.compile("^sixth", Pattern.CASE_INSENSITIVE),
		Pattern.compile("^seventh", Pattern.CASE_INSENSITIVE),
		Pattern.compile("^eighth", Pattern.CASE_INSENSITIVE),
		Pattern.compile("^ninth", Pattern.CASE_INSENSITIVE),
		Pattern.compile("^Annual", Pattern.CASE_INSENSITIVE),
	};
	

	private String findUniqueName_HardcodedSynonymous(String name)
	{
		Iterator<String> hardcodedSubstitutions = synonymous.keySet().iterator();
		while (hardcodedSubstitutions.hasNext()) {
			String conferenceName = hardcodedSubstitutions.next();
			Pattern pattern = Pattern.compile(Pattern.quote(conferenceName), Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(name);
			if (matcher.find()) {
				return synonymous.get(conferenceName);
			}
		}
		
		return null;
	}
	
	
	private String cleanName(final String name)
	{
		boolean runOneMore = true;
		String result = name;
		while (runOneMore) {
			boolean hasChanged = false;
			for (Pattern pattern : commonPrefixes) {
				Matcher matcher = pattern.matcher(result);
				if (matcher.find()) {
					result = matcher.replaceFirst("");
					result = result.trim();
					hasChanged = true;
				} 
			}
			if (hasChanged) {
				runOneMore = true;
			} else {
				runOneMore = false;
			}
		}
		
		return result;
	}
	
	
	private String findUniqueName(final String name)
	{
		String result = null;
		
		result = findUniqueName_HardcodedSynonymous(name);
		if (result != null) {
			return result;
		}
		
		result = cleanName(name);
		return result;
	}
	
	public void merge(Publication pub)
	{
		String booktitle = null;
		String result = booktitle;
		
		if (pub instanceof JournalArticle) {
			JournalArticle article = (JournalArticle) pub;
			Journal journal = article.getJournal();
			booktitle = journal.getName();
		}
		if (pub instanceof EventArticle) {
			EventArticle article = (EventArticle) pub;
			Event event = article.getEvent();
			booktitle = event.getName();
		}
		
		if (booktitle == null) {
			throw new UnsupportedOperationException("Publication has no mergeable field");
		}
		
		result = findUniqueName(booktitle);
		System.out.println(result.toLowerCase());
	}
}
