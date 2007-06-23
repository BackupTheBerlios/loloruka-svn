package org.galeas.config;


import java.io.IOException;

public class SearchProperties {

	/**
	 * @param index_directory
	 * Constructor defines the index directory
	 * @throws IOException 
	 */
	public SearchProperties() throws IOException {
	    String propertiesFolder = "E:\\lucene\\repository\\src\\galeas\\org\\galeas\\config\\";
		String propertiesFile = "search.properties";
	  
		java.util.Properties searchProperties = new java.util.Properties();
	    java.io.FileInputStream fis = new java.io.FileInputStream(new java.io.File(propertiesFolder+propertiesFile));
	    
	    searchProperties.load(fis);
	    
	    System.out.println(searchProperties);		
	}	
	

   	

}
