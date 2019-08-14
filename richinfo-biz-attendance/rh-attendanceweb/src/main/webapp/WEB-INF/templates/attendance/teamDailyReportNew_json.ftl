<#setting number_format="#">
{
    "code": "${code?default('S_OK')}",
    "summary": "${summary?default('')}",
     "var": {
	<#if teamDailyReport?exists>
		"outsideCount": ${teamDailyReport.outsideCount?default(0)},
        "lateCount": ${teamDailyReport.lateCount?default(0)},
        "earlyCount": ${teamDailyReport.earlyCount?default(0)},
        "goNotClockedDays": ${teamDailyReport.goNotClockedCount?default(0)},
        "leaveNotClockedDays": ${teamDailyReport.leaveNotClockedCount?default(0)}
	</#if>
    }
}
