<#setting number_format="#">
{
    "code": "${code?default('S_OK')}",
    "summary": "${summary?default('')}",
    "pageNo":${pageNo?default(0)},
	"pageSize":${pageSize?default(0)},
	"totalCount":${totalCount?default(0)},
	"attendMonth":"${attendMonth?default('')}",
    "var": [
    	<#if attendanceScheduleList?exists>
	 		<#list attendanceScheduleList as list>
	 		{
	 			"uid":"${list.uid?default('')}",
		 		"employeeName":"${list.employeeName?default('')}",
				"schedule":{
					"1":"${list.day1?default('')}",
					"2":"${list.day2?default('')}",
					"3":"${list.day3?default('')}",
					"4":"${list.day4?default('')}",
					"5":"${list.day5?default('')}",
					"6":"${list.day6?default('')}",
					"7":"${list.day7?default('')}",
					"8":"${list.day8?default('')}",
					"9":"${list.day9?default('')}",
					"10":"${list.day10?default('')}",
					"11":"${list.day11?default('')}",
					"12":"${list.day12?default('')}",
					"13":"${list.day13?default('')}",
					"14":"${list.day14?default('')}",
					"15":"${list.day15?default('')}",
					"16":"${list.day16?default('')}",
					"17":"${list.day17?default('')}",
					"18":"${list.day18?default('')}",
					"19":"${list.day19?default('')}",
					"20":"${list.day20?default('')}",
					"21":"${list.day21?default('')}",
					"22":"${list.day22?default('')}",
					"23":"${list.day23?default('')}",
					"24":"${list.day24?default('')}",
					"25":"${list.day25?default('')}",
					"26":"${list.day26?default('')}",
					"27":"${list.day27?default('')}",
					"28":"${list.day28?default('')}",
					"29":"${list.day29?default('')}",
					"30":"${list.day30?default('')}",
					"31":"${list.day31?default('')}"
				}
	 		}
			<#if list_has_next>,</#if>
			</#list>
		</#if>
    ]
}
