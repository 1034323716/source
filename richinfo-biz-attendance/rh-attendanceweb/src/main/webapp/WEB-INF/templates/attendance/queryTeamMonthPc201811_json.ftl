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
		"attendanceId": "${fm(list.attendanceId?default(''))}",
		"attendanceName": "${fm(list.attendanceName?default(''))}",
		"employeeName": "${fm(list.employeeName?default(''))}",
		"normalDays": "${list.normalDays?default('')}",
		"outsideDays": "${list.outsideDays?default('')}",
		"lateDays": "${list.lateDays?default('')}",
		"lateMinutes": "${list.lateMinutes?default('')}",
		"earlyDays": "${list.earlyDays?default('')}",
		"earlyMinutes": "${list.earlyMinutes?default('')}",
		"goNotClockedDays": "${list.goNotClockedDays?default('')}",
		"leaveNotClockedDays": "${list.leaveNotClockedDays?default('')}",
		"appealDays": "${list.appealDays?default('')}",
        "contactId":"${list.contactId?default('')}",
		"totalWorkTime": "${list.totalWorkTime?default('')}"
		}
		<#if list_has_next>,</#if>
	</#list>
	</#if>
    ]
}