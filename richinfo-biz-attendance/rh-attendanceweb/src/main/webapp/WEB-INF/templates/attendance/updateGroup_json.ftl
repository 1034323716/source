<#setting number_format="#">
{
    "code": "${code?default('S_OK')}",
    "summary": "${summary?default('')}",
    <#--"attendType": "${attendType?default('')}",-->
	"var": {
		"employees": [
			<#if employees?exists>
				<#list employees as list>
					{
						"company": "${list.enterName?default('')}",
						"department": "${list.deptName?default('')}",
						"enterpriseId": "${list.enterId?default('')}",
						"userId":"${list.uid?default('')}",
						"name":"${list.employeeName?default('')}",
						"phone":"${list.phone?default('')}",
						"position":"${list.location?default('')}",
						"email":"${list.location?default('')}"
					}
					<#if list_has_next>,</#if>
				</#list>
			</#if>
		],
		"departments":[
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
			]
	}
}
