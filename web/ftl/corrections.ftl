<#global tab>corrections</#global>

<#global headExtra>

<#if zebug?exists>
    <script type="text/javascript">
	  var zebug = true;
	</script>
	<script type="text/javascript" src="../soundmanager/script/soundmanager2.js"></script>
<#else>
    <script type="text/javascript">
	  var zebug = false;
	</script>
	<script type="text/javascript" src="../soundmanager/script/soundmanager2-nodebug-jsmin.js"></script>
</#if>

<script type="text/javascript">
soundManager.url = '../soundmanager/swf/';
</script>
</#global>

<#global main>
<h1>${loc.correction_pending_corrections}</h1>

<#if corrections?has_content>
<#list corrections as corr>
<p>
<b><a href="play?trans=${corr.fragment.transcription.id}">${corr.fragment.transcription.title}</a></b> @
<script type="text/javascript">document.write(toTime(${corr.fragment.time}));</script>
<br/>
<b>${loc.correction_submitted_by}:</b> ${corr.user.fullName} (${corr.user.email}) @ ${corr.submissionDate}.<br/>
<#if corr.fragment.text!=corr.fragment.originalText>
<b>${loc.correction_original}:</b>${corr.fragment.originalText}<br/>
</#if>
<b>${loc.correction_current}:</b>${corr.fragment.text}<br/>
<b>${loc.correction_correction}:</b> ${corr.text}<br/>
<a href='javascript:donothing();' id='playbutton_${corr_index}'
onclick='playOneLine(${corr_index},${corr.fragment.transcription.id},${corr.fragment.time})'>${loc.control_play}</a>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<a href="?accept=${corr.id}">${loc.correction_accept}</a>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<a href="?reject=${corr.id}">${loc.correction_reject}</a></td>
</p>

</#list>
<#else>
${loc.correction_no_pending}
</#if>
</#global>
<#import "layout.ftl" as lay>
<@lay.layout/>
