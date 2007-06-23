package org.galeas.TipsterDigester;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.ExtendedBaseRules;
import org.galeas.utils.RepairXMLDocument;
import org.htmlparser.util.ParserException;
import org.xml.sax.SAXException;

public class TipsterDigester {
	
	static Documents doc;
	//private RepairXMLDocument rep = new RepairXMLDocument();
	
	public TipsterDigester(String fileString) throws IOException, SAXException, ParserException {
		
		File file = new File(fileString);
							
		// Create a temporal File with the valid XMLRoot TAGs	
		File temporalFile = repairDocument(file);
		
		// Parse the File
		digest(temporalFile);
	}
	
	public TipsterDigester(File file) throws IOException, SAXException, ParserException {
			
		// Create a temporal File with the valid XMLRoot TAGs	
		File temporalFile = repairDocument(file);
		
		// Parse the File
		digest(temporalFile);
	}	
	
	
	private File repairDocument(File input) throws IOException, ParserException {
		
		// Get the filename Prefix
		String filename= input.getName();
		String prefixFilename = filename.substring(0,2);
		
		// Create the Temporal output file and its corresponding buffer
		File output = new File("C:\\TEMP\\temporalTipsterDoc.txt");
		BufferedWriter out = new BufferedWriter(new FileWriter(output));
		
		
		out.write("<?xml  version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>\n");
		
		
		
		// Add DTD
		String dtdName = "";
		String strDTD = "";
		String documentType = "";
		if(prefixFilename.equalsIgnoreCase("CR")) {
			dtdName = "CRH.DTD";
			documentType = "CR93";
		}
		else if(prefixFilename.equalsIgnoreCase("FR")) {
			dtdName = "FR94.DTD";
			documentType = "FR";
		}
		else if(prefixFilename.equalsIgnoreCase("FT")) {
			dtdName = "FT.DTD";
			documentType = "FT931";
		}
		else if(prefixFilename.equalsIgnoreCase("FB")) {
			dtdName = "FBIS.DTD";
			documentType = "FBIS";
		}
		else if(prefixFilename.equalsIgnoreCase("LA")) {
			dtdName = "LA.DTD";
			documentType = "LA";
		}
		File dtdFile = new File("C:\\TEMP\\TREC-DTDS\\"+dtdName);
		BufferedReader dtd = new BufferedReader(new FileReader(dtdFile));
        
		while ((strDTD = dtd.readLine()) != null) {
        	//Write the original content in the output buffer 
        	out.write(strDTD+"\n");
        }		
		

		
		
		// Add the initial XML-Root TAG to the output file
		out.write("<DOCUMENTS>\n");
        
		
		
		// READ THE INPUT FILE INTO THE OUTPUT BUFFER		
		// If the input file is a FBIS document, repair its consistency error (TAG Properties)
		// and writes the repaired file into the output buffer
		String str="";

		if(prefixFilename.equalsIgnoreCase("FB")) {
			RepairXMLDocument rep = new RepairXMLDocument();
			
			String reparedFile = rep.getRepairedDocument(input);
			
			//Write the original content in the output buffer 
			out.write(reparedFile);

		}
		// Standard input file will be directly writed into the output buffer
		else {
			
			BufferedReader in = new BufferedReader(new FileReader(input));
	        while ((str = in.readLine()) != null) {
	        	//Write the original content in the output buffer 
	        	out.write(str+"\n");
	        }
	        
	        // Close the input file
	        in.close();
		}
			
        
        // Add the final XML-Root TAG
        out.write("</DOCUMENTS>\n");
        
        // Close the output file
        out.close();	
        
        return output;
	}
   	
		
	public void digest( File file) throws IOException, SAXException {
 
		
		// Get the filename Prefix
		String filename= file.getName();
		String prefixFileName = filename.substring(0,2);		
		
		Digester digester = new Digester();
         digester.setRules( new ExtendedBaseRules() );
         digester.setValidating( true );
         
         digester.addObjectCreate( "DOCUMENTS", Documents.class );                  
         digester.addObjectCreate( "DOCUMENTS/DOC", DocumentItem.class );
         
         /* -------------------------------------------------------
          * Process the document number 
          * -------------------------------------------------------*/
         digester.addBeanPropertySetter( "DOCUMENTS/DOC/DOCNO", "docno" ); 
         
         
         /* -------------------------------------------------------
          * Process the document titles
          * -------------------------------------------------------*/          
         // Process the Congressional Record (CR) documents
         if(prefixFileName.compareToIgnoreCase("CR")==0) {                               
        	 System.out.println("CASE -> CR");
        	 digester.addBeanPropertySetter( "!DOCUMENTS/DOC/TEXT/TTL/*", "title" );          
         }
         // Process the Federal Register (FR94) documents
         else if(prefixFileName.compareToIgnoreCase("FR")==0) {
        	 System.out.println("CASE -> FR94");
        	 digester.addBeanPropertySetter( "!DOCUMENTS/DOC/TEXT/DOCTITLE/*", "title" );          	 
         }
         // Process the Financial Times (FT) documents
         else if(prefixFileName.compareToIgnoreCase("FT")==0) {
        	 System.out.println("CASE -> FT");
        	 digester.addBeanPropertySetter( "!DOCUMENTS/DOC/HEADLINE", "title" );                	 
         }
         // Process the Foreign Broadcasting Information Service (FBIS) documents
         else if(prefixFileName.compareToIgnoreCase("FB")==0) {
        	 System.out.println("CASE -> FBIS");
        	 digester.addBeanPropertySetter( "!DOCUMENTS/DOC/F/*", "title" ); 
             digester.addBeanPropertySetter( "!DOCUMENTS/DOC/TI", "title" );
             digester.addBeanPropertySetter( "!DOCUMENTS/DOC/H1/*", "title" );
             digester.addBeanPropertySetter( "!DOCUMENTS/DOC/H2/*", "title" );
             digester.addBeanPropertySetter( "!DOCUMENTS/DOC/H3/*", "title" );
             digester.addBeanPropertySetter( "!DOCUMENTS/DOC/H4/*", "title" );
             digester.addBeanPropertySetter( "!DOCUMENTS/DOC/H5/*", "title" );
             digester.addBeanPropertySetter( "!DOCUMENTS/DOC/H6/*", "title" );
             digester.addBeanPropertySetter( "!DOCUMENTS/DOC/H7/*", "title" );
             digester.addBeanPropertySetter( "!DOCUMENTS/DOC/H8/*", "title" );      	 
         }  
         // Process the Los Angeles Times (LA) documents
         else if(prefixFileName.compareToIgnoreCase("LA")==0) {
        	 System.out.println("CASE -> LA");
        	 digester.addBeanPropertySetter( "!DOCUMENTS/DOC/HEADLINE/*", "title" );          
             digester.addBeanPropertySetter( "!DOCUMENTS/DOC/BYLINE/*", "title" );                     	 
         }
         else {
        	 System.out.println("****** NOT Classified Document *******");
         }
        	 
       
         
         /* -------------------------------------------------------
          * Process the document text 
          * -------------------------------------------------------*/
         digester.addBeanPropertySetter( "!DOCUMENTS/DOC/*", "text" );
         
         
         digester.addSetNext( "DOCUMENTS/DOC", "addDocument" );
         
         
         /* Parse the document */
         doc = (Documents)digester.parse( file );
         
         
         // Delete the temporal File
         file.delete();         
   }
   

   public void printDocuments() {
	   doc.printDocuments();
   }
   

}