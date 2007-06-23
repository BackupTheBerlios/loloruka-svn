package org.galeas.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import au.id.jericho.lib.html.Attributes;
import au.id.jericho.lib.html.OutputDocument;
import au.id.jericho.lib.html.Source;
import au.id.jericho.lib.html.StartTag;

public class RepairXMLDocument {
	
	public RepairXMLDocument() {				
		
    }

    public String getRepairedDocument(File file) throws FileNotFoundException, IOException  {
		
    	Source source = new Source(new FileInputStream(file));

		OutputDocument outputDocument = new OutputDocument(source);	
		
		List F_StartTags = source.findAllStartTags("F");		
		
		for (Iterator i = F_StartTags.iterator(); i.hasNext();) {
			
			StartTag startTag = (StartTag) i.next();
			
			Attributes attributes = startTag.getAttributes();
			
			String pValue = attributes.getValue("P");
			
			outputDocument.replace(startTag, "<F P=\""+pValue+"\">");
		}		
		
		return outputDocument.toString();
    }

}