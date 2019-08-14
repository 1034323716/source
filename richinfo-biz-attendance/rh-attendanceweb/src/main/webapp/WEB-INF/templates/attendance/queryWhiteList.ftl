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
                "enterpriseId":"${list.enterId?default('')}",
                "contactId": "${list.contactId?default('')}",
                "name": "${fm(list.employeeName?default(''))}"
            }
            <#if list_has_next>,</#if>
            </#list>
            </#if>
            ],
    "approvalRestrict":{
       "restrictStatus": "${attendApprovalRestrict.restrictStatus?default(0)}",
       "restrictNumber": "${attendApprovalRestrict.restrictNumber?default(0)}"
     }
  }
}
