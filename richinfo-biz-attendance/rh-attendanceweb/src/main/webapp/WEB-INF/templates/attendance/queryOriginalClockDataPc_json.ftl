<#setting number_format="#">
{
    "code": "${code?default('S_OK')}",
    "summary": "${summary?default('')}",
    "pageNo":"${pageNo?default('')}",
    "pageSize":"${pageSize?default('')}",
    "totalCount":"${totalCount?default('')}",
    "var": [
	<#if attendRecord?exists>
		<#list attendRecord as temp>
		{
			"attendanceDate":"${temp.attendanceDate?string('yyyy-MM-dd')?default('')}",
			"attendanceTime":"${temp.attendanceTime?string('HH:mm:ss')?default('')}",
			"location":"${temp.location?default('')}",
			"detailAddr":"${temp.detailAddr?default('')}",
			"status":"${temp.status?default('')}",
            "outWorkRemark":"${temp.outWorkRemark?default('')}",
			"attendanceName":"${temp.attendanceName?default('')}",
            "contactId":"${temp.contactId?default('')}",
			"employeeName":"${temp.employeeName?default('')}"
		}
		<#if temp_has_next>,</#if>
		</#list>
	</#if>
    ]
}
