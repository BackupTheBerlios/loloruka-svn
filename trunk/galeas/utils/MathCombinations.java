package org.galeas.utils;

public class MathCombinations {

	public MathCombinations() {
	}
	
	public static int getCombinationWithoutRepetition(int n, int r) {
		//n is the number of objects from which you can choose, 
		//r is the number to be chosen		
		return  (int) ((Factorial.factorial(n))/(Factorial.factorial(r)* Factorial.factorial(n-r)));
	}
	
	
}
