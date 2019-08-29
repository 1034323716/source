<#setting number_format="#">
{
	"code": "${code?default('S_OK')}",
	"summary": "${summary?default('')}",
	"pageNo":"${pageNo?default('')}",
	"pageSize":"${pageSize?default('')}",
	"totalCount":"${totalCount?default('')}",
	"var": [
    	<#if data?exists>
		<#list data as list>
		{
        "deptName":"${fm(list.deptName?default(''))}",
		"attendanceId": "${fm(list.attendanceId?default(''))}",
		"attendanceName": "${fm(list.attendanceName?default(''))}",
		"employeeName": "${fm(list.employeeName?default(''))}",
		"normalDays": "${list.normalDays?default('')}",
		"outsideDays": "${list.outsideDays?default('')}",
		"lateDays": "${list.lateDays?default('')}",
		"earlyDays": "${list.earlyDays?default('')}",
		"notClockedDays": "${list.notClockedDays?default('')}",
		"appealDays": "${list.appealDays?default('')}",
		"totalWorkTime": "${list.totalWorkTime?default('')}"
		}
		<#if list_has_next>,</#if>
	</#list>
	</#if>
    ]
}