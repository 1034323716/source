/**
 * 文件名：AttendEmployeeDao.java
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
import richinfo.attendance.entity.*;
import richinfo.attendance.entity.AttendEmployee.EmployeeChargemanStatus;
import richinfo.attendance.entity.AttendEmployee.EmployeeStatus;
import richinfo.attendance.entity.AttendGroup.GroupStatus;
import richinfo.attendance.util.AssertUtil;
import richinfo.attendance.util.AttendanceConfig;
import richinfo.dbcomponent.exception.PersistException;

import java.util.*;

/**
 * 功能描述：考勤人员 DAO层
 * 
 */
public class AttendEmployeeDao extends BaseAttendanceDao
{
    private final Logger logger = LoggerFactory
        .getLogger(AttendEmployeeDao.class);

    private AttendanceConfig config = AttendanceConfig.getInstance();
    /**
     * 查询已经在其他考勤组的uid列表
     * @param
     * @param users
     * @return
     * @throws PersistException
     */
    public List<String> queryUidInGroups(List<UserInfo> users)
        throws PersistException
    {
        if (AssertUtil.isEmpty(users))
        {
            logger.warn("UserInfo List is empty.");
            return null;
        }
        @SuppressWarnings("unchecked")
        List<String> uids = attendanceDao.queryForList(
            "attendance.queryUidInGroups", users);

        return uids;
    }

    /**
     * 根据uid查找员工信息；查找失败，返回null
     * @param uid 企业联系人ID
     * @return
     */
    public AttendEmployee queryEmployeeByUid(String uid) {
        AttendEmployee result = null;

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("uid", uid);
        map.put("status", EmployeeStatus.Normal.getValue());
        try
        {
            result = (AttendEmployee) attendanceDao.queryForObject(
                "attendance.queryEmployeeByUid", map);
            logger.debug("queryEmployeeByUid success.result={}", result);
        }
        catch (PersistException e)
        {
            logger.error("queryEmployeeByUid error.uid={}", uid, e);
        }

        return result;
    }

    /**
     * 根据uid查找员工信息；查找失败，返回null
     * @param uid 企业联系人ID
     * @return
     */
    public AttendEmployee queryEmployeeTypeByUid(String uid) {
        AttendEmployee result = null;

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("uid", uid);
        map.put("status", EmployeeStatus.Normal.getValue());
        try
        {
            result = (AttendEmployee) attendanceDao.queryForObject(
                "attendance.queryEmployeeTypeByUid", map);
            logger.debug("queryEmployeeTypeByUid success.result={}", result);
        }
        catch (PersistException e)
        {
            logger.error("queryEmployeeTypeByUid error.uid={}", uid, e);
        }

        return result;
    }


    /**
     * 根据enterid查找员工信息；查找失败，返回null
     * @param enterid 企业ID
     * @return
     */
    public List<UserInfo> queryEmployeeByEnterid(String enterid)
    {
        List<UserInfo> result = null;

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("enterId", enterid);
        map.put("status", EmployeeStatus.Normal.getValue());
        try
        {
            result =  attendanceDao.queryForList("attendance.queryEmployeeByEnterid",map);
            logger.debug("queryEmployeeByEnterid success.result={}", result);
        }
        catch (PersistException e)
        {
            logger.error("queryEmployeeByEnterid error.uid={}", enterid, e);
        }

        return result;
    }

