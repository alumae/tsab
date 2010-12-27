<#global tab>login</#global>
<#global main>

<h1>${loc.account_account_operations}</h1>
<ul>

<#if tsabuser?exists && tsabuser.role=="0super">
	<li><a href="users">${loc.account_manage_users}</a>
	<li><a href="corrections">${loc.account_pending_corrections}</a>
</#if>

<li><a href="account?logout=true">${loc.account_log_out}</a>

</ul>

</#global>
<#import "layout.ftl" as lay>
<@lay.layout/>
