package org.galeas.xsearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.galeas.comparable.XtermWordComparator;
import org.galeas.utils.Combinator;

public class Xhit implements Comparable{
	
	private int documentID;
	private float stdRanking;
	private double ranking;
	private List xterms = new ArrayList(); //contains an array with all document terms
	
	
	public Xhit(int documentID) {
		this.documentID = documentID;
		this.ranking = 0;
		this.xterms = null;		
	}

	
	public Xhit(int documentID, List xterms) {
		this.documentID = documentID;
		this.ranking = 0;
		this.xterms = xterms;		
	}

	
	public Xhit(int documentID, List xterms, float stdRanking) {
		this.documentID = documentID;
		this.ranking = 0;
		this.xterms = xterms;	
		this.stdRanking = stdRanking;
	}	
	
	
	private void applyDispersionRanking(String queryString, String rankingType, String dispersion_model, double dispersionRankWeight) {
		
		
		List queryWordsList = new ArrayList();
		String word 	= new XqueryUtils().returnParsedQueryWord(queryString);
		//double weight 	= new XqueryUtils().returnQueryWeight(queryString);
		double weight = 1;
		
		Xterm queryTerm = new Xterm(word); // set the query word value
		queryTerm.SetTermWeight(weight); // set the query weight value	
		queryWordsList.add(queryTerm); // add the query term to the list
		
		applyDispersionRanking(queryWordsList, rankingType, dispersion_model, dispersionRankWeight);
	}
	
	
	private void applyDispersionRanking(String[] queryStrings, String rankingType, String dispersion_model, double dispersionRankWeight) {		
				
		
		List queryWordsList = new ArrayList();
		for (int i=0;i<queryStrings.length;i++) {
			
			String word 	= new XqueryUtils().returnParsedQueryWord(queryStrings[i]);
			
			//double weight 	= new XqueryUtils().returnQueryWeight(queryStrings[i]);
			double weight = 1;
			
			Xterm queryTerm = new Xterm(word); // set the query word value
			queryTerm.SetTermWeight(weight); // set the query weight value	
			queryWordsList.add(queryTerm); // add the query term to the list
		}
		applyDispersionRanking(queryWordsList, rankingType,  dispersion_model, dispersionRankWeight);
	}	
	
	
	
	/* Calculate the Dispersion Ranking Value for the Xhit */
	private void applyDispersionRanking(List queryWordsTerms, String rankingType, String dispersion_model, double weight) {		
		
		double cumulativeRanking=0;

		
		/* Define the comparator type for the query terms 
		 * possible values : word, stemm */
		XtermWordComparator queryComparator = new XtermWordComparator(rankingType);
		
		/* Sort the queryWords list for searching */	
		Collections.sort(queryWordsTerms, queryComparator);

		/* Estimate the lenght of the document */
		double estimatedDocumentLenght = this.getDocumentSize();		
		
		/* Iterator over all Xterm in xterms */
		Iterator it = xterms.iterator();
		
		/* Count the query word contained in the Hit */
		int queryWordsInHitCounter = 0;
		
		while(it.hasNext()) {

			Xterm actualXterm = (Xterm) it.next();			
			
			// Search the actualTerm in the QueryWordsTerms list		
			int queryIndexPosition = Collections.binarySearch(queryWordsTerms, actualXterm, queryComparator);
			
			if(queryIndexPosition >= 0) {
					
				queryWordsInHitCounter++;
				
				// Get the term distribution on the document hit
				double TermDeviation = actualXterm.GetTermDeviation();
				
				/* Initialize the ranking value */
				double rankingValue=0;
				
				/* ------------------------------------------------------------
				 * Ranking Models for Dispersion Ranking
				 * ------------------------------------------------------------*/				
				int TermFrequency = actualXterm.GetFrequency(); // Get the term frequency in the document hit
				
				// Logarithmic Frequency (TDLogF)				
				if(dispersion_model.equalsIgnoreCase("TDLogF")) {
					rankingValue = weight*(TermDeviation/estimatedDocumentLenght)*(1+Math.log(TermFrequency));									
				}
				// Linear Frequency (TDLinF)
				else if(dispersion_model.equalsIgnoreCase("TDLinF")) {
					rankingValue = weight*(TermDeviation/estimatedDocumentLenght)*TermFrequency;
				}
				// SQRT Frequency (TDsqrtF)
				else if(dispersion_model.equalsIgnoreCase("TDsqrtF")) {
					rankingValue = weight*(TermDeviation/estimatedDocumentLenght)*Math.sqrt(TermFrequency);				
				}
				// Only Term Distribution
				else if(dispersion_model.equalsIgnoreCase("OTD")) {
					rankingValue = weight*2*TermDeviation/estimatedDocumentLenght;
				}
				
				/* ------------------------------------
				 * Set the cumulativeRanking value
				 * ------------------------------------*/
				cumulativeRanking += rankingValue;
				/* ------------------------------------*/
			}
		}
		
		
		/* Calculate the final Ranking */
		this.ranking = (1-weight)*this.stdRanking + weight * cumulativeRanking;

	}

	
	private double getQueryIntersection(List queryTerms) {
		
		Object[] queryTermsInHit_ARRAY = queryTerms.toArray();
		
		Combinator queryTermCombinator = new Combinator(queryTermsInHit_ARRAY);
		Object[][] queryTermsPairs = (Object[][]) queryTermCombinator.getPairs();		
		
		double cumulatedIntersection = 0;
		int level = 0;
		for(int i=0;i<queryTermsPairs.length;i++) {
			Xterm queryTerm0 = (Xterm) queryTermsPairs[i][0];
			Xterm queryTerm1 = (Xterm) queryTermsPairs[i][1];
			List queryTerm1List = new ArrayList();
			queryTerm1List.add(queryTerm1);
			queryTerm0.setIntersection(queryTerm1List);
			double intersection = queryTerm0.GetIntersection();
			if(intersection != 0) {
				level++;
				cumulatedIntersection += intersection;
			}

		}
		
		return cumulatedIntersection * level;
	}
	
	
	
