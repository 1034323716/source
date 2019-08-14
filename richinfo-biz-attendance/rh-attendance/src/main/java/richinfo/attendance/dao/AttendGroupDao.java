/**
 * 文件名：AttendGroupDao.java
 * 创建日期： 2017年6月5日
 * 作者：     liuyangfei
 * Copyright (c) 2016-2017 邮箱开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月5日
 *   修改人：liuyangfei
 *   修改内容：
 */
package richinfo.attendance.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import richinfo.attendance.bean.AttendGroupWithEmpRes;
import richinfo.attendance.entity.*;
import richinfo.attendance.entity.AttendEmployee.EmployeeChargemanStatus;
import richinfo.attendance.entity.AttendEmployee.EmployeeStatus;
import richinfo.attendance.entity.AttendEmployee.EmployeeType;
import richinfo.attendance.entity.AttendGroup.GroupStatus;
import richinfo.attendance.entity.vo.DetailVO;
import richinfo.attendance.util.AssertUtil;
import richinfo.dbcomponent.exception.PersistException;

import java.util.*;

/**
 * 功能描述：考勤组DAO层
 * 
 */
public class AttendGroupDao extends BaseAttendanceDao
{
    /** table_name="attendance_group_info"; */

    private Logger logger = LoggerFactory.getLogger(AttendGroupDao.class);


    /**
     * 根据考勤组名称，查询企业内的考勤组
     * @param enterId
     * @param attendanceName
     * @param attendanceId 传递-1，表示不需要attendanceId限制
     * @return
     * @throws PersistException
     */
    public List<AttendGroup> queryAttendGroupByName(String enterId,
        String attendanceName, long attendanceId) throws PersistException
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("enterId", enterId);
        map.put("attendanceName", attendanceName);
        map.put("status", GroupStatus.Normal.getValue());
        map.put("attendanceId", attendanceId);
        @SuppressWarnings("unchecked")
        List<AttendGroup> groupList = attendanceDao.queryForList(
            "attendance.queryAttendGroupByName", map);

