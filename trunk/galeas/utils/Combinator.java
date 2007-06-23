package org.galeas.utils;

public class Combinator {

	private Object[] objectArray;
		
	
	public Combinator(Object[] objects) {
		objectArray = objects;
		
	}
		
	public Object[][] getPairs() {
		
		//System.out.println("lenght:"+objectArray.length);
		int t = MathCombinations.getCombinationWithoutRepetition(objectArray.length,2);
		
		Object[][] pairArray = new Object[t][2];
		int counter = 0;
		for(int i=0;i<objectArray.length;i++) {
			for(int j=i+1; j<objectArray.length;j++) {
				//System.out.println("pairs:"+objectArray[i]+" "+objectArray[j]);
				pairArray[counter][0] = objectArray[i];
				pairArray[counter][1] = objectArray[j];	
				counter++;
			}
				
		}		
		return pairArray;
	}
	
	

	
	
	
	/*
	public static void main(String[] args) {
		String[] objects = new String[] {"A","B","C","D"};
		//Integer[] objects = new Integer[] {Integer.valueOf(1),Integer.valueOf(2),Integer.valueOf(3)};
		
		Combinator comb = new Combinator(objects);
		Object[][] pairs = comb.getPairs();
		for(int i=0;i<pairs.length;i++) {
			System.out.println(pairs[i][0]+" "+pairs[i][1]);
		}
		
	*/
}
