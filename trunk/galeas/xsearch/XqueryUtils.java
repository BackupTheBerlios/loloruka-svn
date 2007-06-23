package org.galeas.xsearch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XqueryUtils {
	
	
	/* Convert the query String in an Array of Strings */
	public String[] queryWordsToArray(String userQuery) {			
		String queryString = userQuery;
		
		String[] queryArray = queryString.split("\\s");		
		List queryWordsList = new ArrayList();
		
		for(int i=0;i<queryArray.length;i++) {
			String q = this.returnParsedQueryWord(queryArray[i]);
			if(q!= null) {
				queryWordsList.add(q);
			}							
		}
		
		
		Iterator it = queryWordsList.iterator();
		String[] queryWordsArray = new String[queryWordsList.size()];
		int counter =0;
		while(it.hasNext()) {
			String s = (String) it.next();
			queryWordsArray[counter++] = s;
		}
		
				
		return queryWordsArray;
	}		
	
	
	
	/* Get a List of Xterms containing the query terms*/
	public List queryToXterms(String userQuery) {
		
		String[] queryWords = queryWordsToArray(userQuery);
		
		/* If defined, extract the weighting values in queryWords 
		 * and create a Xterm object with the query terms */
		List queryWordsTerms = new ArrayList();
		
		for(int i=0;i<queryWords.length;i++) {
			
			String actualqueryWord = queryWords[i];
			String queryTermWord = this.returnParsedQueryWord(actualqueryWord);
			
			//double queryTermWeight = this.returnQueryWeight(actualqueryWord);
			double queryTermWeight = 1;
			
			if(queryTermWord != null) {
				Xterm queryTerm = new Xterm(queryTermWord);
				queryTerm.SetTermWeight(queryTermWeight);
				queryWordsTerms.add(queryTerm);
			}
		}	
		
		return queryWordsTerms;
	}
	
	
	/* Return only alphabetical words of the query strings 
	 * matching the defined Pattern (Regular Expressions) */ 
	public String returnParsedQueryWord(String query) {
	
//		String queryTermWord = null;
		String queryTermWord = "";
		
		//Only alphabetical Words
		Pattern onlyWordsPattern = Pattern.compile("[a-zA-Z]");		
		Matcher onlyWordsMatcher = onlyWordsPattern.matcher(query);
		
		if(onlyWordsMatcher.find()) {		
			// Eliminate Punctuation characters
			queryTermWord = query.replaceAll("[\\p{Punct}]","");
		}		
							
		return queryTermWord;
	}
	

	/* Return (if exist) the weight component of the query string */ 	
//	public double returnQueryWeight(String query) {
//		
//		Pattern pWeight = Pattern.compile("^[01]*\\.[0-9]{1,2}");
//		double queryTermWeight=1;
//		
//		System.out.println("****> returnQueryWeight("+query+")");
//		
//		String[] queryParts = query.split("\\*");				
//		for(int j=0;j<queryParts.length;j++) {
//		      Matcher mWeight = pWeight.matcher(queryParts[j]);
//		      if (mWeight.find()) {
//		    	  queryTermWeight = Double.valueOf(queryParts[j]).doubleValue();
//		      }
//		}		
//		return queryTermWeight;
//	}	
	
	
	
	/**
	 * Parse the query string, generating the strings to exclude 
	 * the statatistical information stored with the Terms
	 * @param query
	 * @return 
	 */
//	public String XQueryParser(String query) {
//		
//		String[] special_search_words = { "OR", "AND", "NOT", "+", "-", "!" , "(" , ")" , ":" , "^" , "]", "{", "}", "~", "*","?" };
//		Arrays.sort(special_search_words);
//		String parsedQueryString = "";  
//		StringTokenizer st = new StringTokenizer(query);
//		
//		
//		while (st.hasMoreTokens()) {
//			String actualToken = st.nextToken();			
//						
//			if(Arrays.binarySearch(special_search_words, actualToken)<0) {
//				//actualToken = new XqueryUtils().returnQueryWord(actualToken) +  ",* ";
//				actualToken = new XqueryUtils().returnParsedQueryWord(actualToken);
//			}
//			parsedQueryString += " "+ actualToken;	        
//	    }
//	    return parsedQueryString;
//	}	
//	
	
	
	
	public String[] queryObjectsToStemmedStrings(List queryObjects) {
		
		int queryObjectsSize = queryObjects.size();
		
		String[] stemmedQueryWord_Array = new String[queryObjectsSize];
		for(int i=0;i<queryObjectsSize;i++) {
			Xterm queryTerm = (Xterm) queryObjects.get(i);
			stemmedQueryWord_Array[i] = queryTerm.GetTermStemmedWord();			
		}
		return stemmedQueryWord_Array;
	}
	
	
}