    /**
     * 根据departmentId查询用户所属考勤组attendanceId，返回对应关系给前端。失败查询失败，返回空列表。
     * @param
     * @return
     */
    public List<AttendDepartmentChooser> queryAttendGroupByDepartment(List<AttendDepartmentChooser> departmentChoosers, boolean flag, long attendanceId) {
        List<AttendDepartmentChooser> result = null;
        if (AssertUtil.isEmpty(departmentChoosers))
        {
            logger.warn("departmentChoosers list is empty.");
            return result;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("departments", departmentChoosers);
        try
        {
            if (flag)
            {
                // 创建考勤组查询成员关系
                result = attendanceDao.queryForList(
                        "attendance.queryAttendGroupByDepartmentId", map);
            }
            else
            {
                // 编辑考勤组查询成员关系
                map.put("attendanceId", attendanceId);
                result = attendanceDao.queryForList(
                        "attendance.queryAttendGroupByDepartmentId2", map);
            }
        }
        catch (Exception e)
        {
            logger.error(
                    "queryAttendGroupByDepartmentId error,isCreateGroup={}|uidSize={}",
                    flag, departmentChoosers.size(), e);
        }
        return result;
    }
    /**
     * 根据uid查询用户所属考勤组attendanceId，返回对应关系给前端。失败查询失败，返回空列表。
     * @param users
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<UserInfo> queryAttendGroupByUid(List<UserInfo> users,
        boolean flag, long attendanceId)
    {
        List<UserInfo> result = null;
        if (AssertUtil.isEmpty(users))
        {
            logger.warn("user list is empty.");
            return result;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("users", users);
        map.put("status", EmployeeStatus.Normal.getValue());
        try
        {
            if (flag)
            {
                // 创建考勤组查询成员关系
                result = attendanceDao.queryForList(
                    "attendance.queryAttendGroupByUid", map);
            }
            else
            {
                // 编辑考勤组查询成员关系
                map.put("attendanceId", attendanceId);
                result = attendanceDao.queryForList(
                    "attendance.queryAttendGroupByUid2", map);
            }
        }
        catch (Exception e)
        {
            logger.error(
                "queryAttendGroupByUid error,isCreateGroup={}|uidSize={}",
                flag, users.size(), e);
        }
        return result;
    }

    /**
     * 查询考勤组用户列表
//     * @param users
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<UserInfo> queryUserList(String enterId ,long attendanceId, int status)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("enterId", enterId);
        map.put("attendanceId", attendanceId);
        map.put("status", status);
        try
        {
            return attendanceDao.queryForList("attendance.queryUserList", map);
        }
        catch (PersistException e)
        {
            logger
                .error("queryUserList error.attendanceId={}", attendanceId, e);
            return null;
        }
    }



    /**
     * 查询考勤审批人员
//     * @param users
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<UserInfo> queryExamineName(long attendanceId)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("attendanceId", attendanceId);
        try
        {
            return attendanceDao.queryForList("attendance.queryExamineName", map);
        }
        catch (PersistException e)
        {
            logger
                    .error("queryExamineName error.attendanceId={}", attendanceId, e);
            return null;
        }
    }

    /**
     * 根据用户号码和企业id查询用户与考勤组的关系 因为目前一个号码可以对应多条和通讯录contactId，所以可能会有多条关系记录
//     * @param users
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<UserInfo> queryUserByPhone(String enterId, String phone)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("enterId", enterId);
        map.put("phone", phone);
        map.put("status", 0);
        try
        {
            return attendanceDao.queryForList("attendance.queryUserByPhone",
                map);
        }
        catch (PersistException e)
        {
            logger.error("queryUserByPhone error.enterId={}|phone={}", enterId,
                phone, e);
            return null;
        }
    }

    /**
     * 查询用户所在考勤组信息
     * @param uid
     * @return
     */
    public UserGroupEntity queryOwnGroup(String uid) throws PersistException
    {
        UserGroupEntity userGroup = new UserGroupEntity();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("uid", uid);
        // 员工状态、考勤组状态均为正常
        map.put("groupStatus", GroupStatus.Normal.getValue());
        map.put("employeeStatus", EmployeeStatus.Normal.getValue());
        userGroup = (UserGroupEntity) attendanceDao.queryForObject(
            "attendance.queryOwnGroup", map);
         Date  calendarDate = new Date();
        if (AssertUtil.isNotEmpty(userGroup)){
            AttendCalendar attendCalendar = (AttendCalendar) attendanceDao.queryForObject("attendance.queryAttendCalendarByDate",calendarDate);
//            String[] rowDays = config.getRowDays(TimeUtil.getCurrentYear()).split(AtdcConsts.SEPARATOR.DATE_LIST);
//            if (AssertUtil.isNotEmpty(rowDays)) {
//                List<String> strings = Arrays.asList(rowDays);
//                if (strings.contains(TimeUtil.date2String(attendCalendar.getCalendarDate(),"yyyy-MM-dd"))) {
//                    userGroup.setWorkdayStatus(0);
//                    return userGroup;
//                }
//            }
            userGroup.setWorkdayStatus(attendCalendar.getStatus());
        }
        return userGroup;
    }


    /**
     * 管理员查看考勤组详情考勤人员名单，包括白名单
     * @param attendanceId
     * @param status
     * @param
     * @return
     */
    public List<UserInfo> queryUserListAndWhitelist(long attendanceId, int status) {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("attendanceId", attendanceId);
        map.put("status", status);
        try
        {
            return attendanceDao.queryForList("attendance.queryUserListAndWhitelist", map);
        }
        catch (PersistException e)
        {
            logger
                    .error("queryUserList error.attendanceId={}", attendanceId, e);
            return null;
        }
    }

    /**
     *
     * @param uid
     * @param
     * @param
     * @return
     */
    public AttendEmployee queryEmployeeByUidAndWhitelist(String uid ) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("uid", uid);
        try
        {
            return (AttendEmployee)attendanceDao.queryForObject("attendance.queryEmployeeByUidAndWhitelist", map);
        }
        catch (PersistException e)
        {
            logger
                    .error("queryEmployeeByUidAndWhitelist error.uid={}", uid, e);
            return null;
        }
    }

