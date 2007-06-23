package org.galeas.xsearch;

public class RqueryTerm implements Comparable{

	private int queryFreq;
	private String queryStr;
	
	public RqueryTerm(String query, int queryFreq) {
		this.queryStr = query;
		this.queryFreq = queryFreq;
	}
	
	public void setQueryTerm(String query, int queryFreq) {
		this.queryStr = query;
		this.queryFreq = queryFreq;
	}
	
	public void addFrequencyValue(int value) {
		this.queryFreq += value;
	}
	
	public int getFreq() {
		return this.queryFreq;
	}
	
	public String getQueryStr() {
		return this.queryStr;
	}

	
	public String toString() {
		return this.getQueryStr() + " " + this.getFreq();
	}
	

	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		RqueryTerm other = (RqueryTerm)arg0;
		if(other.queryStr.equalsIgnoreCase(this.queryStr)) {
			return this.queryFreq - other.queryFreq;
		}
		else return -1;
		
	}
	
	


}
