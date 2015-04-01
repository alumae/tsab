<#macro layout><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

	<link rel ="stylesheet" type="text/css" href="../css/style.css?ver=1.1" title="Style">

	<#if headExtra?exists>
		${headExtra}
	</#if>
	
	<script type='text/javascript'>
	var ctxpath="${ctxpath}";
	var labelPlay="${loc.control_play}";
	var labelPause="${loc.control_pause}";
	
	var labelCalJanuary="${loc.cal_january}";
	var labelCalFebruary="${loc.cal_february}";
	var labelCalMarch="${loc.cal_march}";
	var labelCalApril="${loc.cal_april}";
	var labelCalMay="${loc.cal_may}";
	var labelCalJune="${loc.cal_june}";
    var labelCalJuly="${loc.cal_july}";
    var labelCalAugust="${loc.cal_august}";
    var labelCalSeptember="${loc.cal_september}";
    var labelCalOctober="${loc.cal_october}";
    var labelCalNovember="${loc.cal_november}";
    var labelCalDecember="${loc.cal_december}";
    
	var labelCalSunday="${loc.cal_sunday}";
	var labelCalMonday="${loc.cal_monday}";
	var labelCalTuesday="${loc.cal_tuesday}";
	var labelCalWednesday="${loc.cal_wednesday}";
	var labelCalThursday="${loc.cal_thursday}";
	var labelCalFriday="${loc.cal_friday}";
    var labelCalSaturday="${loc.cal_saturday}";
	
	</script>

	<script type='text/javascript' src='../scripts/prototype.js'></script>
	<script type='text/javascript' src='../scripts/effects.js'></script>
	<script type='text/javascript' src='../scripts/tools.js'></script>
	<script type='text/javascript' src='../scripts/scal.js'></script>
	<script type='text/javascript' src='../scripts/speech.js?ver=1.1'></script>
	
	<link rel="icon" href="../favicon.ico"/>
	<title><#if title?exists>${title} - </#if>TSAB - ${loc.title_transcribed_speech_archive_browser}</title>


	
</head>
<body>

<div id="main-container">
	<div id="page_navi" class="wrap">
	  <div class="col-right">
	    <ul id="nav">
	      <#if doEnableEdit>
		  <li class="page_item"><a href="account">
		  <#if tsabuser?exists>
		  	${tsabuser.fullName} (${tsabuser.email})
		  <#else>
		  	${loc.login}
		  </#if>
		  </a>	
		  </#if>
	      <li class="page_item"><a href="about">${loc.about}</a>
	      <li class="page_item last"><a href="mailto:${loc.email_address}">${loc.email}</a>
	    </ul>
	  </div>
	</div>

	<div id="heading">
	<a href="index"><img src="../img/tsab-header.gif" alt="" border="0"/></a>
	</div>

	<div id="content-outer">
		<div id="content">
			<div id="left-container">
				
				<div id="samplecal" class="scal tinyscal"></div>
			
				<div id="search">
					<form action="search" method="post" accept-charset="UTF-8"/>
						<input name="q" type="input" size="13"/><input type="submit" value="${loc.search_button}"/>
					</form>
				</div>

				<#list mainCategories as catrow>				
					<a href='category?cat=${catrow.id}' id='category_${catrow.id}' onclick='openATab("${catrow.id}"); return false;'
					class='category'>${catrow.title}</a>
					
					<div id='subcat_${catrow.id}' class='subcat' style="display: <#if activeRootCategory?exists && catrow.id=activeRootCategory.id>block<#else>none</#if>">
						<#list subCats[catrow.id?string] as subrow>
							<#if currentCategoryId?exists && currentCategoryId=subrow.id>
								<a href='category?cat=${subrow.id}' id='subcatlink_${subrow.id}'><b>${subrow.title}</b></a><br/>
							<#else>
								<a href='category?cat=${subrow.id}' id='subcatlink_${subrow.id}'>${subrow.title}</a><br/>
							</#if>
						</#list>
					</div>
					
				</#list>
				
				<script type='text/javascript'>
					var openTab = '';
					var openATab = function(tab) {
						if(tab != openTab)
						{
							if($('subcat_'+openTab)) new Effect.BlindUp('subcat_'+openTab, { duration: 0.3 });
							if($('subcat_'+tab)) new Effect.BlindDown('subcat_'+tab, { duration: 0.3 });
							openTab = tab;
						} else {
							if($('subcat_'+openTab)) new Effect.BlindUp('subcat_'+openTab, { duration: 0.3 });
							openTab = '';
						}
					};
				</script>
			</div>
			<div id="right-container">
				${main}
			</div>
		</div> <!-- end of content -->
	</div> <!-- end of content-outer -->

	<div id="footer">${loc.footer_msg}</div>
	
</div>

<script type='text/javascript'>
Event.observe(window, 'load', function() {

	var day = gup('day');
	var now = new Date();
	
	var options = {
    	titleformat:'mmmm yyyy',
        dayheadlength:2,
        weekdaystart:1,
        planner: false,
        year: day?day.substring(6,10)-0:now.getFullYear(), 
        month: day?day.substring(3,5)-0:now.getMonth()+1,
        day: day?day.substring(0,2)-0:now.getDate()
    };

    samplecal = new scal('samplecal', updatecal, options);

    function updatecal(d){
        window.location='calendar?day='+d.format('dd-mm-yyyy');
    }
});
</script>
</body>
</html>
</#macro>
