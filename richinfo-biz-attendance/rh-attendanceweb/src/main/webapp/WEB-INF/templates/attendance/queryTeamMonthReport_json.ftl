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
		"attendanceId": ${list.attendanceId?default(0)},
		"attendanceName": "${fm(list.attendanceName?default(''))}",
		"employeeName": "${fm(list.employeeName?default(''))}",
		"normalDays": ${list.normalDays?default(0)},
		"outsideDays": ${list.outsideDays?default(0)},
		"lateDays": ${list.lateDays?default(0)},
		"earlyDays": ${list.earlyDays?default(0)},
		"notClockedDays": ${list.notClockedDays?default(0)}
		}
		<#if list_has_next>,</#if>
	</#list>
	</#if>
    ]
}