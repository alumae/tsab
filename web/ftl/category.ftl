<#global tab>category</#global>
<#global title><#if category?exists>${category.title}<#else>Category not found</#if></#global>
<#global main>

<#if category?exists>
<h1>${category.title}</h1>

<#if category.parent?exists>
<p class="breadcrumb">${statics["ee.ioc.phon.tsab.web.TsabWebUtil"].getCategoryBreadcrumb(category.parent)}</p>
</#if>

<#if subCatList?exists>
	<!-- <h2>Categories</h2> -->
	<ul>
	<#list subCatList as row>
		<li> <a href="category?cat=${row.id}">${row.title}</a><br/>
	</#list>
	</ul>
</#if>

<#if transList?exists>
	<h2>${loc.heading_transcriptions}</h2>
	<ul>
	<#list transList as row>
		<li> <a href="play?trans=${row.id}">${row.title}</a>
	</#list>
	</ul>
</#if>

</#if>

</#global>
<#import "layout.ftl" as lay>
<@lay.layout/>
