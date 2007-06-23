package org.galeas.xsearch;

import java.util.Iterator;
import java.util.List;

public class StatsTerm implements Comparable {
	private String Word;

	private double WordCenter;

	private double WordDeviation;

	private double IntersectionValue;

	public StatsTerm() {

	}

	public StatsTerm(String Word, double WordCenter, double WordDeviation) {
		this.Word = Word;
		this.WordCenter = WordCenter;
		this.WordDeviation = WordDeviation;
		this.IntersectionValue = 0;
	}

	public int compareTo(Object o) {
		StatsTerm c = (StatsTerm) o;
		if ((this.IntersectionValue - c.IntersectionValue) < 0)
			return -1;
		else if ((this.IntersectionValue - c.IntersectionValue) > 0)
			return 1;
		else
			return 0;
	}

	
	
	public void setIntersectionValue(double value) {
		this.IntersectionValue = value;
	}
	
	
	
	public void setIntersection(List statTermsList) {

		double A_min = this.WordCenter - this.WordDeviation;		
		double A_max = this.WordCenter + this.WordDeviation;
		//System.out.println("A_center: "+this.WordCenter+"  A_Dev: "+this.WordDeviation);
		//System.out.println("A_Limits: ("+A_min+" , "+A_max+")");
		
		
		Iterator it = statTermsList.iterator();
		while (it.hasNext()) {
			
			StatsTerm statTerm = (StatsTerm) it.next();

			double B_min = statTerm.WordCenter - statTerm.WordDeviation;
			double B_max = statTerm.WordCenter + statTerm.WordDeviation;
			
			//System.out.println("B_center: "+statTerm.WordCenter+"  B_Dev: "+statTerm.WordDeviation);
			//System.out.println("B_Limits: ("+B_min+" , "+B_max+")");
			
			if (A_min < B_min && B_min < A_max && A_max < B_max) {
				// case_number = 1;				
				this.IntersectionValue = A_max - B_min;
				//System.out.println("case_number1: "+(A_max - B_min));
			} else if (B_min < A_min && A_min < B_max && B_max < A_max) {
				// case_number = 2;
				this.IntersectionValue = B_max - A_min;
				//System.out.println("case_number2: "+(B_max - A_min));
			}			
			else if (A_min < B_min && B_min < A_max && B_max < A_max) {
				// case_number = 3;
				this.IntersectionValue = B_max - B_min;
				//System.out.println("case_number3: "+(B_max - B_min));
			}
			else if (B_min < A_min && A_min < B_max && A_max < B_max) {
				// case_number = 4;
				this.IntersectionValue = A_max - A_min;
				//System.out.println("case_number4: "+(A_max - A_min));
			} 
			else if (A_max < B_min || B_max < A_min) {
				// case_number = 5;
				this.IntersectionValue = 0;
				//System.out.println("case_number5: 0");
			}

		}

	}

	public String toString() {
		return Word + " [center:" + this.WordCenter + "]" + " [dev: "
				+ this.WordDeviation + "]" + " [int: " + this.IntersectionValue
				+ "]";
	}

	public String getWord() {
		return this.Word;
	}

	public double getWordCenter() {
		return this.WordCenter;
	}

	public double getWordDeviation() {
		return this.WordDeviation;
	}

	public double getIntersectionValue() {
		return this.IntersectionValue;
	}

}