<#global tab>login</#global>
<#global main>
<h1>${loc.users_title}</h1>

<table width="100%" border="0">
<#list users as user>
<tr><td>${user.fullName}</td><td>${user.email}</td>
<td>
<#if user.role=="0super"><b>Super</b></#if>
<#if user.role=="1power"><b>Power</b></#if>
<#if user.role=="2regular"><b>Regular</b></#if><br/>
${loc.users_set_role_to}<br/>
<#if user.role!="0super"><a href="users?grantUserId=${user.id}&grantRole=0super">Super</a></#if>
<#if user.role!="1power"><a href="users?grantUserId=${user.id}&grantRole=1power">Power</a></#if>
<#if user.role!="2regular"><a href="users?grantUserId=${user.id}&grantRole=2regular">Regular</a></#if>
</td>
</tr>
</#list>
</table>

</#global>
<#import "layout.ftl" as lay>
<@lay.layout/>
