package org.galeas.test;

import java.io.IOException;
import java.text.ParseException;

import org.galeas.trec.topics.trec3.TopicsReader;
public class TestingFormat {
	
	public static void main(String args[]) throws ParseException, IOException{
		
		TopicsReader topicReader = new TopicsReader("C:\\TEMP\\TREC3-topics-151-200.txt");
		topicReader.printTopic(180);
	}
	
}
