package org.galeas.xsearch;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Xterm implements Comparable, Cloneable{

	private String word;
	private String stemmedWord;
	private double center;
	private double deviation;
	private int frequency;
	private int maximalPosition;
	private double intersection;
	private double weight=1;
	private PorterStemmer stemmer = new PorterStemmer();
	
	
	public Xterm() {
		this.word = null;
		this.stemmedWord = null;
		this.center = 0;
		this.deviation = 0;
		this.intersection = 0;
	}
	
	public Xterm(String word) {
		
			this.word = word;
//			System.out.println("CASE 01 ->word:"+word);			
			if(word.length()>3) this.stemmedWord = stemmer.stem(word);
			else this.stemmedWord = word;
//			System.out.println("CASE 02");
			this.center = 0;
			this.deviation = 0;
			this.intersection = 0;			
		
	}	
	
	public Xterm (String word, String[] stringStats) throws ParseException {		
		this.word = word;
		
		if(word.length()>4) this.stemmedWord = stemmer.stem(word);
		else this.stemmedWord = word;

		NumberFormat fmt 		= NumberFormat.getNumberInstance();
		Number inputMean		= fmt.parse(stringStats[0]);
		Number inputSD 			= fmt.parse(stringStats[1]);
		Number frequency 		= fmt.parse(stringStats[2]);
		Number maximalPosition 	= fmt.parse(stringStats[3]);
		this.center 			= inputMean.doubleValue();		
		this.deviation 			= inputSD.doubleValue();
		this.frequency 			= frequency.intValue();
		this.maximalPosition 	= maximalPosition.intValue();
	}	

	
	public Object clone() {
		try{
			return super.clone();
		}
		catch (CloneNotSupportedException e) {
			return null;
		}
	}
	
	
	/*
	 * Calculate the intersection value between the actual this.Xterm
	 * and the Xterms in the list xTermsList
	 * The intersection value will be calculated using two parameters:
	 * 
	 * 1. The frequences of the compared terms in the document.
	 * 
	 * 2. The intersection range between both term distributions in the
	 *    document 
	 *    
	 *    
	 *    INTERSECTION(A,B) = Intersection_Range(A,B) * (1 + LOG(frequency(A,B))
	 *    
	 * 3. The intersection will be calculated only if 
	 *    
	 */	
	public void setIntersection(List xTermsList) {	
				
		double A_min 			= this.center - this.deviation;		
		double A_max 			= this.center + this.deviation;
		int A_frequency 		= this.frequency;
		String A_stemmedWord 	= this.stemmedWord;		
		
		/* Get an array with the stemmed query words 
		 * and sort it */
		String[] stemmedQueryWords = new XqueryUtils().queryObjectsToStemmedStrings(xTermsList);
		Arrays.sort(stemmedQueryWords);				
		
		
		/* iterator over all elements of xTermsList 
		 * in this case xTermsList contains all query words */
		Iterator it = xTermsList.iterator();
		while (it.hasNext()) {
			
			Xterm xTerm = (Xterm) it.next();
			
			double B_min = xTerm.center - xTerm.deviation;
			double B_max = xTerm.center + xTerm.deviation;
			int B_frequency = xTerm.frequency;

			/* IMPORTANT : Calculate the intersection value between (A,B) 
			 * ONLY if stem(A) <> stem(B) */		
			if(Arrays.binarySearch(stemmedQueryWords, A_stemmedWord)<0)
			{				
				double A_B_density = 1+Math.log(A_frequency+B_frequency);
				
				if (A_min < B_min && B_min < A_max && A_max < B_max) {
					// case_number = 1;				
					this.intersection = (A_max - B_min)*A_B_density;
					//System.out.println("case_number1: "+(A_max - B_min));
				} else if (B_min < A_min && A_min < B_max && B_max < A_max) {
					// case_number = 2;
					this.intersection = (B_max - A_min)*A_B_density;
					//System.out.println("case_number2: "+(B_max - A_min));
				}			
				else if (A_min < B_min && B_min < A_max && B_max < A_max) {
					// case_number = 3;
					this.intersection = (B_max - B_min)*A_B_density;
					//System.out.println("case_number3: "+(B_max - B_min));
				}
				else if (B_min < A_min && A_min < B_max && A_max < B_max) {
					// case_number = 4;
					this.intersection = (A_max - A_min)*A_B_density;
					//System.out.println("case_number4: "+(A_max - A_min));
				} 
				else if (A_max < B_min || B_max < A_min) {
					// case_number = 5;
					this.intersection = 0;
					//System.out.println("case_number5: 0");
				}
				
			}
			else {
				/* if the stemm of both terms (A,B) is identic
				 * their intersection = 0 */
				this.intersection = 0;
			}
				

		}

	}	
	
	
	public String GetTermWord() {
		return this.word;
	}
	
	public String GetTermStemmedWord() {
		return this.stemmedWord;
	}
	
	public void SetTermWord(String newWord) {
		this.word = newWord;
	}
	
	public void SetTermWeight(double weight) {
		this.weight = weight;
	}
	
	public double GetTermWeight() {
		return this.weight;
	}	
	
	public void AddTermWord(String newWord) {
		this.word += ","+newWord;
	}
	
	public double GetTermCenter() {
		return this.center;
	}	
	
	public double GetTermDeviation() {
		return this.deviation;
	}
	
	public int GetFrequency() {
		return this.frequency;
	}
	
	public int GetMaximalPosition() {
		return this.maximalPosition;
	}	
	
	public double GetIntersection() {
		return this.intersection;
	}
	
	
	
	public void AddIntersectionValue(double externalIntersection) {
		this.intersection += externalIntersection;
	}	
	

	public int compareTo(Object o) {
				
    	Xterm c = (Xterm) o;	   
    	if ((this.intersection - c.intersection)<0) return -1;
    	else if((this.intersection - c.intersection)>0) return 1;
    	else return 0;		
	}	
	
}
