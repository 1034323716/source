<#setting number_format="#">
{
    "code": "${code?default('S_OK')}",
    "summary": "${summary?default('')}",
    "pageNo":"${pageNo?default('')}",
    "pageSize":"${pageSize?default('')}",
    "totalCount":"${totalCount?default('')}",
    "var": [
	<#if employeeMonth?exists>
		<#list employeeMonth as temp>
		{
			"date":"${temp.attendanceDate?string('yyyy-MM-dd')?default('')}",
			"employeeName":"${temp.employeeName?default('')}",
            "deptName":"${temp.deptName?default('')}",
			"attendanceName":"${temp.attendanceName?default('')}",
			"goWork":"${temp.goTime?default('')}",
			"goWorkDesc":"${temp.goWorkDesc?default('')}",
			"leaveWork":"${temp.leaveTime?default('')}",
			"leaveWorkDesc":"${temp.leaveWorkDesc?default('')}",
			"remark":"${temp.remark?default('')}",
			"goLocation":"${temp.goLocation?default('')}",
			"leaveLocation":"${temp.leaveLocation?default('')}",
            "regionStatus":${temp.regionStatus?default(0)},
            "outWorkRemark":"${temp.outWorkRemark?default('')}",
            "contactId":"${temp.contactId?default('')}",
			"workTime":"${temp.workMinutes?default('')}"
		}
		<#if temp_has_next>,</#if>
		</#list>
	</#if>
    ]
}
