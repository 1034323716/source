<#setting number_format="#">
{
	"code": "${code?default('S_OK')}",
	"summary": "${summary?default('')}",
	"normalCount":"${normalCount?default('')}",
	"earlyCount":"${earlyCount?default('')}",
	"earlyTotalMinutes":"${earlyTotalMinutes?default('')}",
	"lateCount":"${lateCount?default('')}",
	"lateTotalMinutes":"${lateTotalMinutes?default('')}",
	"outSideCount":"${outSideCount?default('')}",
	"notClockedCount":"${notClockedCount?default('')}",
	"attendanceName":"${attendanceName?default('')}",
	"earlyList": [
    	<#if earlyList?exists>
			<#list earlyList as list>
			{
			"earlyTime": "${list.leaveWorkTime?default('')}",
			"earlyMinute": "${list.earlyMinutes?default('')}"
			}
			<#if list_has_next>,</#if>
			</#list>
		</#if>
    ],
    "lateList": [
    	<#if lateList?exists>
			<#list lateList as list>
			{
			"lateTime": "${list.goWorkTime?default('')}",
			"lateMinute": "${list.lateMinutes?default('')}"
			}
			<#if list_has_next>,</#if>
			</#list>
		</#if>
    ],
    "notClockedList": [
    	<#if notClockedList?exists>
			<#list notClockedList as list>
			{
			"attendanceDate": "${list.attendanceDate?string('yyyy-MM-dd')}",
			"attendanceState": "${list.attendanceState?default('')}"
			}
			<#if list_has_next>,</#if>
			</#list>
		</#if>
    ]
}