{
	"name": "${t.title?replace("\"", "\\\"")}",
	"file": "${ctxpath}/audio/?id=${t.id}",
	"speech": [
	<#list speech as s>
		["${s.author?replace("\"", "\\\"")}", "${s.time}", "${s.text?replace("\"", "\\\"")}"]<#if s_has_next>,</#if>
	</#list>
	]
}