    /**
     * 保存考勤用户信息
     * @param employee
     */
    public void saveEmployee(AttendEmployee employee) {
        try {
             attendanceDao.insert("attendance.batchSaveEmployees", employee);

        } catch (PersistException e) {
            e.printStackTrace();
            logger.error("saveEmployee error employee={}",employee);

        }
    }

    /**
     * 更新考勤用户信息
     * @param employee
     */
    public void updateEmployee(AttendEmployee employee) {
        try {
            attendanceDao.update("attendance.batchUpdateEmployees",employee);
        } catch (PersistException e) {
            e.printStackTrace();
            logger.error("updateEmployee error employee={}",employee);
        }
    }


    /**
     * 根据uid批量修改员工状态
     * @return
     */
    public boolean batchUpdateEmployeeStatus(List users,int status){
        int result = 0;

        Map map = new HashMap();

        map.put("status",status);
        map.put("users",users);

        try {
            attendanceDao.startTransaction();

            result = attendanceDao.update("attendance.batchUpdateEmployeeStatus",map);

            logger.info("batchUpdateEmployeeStatus success,users={}|result={}",map.get("users"),result);

            return true;
        } catch (Exception e) {
            try {
                attendanceDao.rollbackTransaction();
            } catch (PersistException e1) {
                logger.info("batchUpdateEmployeeStatus success,users={}|result={}",map.get("users"),result,e1);
            }
            logger.info("batchUpdateEmployeeStatus success,users={}|result={}",map.get("users"),result,e);
        }
        finally {
            try {
                attendanceDao.commitTransaction();
            } catch (PersistException e) {
                logger.info("batchUpdateEmployeeStatus success,users={}|result={}",map.get("users"),result,e);            }
        }
        return false;
    }

    /**
     * 查询员工当天的考勤记录
     * @return
     */
    public EmployeeMonthDetail queryNomMonthly(Map map){

        EmployeeMonthDetail employeeMonthDetail = new EmployeeMonthDetail();

        try {
            employeeMonthDetail = (EmployeeMonthDetail) attendanceDao.queryForObject("attendance.queryNomMonthly",map);

            logger.info("queryNomMonthly success,map={}",map);

            return employeeMonthDetail;
        } catch (Exception e) {


            logger.error("queryNomMonthly failed",e);
        }

        return employeeMonthDetail;
    }

