<#global tab>index</#global>
<#global main>

<h1>${loc.heading_recently_added}</h1>
<ul>
<#list recentlyAdded as row>
	<li>
		<#if row.category?exists>
			${statics["ee.ioc.phon.tsab.web.TsabWebUtil"].getCategoryBreadcrumb(row.category)} &gt;			
		</#if>
		 <a href="play?trans=${row.id}"><#if row.title?exists>${row.title}<#else>Untitled</#if></a>
</#list>
</ul>
<h1>${loc.heading_most_popular}</h1>
<ul>
<#list mostPopular as row>
	<li>
		<#if row.category?exists>
			${statics["ee.ioc.phon.tsab.web.TsabWebUtil"].getCategoryBreadcrumb(row.category)} &gt;	
		</#if>
		<a href="play?trans=${row.id}"><#if row.title?exists>${row.title}<#else>Untitled</#if></a>
</#list>
</ul>
</#global>
<#import "layout.ftl" as lay>
<@lay.layout/>
