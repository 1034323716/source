<#setting number_format="#">
{
"code": "${code?default('S_OK')}",
"summary": "${summary?default('')}",
"var": [
<#if employeeMonthDetailVO?exists>
    <#list employeeMonthDetailVO as temp>
    {
        <#switch itemId>
            <#case 1>
            "count":"${temp.lateDays?default('')}",
            "time":"${temp.lateMinutes?default('')}",
            "employeeName":"${temp.employeeName?default('')}"
                <#break>
            <#case 2>
        "count":"${temp.earlyDays?default('')}",
        "time":"${temp.earlyMinutes?default('')}",
        "employeeName":"${temp.employeeName?default('')}"
                <#break>
            <#case 3>
            "count":"${temp.goNotClockedDays?default('')}",
            "employeeName":"${temp.employeeName?default('')}"
                <#break>
            <#case 4>
        "count":"${temp.goNotClockedDays?default('')}",
        "employeeName":"${temp.employeeName?default('')}"
                <#break>
            <#case 5>
        "count":"${temp.leaveNotClockedDays?default('')}",
        "employeeName":"${temp.employeeName?default('')}"
                <#break>
            <#case 6>
        "count":"${temp.outsideDays?default('')}",
        "employeeName":"${temp.employeeName?default('')}"
                <#break>
        </#switch>
    }
        <#if temp_has_next>,</#if>
    </#list>
</#if>
]
}



