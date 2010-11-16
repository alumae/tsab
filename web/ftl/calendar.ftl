<#global tab>index</#global>
<#global title>Home</#global>
<#global main>

<h1>${date?string("dd.MM.yyyy")}</h1>
<#list trans as row>
	<li>
		<#if row.category?exists>
			${statics["ee.ioc.phon.tsab.web.TsabWebUtil"].getCategoryBreadcrumb(row.category)} &gt;			
		</#if>
		 <a href="play?trans=${row.id}"><#if row.title?exists>${row.title}<#else>Untitled</#if></a>
</#list>

</#global>
<#import "layout.ftl" as lay>
<@lay.layout/>