	/* ADD the Query Neighborhood Ranking for the Xhit */	
	private void applyQueryNeighborhoodRanking(String[] neighborTerms) {		
		
	}	
	
	
	private int  getDocumentSize() {
		int[] positions = new int[xterms.size()];
		int counter = 0;
		Iterator it = xterms.iterator();
		while(it.hasNext()) {
			Xterm actualXterm = (Xterm) it.next();
			positions[counter++]=actualXterm.GetMaximalPosition();
		}
		Arrays.sort(positions);
		return positions[positions.length-1];
	}
	
	
	/* Calulate the Xhit ranking */
	public void applyRanking(String queryWord, String rankingType, String dispersion_model, double dispersionRankWeight ) {
		
		String[] queryWords = new String[1]; 
		queryWords[0] = queryWord;
		
		applyDispersionRanking(queryWords, rankingType, dispersion_model, dispersionRankWeight);
	}		
	
	
	/* Calulate the Xhit ranking */
	public void applyRanking(String[] queryWords, String rankingType, String dispersion_model, double dispersionRankWeight) {
		applyDispersionRanking(queryWords, rankingType, dispersion_model, dispersionRankWeight);
	}	
	
	

	/* Calculate the intersection value between queryWords and the terms in xterms
	 * and return the top-n Xterms to extend the query  */	
	public List getExtendedQuery(String[] queryWords, int n) {
		
		
		List queryXterms = getQueryXterms(queryWords);
		
		
		Iterator it = this.xterms.iterator();
		while(it.hasNext()) {
			Xterm actualTerm = (Xterm)it.next();
			
			String actualTermWord = actualTerm.GetTermWord();
			/* if the terms of Xhit are not numbers and greater than
			 * 2 characters, calculate the intersection value with the query terms */
			if(actualTermWord.length()>2 && !stringIsNumeric(actualTermWord)) {
				actualTerm.setIntersection(queryXterms);
			}
		}		

		return getTopXterms(n);
	
	}
	
	
	private boolean stringIsNumeric(String s) {
		try 	{
			Integer.parseInt(s);
		}
		catch(NumberFormatException e)	{
			return false;
		}

		return true;
	}
	
	
	/* Get the top-n ranked Xterms */
	public List getTopXterms(int n) {
	    
		int nrOfReturnedTerms;
		if(this.xterms.size()< n) nrOfReturnedTerms = this.xterms.size();
		else nrOfReturnedTerms = n;
		
		Comparator comparator = Collections.reverseOrder();	
		Collections.sort(this.xterms, comparator);
		
		return this.xterms.subList(0,nrOfReturnedTerms);
	}	

	
	
	/* Get the Xterms list corresponding to the words in queryWords */
	private List getQueryXterms(String[] queryWords) {
		List queryXterms = new ArrayList();
		for(int i=0;i<queryWords.length;i++) {
			Xterm queryXterm = searchXterm(queryWords[i]);
			
			if(queryXterm != null) {
				queryXterms.add(queryXterm);
				//System.out.println("getQueryXterms von ("+queryWords[i]+") = ["+queryXterm.GetTermWord()+", "+queryXterm.GetTermCenter()+" , "+queryXterm.GetTermDeviation()+" ]");
			}
		}		
		return queryXterms;		
	}
	
	
	
	/* Get the Xterm containing the word */
	private Xterm searchXterm(String word) {
				
		Iterator it = this.xterms.iterator();
		while(it.hasNext()) {
			Xterm actualXterm = (Xterm)it.next();
			if(actualXterm.GetTermWord().equalsIgnoreCase(word)) {
				return actualXterm;
			}			
		}
		return null;		
	}
		

	public int getDocumentID() {
		return documentID;
	}

	public double getRanking() {
		return ranking;
	}
	

    public int compareTo(Object o) {
    	Xhit c = (Xhit) o;	   
    	if ((this.ranking - c.ranking)<0) return -1;
    	else if((this.ranking - c.ranking)>0) return 1;
    	else return 0;
    } 	
    
	
}
