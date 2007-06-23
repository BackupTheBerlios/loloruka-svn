package org.galeas.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class ConfigurationManager {

	
	private String configurationFile;
	
	public ConfigurationManager(String configurationFile) {
		this.configurationFile = configurationFile;
	}
	
	
	/**
	 * Return the program default properties
	 * @throws IOException
	 */
	public Properties getDefaultConfiguration() throws IOException {
		
	    Properties prop = new Properties();	    
	    FileInputStream fis = new FileInputStream(configurationFile);
	    prop.loadFromXML(fis);		
	    fis.close();
	    
	    return prop;	    
	}	
	
}
