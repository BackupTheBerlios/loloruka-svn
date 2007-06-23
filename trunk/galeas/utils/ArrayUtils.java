package org.galeas.utils;

public class ArrayUtils {

	
	public int[] Merge(int[] array1, int[] array2) {
		int[] arr = new int[array1.length + array2.length];
		for (int x=0; x < array1.length; x++) {
			arr[x] = array1[x];
		}
		for (int x=0; x < array2.length; x++) {
			arr[x+array1.length] = array2[x];
		}
		
		return arr;
	}
	

	
	public Object[] subArray(Object[] objectArray, int start, int end) {
		
		Object[] resultArray = new Object[end-start+1];
		int counter=0;
		for (int i=start; i<=end; i++) {
			resultArray[counter++] =  objectArray[i];
		}
		
		return resultArray;
	}
	
	 

	
	
}
