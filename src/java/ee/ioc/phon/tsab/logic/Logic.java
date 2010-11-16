package ee.ioc.phon.tsab.logic;

import java.util.Calendar;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Vector;

public class Logic {
/*
    public void checkAttributes(Attributes a, Transaction t) throws LogicException {

    	// update the "viewed" count
    	if(a.hasAttribute("play")) {
    		
    		Vector v = null;
    		Pointer play = null;
    		try {
	    		play = new Pointer("speech.Transcription", (String)a.getAttribute("play"));
	    		
	    		v = t.executeQuery("select t.viewed as viewed from speech.Transcription t where t=$1", play);
    		} catch(Exception e) {
	    		v = t.executeQuery("select t as play, t.viewed as viewed from speech.Transcription t where t.code=$1", (String)a.getAttribute("play"));
    			play = (Pointer)((Dictionary) v.elementAt(0)).get("play");
    		}
    		if(v.size() > 0) {
    			int viewed = ((Dictionary) v.elementAt(0)).get("viewed") == null ? 0 : (Integer)((Dictionary) v.elementAt(0)).get("viewed");
    			
    			Hashtable update = new Hashtable();
    			update.put("viewed", viewed+1);
    			t.update(play, update);
    		}
    		
    	}
    	
    	
    }*/

}
