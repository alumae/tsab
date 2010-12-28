var speech = new Array();
var speakers = new Object();
var speakerCount = 0;
var activeLine = 0;
var doScroll = true;
var scrollTowards = 0;


var curPos = 0; // ms
//var currentSpeechNo = 1;

var schedFrag = -1;
var schedPos = -1;
var schedLine = -1;
var latestFrag = -1;

var editEnabled = false;

var createAudio = function(filename, autoplay) {

	soundManager.onready(function() {
		
		var i=0;
			
		for(i=1;i<=playCount; i++) {
	    
		    // soundManager.destroySound('speech_'+i);
		    var autoload = (i==1);
		    
			soundManager.createSound({
				 id: 'speech_'+i,
				 url: filename+'&fragment='+i,
				 autoLoad: autoload,
				 autoPlay: false,
				 onload: function(success) {
				 
				 	var myid = parseInt(this.sID.split("_")[1]);
				 	if (schedFrag==myid && latestFrag == myid) {
				 		soundManager.stopAll();
				 		this.play();
				 		this.setPosition(schedPos);
				 		setLine(schedLine);
						scrollToView(schedLine);				 		
				 		zlog('Playing scheduled '+myid+' at position '+schedPos+'; state:'+this.readyState);
				 		schedFrag=-1;
				 		schedPos=-1;
				 		schedLine=-1;
				 	}
				 	
				 },
				 whileplaying: function() {
	
					var myid = parseInt(this.sID.split("_")[1]);
					
					if (latestFrag!=myid) {
						condpause(this);
					}
					
					//currentSpeechNo=myid;
					
					// Start preloading next fragment
					if (myid<playCount) {
						var sound = soundManager.getSoundById('speech_'+(myid+1)); // predefined/preloaded sound
						if (sound.readyState==0 || sound.readyState==2) {
							zlog('Preloading next fragment ('+(myid+1)+')');
							condload(sound);
						}
					}
					
					//this.duration -- secs loaded
					moveBar(myid, this.position);
					
					var fragmult = (myid-1)*60*1000; 
					
					if(activeLine < speech.length -1 && (this.position+fragmult) >= speech[activeLine+1][1]) {
						
						setLine(activeLine + 1);
						
						if(doScroll == true) {
							scrollToView(activeLine + 1);
						}
					}
					
					//playNextIfTime(this);
					
				},
				onfinish: function() {
					playNextIfTime(this);
				}
				,
/*				onbeforefinishtime:100,
				onbeforefinish: function() {
					console.log('onbeforefinish triggered. sID='+this.sID+'. playCount='+playCount+'. latestFrag='+latestFrag);
					var myid = parseInt(this.sID.split("_")[1]);					
    				if (myid<playCount && latestFrag == myid) {
						var sound = soundManager.getSoundById('speech_'+(myid+1));
						latestFrag = myid+1;
						soundManager.stopAll();
						sound.play();
						sound.setPosition(0);
						schedFrag=-1;
						schedPos=-1;
						schedLine=-1;
    				}    				
  				}
				*/
				   				
			});
		}
			
	});
		
}

var loadAudio = function(ctxpath, name, autoplay) {
	
	new Ajax.Request(ctxpath+'/p/loadAudio?audio='+name, {
		method:'get',
		onSuccess: function(transport){
		    var json = transport.responseText.evalJSON();
		    speech = json['speech'];
		    activeLine = -1;
		    
		    newSpeech();
		 	
		    createAudio(json['file'], autoplay);
		}
	});
	
}

var submitCorrection = function(ctxpath, tranid, time, newtext) {
	
	new Ajax.Request(ctxpath+'/p/submitcorrection', {
		method:'post',
		parameters:'tranid='+tranid+'&time='+time+'&newtext='+encodeURIComponent(newtext),
		onSuccess: function(transport){
		    //var json = transport.responseText.evalJSON();
		    //speech = json['speech'];
		    //activeLine = -1;		    
		    //newSpeech();		 	
		    //createAudio(json['file'], autoplay);
		}
	});
	
}

var moveBar = function(fragNo, current) {

	var max = playLength*1000;

	var newcur = ((fragNo-1)*60*1000)+current;

	$('position').innerHTML = /*fragNo+'/'+newcur+'/'+ */ toTime(newcur)+" / "+toTime(max);
		
	var maxwidth = $('seekbar').getDimensions().width;
	
	var width = Math.floor(newcur / max * maxwidth);
	
	if(width > maxwidth) width = maxwidth;
	if(width < 0) width = 0;
	
	$('playbar').style.width = width+'px'; 

	curPos = newcur;		
}



