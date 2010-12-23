<#global tab>index</#global>
<#global title><#if transcription.title?exists>${transcription.title}<#else>${loc.trans_untitled}</#if></#global>

<#global headExtra>

<#if zebug?exists>
	<script type='text/javascript'>
	 	var zebug = true;
	</script>
	<script type="text/javascript" src="../soundmanager/script/soundmanager2.js"></script>
<#else>
	<script type='text/javascript'>
		var zebug = false;
	</script>		
	<script type="text/javascript" src="../soundmanager/script/soundmanager2-nodebug-jsmin.js"></script>
</#if>

<script type="text/javascript">
soundManager.url = '../soundmanager/swf/';

<#if zebug?exists>
soundManager.debugMode = true;
</#if>

var playCount = ${playCount};
var playLength = ${playLength}; // in seconds

</script>
</#global>

<#global main>

<#if transcription?exists>
<h1>
<#if transcription.title?exists>
${transcription.title}
<#else>
${loc.trans_untitled}
</#if>
</h1>
<p>
<#if transcription.category?exists>
	<#if transcription.category.parent?exists>
		<a href="category?cat=${transcription.category.parent.id}">${transcription.category.parent.title}</a> &gt;
	</#if>
	<a href="category?cat=${transcription.category.id}">${transcription.category.title}</a>
</#if>
	
</p>

<#if topics?exists>
	<p><b>${loc.topics}</b></br/>
	<#list topics as topic>
		<div class="topic_nav topic_${topic.transientSeq}" style="cursor:pointer;" onclick="javascript:seekPosition(<#if topic.time?exists>${topic.time}<#else>0</#if>);"><#if topic.transientTimeStr?exists>${topic.transientTimeStr}<#else>-</#if> <span class="topic_name">${topic.topicName}</span></div>
	</#list>
	</p>
</#if>

<div id='playcomponent'>
	<div id='seekbar'><div id='playbar'></div></div>
	<a href='#' onclick='playIt(); return false;'>${loc.control_play}</a>
	<a href='#' onclick='pauseIt(); return false;'>${loc.control_pause}</a>
	<span id='position'></span>
</div>

<div id='lines'>
</div>

<script type='text/javascript'>

$('seekbar').observe('mousedown', function(e) {

	var containerLeft = Position.page($('seekbar'))[0];

	var width = Event.pointerX(e) - containerLeft; 
	var maxwidth = $('seekbar').getDimensions().width;
	
	//var sound = soundManager.getSoundById('speech');

	seekPosition(Math.floor(playLength*1000 * (width/maxwidth)));
	
});


Event.observe(window, 'load', function() {

	var speech = '${transcription.id}';//gup('play');

	loadAudio("${ctxpath}", speech, true);
	
	$('position').innerHTML = toTime(0)+" / "+toTime(playLength*1000);
	
});

</script>	

<p>${loc.play_count}: <#if transcription.viewCount?exists>${transcription.viewCount}</#if></p>

<#if relatedRecordings?exists>
<h1>${loc.heading_related_recordings}</h1>
${relatedRecordings}
</#if>

<#else>
<h1>Transcription with the given id not found!</h1>
</#if>

</#global>
<#import "layout.ftl" as lay>
<@lay.layout/>
