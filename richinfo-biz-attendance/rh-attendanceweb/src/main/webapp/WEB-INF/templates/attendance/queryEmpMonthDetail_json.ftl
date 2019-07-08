<#setting number_format="#">
{
"code": "${code?default('S_OK')}",
"summary": "${summary?default('')}",
"restrictStatus": "${restrictStatus?default('0')}",
"var": [
<#if employeeMonthDetailVO?exists>
    <#list employeeMonthDetailVO as temp>
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
    "appealRecord":"${temp.appealRecord?default('')}",
    "recordState":"${temp.recordState?default('')}"
    }
        <#if temp_has_next>,</#if>
    </#list>
</#if>
]
}

