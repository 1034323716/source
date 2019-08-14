<#setting number_format="#">
{
	"code": "${code?default('S_OK')}",
	"summary": "${summary?default('')}",
	"var":
    	<#if data?exists>
		<#list data as list>
		{
		"goNotClockedDays": "${fm(list.goNotClockedDays?default(''))}",
		"leaveNotClockedDays": "${fm(list.leaveNotClockedDays?default(''))}",
		"lateDays": ${list.lateDays?default(0)},
		"outsideDays": ${list.outsideDays?default(0)},
		"earlyDays": ${list.earlyDays?default(0)}
		}
		<#if list_has_next>,</#if>
	</#list>
	</#if>
}
