package org.galeas.xsearch;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;


/**
 * <b>Xhits</b> class contain all <b>Xhit</b> elements
 * @author Galeas
 *  
 */
public class Xhits {
	
	ArrayList extendedHits = new ArrayList();
		
	
	public Xhits() {
		
	}
	
	
	/**
	 * Add a new <b>Xhit</b> element to <b>hits</b>
	 * @param hit
	 */
	public void AddHit(Xhit hit) {
		extendedHits.add(hit);
	}
	
	
	
	/**
	 * Add <b>rankingValue</b> to the actual ranking value of documentID in <b>hits</b> 
	 * @param documentID 
	 * @param rankingValue
	 */
	public void ActualizeDocumentRanking(int documentID, double rankingValue) {

		// Read the position of documentID in the <hits>
		int index = this.IndexOfHit(documentID);		
		
		// Actualize the the corresponding <Xhit> in <hits> 
		// with its rankingValue
		if(index != -1) {
			Xhit actualXhit = (Xhit) this.extendedHits.get(index);	
			this.extendedHits.set(index, actualXhit);
		}
		// Create new objects in the list
		else {
			this.extendedHits.add(new Xhit(documentID));
		}		
	}

	
	
	
	
	
	/*
	 * Get the position of <b>documentID</b> in the <b>hits</b>
	 */
	public int IndexOfHit(int documentID) {
		
		for(int i=0;i<this.extendedHits.size();i++) {
			Xhit actualHit = (Xhit) this.extendedHits.get(i);
			int actualDocID = actualHit.getDocumentID();
			if(actualDocID == documentID){
				return i;
			}
		}		
		return -1;
	}	

	
	
	
	
	/*
	 * Get the first <b>limit</b> ranked Documents from the <b>hits</b>
	 */
	public int[] GetSortedDocumentIDs(int limit) {
		
	    Comparator comparator = Collections.reverseOrder();	
		Collections.sort(this.extendedHits, comparator);
		
		int list_size = this.extendedHits.size();
		int[] documentIDs = new int[list_size];		
		
		
		if(limit != -1) list_size =  limit;		
		
		for(int i=0;i<list_size;i++) {
			Xhit actualXhit = (Xhit) this.extendedHits.get(i);			
			int actualDocumentID = actualXhit.getDocumentID();
			documentIDs[i] = actualDocumentID;
			//System.out.println("XDocument: "+actualDocument.getField("filename").stringValue()+"  "+actual_Xdocument.getRanking());
		}
		
		return documentIDs;
	}	
	
	
	
	public boolean IsEmpty() {
		return extendedHits.isEmpty();
	}
	
			
	public void printExtendedHits() {
	    for (Iterator it=extendedHits.iterator(); it.hasNext(); ) {
	        Xhit actualXhit = (Xhit) it.next();
	        int actualDocumentID = actualXhit.getDocumentID();	 
	        double actualExtendedDocumentRanking = actualXhit.getRanking();
	        System.out.println("EXT_HIT->docID:"+actualDocumentID+" - ranking:"+actualExtendedDocumentRanking);
	    }		
	}	

}
