<#setting number_format="#">
{
    "code": "${code?default('S_OK')}",
    "equipmentLimit":"${equipmentLimit?default('')}",
    "summary": "${summary?default('')}",
    "equipmentList": [
    	<#if equipmentList?exists>
	 		<#list equipmentList as list>
	 		{
            "attendanceName":"${list.attendanceName?default('')}",
            "employeeName":"${list.employeeName?default('')}",
            "contractId":"${list.contractId?default('')}",
            "uid":"${list.uid?default('')}",
            <#--"equipmentSerial":"${list.equipmentSerial?default('')}",-->
            <#--"equipmentStatus":"${list.equipmentStatus?default(0)}",-->
            <#--"equipmentDeviceType":"${list.equipmentDeviceType?default('')}",-->
            <#--"createTime":"${list.createTime?default('')}",-->
            <#--"updateTime":"${list.updateTime?default('')}",-->
            "firstEquipmentSerial":"${list.firstEquipmentSerial?default('')}",
            "firstEquipmentStatus":"${list.firstEquipmentStatus?default(0)}",
            "firstEquipmentDeviceType":"${list.firstEquipmentDeviceType?default('')}",
            "secondEquipmentSerial":"${list.secondEquipmentSerial?default('')}",
            "secondEquipmentStatus":"${list.secondEquipmentStatus?default(0)}",
            "secondEquipmentDeviceType":"${list.secondEquipmentDeviceType?default('')}",
            "thirdEquipmentSerial":"${list.thirdEquipmentSerial?default('')}",
            "thirdEquipmentStatus":"${list.thirdEquipmentStatus?default(0)}",
            "thirdEquipmentDeviceType":"${list.thirdEquipmentDeviceType?default('')}",
            "equipmentLimit":"${list.equipmentLimit?default(0)}"
	 		}
			<#if list_has_next>,</#if>
			</#list>
		</#if>
    ]
}
