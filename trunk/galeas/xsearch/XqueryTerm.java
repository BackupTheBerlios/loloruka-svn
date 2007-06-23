package org.galeas.xsearch;

public class XqueryTerm {

	private String query;
	private float[] positions;
	
	public XqueryTerm(String query) {
		this.query = query;
	}
	
	public XqueryTerm(String query, float[] positions) {
		this.query = query;
		this.positions = positions;
	}
	
	public XqueryTerm(String query, int[] positions) {
		this.query = query;
		float[] positions_temp = new float[positions.length];
		for(int i=0; i<positions.length; i++) {
			Integer posInteger = Integer.valueOf(positions[i]);
			positions_temp[i] = posInteger.floatValue();
		}
		this.positions = positions_temp;
	}	
	
	public String getQuery() {
		return this.query;
	}
	
	public void setQuery(String query) {
		this.query = query;
	}
	
	public float[] getPositions() {
		return this.positions;
	}
	
	public void setPositions(float[] positions) {
		this.positions = positions;
	}
	
	
}
