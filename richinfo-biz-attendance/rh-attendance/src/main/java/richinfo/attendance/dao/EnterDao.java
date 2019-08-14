/**
 * 文件名：EnterDao.java
 * 创建日期： 2018年6月9日
 * 作者：     黄学振
 * Copyright (c) 2009-2011 个邮开发室
 * All rights reserved.
 
 */
package richinfo.attendance.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import richinfo.attendance.bean.AttendEnterRes;
import richinfo.attendance.entity.AttendDepartmentChooser;
import richinfo.attendance.entity.AttendEmployee;
import richinfo.attendance.entity.AttendExamineEntity;
import richinfo.attendance.entity.AttendGroup;
import richinfo.attendance.util.AesUtils;
import richinfo.attendance.util.AssertUtil;
import richinfo.attendance.util.AttendanceConfig;
import richinfo.attendance.util.QytxlUtil;
import richinfo.dbcomponent.exception.PersistException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能描述：企业信息查询
 *
 */
public class EnterDao extends BaseAttendanceDao
{

    private Logger log = LoggerFactory.getLogger(EnterDao.class);



    
    
    public boolean deleteUser(List<AttendEmployee> decreaseMember){
    	try
        {
    		//删除人员
            boolean flag = attendanceDao.batchUpdate(
                    "attendance.batchdeleteEmployeeByContactId", decreaseMember);
            log.info("batchdeleteEmployee success={}|uidSize={}", flag,
            		decreaseMember.size());
            //删除信息推送
            boolean flag2 = attendanceDao.batchDelete(
                    "attendance.batchDelMsgByContactId", decreaseMember);
            //删除班次
            boolean flag3 = attendanceDao.batchDelete(
                    "attendance.batchDelScheduleByContactId", decreaseMember);
            //删除白名单
            boolean flag4 = attendanceDao.batchUpdate("attendance.batchDelWhitelistByContactId",decreaseMember);
            log.info("batchDelWhitelistByContactId success={}|uidSize={}", flag4,
                decreaseMember.size());
            //删除负责人
            boolean flag5 = attendanceDao.batchUpdate(
                    "attendance.batchDelChargemanContactId", decreaseMember);
            log.info("batchDelChargemanContactId success={}|uidSize={}", flag5,
            		decreaseMember.size());
            //删除设备信息
            boolean flag6 = attendanceDao.batchUpdate(
                "attendance.batchUpdateEquipmentAttdenName", decreaseMember);
            log.info("batchUpdateEquipmentAttdenName success={}|uidSize={}", flag6,
                decreaseMember.size());
            //考勤组创建人处理
            for(AttendEmployee attendEmployee : decreaseMember){
               AttendGroup attendGroup = (AttendGroup) attendanceDao.queryForObject("attendance.queryGroupByContactId",attendEmployee);
                if (AssertUtil.isEmpty(attendGroup)){
                    continue;
                }
                try {
                    Map<String, Object> repMap = QytxlUtil.getInstance().getContactIdByEuserId(attendGroup.getEnterId(), attendGroup.getAdminUid());
                    if(0 == (int)repMap.get("error_code")){
                        List<Map<String,String>> userList= (List<Map<String,String>>)repMap.get("contactInfo");
                        Map<String, String> userMap = userList.get(0);
                        attendGroup.setAdminContactId(userMap.get("contactId"));
                        //更新创建人信息
                        int update = attendanceDao.update("attendance.updateGroupContactId", attendGroup);
                        log.info("updateGroupContactId size={}",update);
                        //人员删除
                    }else if (999 == (int)repMap.get("error_code")){
                        int update = attendanceDao.update("attendance.deleteGroupContactId", attendGroup);
                        log.info("deleteGroupContactId size={}",update);
                    }
                } catch (Exception e) {
                    log.error("QytxlUtil.getInstance().getContactIdByEuserId error e={}",e);
                }
            }
            //考勤组创建人处理
            for(AttendEmployee attendEmployee : decreaseMember){
                AttendExamineEntity attendExamineEntity = (AttendExamineEntity) attendanceDao.queryForObject("attendance.queryExamineByContactId",attendEmployee);
                if (AssertUtil.isEmpty(attendExamineEntity)){
                    continue;
                }
                try {
                    Map<String, Object> repMap = QytxlUtil.getInstance().getContactIdByEuserId(attendExamineEntity.getEnterId(), attendExamineEntity.getExamineUid());
                    if(0 == (int)repMap.get("error_code")){
                        List<Map<String,String>> userList= (List<Map<String,String>>)repMap.get("contactInfo");
                        Map<String, String> userMap = userList.get(0);
                        attendExamineEntity.setExamineContactId(userMap.get("contactId"));
                        //更新创建人信息
                        int update = attendanceDao.update("attendance.updateExamineContactId", attendExamineEntity);
                        log.info("updateExamineContactId size={}",update);
                        //人员删除
                    }else if (999 == (int)repMap.get("error_code")){
                        int update = attendanceDao.update("attendance.deleteExamineByContactId", attendExamineEntity);
                        log.info("deleteExamineByContactId size={}",update);
                    }
                } catch (Exception e) {
                    log.error("QytxlUtil.getInstance().getContactIdByEuserId error e={}",e);
                }
            }
            return flag&&flag2&&flag3&&flag4&&flag5;
        }
        catch (PersistException e)
        {
            log.error("deleteUser error", e);
            return false;
        }
    }


    
    
    
    
