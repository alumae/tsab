<#global tab>index</#global>
<#global title><#if transcription.title?exists>${transcription.title}<#else>${loc.trans_untitled}</#if></#global>

<#global headExtra>

<link type="text/css" href="../css/ui-lightness/jquery-ui-1.8.7.custom.css" rel="stylesheet" />	
<script type="text/javascript" src="../js/jquery-1.4.4.min.js"></script>
<script type="text/javascript" src="../js/jquery-ui-1.8.7.custom.min.js"></script>
<script type="text/javascript" src="../js/jquery.editinplace.js"></script>
<script type="text/javascript" src="../js/jquery.hotkeys-0.8.js"></script>

<script type="text/javascript">
jQuery.noConflict();
</script>

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

var loc_corr_edit = '${loc.play_correction_edit}';
var loc_corr_stop_edit = '${loc.play_correction_stop_edit}';

var loc_corr_saving = '${loc.play_correction_saving}';

var transcriptionId = ${transcription.id};

var userExists = <#if tsabuser?exists>true<#else>false</#if>;

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
		<div class="topic_nav topic_${topic.transientSeq}" style="cursor:pointer;" onclick="javascript:seekPosition(<#if topic.time?exists>${topic.time}<#else>0</#if>);"><#if topic.transientTimeStr?exists>${topic.transientTimeStr}<#else>00:00</#if> <span class="topic_name">${topic.topicName}</span></div>
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

	var speechId = '${transcription.id}';//gup('play');

	loadAudio("${ctxpath}", speechId, true);
	
	$('position').innerHTML = toTime(0)+" / "+toTime(playLength*1000);	

	var editToggle = false;
	
	// Dialog			
	jQuery('#editdialog').dialog({
		autoOpen: false,
		width: 600,
		modal: true,
		buttons: {
			"${loc.play_correction_playstop}": function() {
				editToggle=!editToggle;
				if (editToggle==true) { 
					playLine(jQuery('#edit_line').val());
				} else {
					soundManager.stopAll();
				}				
			}, 
			"${loc.play_correction_save}": function() {
				//in-place replace
				
				line = jQuery('#edit_line').val();				
				newtext = jQuery('#edit_textarea').val();
				
				editSpeechLine(line, newtext);
				
				//submit contribution to server
				//alert('submitting change to server');

				tranid = ${transcription.id};
				time = jQuery('#edit_time').val();
		
				submitCorrection('${ctxpath}', tranid, time, newtext);
				
				jQuery(this).dialog("close"); 
			}, 
			"${loc.play_correction_cancel}": function() { 
				jQuery(this).dialog("close"); 
			} 
		}
	});

});

</script>	

<p>${loc.play_count}: <#if transcription.viewCount?exists>${transcription.viewCount}</#if><br/>

<#if enableEdit = "true">
<#if tsabuser?exists><a href="javascript:donothing();" onclick="javascript:toggleEdit();"><span id="editLink">
${loc.play_correction_edit}
</span></a> ${loc.play_correction_legend}
<#else>
${loc.play_correction_login_required}
</#if>
</#if>

</p>

<#if relatedRecordings?exists>
<h1>${loc.heading_related_recordings}</h1>
${relatedRecordings}
</#if>

<#else>
<h1>Transcription with the given id not found!</h1>
</#if>

<div style="position: relative; width: 96%; height: 200px; padding:1% 4%; overflow:hidden;" class="fakewindowcontain">
<div id="editdialog" title="${loc.play_correction_dialog_title}">
	<form>
		<input type="hidden" id="edit_line" name="edit_line"/>
		<input type="hidden" id="edit_time" name="edit_time"/>
		<textarea id="edit_textarea" name="edit_textarea" cols="70" rows="5"></textarea>
	</form>
</div>
</div>

</#global>
<#import "layout.ftl" as lay>
<@lay.layout/>
