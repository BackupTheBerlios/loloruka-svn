package org.galeas.xsearch;

import java.util.ArrayList;
import java.util.List;

/*Save the graphic information for one document */
public class GraphicData {
	
	private String documentName;
	private float maximal_document_position;
	private List queryTerms = new ArrayList();
	
	
	public void addQueryTerm(XqueryTerm queryTerm) {
		queryTerms.add(queryTerm);
	}
	
	public void setMaximaDocumentPosition(float max) {
		this.maximal_document_position = max;
	}
	
	public void setMaximaDocumentPosition(int max) {
		Integer integerValue = Integer.valueOf(max);
		this.maximal_document_position = integerValue.floatValue();
	}	
	
	public List getQueryTermsList() {
		return this.queryTerms;
	}
	
	public float getMaximaDocumentPosition() {
		return this.maximal_document_position;
	}
	
	public void setDocumentName(String name) {
		this.documentName = name;
	}
	
	public String getDocumentName() {
		return this.documentName;
	}
	

	
}
