<#setting number_format="#">
{
    "code": "${code?default('S_OK')}",
    "summary": "${summary?default('')}",
    "pageNo":${pageNo?default(0)},
	"pageSize":${pageSize?default(0)},
	"totalCount":${totalCount?default(0)},
    "var": [
    	<#if data?exists>
	 		<#list data as list>
	 		{
	 			"attendanceId":"${list.attendanceId?default('')}",
		 		"attendanceName":"${list.attendanceName?default('')}",
				"employeeName":"${list.employeeName?default('')}",
	    		"earlyTime":${list.earlyTimes?default(0)},
		    	"lastTime":${list.lastTimes?default(0)},
				"earlyTimeLocation":"${list.earlyTimeLocation?default('')}",
				"lastTimeLocation":"${list.lastTimeLocation?default('')}"
	 		}
			<#if list_has_next>,</#if>
			</#list>
		</#if>
    ]
}
