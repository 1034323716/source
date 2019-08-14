/**
 * 文件名：AttendGroupService.java
 * 创建日期： 2017年6月2日
 * 作者：     liuyangfei
 * Copyright (c) 2016-2017 邮箱开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月2日
 *   修改人：liuyangfei
 *   修改内容：
 */
package richinfo.attendance.service;

import richinfo.attendance.bean.*;
import richinfo.attendance.entity.AttendDepartmentChooser;
import richinfo.attendance.entity.AttendEmployee;
import richinfo.attendance.entity.AttendGroup;
import richinfo.attendance.entity.UserInfo;
import richinfo.attendance.entity.vo.DetailVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 功能描述：考勤组管理模块逻辑实现
 * 
 */
public interface AttendGroupService
{
    /**
     * 创建考勤组
     * @param groupReq
     * @return
     */
    AttendGroupRes createGroup(AttendGroupReq groupReq);

    /**
     * 删除考勤组
     * @param map
     * @return
     */
    AttendGroupRes deleteGroup(AttendGroupReq reqParam);

    /**
     * 编辑考勤组
     * @param map
     * @return
     */
    AttendGroupRes updateGroup(AttendGroupReq reqParam);

    /**
     * 判断考勤人员是否已经在其他考勤组
     * @param groupReq
     * @return
     */
    AttendGroupRes checkEmployee(AttendGroupReq groupReq, boolean flag);

    /**
     * 查询考勤组详情
     * @param map
     * @return
     */
    AttendGroupRes queryGroupDetail(AttendGroupReq reqParam);

    /**
     * 查询考勤组列表
     * @param map
     * @return
     */
    AttendGroupListRes queryGroupList(AttendGroupReq reqParam);

    /**
     * 查询员工所在考勤组
     * @return
     */
    AttendUserInGroupRes queryOwnGroup(AttendGroupReq reqParam);

    /**
     * 获取考勤组信息
     * @param attendanceId
     * @return
     */
    AttendGroup getAttendGroupInfoFromCache(long attendanceId, String enterId);
    
    
    /**
     * 更新考勤組排班
     * @param attendanceId
     * @return
     */
    AttendScheduleRsp updateAttendSchedule(AttendScheduleReq attendScheduleReq);

    /**
     * pc端查询考勤组列表
     * @param map
     * @return
     */
    AttendGroupListRes queryGroupFromPc(AttendGroupReq reqParam);

    /**
     * h5 管理员第一次进入考勤打卡检验是否存在考勤组
     * @param req
     * @return
     */
    AttendGroupListRes checkoutGroup(AttendGroupReq req);

    /**
     * 设置全局白名单
     * @param req
     * @return
     */
    AttendGroupRes setWhiteList(AttendGroupReq req);

    /**
     * 查询白名单
     * @param req
     * @return
     */
    AttendGroupRes queryWhiteList(AttendGroupReq req);

    /**
     * 首次进入企业存在考勤组则检测是否加入考勤组
     * @param
     * @param
     */
    List<AttendDepartmentChooser> detectionJoinGroup(AttendEmployee employee, UserInfo userInfo);

    /**
     * 用户通过选择部门加入考勤组
     * @param req
     * @return
     */
    AttendDepartmentRes joinGroup(AttendDepartmentReq req);

    /**
     * 移除白名单人员
     * @param req
     * @return
     */
    AttendGroupRes removeWhiteListItem(AttendGroupReq req);

    /**
     * 移除考勤组负责人
     * @param req
     * @return
     */
    AttendGroupRes removeChargeMan(AttendGroupReq req);

    /**
     * 检测企业内有无在用考勤组
     * @param req
     * @return
     */
    AttendGroupRes checkEnterGroup(AttendGroupReq req,HttpServletRequest request);

    /**
     * 根据企业，考勤组，员工等筛选条件查询设备列表
     * @param req
     * @return
     */
    AttendGroupRes queryEquipmentList(AttendGroupReq req,HttpServletRequest request);

    /**
     * 设备后台管理系统删除设备
     * @param req
     * @return
     */
    AttendGroupRes removeEquipment(AttendGroupReq req,HttpServletRequest request);

    /**
     * 设备后台管理系统设置设备数量限制
     * @param req
     * @return
     */
    AttendGroupRes setEquipmentLimit(AttendGroupReq req,HttpServletRequest request);

    /**
     * 设备后台管理系统录入设备
     * @param req
     * @return
     */
    AttendGroupRes insertEquipment(AttendGroupReq req,HttpServletRequest request);

    /**
     * 同步检查通讯录同步删除人员
     * @return
     */
    List attendanceSyncVerify();

    /**
     * 获取考勤组的创建人补充contactId 与创建人名称
     */
    void checkoutGroupContactId();

    /**
     * 审批人同步增加contactId
     */
    void checkoutExamineContactId();

    /**
     * 考勤人员同步补充ContactId
     */
    void checkoutEmployeeContactId();

    /**
     * h5独立设置审批限制
     * @param req
     * @return
     */
    AttendGroupRes setApprovalRestrict(AttendGroupReq req);

    /**
     * 更换指定企业审批者，与转移审批单
     * @param map
     * @return
     */
    String updateApprover(Map<String, String> map);

    /**
     * 查询考勤组当天的打卡数据
     * @param map
     * @return
     */
    List<DetailVO> getCurrentDayDate(Map<String, String> map);

    int getCurrentDayDateCount(Map<String, String> map);

    List<String> getAttendanceIdByEnterId(String enterId);

    int getAttendanceIdByEnterIdCount(String enterId);
}