    /**
     * 转移考勤组后更新考勤组信息
     * @return
     */
    public void updateEmployeeMonthDetailBy(EmployeeMonthDetailVO detail){

        try {
            attendanceDao.startTransaction();

            attendanceDao.update("attendance.updateEmployeeMonthDetailByIds",detail);

            logger.info("updateEmployeeMonthDetailById success");

        } catch (Exception e) {
            try {
                attendanceDao.rollbackTransaction();
            } catch (PersistException e1) {
                logger.error("updateEmployeeMonthDetailById rollback failed",e);
            }
            logger.error("updateEmployeeMonthDetailById failed",e);
        }
        finally {
            try {
                attendanceDao.commitTransaction();
            } catch (PersistException e) {
                logger.error("updateEmployeeMonthDetailById commitTransaction failed",e);
            }
        }
    }

    /**
     * 转移考勤组后更新考勤组信息
     * @return
     */
    public void updateEmployeeMonthDetailById(EmployeeMonthDetail detail){

        try {
            attendanceDao.startTransaction();

            attendanceDao.update("attendance.updateEmployeeMonthDetailById",detail);

            logger.info("updateEmployeeMonthDetailById success");

        } catch (Exception e) {
            try {
                attendanceDao.rollbackTransaction();
            } catch (PersistException e1) {
                logger.error("updateEmployeeMonthDetailById rollback failed",e);
            }
            logger.error("updateEmployeeMonthDetailById failed",e);
        }
        finally {
            try {
                attendanceDao.commitTransaction();
            } catch (PersistException e) {
                logger.error("updateEmployeeMonthDetailById commitTransaction failed",e);
            }
        }
    }

    /**
     * 转移考勤组后新增考勤组信息
     * @return
     */
    public void batchSaveEmployeeMonthDetail(EmployeeMonthDetail detail){

        try {
            attendanceDao.startTransaction();

            attendanceDao.insert("attendance.batchSaveEmployeeMonthDetail",detail);

            logger.info("batchSaveEmployeeMonthDetail success");

        } catch (Exception e) {
            try {
                attendanceDao.rollbackTransaction();
            } catch (PersistException e1) {
                logger.error("batchSaveEmployeeMonthDetail rollback failed}",e1);
            }
            logger.error("batchSaveEmployeeMonthDetail failed}",e);
        }
        finally {
            try {
                attendanceDao.commitTransaction();
            } catch (PersistException e) {
                logger.error("batchSaveEmployeeMonthDetail commitTransaction failed",e);
            }
        }
    }

    /**
     * 根据attendanceId查询考勤组负责人
     * @param attendanceId 企业ID
     * @return
     */
    public List<AttendEmployee> queryChargeMansByAttendanceId(String attendanceId) {
        List<AttendEmployee> result = null;

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("attendanceId", attendanceId);
        map.put("status", EmployeeChargemanStatus.InUse.getValue());
        try {
            result =  attendanceDao.queryForList("attendance.queryChargeMansByAttendanceId",map);
            logger.debug("queryChargeMansByAttendanceId success.result={}", result);
        } catch (PersistException e) {
            logger.error("queryChargeMansByAttendanceId error.uid={}", attendanceId, e);
        }
        return result;
    }

    /**
     * 批量更新员工角色类型
     * @return
     */
    public void batchUpdateEmpRoleType(List chargeMans,int roleType){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("chargemanList", chargeMans);
        map.put("roleType", roleType);
        if (roleType == 1) {
//            map.put("status",EmployeeStatus.Normal.getValue());
        }
        try {
            attendanceDao.startTransaction();

            attendanceDao.insert("attendance.batchUpdateEmpRoleType",map);

            logger.info("batchUpdateEmpRoleType success");

        } catch (Exception e) {
            try {
                attendanceDao.rollbackTransaction();
            } catch (PersistException e1) {
                logger.error("batchUpdateEmpRoleType rollback failed}",e1);
            }
            logger.error("batchUpdateEmpRoleType failed}",e);
        }
        finally {
            try {
                attendanceDao.commitTransaction();
            } catch (PersistException e) {
                logger.error("batchUpdateEmpRoleType commitTransaction failed",e);
            }
        }
    }

