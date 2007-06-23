package org.galeas.trec.topics.trec8;

public class Topic {
	private String num;	
	private String title;
	private String desc;
	private String narr;

	public Topic() {
		
	}
		
	
	public String getDesc() {		
		return desc;
	}


	public void setDesc(String desc) {
		this.desc = desc;
	}


	public String getNarr() {
		return narr;
	}


	public void setNarr(String narr) {
		//System.out.println("narr:"+narr);
		this.narr = narr;
	}


	public String getNum() {
		return num;
	}


	public void setNum(String num) {		
		this.num = num;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String toString() {
		
		StringBuffer buf = new StringBuffer();
		buf.append("num\t: " + this.getNum()+"\n");
		buf.append("title\t: " + this.getTitle()+"\n");
		buf.append("desc\t: " + this.getDesc()+"\n");
		buf.append("narr\t: " + this.getNarr()+"\n");
		buf.append("-----------------------------------");
		
		return buf.toString();
	}

	


}
