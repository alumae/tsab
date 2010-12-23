{
	"name": "${t.title?replace("\"", "\\\"")}",
	"file": "${ctxpath}/audio/?id=${t.id}",
	"speech": [
	<#list speech as s>
		["${s.author?replace("\"", "\\\"")}", "${s.time}", "${s.text?replace("\"", "\\\"")}", "${s.transientTopicSeq}",<#if s.transientTopicDesc?exists>"${s.transientTopicDesc}"<#else>""</#if>]<#if s_has_next>,</#if>
	</#list>
	]
}