        return groupList;
    }

    /**
     * 查询考勤组所有成员uid信息
     * @param attendanceId
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<String> queryAttendGroupUid(long attendanceId, int status)
    {
        try
        {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("attendanceId", attendanceId);
            map.put("status", status);
            return attendanceDao.queryForList("attendance.queryAttendGroupUid",
                map);
        }
        catch (Exception e)
        {
            logger.error(
                "queryAttendGroupUid failed,attendanceId={}|status={}",
                attendanceId, status, e);
        }
        return null;
    }

    /**
     * 查询考勤组详细信息
     * @param attendanceId
     * @return
     */
    public AttendGroup queryAttendGroupInfo(long attendanceId, String enterId,
        int status)
    {
        try
        {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("attendanceId", attendanceId);
            map.put("enterId", enterId);
            map.put("status", status);
            return (AttendGroup) attendanceDao.queryForObject(
                "attendance.queryAttendGroupInfo", map);
        }
        catch (Exception e)
        {
            logger
                .error(
                    "queryAttendGroupInfo from DB failed,attendanceId={}|status={}",
                    attendanceId, status, e);
        }
        return null;
    }

    /**
     * 查询考勤组列表
     * @param
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<AttendGroupWithEmpRes> queryAttendGroupList(String enterId,
        int status, int employeeStatus)
    {
        List<AttendGroupWithEmpRes> groupList = new ArrayList<AttendGroupWithEmpRes>();
        try
        {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("enterId", enterId);
            map.put("status", status);
            map.put("employeeStatus", employeeStatus);

            groupList = attendanceDao.queryForList(
                "attendance.queryAttendGroupList", map);

            return groupList;
        }
        catch (Exception e)
        {
            logger.error("queryAttendGroupList failed,enterId={}", enterId, e);
        }
        return groupList;
    }

    /**
     * 查询考勤组列表APP端
     * @param
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<AttendGroupWithEmpRes> queryAttendGroupListOnApp(String enterId, int examinerState,UserInfo userInfo) {
        List<AttendGroupWithEmpRes> groupList = new ArrayList<AttendGroupWithEmpRes>();
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("enterId", enterId);
            map.put("examinerState", examinerState);
            //考勤组管理员兼负责人
            if (userInfo.getRoleType() == 1 && userInfo.getIsAdmin() == 1) {
                map.put("admin", userInfo.getUid());
                map.put("bothIn","bothIn");
                List<String> attendances = attendanceDao.queryForList("attendance.queryChargeGroupByUid", userInfo.getUid());
                if (AssertUtil.isNotEmpty(attendances)) {
                    map.put("attendances",attendances);
                }
            } else if (userInfo.getRoleType() != 1 && userInfo.getIsAdmin() == 1){
                map.put("adminUid", userInfo.getUid());
            } else if (userInfo.getRoleType() == 1 && userInfo.getIsAdmin() != 1) {
                List<String> attendanceIds = attendanceDao.queryForList("attendance.queryChargeGroupByUid", userInfo.getUid());
                if (AssertUtil.isNotEmpty(attendanceIds)) {
                    map.put("chargeOn","chargeOn");
                    map.put("attendanceIds",attendanceIds);
                }
            }

            logger.info("queryAttendGroupListOnAppParam = {}",map);
            groupList = attendanceDao.queryForList("attendance.queryAttendGroupListOnApp", map);

            //logger.info("queryAttendGroupListOnApp success,groupList={}",groupList);

            return groupList;
        } catch (Exception e) {
            logger.error("queryAttendGroupList failed,enterId={}", enterId, e);
        }
        return groupList;
    }

    /**
     * 查询考勤组审批员列表
     * @param
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<AttendGroupWithEmpRes> queryExamineName(String enterId,
                                                            int examinerState)
    {
        List<AttendGroupWithEmpRes> groupList = new ArrayList<>();
        try
        {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("enterId", enterId);
            map.put("examinerState", examinerState);

            groupList = attendanceDao.queryForList(
                    "attendance.queryExamineName", map);

            return groupList;
        }
        catch (Exception e)
        {
            logger.error("queryAttendGroupList failed,enterId={}", enterId, e);
        }
        return groupList;
    }

    /**
     * 查询uid是否在考勤员工信息表中存在记录
     * @param
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<AttendEmployee> queryUidRecord(List<UserInfo> list)
    {
        try
        {
            return attendanceDao
                .queryForList("attendance.queryUidRecord", list);
        }
        catch (Exception e)
        {
            logger.error("queryUidRecord failed.", e);
        }
        return null;
    }

    /**
     * 添加考勤组基本信息，并更新考勤人员列表，进行事务控制
     * @param attendGroup 考勤组基本信息
     * @param attendanceScheduleShiftList 班次列表
     * @param saveList 新增考勤人员
     * @param updateList 修改考勤人员
     * @return
     */
    public boolean saveAttendGroup(AttendGroup attendGroup,List<AttendanceScheduleShift> attendanceScheduleShiftList,
        List<AttendEmployee> saveList, List<AttendEmployee> updateList,
                                   List<String>departmentIds,
                                   List<AttendDepartmentChooser> departmentChoosers,
                                   List<AttendClockSite>attendClockSites){
        try
        {
            // 开启事务控制
            attendanceDao.startTransaction();

            // 保存考勤组基本信息
            logger.debug("attendGroup={}",attendGroup);
            attendanceDao.insert("attendance.saveAttendGroup", attendGroup);
            //保存考勤地点
            for (AttendClockSite attendClockSite :attendClockSites ){
                attendClockSite.setAttendanceId(attendGroup.getAttendanceId());
            }
            attendanceDao.batchInsertNoTransaction("attendance.batchInsertAttendSite",attendClockSites);
            //获取企业白名单
           // String enterId = attendGroup.getEnterId();
            //List<String> uids = attendanceDao.queryForList("attendance.queryAttendWhitelistUid",enterId);
           // logger.info("attendanceDao.queryForList uids ={}",uids);
            // 处理考勤人员
            // 批量处理，使用batchInsertNoTransaction方法，避免batchInsert内部提交事务
            if (AssertUtil.isNotEmpty(saveList))
            {
                // 目前for循环set考勤组ID，数据库组件完善后，改为map传参至sql层
                Iterator<AttendEmployee> iterator = saveList.iterator();
                while (iterator.hasNext()){
                    AttendEmployee nextEmployee = iterator.next();
                    //添加考勤组id
                    nextEmployee.setAttendanceId(attendGroup.getAttendanceId());
                    /*if (AssertUtil.isNotEmpty(uids) && uids.contains(nextEmployee.getUid())){
                        nextEmployee.setStatus(EmployeeStatus.Abnormal.getValue());
                    }*/

                }

                // 新增考勤人员，使用batchInsertNoTransaction方法，避免batchInsert内部提交事务
                attendanceDao.batchInsertNoTransaction(
                    "attendance.batchSaveEmployees", saveList);
            }
            if (AssertUtil.isNotEmpty(updateList))
            {

                // 目前for循环set考勤组ID
                Iterator<AttendEmployee> iterator = updateList.iterator();
                while (iterator.hasNext()){
                    AttendEmployee nextEmployee = iterator.next();
                    //添加考勤组id
                    nextEmployee.setAttendanceId(attendGroup.getAttendanceId());
                    /*if (AssertUtil.isNotEmpty(uids) && uids.contains(nextEmployee.getUid())){
                        nextEmployee.setStatus(EmployeeStatus.Abnormal.getValue());
                    }*/
                }
                // 修改考勤人员的组关系
                attendanceDao.batchUpdateNoTransaction(
                    "attendance.batchUpdateEmployees", updateList);
            }
            //删除部门
            if (AssertUtil.isNotEmpty(departmentIds)){
                attendanceDao.delete("attendance.deleteDepartmentInId" ,departmentIds);
            }
            //添加部门选择器
            if(AssertUtil.isNotEmpty(departmentChoosers)){
                for (AttendDepartmentChooser attendDepartmentChooser : departmentChoosers){
                    attendDepartmentChooser.setAttendanceId(attendGroup.getAttendanceId());
                    attendDepartmentChooser.setCreateTime(new Date());
                    attendDepartmentChooser.setUpdateTime(new Date());
                }
                attendanceDao.batchDeleteNoTransaction("attendance.batchSaveDepartmentChooser",departmentChoosers);
            }

            // 添加考勤组与审批员之间的关系
            AttendExamineEntity entityInfo = assembleAttendExaminer(attendGroup);
            attendanceDao.insert("attendance.insertAttendExamine", entityInfo);

            StringBuilder successLog = new StringBuilder();
            // 成功日志
            successLog.append("saveAttendGroup success.attendGroup=")
                .append(attendGroup).append("|save=").append(saveList.size())
                .append("|update=").append(updateList.size());
            // 添加考勤组排班班次表
            if (attendGroup.getAttendType() == AttendGroup.AttendType.Schedule
                .getValue() && attendanceScheduleShiftList.size() > 0) {
                Date nowDate = new Date();
                StringBuilder sb = new StringBuilder();
                for (AttendanceScheduleShift attendanceScheduleShift : attendanceScheduleShiftList) {
                    attendanceScheduleShift.setAttendanceId(attendGroup.getAttendanceId());
                    attendanceScheduleShift.setCreateTime(nowDate);
                    attendanceScheduleShift.setModifyTime(nowDate);
                    sb.append(attendanceScheduleShift.toString());
                }
                attendanceDao.batchInsertNoTransaction(
                    "attendance.insertAttendanceScheduleShift",
                    attendanceScheduleShiftList);
                logger.info("saveAttendScheduleShift success.{}", sb.toString());
                successLog.append("|scheduleShiftList=").append(attendanceScheduleShiftList.size());
            }
           // logger.info(successLog.toString());

            //添加考勤组负责人 同时修改员工角色类型为考勤组负责人
            if (AssertUtil.isNotEmpty(attendGroup.getChargemanList())) {
                List<UserInfo> chargemanListTemp = attendGroup.getChargemanList();
                List<AttendChargemanlistEntity> chargemanList = new ArrayList<>();
                AttendChargemanlistEntity entity = new AttendChargemanlistEntity();
                for (UserInfo info:chargemanListTemp) {
                    entity.setUid(info.getUid());
                    entity.setAttendanceId(attendGroup.getAttendanceId());
                    entity.setContactId(info.getContactId());
                    entity.setEmployeeName(info.getEmployeeName());
                    entity.setPhone(info.getPhone());
                    entity.setEnterId(info.getEnterId());
                    entity.setEnterName(info.getEnterName());
                    entity.setDeptId(info.getDeptId());
                    entity.setDeptName(info.getDeptName());
                    entity.setEmail(info.getEmail());
                    entity.setPosition(info.getPosition());
                    entity.setStatus(EmployeeChargemanStatus.InUse.getValue());
                    entity.setCreateTime(new Date());
                    entity.setModifyTime(new Date());
                    chargemanList.add(entity);
                    entity = new AttendChargemanlistEntity();
                }
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("chargemanList",chargemanList);
                map.put("roleType", EmployeeType.ChargeMan.getValue());
                List<Map> mapList = new ArrayList<>();
                mapList.add(map);
                if (attendanceDao.batchUpdate("attendance.batchUpdateEmpRoleType",mapList)) {
                    logger.info("batchUpdateEmpRoleType success.mapList={}", mapList);
                    attendanceDao.batchInsertNoTransaction("attendance.batchSaveChargemanList",chargemanList);
                }
            }

            return true;
        }
        catch (Exception e)
        {
            try
            {
                // 事务回滚
                attendanceDao.rollbackTransaction();
            }
            catch (PersistException e1)
            {
                logger.error("saveAttendGroup rollback transaction error.", e1);
            }
            logger.error("saveAttendGroup error,rollback it.attendGroup={}|e={}",
                attendGroup, e);
        }
        finally
        {
            try
            {
                // 事务提交
                attendanceDao.commitTransaction();
            }
            catch (PersistException e)
            {
                logger.error("saveAttendGroup commit transaction error.", e);
            }
        }
        return false;
    
        
    }

    /**
     * 创建考勤组时组装考勤组、审批员基本信息
     * @param groupReq
     * @return
     */
    private AttendExamineEntity assembleAttendExaminer(AttendGroup groupReq)
    {
        AttendExamineEntity examineEntity = new AttendExamineEntity();
        examineEntity.setEnterId(groupReq.getEnterId());
        examineEntity.setExamineUid(groupReq.getExamineUid());
        examineEntity.setExamineName(groupReq.getExamineName());
        examineEntity.setExamineContactId(groupReq.getExamineContactId());
        examineEntity.setExaminerState(1);
        examineEntity.setIsDelete(1);
        examineEntity.setAttendanceId(groupReq.getAttendanceId());
        examineEntity.setCreateTime(new Date());
        examineEntity.setUpdateTime(new Date());
        return examineEntity;
    }

    /**
     * 删除考勤组
     * @param attendGroup
     * @return
     */
    public boolean deleteGroup(AttendGroup attendGroup, UserInfo user)
    {
        int groupResult = 0;
        int userResult = 0;
        try
        {
            // 开启事务
            attendanceDao.startTransaction();

            // 删除考勤组
            groupResult = attendanceDao.update("attendance.deleteGroup",
                attendGroup);

            // 删除考勤组中的考勤成员
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("attendanceId", attendGroup.getAttendanceId());
            map.put("afterStatus", EmployeeStatus.Abnormal.getValue());
            map.put("beforeStatus", EmployeeStatus.Normal.getValue());
            userResult = attendanceDao.update(
                "attendance.deleteEmployeesOfGroup", map);

            // 删除考勤组中的考勤负责人
            List<AttendEmployee> chargemanList = attendanceDao.queryForList("attendance.queryChargeMansUidByAttendanceId",attendGroup.getAttendanceId());
            if (AssertUtil.isNotEmpty(chargemanList)) {
                Map<String, Object> chargeMaps = new HashMap<String, Object>();
                chargeMaps.put("attendanceId", attendGroup.getAttendanceId());
                chargeMaps.put("chargemanList", chargemanList);
                chargeMaps.put("status",EmployeeChargemanStatus.NoneUse.getValue());
                //负责人表中对应的考勤组负责人状态改成不可用
                attendanceDao.update("attendance.batchUpdateEmpChargeStatus", chargeMaps);

                List<String> uids = new ArrayList<>();
                for (AttendEmployee employee:chargemanList) {
                    uids.add(employee.getUid());
                }

                //查询这些负责人在负责的考勤组列表 将其他已经没有在负责的考勤组负责人找出来
                List<String> chargeList = attendanceDao.queryForList("attendance.queryChargeGroupByUids",uids);
                if (AssertUtil.isNotEmpty(chargeList)) {
                    for (String e:chargeList) {
                        uids.remove(e);
                    }
                    if (AssertUtil.isNotEmpty(uids)) {
                        Map paramMap = new HashMap();
                        paramMap.put("roleType", EmployeeType.NormalEmp.getValue());
                        paramMap.put("uids", uids);
                        //把这些已经没有负责的考勤组负责人的身份改成普通员工
                        attendanceDao.update("attendance.batchDeleteEmpRoleType", paramMap);
                    }
                }
            }

            // 删除考勤组、审批员管理表记录信息（软删除）
            long attendanceId = attendGroup.getAttendanceId();
            attendanceDao.update("attendance.deleteAttendExamine", attendanceId);
            //直接删除部门选择器
            attendanceDao.delete("attendance.deleteAttendDepartment", attendanceId);
            //直接删除考勤地点
            attendanceDao.delete("attendance.batchDeleteAttendSite", attendanceId);
            //软删除排班表  排班班次表
            Map<String, Object> paramsMap = new HashMap<String, Object>();
            paramsMap.put("attendanceId", attendGroup.getAttendanceId());
            paramsMap.put("afterStatus", AttendanceSchedule.Status.Deleted.getValue());
            paramsMap.put("beforeStatus", AttendanceSchedule.Status.Normal.getValue());
            int attendScheduleResult = attendanceDao.update(
                "attendance.updateAttendScheduleStatusByParam", paramsMap);
            int attendanceScheduleShiftResult = attendanceDao.update(
                "attendance.deleteAttendanceScheduleShift", paramsMap);
            logger.info("deleteGroup success,phone={}|uid={}|AttendanceId={}|groupResult={}|userResult={}|attendScheduleResult={}|attendanceScheduleShiftResult={}",
                    user.getPhone(), user.getUid(), attendGroup.getAttendanceId(), groupResult,
                    userResult, attendScheduleResult,
                    attendanceScheduleShiftResult);
            return true;
        }
        catch (Exception e)
        {
            try
            {
                attendanceDao.rollbackTransaction();
            }
            catch (Exception e1)
            {
                logger.error(
                    "deleteGroup rollback failed,phone={}|uid={}|reqParam={}",
                    user.getPhone(), user.getUid(), attendGroup, e1);
            }
            logger
                .error(
                    "deleteGroup failed,phone={}|uid={}|reqParam={}|groupResult={}|userResult={}",
                    user.getPhone(), user.getUid(), attendGroup, groupResult,
                    userResult, e);
        }
        finally
        {
            try
            {
                attendanceDao.commitTransaction();
            }
            catch (Exception e)
            {
                logger
                    .error(
                        "deleteGroup commitTransaction failed,phone={}|uid={}|reqParam={}",
                        user.getPhone(), user.getUid(), attendGroup, e);
            }
        }
        return false;
    }

    /**
     * 编辑考勤组
     * @param
     * @param decreaseMember
     * @param updateIncrease
     * @param insertIncrease
     * @return
     */
    public boolean updateGroup(AttendGroup attendGroup,List<AttendanceScheduleShift> attendanceScheduleShift,
        List<AttendEmployee> decreaseMember,
        List<AttendEmployee> updateIncrease,
        List<AttendEmployee> insertIncrease,
        UserInfo user,
        List<AttendDepartmentChooser>AttendDepartmentChooser,
          List<String>departmentIds,
         List<AttendClockSite> attendClockSites)
    {
        try
        {
            // 开启事务
            attendanceDao.startTransaction();
            // 1、新增、更新用户与考勤组的关系
            if (AssertUtil.isNotEmpty(decreaseMember))
            {

                attendanceDao.batchUpdateNoTransaction(
                    "attendance.batchdeleteEmployee", decreaseMember);
            }
            if (AssertUtil.isNotEmpty(decreaseMember))
            {

                attendanceDao.batchUpdateNoTransaction(
                    "attendance.batchRemoveEquipmentAttdenName", decreaseMember);
            }
            if (AssertUtil.isNotEmpty(updateIncrease))
            {

                attendanceDao.batchUpdateNoTransaction(
                    "attendance.batchUpdateEmployees", updateIncrease);
            }
            if (AssertUtil.isNotEmpty(updateIncrease))
            {
                Map<String,Object> updateMap = new HashMap();
                updateMap.put("attendanceName",attendGroup.getAttendanceName());
                updateMap.put("enterId",attendGroup.getEnterId());
                updateMap.put("attendanceId",attendGroup.getAttendanceId());
                updateMap.put("employee",updateIncrease);
                logger.info("updateMap={}",updateMap);
                attendanceDao.update("attendance.updateEquipmentAttdenNameByUid", updateMap);
            }
            if (AssertUtil.isNotEmpty(insertIncrease))
            {

                attendanceDao.batchInsertNoTransaction(
                    "attendance.batchSaveEmployees", insertIncrease);

            }
            if (AssertUtil.isNotEmpty(insertIncrease))
            {
                Map<String,Object> insertMap = new HashMap();
                insertMap.put("attendanceName",attendGroup.getAttendanceName());
                insertMap.put("enterId",attendGroup.getEnterId());
                insertMap.put("attendanceId",attendGroup.getAttendanceId());
                insertMap.put("employee",insertIncrease);
                logger.info("insertMap={}",insertMap);
                attendanceDao.update("attendance.updateEquipmentAttdenNameByUid", insertMap);
            }
            logger.debug("attendGroup={}",attendGroup);
            // 2、更新考勤组基本信息
            attendanceDao.update("attendance.updateGroup", attendGroup);
            //先删除考勤地点  重新插入
            long attendanceId = attendGroup.getAttendanceId();
            attendanceDao.delete("attendance.batchDeleteAttendSite",attendanceId);
            for (AttendClockSite attendClockSite : attendClockSites){
                attendClockSite.setAttendanceId(attendanceId);
            }

            //批量插入考勤地址
            attendanceDao.batchInsertNoTransaction("attendance.batchInsertAttendSite",attendClockSites);
            //删除已经其他考勤组和删除的
            if (AssertUtil.isNotEmpty(departmentIds)){
                //部门选择器，先删除 在重新插入
                attendanceDao.delete("attendance.deleteDepartmentInId",departmentIds);
            }
            //插入部门选择器
            for (AttendDepartmentChooser attendDepartmentChooser :AttendDepartmentChooser){
                attendDepartmentChooser.setAttendanceId(attendanceId);
            }
            attendanceDao.batchInsertNoTransaction("attendance.batchSaveDepartmentChooser",AttendDepartmentChooser);

            // 处理审批员业务
           /* if (AssertUtil.isNotEmpty(attendGroup.getExamineName())
                && AssertUtil.isNotEmpty(attendGroup.getExamineUid()))
            {*/

               String  examineUid = String.valueOf(attendanceDao.queryForObject("attendance.queryGroupExamineUid", attendGroup.getAttendanceId()));

               //不相同说明更换审批人
               if (!examineUid.equals(attendGroup.getExamineUid())){

                   // 不为空表示需要 修改考勤组的审批员信息
                   // 先将考勤组旧审批员状态进行更新，变成非审批员
                   attendanceDao.update("attendance.updateExaminerState",
                       attendanceId);

                   int result = attendanceDao.update(
                       "attendance.updateExamineInfo", attendGroup);
                   if (result < 1)
                   {
                       // 更新失败表明原先没有数据，则直接插入新值
                       AttendExamineEntity entity = new AttendExamineEntity();
                       entity.setEnterId(attendGroup.getEnterId());
                       entity.setAttendanceId(attendGroup.getAttendanceId());
                       entity.setExamineName(attendGroup.getExamineName());
                       entity.setExamineUid(attendGroup.getExamineUid());
                       entity.setExamineContactId(attendGroup.getExamineContactId());
                       entity.setExaminerState(1);
                       entity.setIsDelete(1);
                       Date date = new Date();
                       entity.setCreateTime(date);
                       entity.setUpdateTime(date);
                       attendanceDao.insert("attendance.insertAttendExamine",
                           entity);
                   }

                   //转移审批单到新的审批者
                   Map<String,Object> paramMap = new HashMap<>();
                   paramMap.put("attendanceId",attendGroup.getAttendanceId());
                   paramMap.put("examineUid",attendGroup.getExamineUid());
                   paramMap.put("examineName",attendGroup.getExamineName());
                   //旧审批人id
                   paramMap.put("formerExamineUid",examineUid);
                   //转让审批单
                   attendanceDao.update("attendance.transferExamineInfo", paramMap);
               }
            //}
            // 编辑更新考勤组
            dealAttendanceScheduleShift(attendGroup.getAttendanceId(),
                attendanceScheduleShift);
            logger.info(
                "updateGroup success to DB,phone={}|uid={}|AttendanceId={}",
                user.getPhone(), user.getUid(), attendGroup.getAttendanceId());
            return true;
        }
        catch (Exception e)
        {
            try
            {
                attendanceDao.rollbackTransaction();
            }
            catch (Exception e1)
            {
                logger.error("updateGroup rollback failed,reqParam={}",
                    attendGroup, e1);
            }
            logger.error(
                "updateGroup to DB failed,phone={}|uid={}|reqParam={}",
                user.getPhone(), user.getUid(), attendGroup, e);
        }
        finally
        {
            try
            {
                attendanceDao.commitTransaction();
            }
            catch (Exception e)
            {
                logger.error(
                    "updateGroup commitTransaction failed,reqParam={}",
                    attendGroup, e);
            }
        }
        return false;
    }

    /**
     * 查询所有有效的考勤组列表
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<AttendGroup> queryAllNormalAttendGroup()
    {
        try
        {
            // 考勤组状态为正常
            return attendanceDao.queryForList(
                "attendance.queryAllNormalAttendGroup",
                GroupStatus.Normal.getValue());
        }
        catch (PersistException e)
        {
            logger.error("queryAllNormalAttendGroup error.", e);
            return null;
        }
    }

    /**
     * 根据考勤状态，企业ID，考勤ID查询固定考勤班次规则
     * @return
     */
    @SuppressWarnings("unchecked")
    public String queryAttendanceRule(long attendanceId, int status,int attendType)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("attendanceId", attendanceId);
        map.put("status", status);
        map.put("attendType", attendType);
        try
        {
            return (String) attendanceDao.queryForObject("attendance.queryFixAttendanceRule",map);
        }
        catch (PersistException e)
        {
            logger
                    .error("queryFixAttendanceRule error.attendanceId={}", attendanceId, e);
            return null;
        }
    }

    /**
     * 处理考勤组排班班次表的修改
     * @param attendanceId
     * @param attendanceScheduleShiftList
     */
    @SuppressWarnings("unchecked")
    private void dealAttendanceScheduleShift(long attendanceId,
        List<AttendanceScheduleShift> attendanceScheduleShiftList)
        throws PersistException
    {
        int size = attendanceScheduleShiftList.size();
        List<AttendanceScheduleShift> updateList = new ArrayList<AttendanceScheduleShift>(
            size);
        List<AttendanceScheduleShift> addList = new ArrayList<AttendanceScheduleShift>(
            size);
        List<Long> attendanceScheduleShiftListId = new ArrayList<Long>(size);

        List<Long> allIds = attendanceDao.queryForList(
            "attendance.queryAttScheduleShiftIdByAttendanceId", attendanceId);

        StringBuffer sb = new StringBuffer();
        sb.append("dealAttendanceScheduleShift method.");
        Date nowTime = new Date();
        for (AttendanceScheduleShift attendanceScheduleShift : attendanceScheduleShiftList)
        {
            // 此处没有主键ID 为新增
            if (attendanceScheduleShift.getScheduleShiftId() == null)
            {
                attendanceScheduleShift.setAttendanceId(attendanceId);
                attendanceScheduleShift.setModifyTime(nowTime);
                attendanceScheduleShift.setCreateTime(nowTime);
                addList.add(attendanceScheduleShift);
                sb.append("|add attendanceId:").append(attendanceId);
                continue;
            }
            else if (allIds.contains(attendanceScheduleShift// 需要更新
                .getScheduleShiftId()))
            {
                attendanceScheduleShiftListId.add(attendanceScheduleShift
                    .getScheduleShiftId());
                //attendanceScheduleShift.setModifyTime(nowTime);  sql语句中已更新现在时间
                attendanceScheduleShift.setStatus(0);
                updateList.add(attendanceScheduleShift);
                sb.append("|update attendanceId:").append(attendanceId);
            }
            else
            {// 来到这里说明客户端传进来的排班班次ID有问题 打印日志不做处理
                logger
                    .error(
                        "dealAttendanceScheduleShift error scheduleShiftId:{}|attendanceId：{}",
                        attendanceScheduleShift.getScheduleShiftId(),
                        attendanceId);
            }
        }
        Iterator<Long> iterator = allIds.iterator();
        while (iterator.hasNext() && attendanceScheduleShiftListId.size() > 0)
        {
            Long nowId = iterator.next();
            if (attendanceScheduleShiftListId.contains(nowId))
            {
                iterator.remove();
            }

        }

        // 新增
        if (addList.size() > 0)
        {
            attendanceDao.batchInsertNoTransaction(
                "attendance.insertAttendanceScheduleShift", addList);
        }
        // 更新
        if (updateList.size() > 0)
        {
            attendanceDao.batchUpdateNoTransaction(
                "attendance.updateAttendanceScheduleShift", updateList);
        }
        // 删除
        if (allIds.size() > 0)
        {
            Map<String, Object> paramsMap = new HashMap<String, Object>();
            paramsMap.put("status",
                AttendanceScheduleShift.ShiftStatus.Abnormal.getValue());
            paramsMap.put("allIds", allIds);
            attendanceDao.update("attendance.updateAttScheduleShiftStatus",
                paramsMap);
        }

        logger.debug(sb.toString());
        logger
            .info(
                "dealAttendanceScheduleShift method|add size:{}|update size:{}|delete size{}",
                addList.size(), updateList.size(), allIds.size());
    }

    
    /**
     * 查询考勤组列表
     * @param
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<AttendGroupWithEmpRes> queryAttendGroupFromPc(String enterId,
        int status,UserInfo userInfo) {
        List<AttendGroupWithEmpRes> groupList = new ArrayList<AttendGroupWithEmpRes>();
        try
        {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("enterId", enterId);
            map.put("status", status);
            map.put("adminUid",userInfo.getUid());

            if (userInfo.getRoleType() == 1) {
                List<String> attendanceIds = attendanceDao.queryForList("attendance.queryChargeGroupByUid", userInfo.getUid());
                if (AssertUtil.isNotEmpty(attendanceIds)) {
                    map.put("charge",1);
                    map.put("attendanceIds",attendanceIds);
                }
                logger.info("ChargeAttendances!!!attendanceIds={}",attendanceIds);
            }

            groupList = attendanceDao.queryForList("attendance.queryAttendGroupFromPc", map);

            for (AttendGroupWithEmpRes e:groupList){
              if ("".equals(e.getAdminName()) || e.getAdminName() == null) {
                  e.setAdminName("已离职");
                  logger.info("该员工已经离职了，uid={}|name={}",e.getAdminUid(),e.getAdminName());
              }
            }

            return groupList;
        }
        catch (Exception e)
        {
            logger.error("queryAttendGroupFromPc failed,enterId={}", enterId, e);
        }
        return groupList;
    }

    /**
     * 管理员第一次进入判断是否存在考勤组
     * @param enterId
     * @param status
     * @return
     */
    public List<AttendGroupWithEmpRes> checkoutGroup(String enterId, int status) {
        List<AttendGroupWithEmpRes> groupList = new ArrayList<AttendGroupWithEmpRes>();
        try
        {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("enterId", enterId);
            map.put("status", status);

            groupList = attendanceDao.queryForList(
                    "attendance.checkoutGroup", map);

            return groupList;
        }
        catch (Exception e)
        {
            logger.error("checkoutGroup failed,enterId={}", enterId, e);
        }
        return groupList;

    }

    /**
     * 查询创建者信息
     * @return
     */
    public List<AttendGroup> queryGroupCreator() {
        List<AttendGroup> groupList = new ArrayList<AttendGroup>();
        try {
            groupList = attendanceDao.queryForList("attendance.queryGroupCreator",null);
        } catch (Exception e) {
            logger.error("groupList failed,enterId={}",  e);
        }
        return groupList;
    }

    /**
     * 查询考勤组打卡时间与考勤组类型l
     * @param attendanceId
     * @return
     */
    public List<AttendGroup> queryGroupClockTime(long attendanceId) {
        List<AttendGroup> groupList = new ArrayList<AttendGroup>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("attendanceId", attendanceId);
        try {
            groupList = attendanceDao.queryForList("attendance.queryGroupClockTime",map);
        } catch (Exception e) {
            logger.error("groupList failed,enterId={}",  e);
        }
        return groupList;
    }

    /**
     * 获取考勤组的考勤地点
     * @param attendanceId
     * @return
     */
    public List<AttendClockSite> queryClockSite(long attendanceId) {
        List<AttendClockSite> attendClockSites = new ArrayList<>();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("attendanceId", attendanceId);
        try {
            attendClockSites = attendanceDao.queryForList("attendance.queryClockSite",map);
        } catch (Exception e) {
            logger.error("attendClockSites failed,attendanceId={}|e={}",attendanceId,  e);
        }
        return attendClockSites;
    }

    /**
     * 获取考勤组中的部门选择器
     * @param attendanceId
     * @return
     */
    public List<AttendDepartmentChooser> queryDepartmentChooser(long attendanceId) {
        List<AttendDepartmentChooser> attendDepartmentChoosers = new ArrayList<>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("attendanceId", attendanceId);
        try {
            attendDepartmentChoosers = attendanceDao.queryForList("attendance.queryDepartmentChooser",map);
        } catch (Exception e) {
            logger.error("queryDepartmentChooser failed,attendanceId={}|e={}",attendanceId,  e);
        }
        return attendDepartmentChoosers;
    }

    /**
     * 查询考勤地点返回拼接后字符串
     * @param attendanceId
     * @return
     */
    public List<String> queryAttendGroupStringSite(long attendanceId) {
        try {
            return  attendanceDao.queryForList("attendance.queryAttendGroupStringSite",attendanceId);
        } catch (Exception e) {
            logger.error("queryAttendGroupStringSite failed,attendanceId={}|e={}",attendanceId,  e);
        }
        return null;
    }

    /**
     * 获取正在使用考勤组的企业id
     * @return
     */
    public List<String> queryGroupEnter() {
        List<String> enters = new ArrayList<>();
        try {
            enters =  attendanceDao.queryForList("attendance.queryGroupEnter",null);
        } catch (Exception e) {
            logger.error("queryGroupEnter failed,e={}",  e);
        }
        return enters;
    }

    /**
     * 获取有效考勤组的考勤组id 集合
     * @return
     */
    public List<Long> queryValidAttendanceId() {
        try {
            int status = GroupStatus.Normal.getValue();
            return attendanceDao.queryForList(
                "attendance.queryValidAttendanceId",
                status);
        } catch (PersistException e) {
            logger.error("queryValidAttendanceId query data error !!!!!!!!!!");
        }
        return null;
    }

    /**
     * 根据考勤组id获取有效考勤人员
     * @param attendanceId
     * @return
     */
    public List<AttendEmployee> queryPersonnelByAttendanceId(Long attendanceId) {
        Map<String,Object>map = new HashMap<>();
        map.put("attendanceId",attendanceId);
        map.put("status",EmployeeStatus.Normal.getValue());
        try {
            return attendanceDao.queryForList(
                "attendance.queryPersonnelByAttendanceId",
                map);
        } catch (PersistException e) {
            logger.error("queryPersonnelByAttendanceId query data error !!!!!!!!!! map={}",map);
        }
        return null;
    }

    /**
     * 获取正在考勤的企业id
     * @return
     */
    public List<String> queryAttendEnter() {
        try {
            return attendanceDao.queryForList(
                "attendance.queryAttendEnter", null);
        } catch (PersistException e) {
            logger.error("queryAttendEnter query data error !!!!!!!!!! map={}");
        }
        return null;
    }

    /**
     * 查询考勤负责人
     * @param attendanceId
     * @return
     */
    public List<String> queryAttendGroupChargeMansString(long attendanceId) {
        try {
            return  attendanceDao.queryForList("attendance.queryAttendGroupChargeMansString",attendanceId);
        } catch (Exception e) {
            logger.error("queryAttendGroupChargeMansString failed,attendanceId={}|e={}",attendanceId,  e);
        }
        return null;
    }

    /**
     * 获取企业内所有有效的考勤组
     * @return
     */
    public Integer queryEnterAllGroups(String enterId) {
        Integer count = 0;
        try {
            count =  (Integer) attendanceDao.queryForObject("attendance.queryEnterAllGroups",enterId);
        } catch (Exception e) {
            logger.error("queryGroupEnter failed,e={}",  e);
        }
        return count;
    }

    /**
     * 获取所有正常使用的考勤组信息
     * @return
     */
    public List<AttendGroup> findGroup() {
        try {
            return  attendanceDao.queryForList("attendance.findGroup",null);
        } catch (Exception e) {
            logger.error("findGroup failed,|e={}",  e);
        }
        return null;
    }

    /**
     * 更新contactId
     * @param updateList
     */
    public void checkoutUpdateGroup(List<AttendGroup> updateList) {
        try {
              attendanceDao.batchUpdate("attendance.checkoutUpdateGroup",updateList);
        } catch (Exception e) {
            logger.error("checkoutUpdateGroup failed,|e={}",  e);
        }

    }

    /**
     * 更新删除uid
     * @param delList
     */
    public void checkoutDelGroupUid(List<AttendGroup> delList) {
        try {
            attendanceDao.batchUpdate("attendance.checkoutDelGroupUid",delList);
        } catch (Exception e) {
            logger.error("checkoutDelGroupUid failed,|e={}",  e);
        }
    }

    /**
     * 获取审批人正常使用的
     * @return
     */
    public List<AttendExamineEntity> findCheckoutExamine() {
        try {
           return attendanceDao.queryForList("attendance.findCheckoutExamine",null);
        } catch (Exception e) {
            logger.error("findCheckoutExamine failed,|e={}",  e);
        }
        return null;
    }

    /**
     * 批量更新
     * @param updateList
     */
    public void checkoutUpdateExamine(List<AttendExamineEntity> updateList) {
        try {
            attendanceDao.batchUpdate("attendance.checkoutUpdateExamine",updateList);
        } catch (Exception e) {
            logger.error("checkoutUpdateExamine failed,|e={}",  e);
        }
    }


    /**
     *指定更换企业考勤组审批人与转移审批单
     * @param map
     * @param
     * @return
     */
    public String updateApprover(Map<String, String> map) {
        try {
            attendanceDao.startTransaction();
            //获取审批人id
            String examineUid = map.get("examineUid");
            //获取审批人name
            String examineName = map.get("examineName");
            //获取审批人Contact
            String examineContactId = map.get("examineContactId");
            //获取企业id
            String enterId = map.get("enterId");
            //获取考勤组id
            String attendanceId = map.get("attendanceId");
            //考勤组更换审批人
            //删除旧审判者
            attendanceDao.update("attendance.deleteExamineByAttendanceId",map);
            //修改存在暂停的审批者
            int result = attendanceDao.update("attendance.updateExistsExamine", map);
            //插入新记录
            if (result < 1) {
                // 更新失败表明原先没有数据，则直接插入新值
                AttendExamineEntity entity = new AttendExamineEntity();
                entity.setEnterId(enterId);
                entity.setAttendanceId(Long.parseLong(attendanceId));
                entity.setExamineName(examineName);
                entity.setExamineUid(examineUid);
                entity.setExamineContactId(examineContactId);
                entity.setExaminerState(1);
                entity.setIsDelete(1);
                Date date = new Date();
                entity.setCreateTime(date);
                entity.setUpdateTime(date);
                attendanceDao.insert("attendance.insertAttendExamine",
                    entity);
            }
            //转移审批单
            int in = attendanceDao.update("attendance.transferApprovalByAttendanceId", map);

            logger.info("updateApprover result = {} | in={}",result,in);
            //提交事务
            attendanceDao.commitTransaction();
        } catch (PersistException e) {
            e.printStackTrace();
            try {
                attendanceDao.rollbackTransaction();
            } catch (PersistException e1) {
                e1.printStackTrace();
            }
            return "error";
        }
        return "successful";
    }


    /**
     * 获取当天的考勤组打卡数据
     * @return
     */
    public List<DetailVO> getCurrentDayDate(Map<String, String> map) {
        //获取查询类型
        String type = map.get("type");
        switch (type) {
            case "上午未打卡":
                map.put("goWorkDesc","未打卡");
                break;
            case "迟到":
                map.put("goWorkDesc","迟到");
                break;
            case "下午未打卡":
                map.put("leaveWorkDesc","未打卡");
                break;
            case "早退":
                map.put("leaveWorkDesc","早退");
                break;
            case "全部":
                break;
            default:
                map.put("goWorkDesc","未打卡");
                break;
        }
        try {
            return attendanceDao.queryForList("attendance.getCurrentDayDate", map);
        } catch (Exception e) {
            logger.error("getCurrentDayDate failed,|e={}",  e);
        }
        return null;
    }

    /**
     * 根据enterId获取企业下所有有效考勤组
     * @return
     */
    public List<String> getAttendanceIdByEnterId(String enterId) {
        try {
            return (List<String>)attendanceDao.queryForList("attendance.getAttendanceIdByEnterId",enterId);
        } catch (Exception e) {
            logger.error("getAttendanceIdByEnterId failed,|e={}",  e);
        }
        return null;
    }

    /**
     * 根据enterId获取企业下所有有效考勤组数量
     * @return
     */
    public int getAttendanceIdByEnterIdCount(String enterId) {
        try {
            return (Integer)attendanceDao.queryForObject("attendance.getAttendanceIdByEnterIdCount",enterId);
        } catch (Exception e) {
            logger.error("getAttendanceIdByEnterIdCount failed,|e={}",  e);
        }
        return 0;
    }

    /**
     * 获取当天的考勤组打卡数据总数
     * @return
     */
    public int getCurrentDayDateCount(Map<String, String> map) {
        //获取查询类型
        String type = map.get("type");
        switch (type) {
            case "上午未打卡":
                map.put("goWorkDesc","未打卡");
                break;
            case "迟到":
                map.put("goWorkDesc","迟到");
                break;
            case "下午未打卡":
                map.put("leaveWorkDesc","未打卡");
                break;
            case "早退":
                map.put("leaveWorkDesc","早退");
                break;
            case "全部":
                break;
            default:
                map.put("goWorkDesc","未打卡");
                break;
        }
        try {
            return (Integer) attendanceDao.queryForObject("attendance.getCurrentDayDateCount",map);
        } catch (Exception e) {
            logger.error("findCheckoutExamine failed,|e={}",  e);
        }
        return 0;
    }

    public List<AttendanceEquipment> queryEquipmentList(Map<String,Object> paramMap) {
        try {
            return (List<AttendanceEquipment>)attendanceDao.queryForList(
                "attendance.queryEquipmentList", paramMap);
        } catch (PersistException e) {
            logger.error("queryEquipmentList error.", e);
            return null;
        }
    }

    public List<AttendanceEquipment> queryEquipments(Map<String,Object> paramMap) {
        try {
            return (List<AttendanceEquipment>)attendanceDao.queryForList(
                "attendance.queryEquipments", paramMap);
        } catch (PersistException e) {
            logger.error("queryEquipments error.", e);
            return null;
        }
    }

    public AttendanceEquipment queryEquipmentLimit(Map<String,Object> paramMap) {
        try {
            return (AttendanceEquipment)attendanceDao.queryForObject(
                "attendance.queryEquipmentLimit", paramMap);
        } catch (PersistException e) {
            logger.error("queryEquipmentLimit error.", e);
            return null;
        }
    }

    public AttendanceEquipmentControl queryEquipmentStatus(Map<String,Object> paramMap) {
        try {
            return (AttendanceEquipmentControl)attendanceDao.queryForObject(
                "attendance.queryEquipmentStatus", paramMap);
        } catch (PersistException e) {
            logger.error("queryEquipmentStatus error.", e);
            return null;
        }
    }

    public int queryEquipmentNumByUid(Map<String,Object> paramMap) {
        try {
            return (int)attendanceDao.queryForObject(
                "attendance.queryEquipmentNumByUid", paramMap);
        } catch (PersistException e) {
            logger.error("queryEquipmentNumByUid error.", e);
            return 0;
        }
    }

    public int removeEquipment(Map<String,Object> paramMap) {
        int result = 0;
        try {
            result = attendanceDao.delete("attendance.removeEquipment", paramMap);
        } catch (Exception e) {
            logger.error("removeEquipment failed,|e={}",  e);
        }
        return result;
    }

    public boolean insertEquipment(Map<String,Object> paramMap) {
        try {
            attendanceDao.insert("attendance.insertEquipment", paramMap);
        } catch (Exception e) {
            logger.error("insertEquipment failed,|e={}",  e);
            return false;
        }
        return true;
    }

    public boolean insertEquipmentUseStatus(Map<String,Object> paramMap) {
        try {
            attendanceDao.insert("attendance.insertEquipmentUseStatus", paramMap);
        } catch (Exception e) {
            logger.error("insertEquipmentUseStatus failed,|e={}",  e);
            return false;
        }
        return true;
    }

    public boolean updateEquipmentStatus(Map<String,Object> paramMap) {
        try {
            attendanceDao.update("attendance.updateEquipmentStatus", paramMap);
        } catch (Exception e) {
            logger.error("updateEquipmentStatus failed,|e={}",  e);
            return false;
        }
        return true;
    }

    public boolean updateEquipmentStatusByUid(Map<String,Object> paramMap) {
        try {
            attendanceDao.update("attendance.updateEquipmentStatusByUid", paramMap);
        } catch (Exception e) {
            logger.error("updateEquipmentStatusByUid failed,|e={}",  e);
            return false;
        }
        return true;
    }

    public boolean floatEquipment(Map<String,Object> paramMap) {
        try {
            if (!"0".equals((String)paramMap.get("equipmentLimit")))  {
                switch ((String)paramMap.get("equipmentLimit")) {
                    case "1":
                        paramMap.put("limitCount","1");
                        break;
                    case "2":
                        paramMap.put("limitCount","2");
                        break;
                    case "3":
                        paramMap.put("limitCount","3");
                        break;
                }
                attendanceDao.delete("attendance.floatEquipment", paramMap);
            } else {
                attendanceDao.delete("attendance.setOffEquipment", paramMap);
            }
        } catch (Exception e) {
            logger.error("updateEquipmentStatus failed,|e={}",  e);
            return false;
        }
        return true;
    }

