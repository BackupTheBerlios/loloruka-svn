package org.galeas.TipsterParser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.Segment;
import au.id.jericho.lib.html.Source;

public class TipsterParser {

	private List documentsList = new ArrayList();
	private Source source;
	private File file;

	
//	public void TipsterParser() {
//
//	}

	
	public List getDocumentList() {
		return documentsList;
	}
	
	public void  printDocumentList() {
		System.out.println(documentListToBuffer().toString());
	}
	
	public StringBuffer documentListToBuffer() {
		
		StringBuffer documentString = new StringBuffer();
		
		documentString.append("\n\n\n################### NEWFILE: "+file.getName()+" ###################\n");
		
		Iterator it = documentsList.iterator();
		while (it.hasNext()) {
			TipsterDocument td = (TipsterDocument)it.next();
			documentString.append("--------------------------------\n");
			documentString.append("docno:"+td.getDocno()+"\n");
			documentString.append("title:"+td.getTitle()+"\n");
			documentString.append("text:"+td.getText()+"\n");			
		}		
		
		return documentString;
	}
	
	
	public void documentListToFile(File outputFile) throws IOException {
		
        BufferedWriter out = new BufferedWriter(new FileWriter(outputFile,true));
        out.write(documentListToBuffer().toString());
        out.close();
	}
	
	
	
	public void parseFile(File file) throws FileNotFoundException, IOException {

		
		this.file = file;
		
		
		String fileName = file.getName();
		String fileNamePrefix = fileName.substring(0, 2);
		
		// Get the Regular Expresion for the actual document
		String[] docRegExp 	= getDocRegExp(fileNamePrefix);
		String regEx_Title 	= docRegExp[0];
		String regEx_Text 	= docRegExp[1];	
		
		if(regEx_Title != null && regEx_Text != null) {

			source = new Source(new FileInputStream(file));
			source.fullSequentialParse();

			List elementList = source.findAllElements("DOC");

			
			System.out.print("Processing File: "+file.getName());				
			System.out.print("\tcontaining ["+elementList.size()+"] xml-documents");
			System.out.println("\twith RegularExp:"+regEx_Title+"#"+regEx_Text);
			
			
			
			for (Iterator i = elementList.iterator(); i.hasNext();) {
				
				// Actual DOC Element
				Element element = (Element) i.next();
				
				// DOC Element positions
				int docBegin = element.getBegin();
				int docEnd = element.getEnd();
				
				// Create the DOC Segment
				Segment docSegment = new Segment(source, docBegin, docEnd);
				
						
				// Initialize the docno, title and text variables
				String docno = "";
				StringBuffer title = new StringBuffer();
				StringBuffer text = new StringBuffer();
							
				// Get ALL Elements (TAGs) of the Document
				List elementsOfDoc = docSegment.findAllElements();
				Iterator it = elementsOfDoc.iterator();
				
				while (it.hasNext()) {

					Element actualElement = (Element) it.next();

					// Get DOCNO
					if (actualElement.getName().equalsIgnoreCase("DOCNO")) {
						docno = actualElement.getContent().extractText();					
					}								
					// Get TITLE
					else if (actualElement.getName().matches(regEx_Title)) {
						title.append(actualElement.getContent().extractText());
					}
					// Get TEXT
					else if (actualElement.getName().matches(regEx_Text)) {
						text.append(actualElement.getContent().extractText());
					}
				}
				
				// Save the values in a TipsterDocument
				TipsterDocument tipsterDoc = new TipsterDocument();
				tipsterDoc.setDocno(docno);
				tipsterDoc.setTitle(title.toString());
				tipsterDoc.setText(text.toString());
				
				
				// Add the Tipster Document to the documents list
				documentsList.add(tipsterDoc);	
			}			
			
		}
		else {
			System.out.println("Skipping File "+fileName+" .....was not parsed");			
		}
		

		
		
	}

	
	private String[] getDocRegExp(String fileNamePrefix) {

		
		String[] selectedRegExp = {null,null};

		if (fileNamePrefix.equalsIgnoreCase("CR")) {
			selectedRegExp[0] = "ttl";
			selectedRegExp[1] = "doc";			
		} else if (fileNamePrefix.equalsIgnoreCase("FR")) {
			selectedRegExp[0] = "doctitle";
			selectedRegExp[1] = "doc";
		} else if (fileNamePrefix.equalsIgnoreCase("FT")) {
			selectedRegExp[0] = "headline";
			selectedRegExp[1] = "doc";
		} else if (fileNamePrefix.equalsIgnoreCase("FB")) {
			selectedRegExp[0] = "f|h\\d";
			selectedRegExp[1] = "doc";
		} else if (fileNamePrefix.equalsIgnoreCase("LA")) {
			selectedRegExp[0] = "headline|byline";
			selectedRegExp[1] = "doc";
		}
		
		return selectedRegExp;
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		TipsterParser tp = new TipsterParser();
		tp.parseFile(new File("C:\\TEMP\\testTipster\\test"));
	}

}