    /**
     * 批量更新负责人状态
     * @return
     */
    public boolean batchUpdateEmpChargeStatus(List chargeMans,int status,String attendanceId){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("chargemanList", chargeMans);
        map.put("status", status);
        map.put("attendanceId", attendanceId);

        try {
            attendanceDao.startTransaction();

            attendanceDao.update("attendance.batchUpdateEmpChargeStatus",map);

            logger.info("batchUpdateEmpChargeStatus success,"
                + "chargemanList={},status={},attendanceId={},",chargeMans,status,attendanceId);

            return true;

        } catch (Exception e) {
            try {
                attendanceDao.rollbackTransaction();
            } catch (PersistException e1) {
                logger.error("batchUpdateEmpChargeStatus rollback failed}",e1);
            }
            logger.error("batchUpdateEmpChargeStatus failed}",e);
        }
        finally {
            try {
                attendanceDao.commitTransaction();
            } catch (PersistException e) {
                logger.error("batchUpdateEmpChargeStatus commitTransaction failed",e);
            }
        }
        return false;
    }

    /**
     * 批量插入考勤组负责人
     * @return
     */
    public void batchSaveChargemanList(List<AttendChargemanlistEntity> chargeMans){

        try {
            attendanceDao.startTransaction();

            attendanceDao.batchInsert("attendance.batchSaveChargemanList",chargeMans);

            logger.info("batchSaveChargemanList success");

        } catch (Exception e) {
            try {
                attendanceDao.rollbackTransaction();
            } catch (PersistException e1) {
                logger.error("batchSaveChargemanList rollback failed}",e1);
            }
            logger.error("batchSaveChargemanList failed}",e);
        }
        finally {
            try {
                attendanceDao.commitTransaction();
            } catch (PersistException e) {
                logger.error("batchSaveChargemanList commitTransaction failed",e);
            }
        }
    }


    /**
     * 根据uid查找负责的考勤组；查找失败，返回null
     * @param uid 企业联系人ID
     * @return
     */
    public List<String> queryChargeGroupByUid(String uid) {
        List<String> result = new ArrayList<>();

        try
        {
            result = attendanceDao.queryForList("attendance.queryChargeGroupByUid", uid);
            logger.debug("queryChargeGroupByUid success.result={}", result);
        }
        catch (PersistException e)
        {
            logger.error("queryChargeGroupByUid error.uid={}", uid, e);
        }

        return result;
    }

    /**
     * 获取正在使用contactId为空的考勤人员
     * @return
     */
    public List<AttendEmployee> findCheckoutEmployee() {
        try {
            return  attendanceDao.queryForList("attendance.findCheckoutEmployee",null);
        } catch (Exception e) {
            logger.error("findCheckoutEmployee failed,|e={}",  e);
        }
        return null;
    }

    /**
     * 同步更新考勤人员contactId
     * @param updateList
     */
    public void checkoutUpdateEmployee(List<AttendEmployee> updateList) {
        try {
            attendanceDao.batchUpdate("attendance.checkoutUpdateEmployee",updateList);
        } catch (Exception e) {
            logger.error("checkoutUpdateEmployee failed,|e={}",  e);
        }
    }

    /**
     * 修改已经删除人员的状态
     * @param delList
     */
    public void checkoutDelEmployee(List<AttendEmployee> delList) {
        try {
            attendanceDao.batchUpdate("attendance.checkoutDelEmployee",delList);
        } catch (Exception e) {
            logger.error("checkoutDelEmployee failed,|e={}",  e);
        }
    }

    /**
     * 根据uids查找负责的考勤组；查找失败，返回null
     * @param uid 企业联系人ID
     * @return
     */
    public List<String> queryChargeGroupByUids(List<String> uid) {
        List<String> result = new ArrayList<>();

        try
        {
            result = attendanceDao.queryForList("attendance.queryChargeGroupByUids", uid);
            logger.debug("queryChargeGroupByUids success.result={}", result);
        }
        catch (PersistException e)
        {
            logger.error("queryChargeGroupByUids error.uid={}", uid, e);
        }

        return result;
    }