//    public int floatEquipment(Map<String,Object> paramMap) {
//        int result = 0;
//        try {
//            attendanceDao.startTransaction();
//            result = attendanceDao.update("attendance.setEquipmentLimit", paramMap);
//            if (result > 0) {
//                if (!"0".equals((String)paramMap.get("equipmentLimit"))) {
//                    switch ((String)paramMap.get("equipmentLimit")) {
//                        case "1":
//                            paramMap.put("limitCount","1");
//                            break;
//                        case "2":
//                            paramMap.put("limitCount","2");
//                            break;
//                        case "3":
//                            paramMap.put("limitCount","3");
//                            break;
//                    }
//                    result = attendanceDao.update("attendance.floatEquipment", paramMap);
//                } else {
//                    result = attendanceDao.update("attendance.setOffEquipment", paramMap);
//                }
//            }
//            attendanceDao.commitTransaction();
//        } catch (Exception e) {
//            try {
//                attendanceDao.rollbackTransaction();
//            } catch (PersistException e1) {
//                e1.printStackTrace();
//            }
//        }
//        return result;
//    }


    //获取正在使用的考勤组
    public List<String> findGroupInNormal(Map<String,Integer> paramMap) {
        try {
            logger.info("findGroupInNormal paramMap = {}",paramMap);
           return  attendanceDao.queryForList("attendance.findGroupInNormal",paramMap);
        } catch (PersistException e) {
            e.printStackTrace();
            logger.error("findGroupInNormal failed,|e={}",  e);
        }
        return null;
    }

    //获取正在使用的考勤组
    public void  updateEquipmentAttdenNameByUid(Map<String,Object> updateMap) {
        try {
            attendanceDao.update("attendance.updateEquipmentAttdenNameByUid", updateMap);
        } catch (PersistException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询负责的考勤组
     * @param uid
     * @return
     */
    public List<String> queryGroupPrincipalByUid(String uid) {
        try {
            return  attendanceDao.queryForList("attendance.queryGroupPrincipalByUid",uid);
        } catch (PersistException e) {
            e.printStackTrace();
            logger.error("queryGroupPrincipalByUid failed,|e={}|uid={}",  e,uid);
            return null;
        }
    }

    public int queryGroupCharge(Map<String,Object> paramMap) {
        try {
            return (int)attendanceDao.queryForObject(
                "attendance.queryGroupCharge", paramMap);
        } catch (PersistException e) {
            logger.error("queryGroupCharge error.", e);
            return 0;
        }
    }
}
