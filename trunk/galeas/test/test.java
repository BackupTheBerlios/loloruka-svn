package org.galeas.test;

import au.id.jericho.lib.html.*;

import java.util.*;
import java.io.*;

public class test {

	private static Source source;
	private static String fbisTITLE_RE = "f|h\\d";
	private static String fbisTEXT_RE = "doc";
	
	public static void main(String[] args) throws Exception {

		File file = new File(args[0]);
		
		source = new Source(new FileInputStream(file));
		source.fullSequentialParse();
		
		List elementList = source.findAllElements("DOC");
		
		
		for (Iterator i=elementList.iterator(); i.hasNext();) {
			Element element=(Element)i.next();
			
			int docBegin = element.getBegin();
			int docEnd	= element.getEnd();
			
			Segment docSegment = new Segment(source, docBegin, docEnd);
			System.out.println("----------->:"+element.getName());
			List elementsOfDoc = docSegment.findAllElements();
			Iterator it = elementsOfDoc.iterator();
			while(it.hasNext()) {
				
				Element actualElement = (Element)it.next();
				
				// Get DOCNO
				if(actualElement.getName().matches(fbisTITLE_RE)) {
					System.out.println("Title:"+actualElement.getContent().extractText());
				}
				// Get TITLE
				else if(actualElement.getName().matches(fbisTEXT_RE)) {
					System.out.println("Text:"+actualElement.getContent().extractText());
				}				
			}			
		}

	}	
	
}
