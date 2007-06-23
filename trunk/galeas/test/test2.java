package org.galeas.test;

import org.galeas.xsearch.QueryStatsOnResults;
import org.galeas.xsearch.RqueryTerm;

public class test2 {

	public static void main(String[] args) throws Exception {
		
		QueryStatsOnResults rq = new QueryStatsOnResults();
		RqueryTerm[] RqueryTermArray = new RqueryTerm[8];
		RqueryTermArray[0]  = new RqueryTerm("hola",5);
		RqueryTermArray[1]  = new RqueryTerm("chao",2);
		RqueryTermArray[2]  = new RqueryTerm("hola",1);
		RqueryTermArray[3]  = new RqueryTerm("chao",3);
		RqueryTermArray[4]  = new RqueryTerm("oso",3);
		RqueryTermArray[5]  = new RqueryTerm("pez",23);
		RqueryTermArray[6]  = new RqueryTerm("oso",37);
		RqueryTermArray[7]  = new RqueryTerm("chao",3);
		
		for(int i=0;i<RqueryTermArray.length;i++) {
			rq.addQueryTerm(RqueryTermArray[i]);
		}
		
		System.out.println(rq.toString());
	}
}
