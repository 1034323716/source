<#setting number_format="#">
{
    "code": "${code?default('S_OK')}",
    "summary": "${summary?default('')}",
    "restrictStatus": "${restrictStatus?default('0')}",
    "var": [
	<#if employeeMonth?exists>
		<#list employeeMonth as temp>
		{
			"attendanceId":"${temp.attendanceId?default('')}",
			"monthRcdId":"${temp.monthRcdId?default('')}",
			"attendanceDate":"${temp.date?default('')}",
			"goWork":"${temp.goTime?default('')}",
			"goWorkDesc":"${temp.goWorkDesc?default('')}",
			"leaveWork":"${temp.leaveTime?default('')}",
			"leaveWorkDesc":"${temp.leaveWorkDesc?default('')}",
			"remark":"${temp.remark?default('')}",
			"recordState":"${temp.recordState?default('')}",
			"appealId":"${temp.appealId?default('')}",
			"scheduleShiftId":"${temp.scheduleShiftId?default('')}",
			"scheduleShiftName":"${temp.scheduleShiftName?default('')}",
			"scheduleShiftWorkTime":"${temp.scheduleShiftWorkTime?default('')}",
			"attendType":"${temp.attendType?default('')}"
		}
		<#if temp_has_next>,</#if>
		</#list>
	</#if>
    ]
}
