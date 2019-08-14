<#setting number_format="#">
{
    "code": "${code?default('S_OK')}",
    "summary": "${summary?default('')}",
    "charge": "${charge?default('0')}",
    <#--"allowOutRangeClock": "${allowOutRangeClock?default('0')}",-->
    "var": {
	    <#if userGroup?exists>
			"enterId": "${userGroup.enterId?default('')}",
			"enterName": "${userGroup.enterName?default('')}",
			"id": "${userGroup.uid?default('')}",
		    "employeeName": "${fm(userGroup.employeeName?default(''))}",
			"phone": "${userGroup.phone?default('')}",
			"employeeId":"${userGroup.employeeId?default(0)}",
			"status":${userGroup.isAdmin?default(0)},
            "roleType":${userGroup.roleType?default(0)},
		    "attendanceId": "${userGroup.attendanceId?default(0)}",
		    "attendanceName": "${fm(userGroup.attendanceName?default(''))}",
			"outRange": "${userGroup.attendanceOutRange?default(0)}",
			"examineName":"${userGroup.examineName?default('')}",
			"examineUid":"${userGroup.examineUid?default('')}",
			"workdayStatus":"${userGroup.workdayStatus?default('0')}",
			"attendType":"${userGroup.attendType?default('')}",
			"fixedAttendRule":"${userGroup.fixedAttendRule?default('')}",
			"freeAttendRule":"${userGroup.freeAttendRule?default('')}",
			"allowLateTime":"${userGroup.allowLateTime?default('0')}",
			"relyHoliday":"${userGroup.relyHoliday?default('')}",
            "isAllowedOutRangeClock":"${userGroup.isAllowedOutRangeClock?default('0')}",
            "useFlexibleRule":"${userGroup.useFlexibleRule?default('0')}",
            "flexitime":"${userGroup.flexitime?default('')}",
			"attendClockSites":[
			<#if userGroup.attendClockSites?exists>
				<#list userGroup.attendClockSites as list>
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
		</#if>
	}
}
