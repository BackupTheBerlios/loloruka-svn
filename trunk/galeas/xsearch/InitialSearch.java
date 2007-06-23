package org.galeas.xsearch;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.StringTokenizer;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermPositionVector;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * @author Galeas
 *
 */
public class InitialSearch {
	
	private Query parsedQuery;
	Hits initialHits;
	private IndexSearcher indexSearcher;
	public IndexReader indexReader;	
	private QueryParser queryParser;
	private Xhits extendedHits;
	
	
	public InitialSearch() throws IOException {

		// Initial Parameters
		String indexDirString = "c:\\TEMP\\index";
		
		// Specify the index directory
		Directory fsDir = FSDirectory.getDirectory(new File(indexDirString), false);		
		
		
		indexReader = IndexReader.open(fsDir);	
		
		// Set the index searcher
		indexSearcher = new IndexSearcher(fsDir);			
		
		// Set the analyzer for the query and parse the query
		StandardAnalyzer queryAnalyzer = new StandardAnalyzer();		
		queryParser = new QueryParser("contents", queryAnalyzer);
		queryParser.setDefaultOperator(QueryParser.AND_OPERATOR);		
	}




	public void search(String queryString) throws IOException, ParseException {
		
		//Parse the query
		parsedQuery = queryParser.parse(this.xQueryParser(queryString));
		
		System.out.println("parsedQuery:"+parsedQuery.toString());
		
		// Execute the parsed query
		initialHits = indexSearcher.search(parsedQuery);	
	}
	
	
	/**
	 * @return the parsed query
	 */
	public Query getParsedQuery() {
		return parsedQuery;
	}
	
	
	/**
	 * Parse the query string, generating the strings to exclude 
	 * the statatistical information stored with the Terms
	 * @param query
	 * @return 
	 */
	public String xQueryParser(String query) {
		
		String[] special_search_words = { "OR", "AND", "NOT", "+", "-", "!" , "(" , ")" , ":" , "^" , "]", "{", "}", "~", "*","?" };
		Arrays.sort(special_search_words);
		String parsedQueryString = "";  
		StringTokenizer st = new StringTokenizer(query);
		
		while (st.hasMoreTokens()) {
			String actualToken = st.nextToken();			
			if(Arrays.binarySearch(special_search_words, actualToken)<0) {
				actualToken = actualToken + ",* ";
			}
			parsedQueryString = parsedQueryString + " "+ actualToken;	        
	    }
	    return parsedQueryString;
	}

	
	/**
	 * @throws Exception
	 * Print the Initial Document Hits
	 */
	public void printInitialHits() throws Exception{
				
		int numberOfHits = this.initialHits.length();

	    // Print out the results
		for (int i=0;i<numberOfHits;i++) {
			Document actualDocument = initialHits.doc(i);
			
			int docID = initialHits.id(i);
			System.out.println("\ndoc:"+actualDocument.toString());
			PrintAllStatTermsOfDocumentID(docID);
		}		
		
	}

	
	
	
	public void PrintAllStatTermsOfDocumentID(int docID) throws IOException {
		
		// Get vector with all Terms frequency and Positions in actual document (docID)									
		TermPositionVector termPositionsVector = (TermPositionVector) indexReader.getTermFreqVector(docID,"contents");
				
		// Get an String Array with all Terms (Term-Stats-String) of docID
		// The Term-Stats-String contain three values separated with blank spaces:
		// "[word] [word_center] [word_deviation]"
		String[] vectorTermString = termPositionsVector.getTerms();
	
				
		// LOOP over all Term-Stats-String in actual document (docID)				
		for (int k = 0; k < vectorTermString.length; k++) {
			
			
			int[] termPositions =  termPositionsVector.getTermPositions(k);
			String actualTermString = vectorTermString[k];
			System.out.println("");
			System.out.print(actualTermString);
			
			for(int i=0;i<termPositions.length;i++) {
				System.out.print(" ["+termPositions[i]+"]");
			}
		}
	}
	
	
}
