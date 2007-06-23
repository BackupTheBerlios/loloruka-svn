package org.galeas.xsearch;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.galeas.utils.ConfigurationManager;

public class TRECSearch3 {

	public static void main(String args[]) throws Exception {
		
		
		String[] topicIds = new String[] {
		"401","402","403","404","405","406","407","408","409","410",
		"411","412","413","414","415","416","417","418","419","420",
		"421","422","423","424","425","426","427","428","429","430",
		"431","432","433","434","435","436","437","438","439","440",
		"441","442","443","444","445","446","447","448","449","450"};
		
//		String[] topicIds = new String[] {
//		"448"}; //407, 430, 401, 435, 406, 416	
		
		
		String propertiesFile = "/home/patricio/TREC_Evaluation/conf/properties.xml";
		ConfigurationManager configManager = new ConfigurationManager(propertiesFile);
		Properties properties = configManager.getDefaultConfiguration();		
		
		
		TRECSearch trecSearch = new TRECSearch();
		
		
		/* Get the results output files 
		 * Standard & Extended output */			
		String StandardResultsFile = properties.getProperty("standard_results_file").trim();
		BufferedWriter standardOut = new BufferedWriter(new FileWriter(StandardResultsFile));		
		        
		String ExpandedResultsFile = properties.getProperty("expanded_results_file").trim();
		BufferedWriter expandedOut = new BufferedWriter(new FileWriter(ExpandedResultsFile));      		 
			
		List topicIdsList = Arrays.asList(topicIds);
		Iterator it = topicIdsList.iterator();
		while(it.hasNext()) {
			
			int topicID = Integer.valueOf((String)it.next()).intValue();
			
			System.out.println("*************** topicID:"+topicID+" *****************");
			
			trecSearch.searchWithTopicID(topicID,"title");
			
			int numberOfResultsDocuments = trecSearch.getResultsLenght();
			

			if(numberOfResultsDocuments>0) {
				
				/* ------------------------------------------------------------
				 * Calculate the TFIDF Reaults 
				 * ------------------------------------------------------------*/
				String topicStandardResults = trecSearch.getResults("standard");
//				System.out.println("The TFIDF Results :");
//				System.out.print(topicStandardResults);
				standardOut.write(topicStandardResults);			
				/* ------------------------------------------------------------*/
				
				
				/* ------------------------------------------------------------
				 * Calculate the Dispersion Ranking to obtain the Expanded Query 
				 * ------------------------------------------------------------*/			
				String topicDispersionResults = trecSearch.getResults("dispersion");
//				System.out.println("The Dispersion Results :");
//				System.out.print(topicDispersionResults);
				/* ------------------------------------------------------------*/
				
				
				/* ------------------------------------------------------------
				 * Get the Expanded Query based on the dispersion ranking 
				 * ------------------------------------------------------------*/
				String[] expandedQueryArray = trecSearch.getExpandedQueryArray();
				String expandedQueryString="";
				int expandedQueryArray_Lenght = expandedQueryArray.length;
				for(int k=0; k<expandedQueryArray_Lenght; k++) {
					expandedQueryString += expandedQueryArray[k];
					if(k<expandedQueryArray_Lenght-1) expandedQueryString += " OR ";
//					System.out.println("expandedQueryString: "+expandedQueryArray[k]);	
				}
				if(expandedQueryString.length()>0)
				expandedQueryString = " AND ( " + expandedQueryString + " ) ";
				/* ------------------------------------------------------------*/
				
				
				/* -----------------------------------------
				 * Calculate the expanded Ranking
				 * -----------------------------------------*/
				String topicQueryString = trecSearch.getTopicQuery();
				
				//Boost the Topic Query
				String boostFactor = "^2";
				topicQueryString = "(" + topicQueryString + ")" + boostFactor + " " ;
				
				String extendedQuery = topicQueryString + expandedQueryString;
				System.out.println("extendedQuery : "+extendedQuery);
				
				trecSearch.searchWithSearchString(extendedQuery, topicID);
				String topicExpandedResults = trecSearch.getResults("standard");

				System.out.println("The Expanded-TFIDF Results :");
//				System.out.print(topicExpandedResults);
				expandedOut.write(topicExpandedResults);
				/* -----------------------------------------*/				
				
			}
			else {
				System.out.println("...... No Results for Topic: "+topicID+"!! ......");
			}
		}
		
		standardOut.close();
		expandedOut.close();		
		
	}	
		
}

