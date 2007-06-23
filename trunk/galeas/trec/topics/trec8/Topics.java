package org.galeas.trec.topics.trec8;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Topics {

	private List topicsList = new ArrayList();
		
	public void addTopic(Topic topic) {
		topicsList.add(topic);
	}
	
	public List getTopicList() {
		return topicsList;
	}
	
	public void printTopics() {
		Iterator it = topicsList.iterator();
		while (it.hasNext()) {
			Topic actualTopic = (Topic) it.next();
			System.out.println(actualTopic.toString());
		}
	}
	
	
	public Topic getTopic(int topnum) {
		
		Iterator it = topicsList.iterator();
		while (it.hasNext()) {
			Topic actualTopic = (Topic) it.next();
			 
			if(Integer.valueOf(actualTopic.getNum()).intValue() == topnum) {
				return actualTopic;
			}
		}		
		return null;		
	}
	
	public String getTopicTitle(int topnum) {		
		return this.getTopic(topnum).getTitle();
	}

	public String getTopicDesc(int topnum) {
		
		return this.getTopic(topnum).getDesc();
	}
	
	public String getTopicNarr(int topnum) {		
		return this.getTopic(topnum).getNarr();
	}	
	
	
}
