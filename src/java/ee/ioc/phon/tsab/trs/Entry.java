package ee.ioc.phon.tsab.trs;

import ee.ioc.phon.tsab.domain.TranscriptionFragment;

public class Entry {

	int time;
	String author;
	String line;
	
	public Entry(int time, String author, String line) {
		this.time = time;
		this.author = author;
		this.line = line;
	}
	
	public TranscriptionFragment generateTranscriptionFragment() {	  
	  TranscriptionFragment t = new TranscriptionFragment();
	  t.setTime(new Long(time));
	  t.setAuthor(author);
	  t.setText(line);
		return t;		
	}
	
}
