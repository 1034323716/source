<#setting number_format="#">
{
		"code": "${code?default('S_OK')}",
		"summary": "${summary?default('')}",
		"clockStatue":${clockStatue?default(0)},
		"var": [
		<#if attendClockVos?exists>
			<#list attendClockVos as list>
			{
            "location":"${list.location?default('')}",
            "time":${list.time?default(0)},
			"regionStatus":${list.regionStatus?default(0)},
            <#--"outWorkRemark":${list.outWorkRemark?default('')},-->
			"status":${list.status?default(0)},
            "amPmStatue":${list.amPmStatue?default(0)}
			}
				<#if list_has_next>,</#if>
			</#list>
		</#if>
		]
}
