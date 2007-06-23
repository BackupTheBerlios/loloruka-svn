package org.galeas.utils;

/**
 * This program computes the factorial of a number
 */
public class Factorial {

	public Factorial() {
		
	}
	
	public static double factorial(int x) {
		if (x < 0)
			return 0.0;
		double fact = 1.0;
		while (x > 1) {
			fact = fact * x;
			x = x - 1;
		}
		return fact;
	}
}
