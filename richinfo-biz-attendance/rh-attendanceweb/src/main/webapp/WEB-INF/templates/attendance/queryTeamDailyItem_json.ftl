<#setting number_format="#">
{
"code": "${code?default('S_OK')}",
"summary": "${summary?default('')}",
"pageNo":"${pageNo?default('')}",
"pageSize":"${pageSize?default('')}",
"totalCount":"${totalCount?default('')}",
"isNewData": ${isNewData?default('0')},
"var": [
<#if data?exists>
    <#list data as list>
    {
    "uid": "${list.uid?default('')}",
    "attendanceName": "${fm(list.attendanceName?default(''))}",
    "employeeName": "${fm(list.employeeName?default(''))}",
    "earlyMinutes": "${list.earlyMinutes?default('')}",
    "lateMinutes": "${list.lateMinutes?default('')}",
    "notClockedCount": "${list.count?default('')}"
    }
        <#if list_has_next>,</#if>
    </#list>
</#if>
]
}