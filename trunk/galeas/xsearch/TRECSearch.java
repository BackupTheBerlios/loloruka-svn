package org.galeas.xsearch;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.lucene.queryParser.ParseException;
import org.galeas.trec.topics.trec8.TopicsReader;
import org.galeas.utils.ConfigurationManager;


public class TRECSearch {
	
//	private String[] topicIds = new String[] {
//			"401","402","403","404","405","406","407","408","409","410",
//			"411","412","413","414","415","416","417","418","419","420",
//			"421","422","423","424","425","426","427","428","429","430",
//			"431","432","433","434","435","436","437","438","439","440",
//			"441","442","443","444","445","446","447","448","449","450"};

	public String[] topicIds = new String[] {
			"406"}; //407, 430, 401, 435, 406, 410
	private NewSearch is;
	private String propertiesFile = "/home/patricio/TREC_Evaluation/conf/properties.xml";
	private static Properties properties;
	public static Logger logger = Logger.getRootLogger();
	
	public TRECSearch() throws IOException, ParseException {
		
		is = new NewSearch();	
		ConfigurationManager configManager = new ConfigurationManager(this.propertiesFile);
		properties = configManager.getDefaultConfiguration();
		
	}
	
	
	public void searchWithTopicID(int topicID, String searchIn) throws Exception  {
		
		
		String topicsFileString = properties.getProperty("topics_file").trim();
		TopicsReader topicsReader = new TopicsReader(topicsFileString);
		
		String searchString = "";
		if(searchIn.compareToIgnoreCase("title")==0) {
			searchString = topicsReader.getTopicTitle(topicID);
		}
		else if(searchIn.compareToIgnoreCase("desc")==0) {
			searchString = topicsReader.getTopicDesc(topicID);
		}
		else if(searchIn.compareToIgnoreCase("titledesc")==0) {
			searchString = topicsReader.getTopicTitle(topicID) + topicsReader.getTopicDesc(topicID);
		}
		
//		System.out.println("Topic Search String:"+searchString);
		
		is.search(searchString, topicID);	
		
	}
	
	public void searchWithSearchString(String searchString, int topicID) throws Exception, ParseException {
		
		is.search(searchString, topicID);
	}
	
	
	public String getResults(String searchType) throws Exception {
				
		String results = "";
		if(searchType.equalsIgnoreCase("standard")) {
			results = is.getInitialHits();
		}
		else if(searchType.equalsIgnoreCase("dispersion")) {
			
			results = is.getDispersionHits();
		}
		else if(searchType.equalsIgnoreCase("dispersionplus")) {
			
			results = is.getDispersionHitsPlus();
		}		
		/*
		else if(searchType.equalsIgnoreCase("expanded")) {
			String[] expandedQueryTerms = is.getExpandedQueryArray();
			
		}	
		*/	
		return results;
	}
	
	
	
	public int getResultsLenght() {
		return is.getInitialHitsLenght();
	}
	
	
	public QueryStatsOnResults getReducedQuery() throws IOException {
		
		return is.getReducedQuery();
		
	}
	
	public void printExpandedQuery() {
		is.printExpandedQuery();
	}
	
	
	public String[] getExpandedQueryArray() {
		return is.getExpandedQueryArray();
	}
	
	
	public String getExpandedQueryString() {
		return is.getExpandedQueryString();
	}
	
	
	public String getTopicQuery() {
		return is.getUserQuery();
	}
	

	
	

	
	
	public static void main(String args[]) throws Exception {
		
		// Logger Definition
		SimpleLayout layout = new SimpleLayout();
		ConsoleAppender consoleAppender = new ConsoleAppender(layout);
		logger.addAppender(consoleAppender);
		FileAppender fileAppender = new FileAppender(layout,
				"/home/patricio/TREC_Evaluation/logs/TREC-Runs.log", false);
		logger.addAppender(fileAppender);
		// ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF:
		logger.setLevel(Level.WARN);		
				
		
		TRECSearch trecSearch = new TRECSearch();
		
		
	
		/* Get the dispersion ranking model and its weighting scheme */			
		String dispersion_model = properties.getProperty("dispersion_model");
		String  dispersion_rank_weight = properties.getProperty("dispersion_rank_weight").trim();
		
		/* Get the results output files 
		 * Standard & Extended output */			
		String StandardResultsFile = properties.getProperty("standard_results_file").trim();
		BufferedWriter standardOut = new BufferedWriter(new FileWriter(StandardResultsFile));		
		        
		String ExtendedResultsFile = properties.getProperty("dispersion_results_file").trim() + "_w_" + dispersion_rank_weight;
		BufferedWriter extendedOut = new BufferedWriter(new FileWriter(ExtendedResultsFile));      		 
		
		List topicIdsList = Arrays.asList(trecSearch.topicIds);
		Iterator it = topicIdsList.iterator();
		while(it.hasNext()) {
			int topicID = Integer.valueOf((String)it.next()).intValue();
			
			trecSearch.searchWithTopicID(topicID,"title");
			
			String topicStandardResults = trecSearch.getResults("standard");
			
			System.out.println("The TFIDF Results :");
//			System.out.print(topicStandardResults);
			standardOut.write(topicStandardResults);
			
			String topicDispersionResults = trecSearch.getResults("dispersionplus");
			
			System.out.println("---------------------------------------------");	
			System.out.println("The Dispersion Results : "+dispersion_model+" ("+dispersion_rank_weight+")");
//			System.out.print(topicDispersionResults);			
			extendedOut.write(topicDispersionResults);
			
		}
		
		standardOut.close();
		extendedOut.close();
		
		
		/* print extended query*/
		trecSearch.printExpandedQuery();
		
		
	}	


	
}
