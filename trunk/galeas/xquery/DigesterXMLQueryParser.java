package org.galeas.xquery;

import java.io.File;
import java.io.IOException;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

public class DigesterXMLQueryParser {

    /**
     * Prints the contact information to standard output.
     *
     * @param contact the <code>Topic</code> to print out
     */
    public void printTopic(Topic topic)
    {
        System.out.println("NUM   : " + topic.getNum());
        System.out.println("TITLE : " + topic.getTitle());
        System.out.println("DESC  : " + topic.getDesc());
        System.out.println("NARR  : " + topic.getNarr());
    }	
	
    
    /**
     * Configures Digester rules and actions, parses the XML file specified
     * as the first argument.
     *
     * @param args command line arguments
     */    
	public static void main(String[] args) throws IOException, SAXException {
		
		// instantiate Digester and disable XML validation
		Digester dig = new Digester();
	    dig.setValidating(false);	
	    
	    // instantiate DigesterXMLHandler class
	    dig.addObjectCreate("trec8-topics", DigesterXMLQueryParser.class);	    
	    
	    // instantiate Contact class
	    dig.addObjectCreate("trec8-topics/top", Topic.class);	    

	    
        // set different properties of Topic instance using specified methods
	    dig.addCallMethod("trec8-topics/top/num", "setNum", 0);
	    dig.addCallMethod("trec8-topics/top/title", "setTitle", 0);
	    dig.addCallMethod("trec8-topics/top/desc", "setDesc", 0);
	    dig.addCallMethod("trec8-topics/top/narr", "setNarr", 0);
    
	    // call 'printTopic' method when the next 'topics/top' pattern is seen
        dig.addSetNext("trec8-topics/top", "printTopic" );	    
	   	    
        // now that rules and actions are configured, start the parsing process
        DigesterXMLQueryParser abp = (DigesterXMLQueryParser) dig.parse(new File(args[0]));        
	}
	
	
	public static class Topic {
		private String num;
		private String title;
		private String desc;
		private String narr;
		
		
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
		
		
		
	}
	
	
}
