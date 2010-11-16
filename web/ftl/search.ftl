<#global tab>index</#global>
<#global title>${loc.search_title}</#global>

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

<h1>${loc.search_title}</h1>

${searchResult}

</#global>
<#import "layout.ftl" as lay>
<@lay.layout/>
