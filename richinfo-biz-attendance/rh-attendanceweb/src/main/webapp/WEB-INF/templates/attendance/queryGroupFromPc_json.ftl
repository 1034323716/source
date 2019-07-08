<#setting number_format="#">
{
    "code": "${code?default('S_OK')}",
    "summary": "${summary?default('')}",
    "var": [
    	<#if data?exists>
	 <#list data as list>
	 {
		"attendanceId": "${list.attendanceId?default(0)}",
       	"attendanceName": "${list.attendanceName?default('')}",
		"attendType": "${list.attendType?default('')}",
		"fixedAttendRule": "${list.fixedAttendRule?default('')}",
		"freeAttendRule": "${list.freeAttendRule?default('')}",
        "adminned": "${list.adminned?default(0)}",
        "adminName": "${list.adminName?default(0)}",
		 "locations": [
			 <#if list.locations?exists>
				 <#list list.locations as location>
				 "${location?default('')}"
					 <#if location_has_next>,</#if>
				 </#list>
			 </#if>

		 ]
	}
	<#if list_has_next>,</#if>
	</#list>
	</#if>
    ]
}
