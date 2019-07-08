<#setting number_format="#">
{
	"code": "${code?default('S_OK')}",
	"summary": "${summary?default('')}",
	"var": {
		"attendanceId": "${attendanceId?default(0)}",
		"attendanceName": "${fm(attendanceName?default(''))}",
		"enterId":"${enterId?default('')}",
		"enterName": "${fm(enterName?default(''))}",
		"amTime": "${amTime?default('')}",
		"pmTime": "${pmTime?default('')}",
		"outRange": "${attendanceOutRange?default(0)}",
		"examineName":"${examineName?default('')}",
		"examineUid":"${examineUid?default('')}",
		"examineContactId":"${examineContactId?default('')}",
		"attendType":"${attendType?default('')}",
		"fixedAttendRule":"${fixedAttendRule?default('')}",
		"freeAttendRule":"${freeAttendRule?default('')}",
		"allowLateTime":"${allowLateTime?default('0')}",
		"relyHoliday":"${relyHoliday?default('')}",
        "adminName": "${adminName?default('')}",
        "isAllowedOutRangeClock":"${isAllowedOutRangeClock?default('0')}",
        "useFlexibleRule":"${useFlexibleRule?default('0')}",
        "flexitime":"${flexitime?string("#####.#####")?default('0')}",
        "adminContactId": "${adminContactId?default('')}",
        "chargemanList": [
        <#if chargemanList?exists>
            <#list chargemanList as list>
            {
            "euserId": "${list.uid?default('')}",
            "contactId": "${list.contactId?default('')}",
            "name": "${fm(list.employeeName?default(''))}",
            "enterpriseId":"${enterId?default('')}"
            }
            <#if list_has_next>,</#if>
            </#list>
            </#if>
        ],
		"employees": [
			<#if employees?exists>
			<#list employees as list>
			{
				"euserId": "${list.uid?default('')}",
				"contactId": "${list.contactId?default('')}",
				"name": "${fm(list.employeeName?default(''))}",
				"enterpriseId":"${enterId?default('')}"
			}
			<#if list_has_next>,</#if>
			</#list>
			</#if>
		],
		"departments": [
		<#if attendDepartmentChoosers?exists>
			<#list attendDepartmentChoosers as list>
			{
			"departmentId": "${list.departmentId?default(0)}",
			"departmentName": "${fm(list.departmentName?default(''))}",
			"enterpriseId":"${list.enterpriseId?default('')}"
			}
				<#if list_has_next>,</#if>
			</#list>
		</#if>
		],
		"attendClockSites":[
			<#if attendClockSites?exists>
				<#list attendClockSites as list>
				{
                "location": "${fm(list.location?default(''))}",
                "detailAddr": "${fm(list.detailAddr?default(''))}",
                "longitude":"${list.longitude?string(".##########")?default('')}",
                "latitude":"${list.latitude?string(".##########")?default('')}",
                "range": "${list.attendanceRange?default(0)}"
				}
					<#if list_has_next>,</#if>
				</#list>
			</#if>
		]
	}
}