var playIt = function() {
	
	var fragment = Math.floor(curPos/60000)+1;
	latestFrag = fragment;
		
	var sound = soundManager.getSoundById('speech_'+fragment); // predefined/preloaded sound
	soundManager.stopAll();
	condload(sound);
	
	var soundPos = curPos - (fragment-1)*60000;
	zlog('Setting playIt position to '+soundPos+'. Sound duration:'+sound.duration+'. ReadyState:'+sound.readyState);

	sound.play();
	sound.setPosition(soundPos);
	
	//var realpos = (currentSpeechNo-1)*60*1000 + sound.position;
	
	setLine(findLine(curPos));

}

var pauseIt = function() {
	//var sound = soundManager.getSoundById('speech_'+currentSpeechNo); // predefined/preloaded sound
	soundManager.stopAll();
	//sound.pause();	
}


var activate = function(i) {
	if($("line_"+i))
		$("line_"+i).addClassName("active");
}
var deactivate = function(i) {
	if($("line_"+i))
		$("line_"+i).removeClassName("active");
}

var setLine = function(i) {

	deactivate(activeLine);
	activate(i);
	activeLine = i;
	
}

var findLine = function(time) {
	
	for(i=0;i<speech.length; i++) {
		if(speech[i][1] > time)
			return i-1 < 0 ? 0 : i-1;
	}
}

var seekLine = function(i) {

	var pos = speech[i][1];	

	if (editEnabled==true) {
		editToggle = false;
		jQuery('#edit_textarea').val(speech[i][2]);
		jQuery('#edit_line').val(i);
		jQuery('#edit_time').val(pos);		
		jQuery('#editdialog').dialog('open');		
		return;
	}

	playLine(i);

}

var playLine = function(i) {
	var pos = speech[i][1];	
	var fragmult = Math.floor(pos/1000/60)+1;
	
	var fragpos = fragmult;
	
	var sound = soundManager.getSoundById('speech_'+fragmult); // predefined/preloaded sound
	//sound.load();

	setLine(i);

	//var soundToPause = soundManager.getSoundById('speech_'+currentSpeechNo);	
	soundManager.stopAll();
	
	var newpos = pos-((fragmult-1)*1000*60);
	//alert('fragmult:'+fragmult+'; newpos:'+newpos);
	
	latestFrag = fragmult;

	if (sound.duration>=newpos) {
		sound.play();
		sound.setPosition(newpos);
		//bufferState('Playing '+fragpos+' at position '+newpos);	
	} else {
		condload(sound);
		//sound.setPosition(newpos);
		schedFrag = fragpos;
		schedPos = newpos;
		schedLine = i;
		
		//bufferState('Scheduling play '+fragpos+' at position '+newpos);
	}
	//bufferState('Playing '+fragmult+' at position '+newpos);
}

// pos -- ms position
var seekPosition = function(pos) {
	
	var fragpos = Math.floor(pos/1000/60)+1;
	
	var sound = soundManager.getSoundById('speech_'+fragpos); // predefined/preloaded sound
	//sound.load();

	line = findLine(pos);	
	setLine(line);
	scrollToView(line);
	

	soundManager.stopAll();
	var newpos = pos-((fragpos-1)*1000*60);

	latestFrag = fragpos;
	
	if (sound.duration>=newpos) {
		sound.play();
		sound.setPosition(newpos);
		zlog('Playing '+fragpos+' at position '+newpos);		
	} else {
		condload(sound);
		//sound.setPosition(newpos);
		schedFrag = fragpos;
		schedPos = newpos;
		schedLine = line;		
		zlog('Scheduling play '+fragpos+' at position '+newpos+';pos:'+pos+';line:'+line);
	}
}

var moveScroll = function() {
	
	if($('lines').scrollTop != scrollTowards && doScroll == true) {
		
		if(Math.abs($('lines').scrollTop - scrollTowards) < 10) {
			$('lines').scrollTop = scrollTowards;
		}
		else {
			$('lines').scrollTop = ($('lines').scrollTop + scrollTowards) / 2; 
		}
		
		window.setTimeout("moveScroll()", 100);
		
	}
	
}

var scrollToView = function(line) {
	
	scrollTowards = $("linetop_"+line).offsetTop - findPosY($('lines')) - ($('lines').offsetHeight / 2) + 10;
	if(scrollTowards < 0) scrollTowards = 0;
	moveScroll();
	
}

var toTime = function(ms) {
	
	ms = Math.floor(ms/1000);
	
	return Math.floor(ms/60)+":"+(ms%60 < 10 ? "0" : "")+(ms%60);
	
}

