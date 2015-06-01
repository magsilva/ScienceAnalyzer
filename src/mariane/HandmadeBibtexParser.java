package mariane;

import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;

import net.sf.jabref.BibtexDatabase;
import net.sf.jabref.BibtexEntry;
import lode.model.publication.Collection;
import lode.model.publication.EventArticle;
import lode.model.publication.PageSequence;
import lode.model.publication.Person;

public class HandmadeBibtexParser {
	private String PreferredLanguage;
	private Boolean IgnoreErrors;
	
	public String getPreferredLanguage() {
		return PreferredLanguage;
	}

	public void setPreferredLanguage(String preferredLanguage) {
		PreferredLanguage = preferredLanguage;
	}

	public Boolean getIgnoreErrors() {
		return IgnoreErrors;
	}

	public void setIgnoreErrors(Boolean ignoreErrors) {
		IgnoreErrors = ignoreErrors;
	}

	public Collection parse(File bibFile, BufferedReader reader){
		PrepareDatabase data = new PrepareDatabase(bibFile);
		
		BibtexDatabase database = data.getDatabase();
        
		Collection collection = new Collection();
		
		for (BibtexEntry entry : database.getEntries()) {
            if (!entry.getField("bibtexkey").startsWith("proceedings") && !entry.getField("bibtexkey").startsWith("journal")) {
            	EventArticle publication = getPublication(entry);
            	
            	if(publication != null){
            		collection.add(publication);
            	}
            }
        }
        return collection;
	}

	private EventArticle getPublication(BibtexEntry entry) {
		EventArticle pub = new EventArticle();
		
		/* auhtor */
		String a = entry.getField("author");
		String[] splitAuhtors = a.split(" and ");
		
		
		for (int i = 0; i < splitAuhtors.length; i++) {
			String name = getSobrenome(splitAuhtors[i].toLowerCase());
			Person person = new Person();
			person.setName(name);
			pub.addAuthors(person);
		}
		
		
		/* referenceID*/
		String referenceID = entry.getCiteKey();
		pub.setReferenceId(referenceID);
		
		
		/* title */
		String title = entry.getField("title");
		pub.setTitle(title);
		
		/* abstract */
		String abs = entry.getField("abstract");
		pub.setAbstractText(abs);
		
		/* year */
		String year = entry.getField("year");
		pub.setYear(Integer.parseInt(year));
		
		/* pages */
		String pag = entry.getField("pages");
		int[] pages = pages(pag);
		
		if(pages != null){
			PageSequence pS = new PageSequence(pages[0], pages[1]);
			pub.setPages(pS);
		}

		/* volume */
		String volume = entry.getField("volume");
		
		if(volume != null){
			pub.setVolume(volume);
		}
		pub.setVolume(volume);
		
		/* DOI */
		String doi = entry.getField("doi");
		pub.setDoi(doi);
		
		return pub;
	}
	
    private String getSobrenome(String name) {
    	String rt = "";
    	if(name.contains("ç")){
    		name = name.replace("ç", "c");
    	}
    		String n[] = name.split(" ");
    	
    		if(n.length > 1 && n[1].length() > 1){
    			Character aux = n[1].charAt(0);
    			rt = rt.concat(n[0]).concat(" ").concat(aux.toString());
    		}else{
    			rt = rt.concat(n[0]).concat(" ");
    		}
    	
    	
	return rt.replace(".", "");
	}

	private int[] pages(String field) {
        int[] pages = new int[2];
        if (field != null) {
            String p[] = new String[2];

            if (field.contains("--")) {
                p = field.split("--");
            } else if (field.contains("-")) {
                p = field.split("-");
            }
            // usando try/catch pois tem alguns campos das bases scopus que vem com letras no campo page.
            try{
                pages[0] = Integer.parseInt(p[0]);
                pages[1] = Integer.parseInt(p[1]);
            }catch(Exception e){
                return null;
            }
        return pages;
        }
       return pages;
    }
}
