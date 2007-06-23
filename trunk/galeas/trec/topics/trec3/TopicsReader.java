package org.galeas.trec.topics.trec3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TopicsReader {

	private String record = null;

	private BufferedReader br;


	public TopicsReader(String pathname) {
		try {
			FileReader fr = new FileReader(pathname);
			br = new BufferedReader(fr);
		} catch (IOException e) {
			// catch possible io errors from readLine()
			System.out.println("Uh oh, got an IOException error!");
			e.printStackTrace();
		}
	}

	private Topic readTopic(int topicID) throws IOException {
		
		Topic topic = new Topic();
		
		record = new String();
		String topicString = "";
		boolean topicFounded = false;
		while ((record = br.readLine()) != null) {

			//System.out.println(record); 
			if (record.indexOf("<num>") != -1
					&& record.indexOf("Number:") != -1
					&& record.indexOf(String.valueOf(topicID)) != -1) {
				topicFounded = true;
			}
			
			
			if(topicFounded) {
				topicString +=record;
				if(record.indexOf("</top>") != -1) {
					
					//System.out.println(topicString);
					int titlePos = topicString.indexOf("<title>");
					int descPos = topicString.indexOf("<desc>");
					int narrPos = topicString.indexOf("<narr>");
					
					String title = topicString.substring(titlePos+7, descPos).replaceFirst("Topic:","").trim();
					
					String desc = topicString.substring(descPos+6, narrPos).replaceFirst("Description:","").trim();
					String narr = topicString.substring(narrPos+6, topicString.length()-6).replaceFirst("Narrative:","").trim();
					
					topic.setId(topicID);
					topic.setTitle(title);
					topic.setDescription(desc);
					topic.setNarrative(narr);
					
					return topic;
				}				
			}
			

			
		}

		return null;
	}

	public void printTopic(int topicID) throws IOException {
		
		Topic topicToPrint = readTopic(topicID);
		System.out.println("id:" + topicToPrint.getId());
		System.out.println("title:" + topicToPrint.getTitle());
		System.out.println("desc:" + topicToPrint.getDescription());
		System.out.println("narr:" + topicToPrint.getNarrative());
	}

	public String getTopicTitle(int topicID) throws IOException {
		Topic topic = readTopic(topicID);
		return topic.getTitle();
	}

	public String getTopicDescription(int topicID) throws IOException {
		Topic topic = readTopic(topicID);
		return topic.getDescription();
	}

	public String getTopicNarrative(int topicID) throws IOException {
		Topic topic = readTopic(topicID);
		return topic.getNarrative();
	}

	public void topicToString() throws IOException {

		record = new String();
		while ((record = br.readLine()) != null) {
			System.out.println(record);
		}
	}

}
