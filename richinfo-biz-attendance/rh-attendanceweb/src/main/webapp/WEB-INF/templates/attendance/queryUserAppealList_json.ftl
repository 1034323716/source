<#setting number_format="#">
{
    "code": "${code?default('S_OK')}",
    "summary": "${summary?default('')}",
    "pageNo":"${pageNo?default('')}",
    "pageSize":"${pageSize?default('')}",
    "totalCount":"${totalCount?default('')}",
    "var": [
        <#if data?exists>
                <#list data as temp>
                {
                        "appealId":"${temp.appealId?default('')}",
                        "enterId":"${temp.enterId?default('')}",
                        "uid":"${temp.uid?default('')}",
                        "name":"${temp.name?default('')}",
                        "reason":"${temp.reason?default('')}",
                        "attendanceDate":"${(temp.attendanceDate?string('yyyy-MM-dd'))?default('')}",
                        "goWork":"${(temp.goWork?string('HH:mm:ss'))?default('')}",
                        "goWorkDesc":"${temp.goWorkDesc?default('')}",
                        "leaveWork":"${(temp.leaveWork?string('HH:mm:ss'))?default('')}",
                        "leaveWorkDesc":"${temp.leaveWorkDesc?default('')}",
                        "examineUid":"${temp.examineUid?default('')}",
                        "examineName":"${temp.examineName?default('')}",
                        "remark":"${temp.remark?default('')}",
                        "appealRecord":"${temp.appealRecord?default('')}",
                        "examineState":"${temp.examineState?default('')}",
                        "examineResult":"${temp.examineResult?default('')}",
                        "createTime":"${(temp.createTime?string('yyyy-MM-dd HH:mm:ss'))?default('')}",
                        "updateTime":"${(temp.updateTime?string('yyyy-MM-dd HH:mm:ss'))?default('')}"
                }
                <#if temp_has_next>,</#if>
                </#list>
        </#if>
    ]
}
