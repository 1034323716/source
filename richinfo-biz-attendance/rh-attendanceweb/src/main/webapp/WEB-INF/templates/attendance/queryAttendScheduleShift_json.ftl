<#setting number_format="#">
{
    "code": "${code?default('S_OK')}",
    "summary": "${summary?default('')}",
    "pageNo":${pageNo?default(0)},
	"pageSize":${pageSize?default(0)},
	"totalCount":${totalCount?default(0)},
    "var": [
    	<#if data?exists>
	 		<#list data as list>
	 		{
	 			"scheduleShiftId":"${list.scheduleShiftId?default('')}",
		 		"scheduleShiftName":"${list.scheduleShiftName?default('')}",
				"workTime":"${list.workTime?default('')}"
	 		}
			<#if list_has_next>,</#if>
			</#list>
		</#if>
    ]
}
