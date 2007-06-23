package org.galeas.comparable;

import java.util.Comparator;

import org.galeas.xsearch.Xterm;

public class XtermWordComparator implements Comparator {
	
	// Define the type of comparation (standard/stemm)
	String comparatorType;

	
	public XtermWordComparator() {
		this.comparatorType = "word";
	}	
	
	public XtermWordComparator(String comparatorType) {
		this.comparatorType = comparatorType;
	}

	public int compare(Object xTerm, Object another_xTerm) {
		String word1 = ((Xterm) xTerm).GetTermWord().toUpperCase();
		String word2 = ((Xterm) another_xTerm).GetTermWord().toUpperCase();

		String stemmedWord1 = ((Xterm) xTerm).GetTermStemmedWord()
				.toUpperCase();
		String stemmedWord2 = ((Xterm) another_xTerm).GetTermStemmedWord()
				.toUpperCase();

		if (this.comparatorType.equalsIgnoreCase("stemm"))
			return stemmedWord1.compareTo(stemmedWord2);
		else
			return word1.compareTo(word2);
	}

}
