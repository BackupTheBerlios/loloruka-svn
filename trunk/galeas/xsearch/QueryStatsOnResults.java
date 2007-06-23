package org.galeas.xsearch;

import java.util.Iterator;
import java.util.TreeSet;

public class QueryStatsOnResults {

	private TreeSet queryTerms = new TreeSet();;

	public void QueryStatsOnResults() {
		
	}

	
	public String toString() {

		return this.queryTerms.toString();
	}
	
	public int size() {
		return queryTerms.size();
	}

	private RqueryTerm extractQueryTermFromList(RqueryTerm queryTerm) {
		Iterator it = queryTerms.iterator();
		while (it.hasNext()) {
			RqueryTerm listQueryTerm = (RqueryTerm) it.next();
			if (listQueryTerm.getQueryStr().equalsIgnoreCase(
					queryTerm.getQueryStr())) {
				it.remove();
			
				return listQueryTerm;
			}
		}
		return null;
	}

	
	
	public void addQueryTerm(RqueryTerm queryTerm) {
		
		RqueryTerm queryTermInList = extractQueryTermFromList(queryTerm);
		
		
		if(queryTermInList == null) {
			queryTerms.add(queryTerm);
		}
		else {
			queryTermInList.addFrequencyValue(queryTerm.getFreq());
			queryTerms.add(queryTermInList);
		}
	}	
	
	
	public String getLeastFrequentlyTerm() {
		
		RqueryTerm firstTerm = (RqueryTerm)queryTerms.first();
		String firstTermString = firstTerm.getQueryStr();
		return firstTermString;		
	}
	
	
	public String[] getMostFrequentlyTerms() {

		queryTerms.remove(queryTerms.first());
		
		int counter = 0;
		int size = queryTerms.size();
		String[] returnedQueryTerms = new String[size];
		Iterator it = queryTerms.iterator();
		while (it.hasNext()) {
				RqueryTerm term = (RqueryTerm) it.next();
				returnedQueryTerms[counter++] = term.getQueryStr();				
		}
		
		return returnedQueryTerms;
	}
	
	public String[] getAllTerms() {
		int counter = 0;
		int size = queryTerms.size();
		String[] returnedQueryTerms = new String[size];
		Iterator it = queryTerms.iterator();
		while (it.hasNext()) {
				RqueryTerm term = (RqueryTerm) it.next();
				returnedQueryTerms[counter++] = term.getQueryStr();				
		}
		
		return returnedQueryTerms;		
	}

}