    /**
     * 从数据库查询所有未注册的企业
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<AttendEnterRes> queryUnregisteredEnters()
    {
        try
        {
            // 考勤组状态为正常
            Map<String, Object> reqMap = new HashMap<String, Object>();
            
            return attendanceDao.queryForList(
                "attendance.queryUnregisteredEnters",reqMap);
        }
        catch (PersistException e)
        {
            log.error("queryUnregisteredEnters error.", e);
            return null;
        }
    }
    
    /**
     * 根据企业Id获取企业信息
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<AttendEnterRes> queryEnterByEnterId(String enterId)
    {
        try
        {
            // 考勤组状态为正常
            Map<String, Object> reqMap = new HashMap<String, Object>();
            reqMap.put("enterId", enterId);
            
            return attendanceDao.queryForList(
                "attendance.queryEnterByEnterId",reqMap);
        }
        catch (PersistException e)
        {
            log.error("queryEnterByEnterId error.", e);
            return null;
        }
    }
    
    /**
     * 更新注册企业通讯录回调状态
     * @return
     */
    public int updateEnterStatus(String enterId,int status,String eventType)
    {
    	int result = -1;
        try
        {
            attendanceDao.startTransaction();
            // 考勤组状态为正常
            Map<String, Object> reqMap = new HashMap<String, Object>();
            reqMap.put("enterId", enterId);
            reqMap.put("status", status);
            reqMap.put("eventType", eventType);
            log.info("updateEnterStatus param reqMap={}",reqMap);
            result =  attendanceDao.update("attendance.updateEnterStatus",reqMap);
            attendanceDao.commitTransaction();
            return result;
        }
        catch (PersistException e)
        {
            try {
                attendanceDao.rollbackTransaction();
            } catch (PersistException e1) {
            }
            log.error("updateEnterStatus error.", e);
            return result;
        }
    }
    
    /**
     * 添加企业信息
     * @return
     */
    public int insertEnterStatus(String enterId,int status,String eventType)
    {
    	int result = -1;
        try
        {
            attendanceDao.startTransaction();
            // 考勤组状态为正常
            Map<String, Object> reqMap = new HashMap<String, Object>();
            reqMap.put("enterId", enterId);
            reqMap.put("status", status);
            reqMap.put("eventType", eventType);
            log.info("insertEnterStatus param reqMap={}",reqMap);
            result =  attendanceDao.update("attendance.insertEnter",reqMap);
            attendanceDao.commitTransaction();
            return result;
        }
        catch (PersistException e)
        {
            try {
                attendanceDao.rollbackTransaction();
            } catch (PersistException e1) {
            }
            log.error("insertEnter error.", e);
            return result;
        }
    }

    /**
     * 查询
     * @param status
     * @return
     */
    public List<String> queryEnterCallBack(int status) {
        List<String> enters = new ArrayList<>();

        Map<String,Object> map = new HashMap<>();
        map.put("status",status);
        try {
            enters =  attendanceDao.queryForList("attendance.queryEnterCallBack", map);
        } catch (PersistException e) {
            e.printStackTrace();
            log.error("queryEnterCallBack  query data  error!");
        }
        return enters;
    }

    /**
     * 删除部门
     * @param attendDepartmentChoosers
     */
    public void deleteDepartment(List<AttendDepartmentChooser> attendDepartmentChoosers) {
        try
        {

            boolean flag = attendanceDao.batchDelete(
                    "attendance.batchDeleteDepartment", attendDepartmentChoosers);
            log.info("deleteDepartment success={}|uidSize={}", flag,
                    attendDepartmentChoosers.size());

        }
        catch (PersistException e)
        {
            log.error("batchDelMsgByUids error", e);

        }
    }

