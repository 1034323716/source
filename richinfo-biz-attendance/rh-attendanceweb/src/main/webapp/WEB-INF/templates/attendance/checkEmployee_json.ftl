<#setting number_format="#">
{
    "code": "${code?default('S_OK')}",
    "summary": "${summary?default('')}",
    "employees":[
	<#if employees?exists>
	<#list employees as employee>
	{	
		"euserId":"${employee.uid?default('')}",
		"name":"${employee.employeeName?default('')}",
		"attendanceId":${employee.attendanceId?default(0)}
	}
	<#if employee_has_next>,</#if>
	</#list>
	</#if>
    ],
	"departments":[
	<#if attendDepartmentChoosers?exists>
		<#list attendDepartmentChoosers as department>
		{
		"departmentId":"${department.departmentId?default('')}",
		"departmentName":"${department.departmentName?default('')}",
		"attendanceId":${department.attendanceId?default(0)}
		}
			<#if department_has_next>,</#if>
		</#list>
	</#if>
	]
}
