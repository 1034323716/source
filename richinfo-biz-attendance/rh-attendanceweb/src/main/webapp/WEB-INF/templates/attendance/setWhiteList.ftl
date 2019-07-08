<#setting number_format="#">
{
"code": "${code?default('S_OK')}",
"summary": "${summary?default('')}",
"var":{
"employees":[
<#if whitelistEntities?exists>
    <#list whitelistEntities as list>
    {
    "euserId": "${list.uid?default('')}",
    "contactId": "${list.contactId?default('')}",
    "name": "${fm(list.employeeName?default(''))}",
    "enterpriseId":"${list.enterId?default('')}"
    }
        <#if list_has_next>,</#if>
    </#list>
</#if>
]
}
}
