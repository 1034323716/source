<#setting number_format="#">
{
    "code": "${code?default('S_OK')}",
    "summary": "${summary?default('')}",
     "var": {
	<#if teamDailyReport?exists>
		"normalCount": ${teamDailyReport.normalCount?default(0)},
		"outsideCount": ${teamDailyReport.outsideCount?default(0)},
        "lateCount": ${teamDailyReport.lateCount?default(0)},
        "earlyCount": ${teamDailyReport.earlyCount?default(0)},
        "notClockedCount": ${teamDailyReport.notClockedCount?default(0)}
	</#if>
    }
}