package org.galeas.utils;

import java.util.ArrayList;
import java.util.List;

import org.galeas.xsearch.Xterm;

public class XtermUtils {

	public XtermUtils() {
		
	}
	
	public double getCombinatedIntersection(Xterm[] xterms) {
		
		int intersectionLevel=0;
		double cumulatedIntersection = 0;
		Combinator xtermCombinator = new Combinator(xterms);
		
		/* Get all possible pair combinations of xterms (without repetition)
		 * n! / (r! * (n-r)!)
		 * where 
		 * n = nr. of xterms
		 * r = 2 (pairs)
		 *  */
		Xterm[][] xtermArray = (Xterm[][]) xtermCombinator.getPairs();
		
		
		/* Get the cumulated Intersection between all pairs */
		for(int i=0;i<xtermArray.length;i++) {
			Xterm xterm1 = (Xterm) xtermArray[i][0];
			Xterm xterm2 = (Xterm) xtermArray[i][1];
			
			/* Calculate Intersection between term1 and term2 
			 * The result value will be saved in term2*/			
			List xterm2List = new ArrayList();
			xterm2List.add(xterm2);
			xterm1.setIntersection(xterm2List);
			
			/* Get the calculated intersection value and add it
			 * to the cummulated intersection */
			double intersectionValue = xterm1.GetIntersection();
			cumulatedIntersection += intersectionValue;
			
			/* If the intersection value not cero, increment the intersectionLevel
			 * The intersection level defines how many terms have a common region */
			if(intersectionValue != 0) intersectionLevel++;
		}
		
		/* The levelCumulatedIntersections reflects the levels of intersection of the regions
		 * the more terms have a common intersection region 
		 * greater is the levelCumulatedIntersections value */
		double levelCumulatedIntersection = cumulatedIntersection*intersectionLevel;
		
		return levelCumulatedIntersection;
	}
	
	
}