    /**
     * 设置为企业全局考勤组负责人
     * @return
     */
    public void setEnterChargeMan(UserInfo userInfo){

        try {
            attendanceDao.startTransaction();

            attendanceDao.update("attendance.setEnterChargeMan",userInfo);

            logger.info("setEnterChargeMan success");

        } catch (Exception e) {
            try {
                attendanceDao.rollbackTransaction();
            } catch (PersistException e1) {
                logger.error("setEnterChargeMan rollback failed}",e1);
            }
            logger.error("setEnterChargeMan failed}",e);
        }
        finally {
            try {
                attendanceDao.commitTransaction();
            } catch (PersistException e) {
                logger.error("setEnterChargeMan commitTransaction failed",e);
            }
        }
    }

    /**
     * 移除企业负责人身份
     * @param userInfo
     */
    public void removeChargeManRole(UserInfo userInfo) {
        try {
            attendanceDao.insert("attendance.removeChargeManRole",userInfo);
        } catch (Exception e) {
            logger.error("removeChargeManRole failed,|e={}",  e);
        }
    }

    /**
     * 查询uid有多少个负责的考勤组
     * @param
     */
    public int queryAttendanceIdByUid(String uid) {
        int count = 0;
        try {
            count = (Integer) attendanceDao.queryForObject("attendance.queryAttendanceIdByUid",uid);
            return count;
        } catch (Exception e) {
            logger.error("queryAttendanceIdByUid failed,|e={}",  e);
        }
        return count;
    }

    /**
     * 查询enterId下有多少个考勤组
     * @param
     */
    public int queryAttendanceIdByEnterId(String enterId) {
        int count = 0;
        try {
            count = (Integer) attendanceDao.queryForObject("attendance.queryAttendanceIdByEnterId",enterId);
            return count;
        } catch (Exception e) {
            logger.error("queryAttendanceIdByEnterId failed,|e={}",  e);
        }
        return count;
    }

    /**
     * 查询没有手机号码正常状态的用户  每次查询100个
     * @return
     */
    public List<AttendEmployee> findEmployeeNoPhone(String attendanceId) {
        try {
          return   attendanceDao.queryForList("attendance.findEmployeeNoPhone",attendanceId);
        } catch (PersistException e) {
            e.printStackTrace();
            logger.error("findEmployeeNoPhone failed,|e={}",  e);
        }
        return null ;
    }

    /**
     * 批量补充更新用户手机号码
     * @param complementEmployees
     */
    public void batchSaveComplementEmployee(List<AttendEmployee> complementEmployees) {
        try {
            boolean result = attendanceDao.batchUpdate("attendance.updateComplementEmployee", complementEmployees);
            logger.info("batchSaveComplementEmployee size={}|result={}",complementEmployees.size(),result);
        } catch (PersistException e) {
            e.printStackTrace();
            logger.error("batchSaveComplementEmployee failed,|e={}",  e);
        }
    }

    /**
     * 更新用户短信打卡开关设置
     * @return 更新的行数
     */
    public int updateEmployeeSMSSwitch(String uid, int status) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("uid", uid);
        params.put("status", status);
        int modifyLine = 0;
        try {
            modifyLine = attendanceDao.update("attendance.updateEmployeeSMSSwitch",params);
        } catch (PersistException e) {
            e.printStackTrace();
            logger.error("updateEmployee error params : {}, exception : {}", params, e.toString());
        }
        return modifyLine;
    }

    /**
     * 获取用户短信打卡开关状态
     */
    public int getEmployeeSMSSwitch(String uid) {
        int SMSSwitchStatum = 0;
        try {
            SMSSwitchStatum = (int) attendanceDao.queryForObject("attendance.getMessageBySSMSwitch",uid);
        } catch (PersistException e) {
            e.printStackTrace();
            logger.error("getEmployeeSMSSwitch error params : {}, exception : {}", uid, e.toString());
        }
        return SMSSwitchStatum;
    }

    /**
     * 更新用户信息首次短信发送标识
     * @param uid
     */
    public void updateEmployeeFirstSend(String uid) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("uid", uid);
        params.put("firstSend",0);
        try {
            attendanceDao.update("attendance.updateEmployeeFirstSend",params);
        } catch (PersistException e) {
            e.printStackTrace();
            logger.error("updateEmployee error params : {}, exception : {}", params, e.toString());
        }
    }
}
