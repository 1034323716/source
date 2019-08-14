<#setting number_format="#">
{
    "code": "${code?default('S_OK')}",
    "summary": "${summary?default('')}",
    "isEmptyAttendance":"${isEmptyAttendance?default(0)}",
    "attendanceName":"${attendanceName?default('')}",
    "examine":{
        "examineUid":"${examineUid?default('')}",
        "examineContactId":"${examineContactId?default('')}",
        "examineName":"${examineName?default('')}"
        },
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
    ]
}
}
