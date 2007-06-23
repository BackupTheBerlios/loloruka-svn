package org.galeas.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.galeas.utils.ConfigurationManager;
import org.xml.sax.SAXException;

public class TestProperties {

	/**
	 * @param args
	 * @throws IOException
	 * @throws SAXException
	 */
	public static void main(String[] args) throws IOException, SAXException {

		ConfigurationManager configManager = new ConfigurationManager(
				"E:\\Galeas\\TREC_Evaluation\\conf\\properties.xml");
		Properties properties = configManager.getDefaultConfiguration();
		String topics_file = properties.getProperty("topics_file").trim();

		//String topics_file2 = "E:\\Galeas\\TREC_Evaluation\\topics\\TREC8_topics_401_450.xml";

		System.out.println(topics_file);
		//System.out.println(topics_file2);
		
		
		  BufferedReader in = new BufferedReader(new FileReader(topics_file));
		  String str; while ((str = in.readLine()) != null) {
		  System.out.println(str); } in.close();
		 
		// TopicsReader topicsReader = new TopicsReader(topics_file);
	}

}
