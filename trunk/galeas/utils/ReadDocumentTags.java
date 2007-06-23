package org.galeas.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import au.id.jericho.lib.html.Source;
import au.id.jericho.lib.html.StartTag;

public class ReadDocumentTags {
	
	
	public void getTagContent(File file) throws FileNotFoundException, IOException {
		Source source = new Source(new FileInputStream(file));

		
		
		List DOC_StartTags = source.findAllStartTags("DOC");		
		
		for (Iterator i = DOC_StartTags.iterator(); i.hasNext();) {
			
			StartTag startTag = (StartTag) i.next();
			
			List childrenTags = startTag.getChildElements();
			
			for (Iterator j = childrenTags.iterator(); j.hasNext();) {
				StartTag childTag = (StartTag) j.next();
				if(childTag.getName().equalsIgnoreCase("DOCNO")) {
					System.out.println(childTag.toString());
				}
			}
			
		}		
			
	}
	
	

	
	
}

