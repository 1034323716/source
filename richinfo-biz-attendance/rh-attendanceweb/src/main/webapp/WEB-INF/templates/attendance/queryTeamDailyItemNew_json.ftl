<#setting number_format="#">
{
"code": "${code?default('S_OK')}",
"summary": "${summary?default('')}",
"isNewData": ${isNewData?default('0')},
"var": [
<#if employeeMonth?exists>
    <#list employeeMonth as list>
    {
    "employeeName": "${fm(list.employeeName?default(''))}",
    "goWorkDesc": "${fm((list.goWorkDesc=='未打卡')?string('上班未打卡',list.goWorkDesc))}",
    "leaveWorkDesc": "${fm((list.leaveWorkDesc=='未打卡')?string('下班未打卡',list.leaveWorkDesc))}",
    "regionStatus": ${list.regionStatus?default(0)}
    }
        <#if list_has_next>,</#if>
    </#list>
</#if>
]
}