<#setting number_format="#">
{
    "code": "${code?default('S_OK')}",
    "summary": "${summary?default('')}",
    "var": {
		"resultCode":"${resultCode?default('1')}",
		"msgid":"${inResponseTo?default('')}",
		"systemTime":"${systemTime?default('')}",
		"artifact":"${artifact?default('')}"
    }
}
