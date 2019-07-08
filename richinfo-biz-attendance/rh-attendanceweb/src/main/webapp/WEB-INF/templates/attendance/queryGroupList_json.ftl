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
        "examineName": "${list.examineName?default('')}",
        "adminName": "${list.adminName?default('')}",
        "adminContactId": "${list.adminContactId?default('')}",
     "chargeMans": [
         <#if list.chargeMans?exists>
             <#list list.chargeMans as chargeMan>
             "${chargeMan?default('')}"
                 <#if chargeMan_has_next>,</#if>
             </#list>
         </#if>
     ],
		 "locations": [
				 <#if list.locations?exists>
					 <#list list.locations as location>
                    	 "${location?default('')}"
						 <#if location_has_next>,</#if>
					 </#list>
				 </#if>
	 		]
	 <#--"attendMember": "${list.employeesName?default('')}"-->
	}
	<#if list_has_next>,</#if>
	</#list>
	</#if>
    ]
}
