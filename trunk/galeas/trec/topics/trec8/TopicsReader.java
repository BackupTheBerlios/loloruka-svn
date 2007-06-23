package org.galeas.trec.topics.trec8;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;
import java.io.File;
import java.io.IOException;


public class TopicsReader {
	
	private Topics topics;
   
	/*
	public static void main(String[] args) throws IOException, SAXException {
		TopicsReader tr = new TopicsReader(args[0]);
		tr.printTopics();
	}
	*/
	
	public TopicsReader(String topicsDoc) throws IOException, SAXException {
		File inputFile = new File( topicsDoc );
		digest(inputFile);
	}
	
	public TopicsReader(File topicsFile) throws IOException, SAXException {
		digest(topicsFile);
	}
	
	
	public void digest( File topicsFile ) throws IOException, SAXException {

         Digester digester = new Digester();
         digester.setValidating( false );
         
         digester.addObjectCreate( "trec8-topics", Topics.class );
         
         digester.addObjectCreate( "trec8-topics/top", Topic.class );
         digester.addBeanPropertySetter( "trec8-topics/top/num", "num" ); 
         digester.addBeanPropertySetter( "trec8-topics/top/title", "title" );
         digester.addBeanPropertySetter( "trec8-topics/top/desc", "desc" );
         digester.addBeanPropertySetter( "trec8-topics/top/narr", "narr" );         
         digester.addSetNext( "trec8-topics/top", "addTopic" );
                  
         topics = (Topics)digester.parse( topicsFile );
   }
   
   
   public void printTopics() {
	   topics.printTopics();
   }
   
   
	public String getTopicTitle(int topnum) {		
		return topics.getTopic(topnum).getTitle();
	}

	public String getTopicDesc(int topnum) {
		
		return topics.getTopic(topnum).getDesc();
	}
	
	public String getTopicNarr(int topnum) {		
		return topics.getTopic(topnum).getNarr();
	}	   
}