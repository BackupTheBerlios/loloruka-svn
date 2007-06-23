package org.galeas.xsearch;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.galeas.utils.ConfigurationManager;

public class TRECSearch2 {

	

	
	public static void main(String args[]) throws Exception {
		
		
//		String[] topicIds = new String[] {
//		"401","402","403","404","405","406","407","408","409","410",
//		"411","412","413","414","415","416","417","418","419","420",
//		"421","422","423","424","425","426","427","428","429","430",
//		"431","432","433","434","435","436","437","438","439","440",
//		"441","442","443","444","445","446","447","448","449","450"};
		
		String[] topicIds = new String[] {
		"448"}; //448, 407, 430, 401, 435, 406			
		
		
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
				trecSearch.printExpandedQuery();
				/* ------------------------------------------------------------*/
				
				
				/* ------------------------------------------------------------
				 * Get array with the query terms and its total frequency 
				 * in the results.
				 * Thus we obtain the query term with the minimal frequency
				 * and extend it in the original query with OR operators 
				 * using the extended query terms 
				 * ------------------------------------------------------------*/
				
				/* Create the object containing statistics of the
				 * query terms on the search results 
				 * (total frequency of each terms on the results) */
				QueryStatsOnResults queryStatsOnResults = trecSearch.getReducedQuery();
				
//				System.out.println("reducedQuery.SIZE:"+queryStatsOnResults.size());
				
				System.out.println("reducedQuery:"+queryStatsOnResults);
				
				
				/* Get al least frequently Query Term */
				String leastFrequentlyTerm = queryStatsOnResults.getLeastFrequentlyTerm();
				System.out.println("-> leastFrequentlyTerm : "+leastFrequentlyTerm);
				
				/* Get the most frequently Query Terms 
				 * (n-1) terms will be returned */
				String[] mostFrequentlyTermsArray = queryStatsOnResults.getMostFrequentlyTerms();
				for(int h=0;h<mostFrequentlyTermsArray.length;h++) {
					System.out.println("-> mostFrequentlyTermsArray : "+mostFrequentlyTermsArray[h]);
				}
				
				
				/* Select the Term to expand the query 
				 * (1) term will be returned  */
				String SelectedTermToExpand = getSelectedTermToExpand(mostFrequentlyTermsArray, expandedQueryArray);
				System.out.println("--> SelectedTermToExpand : "+SelectedTermToExpand);
		
				
				/* -----------------------------------------
				 * Create the extended query string
				 * -----------------------------------------*/
				// More relevant terms
				String extendedQuery = "";
				for(int i=0; i<mostFrequentlyTermsArray.length;i++) {
					extendedQuery += mostFrequentlyTermsArray[i] + " AND ";
				}
				
				// Extended Terms
				if(extendedQuery.equalsIgnoreCase("")) {
					extendedQuery += " (" + leastFrequentlyTerm + " AND " + SelectedTermToExpand + ")";
				}
				else {
					extendedQuery += " (" + leastFrequentlyTerm + " OR " + SelectedTermToExpand + ")";	
				}
				
				
				// System.out.println("EXPANDED QUERY:"+extendedQuery);
				/* -----------------------------------------*/
	
				
				
				/* -----------------------------------------
				 * Calculate the expanded Ranking
				 * -----------------------------------------*/
				trecSearch.searchWithSearchString(extendedQuery, topicID);
				String topicExpandedResults = trecSearch.getResults("standard");
//				System.out.println("The Expanded-TFIDF Results :");
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
	
	
	
	private static String getSelectedTermToExpand(String[] mostFrequentlyTermsArray, String[] expandedQueryArray) {
		Arrays.sort(mostFrequentlyTermsArray);
		for(int i=0;i<expandedQueryArray.length;i++) {
			if(Arrays.binarySearch(mostFrequentlyTermsArray,expandedQueryArray[i])<0) {
				return expandedQueryArray[i];
			}
		}		
		return null;
	}	
}

