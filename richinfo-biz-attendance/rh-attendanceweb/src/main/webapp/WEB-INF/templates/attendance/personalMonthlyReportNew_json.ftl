<#setting number_format="#">
{
    "code": "${code?default('S_OK')}",
    "summary": "${summary?default('')}",
    "var": [
	<#if employeeMonthDetailVO?exists>
		<#list employeeMonthDetailVO as temp>
		{
			"goWorkNoClockCount":"${temp.goNotClockedDays?default('')}",
			"leaveWorkNoClockCount":"${temp.leaveNotClockedDays?default('')}",
			"lateClockCount":"${temp.lateDays?default('')}",
			"lateMinutes":"${temp.lateMinutes?default('')}",
			"earlyClockCount":"${temp.earlyDays?default('')}",
			"earlyMinutes":"${temp.earlyMinutes?default('')}",
			"appealCount":"${temp.appealDays?default('')}"
		}
		<#if temp_has_next>,</#if>
		</#list>
	</#if>
    ]
}