var newSpeech = function() {

	speakerCount = 0;
	speakers = new Object();

	var lines = "";
	for(i = 0; i < speech.length; i++)
	{
		if(speakers[speech[i][0]] == null) {
			speakers[speech[i][0]] = ++speakerCount;
		}
		
		sameone = (i>0 && speech[i][0]==speech[i-1][0]);
		
		lines += "<div class='topic topic_"+speech[i][3]+"' title='"+speech[i][4]+"' >"+ 
				"<div class='speaker speaker_"+speakers[speech[i][0]]+"' id='linetop_"+i+"' >"+
				"<div class='line"+(sameone?" sameone":"")+"' id='line_"+i+"' onclick='seekLine("+i+"); return false;'>"+
					( !sameone ? "<strong>"+speech[i][0]+":</strong> " : "")+
					speech[i][2]+
				"</div></div></div>";
	}
	$('lines').innerHTML = lines;
	
	Event.observe($("lines"), 'mouseover', function() {  
		doScroll = false;  
	});  
	
	Event.observe($("lines"), 'mouseout', function() {  
		doScroll = true;  
	});  
	
}

var searchPlaying = -1;

var searchPlay = function(filename, id, pos) {
	soundManager.onready(function() {
	    soundManager.destroySound('searchplay1');
	    soundManager.destroySound('searchplay2');
	    
		var fragno = Math.floor(pos/1000/60)+1;
		var relpos = pos-((fragno-1)*60*1000);
		
		//alert('relpos:'+relpos);
				
		soundManager.createSound({
			 id: 'searchplay1',
			 url: filename+'&fragment='+fragno,
			 autoLoad: true,
			 autoPlay: false,
			 onload: function() {
			 
			 this.play();
			 this.setPosition(relpos);
			 //alert('playing at '+relpos);			 
			 	
			 },
			 onfinish: function () {
			 	var sound = soundManager.getSoundById('searchplay2'); // predefined/preloaded sound
			 	sound.play();
			 }
		});
		soundManager.createSound({
			 id: 'searchplay2',
			 url: filename+'&fragment='+(fragno+1),
			 autoLoad: true,
			 autoPlay: false,
			 onfinish: function() {
			 	//this.play();
			 	//this.setPosition(pos);
			 	stopOneLine(id);
			 }
		});

	});
}

var stopOneLine = function(id) {
	
	var sound1 = soundManager.getSoundById('searchplay1');
	var sound2 = soundManager.getSoundById('searchplay2');
	if(sound1) {
		sound1.pause();
		soundManager.destroySound('searchplay1');
	}
	if(sound2) {
		sound2.pause();
		soundManager.destroySound('searchplay2');
	}

	searchPlaying = -1;
	
	$('playbutton_'+id).innerHTML = labelPlay;
}

var playOneLine = function(id, uid, posinfo) {

	oldPlaying = searchPlaying;
	if(searchPlaying != -1) {
		stopOneLine(searchPlaying);
	}
	
	if(oldPlaying != id) {
		
		searchPlay(ctxpath+'/audio/?id='+uid, id, posinfo);

		searchPlaying = id;
		$('playbutton_'+id).innerHTML = labelPause/*+'('+fragno+';'+posinfo+')'*/;
	}
		
}

var condload = function(sound) {
	if (!(sound.loaded && sound.duration>0)) {
		sound.load();
	}
}

var condpause = function(sound) {
	if (!(sound.loaded && sound.duration>0)) {
		sound.pause();
	}	
}

var playNextIfTime = function(currentSound) {

	// Less than 100ms until the end of the clip. start playing next
	
	//if (currentSound.readyState==3 && currentSound.duration>0 && currentSound.position>0 && (currentSound.duration-currentSound.position)==0)
	{
		zlog('playnext triggered. sID='+currentSound.sID+'. Position='+currentSound.position+'. Duration='+currentSound.duration);
		var myid = parseInt(currentSound.sID.split("_")[1]);					
		if (myid<playCount && latestFrag == myid) {
			var sound = soundManager.getSoundById('speech_'+(myid+1));
			latestFrag = myid+1;
			soundManager.stopAll();
			sound.play();
			sound.setPosition(0);
			schedFrag=-1;
			schedPos=-1;
			schedLine=-1;
		}	
	}

 }
 
 var zlog = function(text) {
 	if ( typeof(zebug)!='undefined' && typeof(window.console)!='undefined' && typeof(window.console.log)!='undefined') {
 		window.console.log(text);
 	}
 }
 
 var donothing = function() {
 }
 
 var toggleEdit = function() {
 	editEnabled = !editEnabled;
 	
 	if (editEnabled) {
 		$('editLink').innerHTML=loc_corr_stop_edit;
 	} else {
 		$('editLink').innerHTML=loc_corr_edit;
 	}
 }
 
 var editSpeechLine = function(i, newtext) { 	
 	speech[i][2]=newtext;
 	 
	sameone = (i>0 && speech[i][0]==speech[i-1][0]);
	line = ( !sameone ? "<strong>"+speech[i][0]+":</strong> " : "")+speech[i][2];
 	$('line_'+i).innerHTML = line;
 }
 