    /**
     * 删除登记企业
     */
    public boolean deleteEnter()   {
        try {
            attendanceDao.delete(
                "attendance.deleteEnter", null);
            return true;
        } catch (PersistException e) {
            log.error("deleteEnter error e={}",e);
        }
        return false;
    }

    /**
     * 根据删除的企业删除考勤组，审批人，部门
     * @param delEnter
     */
    public void delEnterGroup(List<String> delEnter) {
        log.info("delEnterGroup data delete start==============delEnter={}",delEnter);
        try {
            attendanceDao.startTransaction();
            //删除企业中所有考勤组
            attendanceDao.update(
                "attendance.delGroupByEnterId", delEnter);
            //删除企业中所有考勤人员
            attendanceDao.update(
                "attendance.delEmployeeByEnterId", delEnter);
            //删除企业中所有考勤白名单
            attendanceDao.update(
                "attendance.delWhitelistByEnterId", delEnter);
            //删除审批人
            attendanceDao.update(
                "attendance.delExamineByEnterId", delEnter);
            //删除考勤组负责人
            attendanceDao.update(
                "attendance.delGroupChargemanByEnterId", delEnter);
            //删除部门
            attendanceDao.delete(
                "attendance.delDepartmentByEnterId", delEnter);
            //删除待发信息
            attendanceDao.delete(
                "attendance.delMsgByEnterId", delEnter);
            //删除注册回调
            attendanceDao.delete(
                "attendance.delCallBackEnterId", delEnter);
            //删除设备控制表
            attendanceDao.delete(
                "attendance.delEquipmentStatusByEnterId", delEnter);
            //删除设备列表
            attendanceDao.delete(
                "attendance.delEquipmentListByEnterId", delEnter);
            attendanceDao.commitTransaction();
        }catch (Exception e){
            try {
                attendanceDao.rollbackTransaction();
            } catch (PersistException e1) {
                e1.printStackTrace();
            }
            log.error("delEnterGroup data error delEnter={}",delEnter);
        }
        log.info("delEnterGroup data delete end==============");
    }

    /**
     * 用户更新
     * @param decreaseMember
     */
    public void userModify(List<AttendEmployee> decreaseMember) {
        for (AttendEmployee attendEmployee  : decreaseMember ) {
            Map<String, Object> rspMap= null;
            try {
                rspMap = QytxlUtil.getInstance().getItem(attendEmployee.getEnterId(), attendEmployee.getContactId());
            } catch (Exception e) {
                log.info("QytxlUtil.getInstance().getItem 一次失败 e={}",e.getMessage());
                try {
                    rspMap = QytxlUtil.getInstance().getItem(attendEmployee.getEnterId(), attendEmployee.getContactId());
                } catch (Exception e1) {
                    log.info("QytxlUtil.getInstance().getItem 二次失败 e={}",e1.getMessage());
                }
            }
            int error_code = ((Double)rspMap.get("error_code")).intValue();
            log.info("==============error_code={}",error_code);
            if (AssertUtil.isEmpty(rspMap) || 0 != error_code || rspMap.get("item") == null){
                return;
            }
            Map<String, Object> item = (Map<String, Object>) rspMap.get("item");
            String euserId = (String) item.get("euserId");
            String employeeName = (String) item.get("name");
            log.info("userModify item={}||employeeName={}",item,employeeName);
                try
                {
                    employeeName = AesUtils.decrypt(employeeName, AttendanceConfig.getInstance()
                        .getProperty("attend.qytxl.aes_key",
                            "6af15ca383ee45dd"));
                }
                catch (Exception e)
                {
                    log.info("AesUtils.decrypt error e={}",e);
                }

            attendEmployee.setUid(euserId);
            attendEmployee.setEmployeeName(employeeName);
            try {
                attendanceDao.startTransaction();
                //更新考勤人员名字
                attendanceDao.update(
                    "attendance.updateEmployeeName", attendEmployee);
                //更新考勤白名单人员名字
                attendanceDao.update(
                    "attendance.updateWhiteListEmployeeName", attendEmployee);
                //更新负责人的名字
                attendanceDao.update(
                    "attendance.updateChargemanEmployeeName", attendEmployee);
                //更新考勤审批人员名字
                attendanceDao.update(
                    "attendance.updateExamineUserName", attendEmployee);
                attendanceDao.commitTransaction();
            } catch (PersistException e) {
                try {
                    attendanceDao.rollbackTransaction();
                } catch (PersistException e1) {
                }
                log.error("userModify data error attendEmployee={}",attendEmployee);
            }
        }
    }
}
