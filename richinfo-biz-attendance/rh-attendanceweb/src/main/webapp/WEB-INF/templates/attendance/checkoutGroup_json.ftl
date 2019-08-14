<#setting number_format="#">
{
    "code": "${code?default('S_OK')}",
    "summary": "${summary?default('')}",
    "var": [
    	<#if data?exists>
	 <#list data as list>
	 {
       	"attendanceName": "${list.attendanceName?default('')}"
	}
	<#if list_has_next>,</#if>
	</#list>
	</#if>
    ]
}
