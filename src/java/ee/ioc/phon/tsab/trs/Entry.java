package ee.ioc.phon.tsab.trs;

import ee.ioc.phon.tsab.domain.TranscriptionFragment;

public class Entry {

	int time;
	String author;
	String line;
	String topic;
	
	public Entry(int time, String author, String line, String topic) {
		this.time = time;
		this.author = author;
		this.line = line;
		this.topic = topic;
	}
	
	public TranscriptionFragment generateTranscriptionFragment() {	  
	  TranscriptionFragment t = new TranscriptionFragment();
	  t.setTime(new Long(time));
	  t.setAuthor(author);
	  t.setText(line);
	  t.setTransientTopicDesc(topic);
		return t;		
	}
	
}
