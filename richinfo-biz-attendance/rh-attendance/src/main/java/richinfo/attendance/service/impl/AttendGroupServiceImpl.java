/**
 * 文件名：AttendGroupServiceImpl.java
 * 创建日期： 2017年6月2日
 * 作者：     liuyangfei
 * Copyright (c) 2016-2017 邮箱开发室
 * All rights reserved.

 * 修改记录：
 * 	1.修改时间：2017年6月2日
 *   修改人：liuyangfei
 *   修改内容：
 */
package richinfo.attendance.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import richinfo.attendance.asyn.MessageUpdateAsynTask;
import richinfo.attendance.asyn.RegisterQytxlAsynTask;
import richinfo.attendance.bean.*;
import richinfo.attendance.cache.UserInfoCache;
import richinfo.attendance.common.*;
import richinfo.attendance.common.AtdcResultSummary.ATDC104;
import richinfo.attendance.dao.*;
import richinfo.attendance.entity.*;
import richinfo.attendance.entity.AttendEmployee.EmployeeChargemanStatus;
import richinfo.attendance.entity.AttendEmployee.EmployeeStatus;
import richinfo.attendance.entity.AttendEmployee.EmployeeType;
import richinfo.attendance.entity.AttendGroup.GroupStatus;
import richinfo.attendance.entity.vo.AttendanceEquipmentVO;
import richinfo.attendance.entity.vo.DetailVO;
import richinfo.attendance.service.AttendGroupService;
import richinfo.attendance.util.*;
import richinfo.bcomponet.asyn.AsynTaskProcess;
import richinfo.bcomponet.cache.CachedUtil;
import richinfo.dbcomponent.exception.PersistException;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static richinfo.attendance.common.AtdcResultSummary.ATDC108.NO_DATA;
//import scala.util.parsing.json.JSON;

/**
 * 功能描述：考勤组管理模块逻辑实现
 *
 */
public class AttendGroupServiceImpl extends ServiceObject implements
    AttendGroupService
{
    private final Logger logger = LoggerFactory
        .getLogger(AttendGroupServiceImpl.class);

    private AttendGroupDao groupDao = new AttendGroupDao();
    private AttendEmployeeDao employeeDao = new AttendEmployeeDao();
    private AttendAppealDao appealDao = new AttendAppealDao();
    private AttendanceConfig config = AttendanceConfig.getInstance();
    private AttendanceScheduleDao attendanceScheduleDao = new AttendanceScheduleDao();
    private AttendWhitelistDao attendWhitelistDao = new AttendWhitelistDao();
    private AttendCalendarDao calendarDao = new AttendCalendarDao();
    private AttendDepartmentDao attendDepartmentDao = new AttendDepartmentDao();
    private UserInfoCache userInfoCache = UserInfoCache.getInstance();
    private AttendLoginDao loginDao = new AttendLoginDao();
    private EnterDao enterDao = new EnterDao();

    @Override
    public AttendGroupRes createGroup(AttendGroupReq groupReq) {
        // 校验用户是否为管理员
        AttendGroupRes groupRes = checkIsAdmin(groupReq);
        if (!groupRes.isSuccess()) {
            logger.warn(
                "createGroup require admin privilege.groupReq={}|userInfo={}",
                groupReq, groupReq.getUserInfo());
            return groupRes;
        }
        // 进行参数的基本校验
        groupRes = checkReqParameter(groupReq);
        if (!groupRes.isSuccess()) {
            logger.warn(
                    "createGroup checkReqParameter failed.groupReq={}|userInfo={}|code={}|summary={}",
                    groupReq, groupReq.getUserInfo(), groupRes.getCode(),
                    groupRes.getSummary());
            return groupRes;
        }
        logger.info(
            "createGroup checkReqParam success,phone={}|uid={}|reqParam={}",
            groupReq.getUserInfo().getPhone(), groupReq.getUserInfo().getUid(),
            groupReq.getAttendanceId());

        // 组装考勤人员信息
        try {
            // 获取已经在其他考勤组的uid列表
            List<String> uids = employeeDao.queryUidInGroups(groupReq.getEmployees());
            // 批量新增、修改的考勤人员列表
            List<AttendEmployee> saveList = new ArrayList<AttendEmployee>();
            List<AttendEmployee> updateList = new ArrayList<AttendEmployee>();
            // 区别考勤人员，便于批量新增、修改
            distinguishEmployee(groupReq, uids, saveList, updateList);
            //获取已经在其他考勤组的部门id
            List<String>departmentIds =  attendDepartmentDao.queryDepartmentInGroups(groupReq.getAttendDepartmentChoosers());
            /*// 批量新增、修改的部门列表
            List<AttendDepartmentReq> saveDepartmentList = new ArrayList<>();
            List<AttendDepartmentReq> updateDepartmentList = new ArrayList<>();*/
            /*// 区别考勤人员，便于批量新增、修改
            distinguishDepartment(groupReq, departmentIds, saveDepartmentList, updateDepartmentList);*/
            // 组装考勤组基本信息
            AttendGroup attendGroup = assembleAttendGroup(groupReq);

            // 保存考勤组基本信息、考勤人员列表，在事务中处理
            if (groupDao.saveAttendGroup(attendGroup, groupReq.getScheduleShifts(), saveList, updateList,
                departmentIds,groupReq.getAttendDepartmentChoosers(),groupReq.getAttendClockSites())) {
                logger.info(
                        "saveGroupInfo success,id={}|name={}|operateUid={}|useTime={}",
                        attendGroup.getAttendanceId(), attendGroup.getAttendanceName(),
                        groupReq.getUserInfo().getUid(), groupReq.getUseTime());

                // 创建考勤组信息时，更新消息待发表中的推送目标数据
               MessageUpdateInfo info = new MessageUpdateInfo(null,
                    attendGroup, false, AttendanceUtil.getCguid(), null,
                    updateList, saveList, false);

                AsynTaskProcess.asynExecTask(new MessageUpdateAsynTask(info));
                //异步注册企业通讯录回调接口
                AsynTaskProcess.asynExecTask(new RegisterQytxlAsynTask(attendGroup.getEnterId()));
                logger.info(
                        "createGroup put it to MessageUpdateAsynTask. cguid={}|isUpdate={}|{}",
                        info.getCguid(), info.isUpdate(),
                        info.getNewAttendGroup().getAttendanceId());
            }
            else
            {
                logger.error("save employees failed.attendGroup={}", groupReq);
                return assemableDataPersistenceError(groupRes);
            }
        }
        catch (Exception e)
        {
            logger.error("save employees error.attendGroup={}", groupReq, e);
            return assemableDataPersistenceError(groupRes);
        }

        //把考勤组类型传给前端 用来判断是否需要隐藏弹性班制栏
        groupRes.setAttendType(groupReq.getAttendType());

        return groupRes;
    }



    @Override
    public AttendGroupRes checkEmployee(AttendGroupReq groupReq, boolean flag)
    {
        // 校验用户是否为管理员
        AttendGroupRes groupRes = checkIsAdmin(groupReq);
        if (!groupRes.isSuccess())
        {
            logger.warn(
                    "check employee require admin privilege.groupReq={}|userInfo={}",
                    groupReq, groupReq.getUserInfo());
            return groupRes;
        }
        // 校验考勤人员的合法性，剔除并返回无效用户
        List<UserInfo> invalidUsers = dealUsefulEmployee(groupReq,
            groupReq.getUserInfo());

        // 校验部门的合法性，剔除并返回无效部门
        List<AttendDepartmentChooser> invalidDepartment = dealDepartmentChooser(groupReq,
                groupReq.getUserInfo());
        List<UserInfo> users = groupReq.getEmployees();
        List<AttendDepartmentChooser> departmentChoosers = groupReq.getAttendDepartmentChoosers();
        // 用户列表校验
        if (AssertUtil.isEmpty(users) && AssertUtil.isEmpty(departmentChoosers))
        {
            logger.info("Users is empty.userInfo={}", groupReq.getUserInfo());
            groupRes.setCode(AtdcResultCode.ATDC104.PARAMS_NULL);
            groupRes.setSummary(AtdcResultSummary.ATDC104.EMPLOEES_IS_EMPTY);
            return groupRes;
        }

        // 查询已经有的考勤组的user列表
        List<UserInfo> userInAttend = employeeDao.queryAttendGroupByUid(users,
            flag, groupReq.getAttendanceId());
        // 查询已经有的考勤组的部门列表
        List<AttendDepartmentChooser> departmentInAttend = employeeDao.queryAttendGroupByDepartment(departmentChoosers,
                flag, groupReq.getAttendanceId());

        // 设置请求用户的考勤组关系，最终数据保存users列表中
        assembleUserList(users, userInAttend, invalidUsers);
        //设置请求用户的考勤组关系，最终数据保存Department列表中
        assembleDepartmentList(departmentChoosers, departmentInAttend, invalidDepartment);
        // 返回用户的考勤组关系
        groupRes.setEmployees(users);
        groupRes.setAttendDepartmentChoosers(departmentChoosers);
        return groupRes;
    }

    /**
     * //设置请求用户的考勤组关系，最终数据保存Department列表中
     * @param departmentChoosers 请求中有效的
     * @param departmentInAttend 已经在其他考勤组的
     * @param invalidDepartment  请求中无效的用户列表
     */
    private void assembleDepartmentList(List<AttendDepartmentChooser> departmentChoosers,
                                        List<AttendDepartmentChooser> departmentInAttend,
                                        List<AttendDepartmentChooser> invalidDepartment) {

        if (AssertUtil.isEmpty(departmentInAttend))
        {
            return;
        }
        // 设置已经在考勤组的部门的attendanceId
        for (AttendDepartmentChooser temp : departmentInAttend)
        {
            for (AttendDepartmentChooser reqDepartment : departmentChoosers)
            {
                if (reqDepartment.getDepartmentId().equals(temp.getDepartmentId()))
                {
                    // 设置为所在的考勤组ID
                    reqDepartment.setAttendanceId(temp.getAttendanceId());
                    break;
                }
            }
        }
        // 无效的用户列表的考勤组ID设置为-1
        for (AttendDepartmentChooser department : invalidDepartment)
        {
            department.setAttendanceId(-1L);
        }
        // 将无效的用户列表，添加到userList中，一并返回给前端
        departmentChoosers.addAll(invalidDepartment);

    }


    /**
     * 组装用户的考勤组关系。 不在考勤组的用户，其考勤组ID设为默认值0。无效的用户列表，其考勤组ID设为默认值-1
     * @param users 请求中有效的用户列表
     * @param userInAttend 已经在其他考勤组的用户列表
     * @param invalidUsers 请求中无效的用户列表
     */
    private void assembleUserList(List<UserInfo> users,
        List<UserInfo> userInAttend, List<UserInfo> invalidUsers)
    {
        if (AssertUtil.isEmpty(userInAttend))
        {
            return;
        }
        // 设置已经在考勤组的user的attendanceId
        for (UserInfo temp : userInAttend)
        {
            for (UserInfo reqUser : users)
            {
                if (reqUser.getUid().equals(temp.getUid()))
                {
                    // 设置为所在的考勤组ID
                    reqUser.setAttendanceId(temp.getAttendanceId());
                    break;
                }
            }
        }

        // 无效的用户列表的考勤组ID设置为-1
        for (UserInfo user : invalidUsers)
        {
            user.setAttendanceId(-1L);
        }
        // 将无效的用户列表，添加到userList中，一并返回给前端
        users.addAll(invalidUsers);

    }

    /**
     * 区别部门，便于批量新增、修改
     * @param groupReq
     * @param
     * @param saveDepartmentList
     * @param updateDepartmentList
     */
    private void distinguishDepartment(AttendGroupReq groupReq,
                                       List<String> departmentIds,
                                       List<AttendDepartmentChooser> saveDepartmentList,
                                       List<AttendDepartmentChooser> updateDepartmentList) {
        if (AssertUtil.isNotEmpty(groupReq.getAttendDepartmentChoosers())){
            String enterId = groupReq.getEnterId();
            for (AttendDepartmentChooser attendDepartmentChooser : groupReq.getAttendDepartmentChoosers()){
                attendDepartmentChooser.setCreateTime(new Date());
                attendDepartmentChooser.setUpdateTime(new Date());
                if (AssertUtil.isNotEmpty(departmentIds) && departmentIds.contains(attendDepartmentChooser.getDepartmentId())){
                    updateDepartmentList.add(attendDepartmentChooser);
                }else {
                    saveDepartmentList.add(attendDepartmentChooser);
                }
            }
        }
    }
    /**
     * 区别考勤人员，便于批量新增、修改
     * @param groupReq
     * @param uids
     * @param saveList
     * @param updateList
     */
    private void distinguishEmployee(AttendGroupReq groupReq, List<String> uids,
                                     List<AttendEmployee> saveList,
                                     List<AttendEmployee> updateList) {
        String enterId = groupReq.getEnterId();
        // 循环判断是否已经在其他考勤组中，保证一个员工仅在一个考勤组中
        for (UserInfo user : groupReq.getEmployees()) {
            AttendEmployee employee = new AttendEmployee();
            // 考勤组ID字段，暂时不设置，在事务中入库成功后添加
            // 组装实体类
            employee.setEnterId(enterId);
            employee.setEnterName( groupReq.getUserInfo().getEnterName());
            employee.setUid(user.getUid());
            employee.setContactId(user.getContactId());
            employee.setEmployeeName(user.getEmployeeName());
            employee.setPhone(user.getPhone());
            employee.setDeptId(user.getDeptId());
            employee.setDeptName(user.getDeptName());
            employee.setEmail(user.getEmail());
            employee.setPosition(user.getPosition());
            employee.setStatus(EmployeeStatus.Normal.getValue());
            employee.setRoleType(0);
            // 设置创建时间，若为更新操作，该字段不会更新
            employee.setCreateTime(new Date());
            // 设置修改时间
            employee.setModifyTime(new Date());

            // 判断该uid是否已经在其他考勤组中
            if (AssertUtil.isNotEmpty(uids) && uids.contains(employee.getUid())) {
                // 添加到修改列表
                updateList.add(employee);
            } else {
                // 添加到新增列表中
                saveList.add(employee);
            }
        }
    }

    /**
     * 组装考勤组基本信息
     * @param groupReq
     * @return
     */
    private AttendGroup assembleAttendGroup(AttendGroupReq groupReq) {
        AttendGroup attendGroup = new AttendGroup();

        attendGroup.setAttendanceName(groupReq.getAttendanceName());
        attendGroup.setEnterId(groupReq.getEnterId());
        if (AssertUtil.isNotEmpty(groupReq.getEnterName())) {
            attendGroup.setEnterName(groupReq.getEnterName());
        }else {
            attendGroup.setEnterName(groupReq.getUserInfo().getEnterName());
        }
        attendGroup.setAmTime(groupReq.getAmTime());
        attendGroup.setPmTime(groupReq.getPmTime());
        attendGroup.setLocation(groupReq.getLocation());
        attendGroup.setDetailAddr(groupReq.getDetailAddr());
        attendGroup.setLongitude(groupReq.getLongitude());
        attendGroup.setLatitude(groupReq.getLatitude());
        attendGroup.setAttendanceRange(groupReq.getRange());
        // 新增外勤打卡范围选择
        attendGroup.setAttendanceOutRange(groupReq.getOutRange());
        // 考勤组状态设为正常
        attendGroup.setStatus(GroupStatus.Normal.getValue());
        // 创建者uid
        attendGroup.setAdminUid(groupReq.getUserInfo().getUid());
        attendGroup.setAdminContactId(groupReq.getUserInfo().getContactId());
        attendGroup.setAdminName(groupReq.getUserInfo().getEmployeeName());
        // 设置创建时间、修改时间
        attendGroup.setCreateTime(new Date());
        attendGroup.setModifyTime(new Date());
        attendGroup.setExamineName(groupReq.getExamineName());
        attendGroup.setExamineUid(groupReq.getExamineUid());
        attendGroup.setExamineContactId(groupReq.getExamineContactId());
        attendGroup.setAttendType(groupReq.getAttendType());
        attendGroup.setFixedAttendRule(groupReq.getFixedAttendRule());
        attendGroup.setFreeAttendRule(groupReq.getFreeAttendRule());
        attendGroup.setAllowLateTime(groupReq.getAllowLateTime());
        attendGroup.setRelyHoliday(groupReq.getRelyHoliday());
        if (AssertUtil.isNotEmpty(groupReq.getChargemanList())) {
            attendGroup.setChargemanList(groupReq.getChargemanList());
        }
        if ( AttendGroup.AttendType.Fix.getValue() ==  groupReq.getAttendType() ){
            String fixedAttendRule = groupReq.getFixedAttendRule();
            fixedAttendRule = attendTimeForm(fixedAttendRule);
            attendGroup.setFixedAttendRule(fixedAttendRule);
        }
        //提交是否外勤，弹性上限
        attendGroup.setIsAllowedOutRangeClock(groupReq.getIsAllowedOutRangeClock());
        if (AssertUtil.isNotEmpty(groupReq.getUseFlexibleRule())) {
            attendGroup.setUseFlexibleRule(groupReq.getUseFlexibleRule());
            if (groupReq.getUseFlexibleRule()==0) {
                attendGroup.setFlexitime(groupReq.getFlexitime());
            }
        }
        return attendGroup;
    }

    /**
     * 考勤时间转换
     *
     * @param fixedAttendRule
     */
    private String attendTimeForm(String fixedAttendRule) {
        Map<String,Object> jsonObject = JSON.parseObject(fixedAttendRule);
        Set<String> strings = jsonObject.keySet();
        for (String key: strings){
            String time = (String) jsonObject.get(key);
            String[] split = time.split("-");
            String goToWork =split[0];
            String offDuty =split[1];
            String[] goToWorks = goToWork.split(":");
            String[] offDutys = offDuty.split(":");
            int amH = Integer.parseInt(goToWorks[0]);
            int amm = Integer.parseInt(goToWorks[1]);
            int pmH = Integer.parseInt(offDutys[0]);
            int pmm = Integer.parseInt(offDutys[1]);
            int mm = 0;
            int HH = 0;
            if ((pmm - amm) >= 0 ){
                mm = pmm - amm;
            }else {
                pmH =  pmH - 1;
                mm = pmm+60-amm;
            }
            if (pmH - amH == 1) {
                HH = pmH-1 - amH;
                mm = mm+60;
            }else {
                HH = pmH - amH;
            }
            if (HH/2 >= 3 ){
                String str = amH+3<10?"0"+(amH+3):(amH+3)+"";
                goToWork = goToWork+"-"+str+":"+(amm<10?"0"+amm:amm);
                offDuty = str+":"+(amm<10?"0"+amm:amm)+"-"+offDuty;
                Map<String,String>map = new HashMap<>();
                map.put("amTime",goToWork);
                map.put("pmTime",offDuty);
                jsonObject.put(key,map);
            }else {
                HH = HH/2;
                mm = HH%2!=0? (mm+60)/2 : mm/2;

                if (amm+mm >= 60){
                    String H =  HH + 1+amH <10?"0"+(HH + 1+amH):(HH + 1+amH)+"";
                    String m = amm+mm-60<10?"0"+(amm+mm-60):(amm+mm-60)+"";
                    goToWork = goToWork+"-"+H+":"+m;
                    offDuty = H+":"+m+"-"+offDuty;
                    Map<String,String>map = new HashMap<>();
                    map.put("amTime",goToWork);
                    map.put("pmTime",offDuty);
                    jsonObject.put(key,map);
                }else {
                    String H = HH +amH <10?"0"+(HH +amH):(HH +amH)+"";
                    String m = amm+mm <10?"0"+(amm+mm):(amm+mm)+"";
                    goToWork = goToWork+"-"+H+":"+m;
                    offDuty = H+":"+m+"-"+offDuty;
                    Map<String,String>map = new HashMap<>();
                    map.put("amTime",goToWork);
                    map.put("pmTime",offDuty);
                    jsonObject.put(key,map);
                }
            }
        }
        return JSONObject.toJSONString(jsonObject);
    }

//    public static void main2(String[] args) {
//        String strJson = "{"+"\"1\""+":"+"\"06:10-18:10\"," +"\"2\""+":"+"\"06:30-15:10\""+"}";
//        Map<String,Object> jsonObject = JSON.parseObject(strJson);
//        Set<String> strings = jsonObject.keySet();
//        for (String key: strings){
//            String time = (String) jsonObject.get(key);
//
//            String[] split = time.split("-");
//            String goToWork =split[0];
//            String offDuty =split[1];
//            String[] goToWorks = goToWork.split(":");
//            String[] offDutys = offDuty.split(":");
//
//            int amH = Integer.parseInt(goToWorks[0]);
//            int amm = Integer.parseInt(goToWorks[1]);
//            System.out.println(amm);
//            int pmH = Integer.parseInt(offDutys[0]);
//            int pmm = Integer.parseInt(offDutys[1]);
//            int mm = 0;
//            int HH = 0;
//            if ((pmm - amm) >= 0 ){
//                mm = pmm - amm;
//            }else {
//                pmH =  pmH - 1;
//                mm = pmm+60-amm;
//            }
//
//            if (pmH - amH == 1) {
//                HH = pmH-1 - amH;
//                mm = mm+60;
//            }else {
//                HH = pmH - amH;
//            }
//
//            System.out.println( "mm="+mm+"   HH="+HH);
//
//            if (HH/2 >= 3 ){
//                String str = amH+3<10?"0"+(amH+3):(amH+3)+"";
//                goToWork = goToWork+"-"+str+":"+(amm<10?"0"+amm:amm);
//                offDuty = str+":"+(amm<10?"0"+amm:amm)+"-"+offDuty;
//
//                System.out.println( "am="+goToWork+"   pm="+offDuty);
//
//                Map<String,String>map = new HashMap<>();
//                map.put("amTime",goToWork);
//                map.put("pmTime",offDuty);
//                jsonObject.put(key,map);
//            }else {
//                HH = HH/2;
//                mm = HH%2!=0? (mm+60)/2 : mm/2;
//                System.out.println( "mm="+mm+"   HH="+HH);
//                if (amm+mm >= 60){
//                    String H =  HH + 1+amH <10?"0"+(HH + 1+amH):(HH + 1+amH)+"";
//                    String m = amm+mm-60<10?"0"+(amm+mm-60):(amm+mm-60)+"";
//                    goToWork = goToWork+"-"+H+":"+m;
//                    offDuty = H+":"+m+"-"+offDuty;
//                    System.out.println( "am="+goToWork+"   pm="+offDuty);
//                    Map<String,String>map = new HashMap<>();
//                    map.put("amTime",goToWork);
//                    map.put("pmTime",offDuty);
//                    jsonObject.put(key,map);
//                }else {
//                    String H = HH +amH <10?"0"+(HH +amH):(HH +amH)+"";
//                    String m = amm+mm <10?"0"+(amm+mm):(amm+mm)+"";
//                    goToWork = goToWork+"-"+H+":"+m;
//                    offDuty = H+":"+m+"-"+offDuty;
//                    System.out.println( "am="+goToWork+"   pm="+offDuty);
//                    Map<String,String>map = new HashMap<>();
//                    map.put("amTime",goToWork);
//                    map.put("pmTime",offDuty);
//                    jsonObject.put(key,map);
//                }
//
//            }
//        }
//        System.out.println(JSONObject.toJSONString(jsonObject));
//    }

    /**
     * 判断企业内考勤组名称是否重复
     * @param  groupReq
     *   isUpdat 是否是编辑考勤组时校验考勤组名称
     * @return
     */
    private AttendGroupRes checkAttendName(AttendGroupReq groupReq,
        boolean isUpdate)
    {
        AttendGroupRes groupRes = new AttendGroupRes();

        List<AttendGroup> list;
        try
        {
            if (!isUpdate)
            {
                list = groupDao.queryAttendGroupByName(groupReq.getEnterId(),
                    groupReq.getAttendanceName(), -1);
            }
            else
            {
                list = groupDao.queryAttendGroupByName(groupReq.getEnterId(),
                    groupReq.getAttendanceName(), groupReq.getAttendanceId());
            }
            if (AssertUtil.isNotEmpty(list))
            {
                // 企业内考勤组名称冲突
                groupRes.setCode(AtdcResultCode.ATDC106.ATTEND_NAME_CONFLICT);
                groupRes
                    .setSummary(AtdcResultSummary.ATDC106.ATTEND_NAME_CONFLICT);
                logger
                    .error(
                        "attendanceName has conflicted.enterId={}|attendanceName={}",
                        groupReq.getEnterId(), groupReq.getAttendanceName());

                return groupRes;
            }
        }
        catch (PersistException e)
        {
            logger.error(
                "queryAttendGroupByName error.enterId={}|attendanceName={}",
                groupReq.getEnterId(), groupReq.getAttendanceName(), e);

            return assemableDataPersistenceError(groupRes);
        }

        return groupRes;
    }

    /**
     * 组装数据库异常的错误码和描述
     * @param groupRes
     */
    private AttendGroupRes assemableDataPersistenceError(AttendGroupRes groupRes)
    {
        // db异常
        groupRes.setCode(AtdcResultCode.ATDC107.DATA_PERSISTENCE_ERROR);
        groupRes.setSummary(AtdcResultSummary.ATDC107.DATA_PERSISTENCE_ERROR);

        return groupRes;
    }

    /**
     * 判断是否为管理员身份
     * @param groupReq
     * @return
     */
    private AttendGroupRes checkIsAdmin(AttendGroupReq groupReq)
    {
        AttendGroupRes groupRes = new AttendGroupRes();
        // 判断是否为管理员身份
        if (groupReq.getUserInfo().getIsAdmin() != 1 && groupReq.getUserInfo().getRoleType() != 1)
        {
            logger.warn("user is not admin.user={}", groupReq.getUserInfo());
            groupRes.setCode(AtdcResultCode.ATDC106.NOT_ADMIN);
            groupRes.setSummary(AtdcResultCode.ATDC106.NOT_ADMIN);
            return groupRes;
        }

        return groupRes;
    }

    /**
     * 校验请求参数
     * @param groupReq
     * @return
     */
    private AttendGroupRes checkReqParameter(AttendGroupReq groupReq)
    {
        AttendGroupRes groupRes = new AttendGroupRes();
        // 校验考勤组名称
        if (AssertUtil.isEmpty(groupReq.getAttendanceName()))
        {
            groupRes.setCode(AtdcResultCode.ATDC104.PARAMS_NULL);
            groupRes.setSummary(AtdcResultSummary.ATDC104.PARAMS_NULL);
            return groupRes;
        }
        // 考勤组名称长度校验
        if (groupReq.getAttendanceName().length() > config
            .getAttendNameLength())
        {
            groupRes
                .setCode(AtdcResultCode.ATDC104.ATTENDANCENAME_LENGTH_ILLEGAL);
            groupRes
                .setSummary(AtdcResultSummary.ATDC104.ATTENDANCENAME_LENGTH_ILLEGAL);
            return groupRes;
        }
        // 考勤组名称字符校验 UTF-8 可非法限制表情符号等
        if (AttendanceUtil.isEmoji(groupReq.getAttendanceName()))
        {
            groupRes.setCode(AtdcResultCode.ATDC104.PARAMS_INCLUDE_EMOJI);
            groupRes.setSummary(AtdcResultSummary.ATDC104.PARAMS_INCLUDE_EMOJI);
            return groupRes;
        }
        // 校验企业ID
        if (AssertUtil.isEmpty(groupReq.getEnterId()))
        {
            groupRes.setCode(AtdcResultCode.ATDC104.PARAMS_NULL);
            groupRes.setSummary(AtdcResultSummary.ATDC104.PARAMS_NULL);
            return groupRes;
        }
        // 校验企业ID参数和回话中的ID是否一致
        if (!groupReq.getEnterId().equals(groupReq.getUserInfo().getEnterId()))
        {
            logger.warn("reqEnterId={}|userInfo={}", groupReq.getEnterId(),
                groupReq.getUserInfo());
            groupRes.setCode(AtdcResultCode.ATDC106.NOT_BELONGTO_ADMIN);
            groupRes.setSummary(AtdcResultSummary.ATDC106.NOT_BELONGTO_ADMIN);
            return groupRes;
        }
        // 判断企业内考勤组名称是否重复
        groupRes = checkAttendName(groupReq, false);
        if (!groupRes.isSuccess())
        {
            logger.warn("attendance name repeat.groupReq={}", groupReq);
            return groupRes;
        }
        //固定班 判断校验{"1":{"amTime":"08:30-11:30","pmTime":"13:00-17:30"},"2":{"amTime":"08:30-11:30","pmTime":"13:00-17:30"},"3":{"amTime":"08:30-11:30","pmTime":"13:00-17:30"}
        // ,"4":{"amTime":"08:30-11:30","pmTime":"13:00-17:30"},"5":{"amTime":"08:30-11:30","pmTime":"13:00-17:30"}}
        // 校验上班时间
        if (AttendGroup.AttendType.Fix.getValue() == groupReq.getAttendType()){
            String fixedAttendRule = groupReq.getFixedAttendRule();
            if (AssertUtil.isEmpty(fixedAttendRule)){
                groupRes.setCode(AtdcResultCode.ATDC106.TIME_FORMAT_ERROR);
                groupRes.setSummary(AtdcResultSummary.ATDC106.TIME_FORMAT_ERROR);
                return groupRes;
            }
            Map <String,Object>jsonObject = JSON.parseObject(fixedAttendRule);
            logger.info("判断固定班上班时间，fixedAttendRule={}",fixedAttendRule);
            Set<String> keys = jsonObject.keySet();
            for (String key : keys ){
               String  time  = String.valueOf(jsonObject.get(key));
                String[] times = time.split("-");
                if (!AtdcTimeUtil.isWorkTimeLegal(times[0])
                        || !AtdcTimeUtil.isWorkTimeLegal(times[0]))
                {
                    groupRes.setCode(AtdcResultCode.ATDC106.TIME_FORMAT_ERROR);
                    groupRes.setSummary(AtdcResultSummary.ATDC106.TIME_FORMAT_ERROR);
                    return groupRes;
                }
                // 对比上班时间和下班时间，目前不支持夜班
                if (AtdcTimeUtil.compareAmtimeAndPmtime(times[0],
                        times[1]) > 0)
                {
                    groupRes.setCode(AtdcResultCode.ATDC106.PM_EARLIER_AM);
                    groupRes.setSummary(AtdcResultSummary.ATDC106.PM_EARLIER_AM);
                    return groupRes;
                }
            }

        }
        List<AttendClockSite> attendClockSites = groupReq.getAttendClockSites();
        if (AssertUtil.isEmpty(attendClockSites)){
            groupRes.setCode(AtdcResultCode.ATDC104.PARAMS_NULL);
            groupRes.setSummary(AtdcResultSummary.ATDC104.PARAMS_NULL);
            return groupRes;
        }
        if (attendClockSites.size() > 10){
            groupRes.setCode(AtdcResultCode.ATDC104.PARAMS_INVALID);
            groupRes.setSummary(AtdcResultSummary.ATDC104.ATTEND_SITE_SIZE);
            return groupRes;
        }
        //获取其中一个考勤范围判断
        int attendanceRange = attendClockSites.get(0).getAttendanceRange();
        for (AttendClockSite attendClockSite : attendClockSites){
            //目前只接受考勤统一范围
            if(attendanceRange != attendClockSite.getAttendanceRange()){
                groupRes.setCode(AtdcResultCode.ATDC104.PARAMS_INVALID);
                groupRes.setSummary(AtdcResultSummary.ATDC104.PARAMS_ILLEGAL_UID);
                return groupRes;
            }
            // 校验考勤地点
            if (AssertUtil.isEmpty(attendClockSite.getLocation())
                    || AssertUtil.isEmpty(attendClockSite.getDetailAddr()))
            {
                groupRes.setCode(AtdcResultCode.ATDC104.PARAMS_NULL);
                groupRes.setSummary(AtdcResultSummary.ATDC104.PARAMS_NULL);
                return groupRes;
            }
            // 校验考勤经度、纬度
            if (attendClockSite.getLongitude() <= 0 || attendClockSite.getLatitude() <= 0)
            {
                groupRes.setCode(AtdcResultCode.ATDC104.PARAMS_INVALID);
                groupRes.setSummary(AtdcResultSummary.ATDC104.PARAMS_INVALID);
                return groupRes;
            }
            // 校验内勤(公司打卡)有效范围
            if (attendClockSite.getAttendanceRange() <= 0)
            {
                groupRes.setCode(AtdcResultCode.ATDC104.PARAMS_INVALID);
                groupRes.setSummary(AtdcResultSummary.ATDC104.PARAMS_INVALID);
                return groupRes;
            }
            // 考勤地点,长度校验
            String location = attendClockSite.getLocation();
            if (location.length() > config.getCommonNameLength())
            {
                logger
                        .warn("location is too long,cut it off.groupReq={}", groupReq);
                groupReq.setLocation(location.substring(0,
                        config.getCommonNameLength()));
            }
            // 考勤详细地址,长度校验
            String detailAddr = attendClockSite.getDetailAddr();
            if (detailAddr.length() > config.getDetailAddrLength())
            {
                logger.warn("detailAddr is too long,cut it off.groupReq={}",
                        groupReq);
                groupReq.setDetailAddr(detailAddr.substring(0,
                        config.getDetailAddrLength()));
            }
        }


        // 校验外勤(公司外打卡)有效范围
       /* if (groupReq.getOutRange() <= 0)
        {
            groupRes.setCode(AtdcResultCode.ATDC104.PARAMS_INVALID);
            groupRes.setSummary(AtdcResultSummary.ATDC104.PARAMS_INVALID);
            return groupRes;
        }*/
        // 校验考勤人员的合法性，排除并返回无效用户
        List<UserInfo> invalidUsers = dealUsefulEmployee(groupReq,
            groupReq.getUserInfo());
        // 校验考勤部门的合法性，排除并返回部门
        List<AttendDepartmentChooser> invalidDepartment = dealDepartmentChooser(groupReq,
                groupReq.getUserInfo());
        // 校验有效考勤人员列表与部门列表
        if (AssertUtil.isEmpty(groupReq.getEmployees())&& AssertUtil.isEmpty(groupReq.getAttendDepartmentChoosers())) {
            logger.warn("employees is empty,check it.userInfo={}",
                groupReq.getUserInfo());
            groupRes.setCode(AtdcResultCode.ATDC104.PARAMS_NULL);
            groupRes.setSummary(AtdcResultSummary.ATDC104.EMPLOEES_IS_EMPTY);
            return groupRes;
        }
        // 返回无效用户，支持考勤人员局部失败
        groupRes.setEmployees(invalidUsers);
        groupRes.setAttendDepartmentChoosers(invalidDepartment);
        // 企业名称，进行长度校验，超长截断
        String enterName = groupReq.getEnterName();
        if (AssertUtil.isNotEmpty(enterName)
            && enterName.length() > config.getCommonNameLength()) {
            logger.warn("enterName is too long,cut it off.groupReq={}", groupReq);
            groupReq.setEnterName(enterName.substring(0, config.getCommonNameLength()));
        }


        // 新增审批人员设定，必填选项（考勤二期需求）
        if (AssertUtil.isEmpty(groupReq.getExamineName())
            || AssertUtil.isEmpty(groupReq.getExamineUid()))
        {
            groupRes.setCode(AtdcResultCode.ATDC104.ATTEND_NOTADD_EXAMINER);
            groupRes
                .setSummary(AtdcResultSummary.ATDC104.ATTEND_NOTADD_EXAMINER);
            return groupRes;
        }
        //堵住排班  不予创建排班
        if (AttendGroup.AttendType.Schedule.getValue() == groupReq
                .getAttendType()){
            groupRes.setCode(AtdcResultCode.ATDC106.ATTEND_GROUP_MISMATCH);
            groupRes.setSummary("暂时不予创建排班考勤组！");
            return groupRes;
        }

        // 校验排班时间
        if (AttendGroup.AttendType.Schedule.getValue() == groupReq
            .getAttendType() && groupReq.getScheduleShifts().size() > 0)
        {
            for (AttendanceScheduleShift attendanceScheduleShift : groupReq
                .getScheduleShifts())
            {
                if (!AtdcTimeUtil.isWorkTimeLegal(attendanceScheduleShift
                    .getWorkTime()))
                {
                    groupRes.setCode(AtdcResultCode.ATDC106.TIME_FORMAT_ERROR);
                    groupRes
                        .setSummary(AtdcResultSummary.ATDC106.TIME_FORMAT_ERROR);
                    return groupRes;
                }
            }
        }
        return groupRes;
    }

    @Override
    public AttendGroupRes deleteGroup(AttendGroupReq reqParam)
    {
        AttendGroupRes respBean = new AttendGroupRes();

        // 获取会话信息
        UserInfo user = reqParam.getUserInfo();

        checkDeleteGroupParam(reqParam, respBean, user);
        if (!respBean.isSuccess())
        {
            logger.info("deleteGroup checkParam failed,phone={}|uid={}|sessionEnterId={}|reqParam={}|resultCode={}|resultSummary={}",
                    user.getPhone(), user.getUid(), user.getEnterId(),
                    reqParam, respBean.getCode(), respBean.getSummary());
            return respBean;
        }
        logger.info(
            "deleteGroup checkParam success,phone={}|uid={}|reqParam={}",
            user.getPhone(), user.getUid(), reqParam.getAttendanceId());

        // 注入实体bean
        AttendGroup group = dealDeleteBeanParam(reqParam);

        // 4、删除考勤组（软删除）
        boolean result = groupDao.deleteGroup(group, user);
        if (!result) {
            respBean.setCode(AtdcResultCode.ATDC107.DATA_PERSISTENCE_ERROR);
            respBean.setSummary(AtdcResultSummary.ATDC107.DATA_PERSISTENCE_ERROR);
        } else {
            // 删除考勤组时，删除该考勤组的待发消息。
            MessageUpdateInfo info = new MessageUpdateInfo(group, group, false,
                AttendanceUtil.getCguid(), null, null, null, true);
            AsynTaskProcess.asynExecTask(new MessageUpdateAsynTask(info));
            logger.info("deleteGroup put it to MessageUpdateAsynTask. cguid={}|isUpdate={}|{}",
                    info.getCguid(), info.isUpdate(), info.getNewAttendGroup().getAttendanceId());
        }
        return respBean;
    }

    /**
     * 将删除考勤组的请求参数注入到实体bean中
     * @param reqParam
     * @return
     */
    private AttendGroup dealDeleteBeanParam(AttendGroupReq reqParam)
    {
        AttendGroup group = new AttendGroup();
        group.setAttendanceId(reqParam.getAttendanceId());
        group.setModifyTime(new Date());
        group.setStatus(GroupStatus.Abnormal.getValue());
        return group;
    }

    /**
     * 校验删除考勤组请求参数
     * @param reqParam
     * @param respBean
     * @return
     */
    private void checkDeleteGroupParam(AttendGroupReq reqParam,
        AttendGroupRes respBean, UserInfo user)
    {
        long attendanceId = reqParam.getAttendanceId();
        // 1、校验请求参数
        if (AssertUtil.isEmpty(reqParam.getEnterId()) || attendanceId <= 0)
        {
            respBean.setCode(AtdcResultCode.ATDC104.PARAMS_NULL);
            respBean.setSummary(AtdcResultSummary.ATDC104.PARAMS_NULL);
            return;
        }
        // 2、校验当前用户是否为管理员
        if (user.getIsAdmin() != 1 && (user.getRoleType()) != 1) {
            respBean.setCode(AtdcResultCode.ATDC106.NOT_ADMIN);
            respBean.setSummary(AtdcResultSummary.ATDC106.NOT_ADMIN);
            return;
        }

        // 查询出考勤组数据通过传递过来的考勤组Id
        AttendGroup group = groupDao.queryAttendGroupInfo(attendanceId,
            user.getEnterId(), GroupStatus.Normal.getValue());
        // 考勤组是否存在
        if (AssertUtil.isEmpty(group))
        {
            respBean.setCode(AtdcResultCode.ATDC106.ATTENDANCEGROUP_NOT_EXISTS);
            respBean
                .setSummary(AtdcResultSummary.ATDC106.ATTENDANCEGROUP_NOT_EXISTS);
            return;
        }
        // 校验考勤组是否属于管理员
        if (!reqParam.getEnterId().equals(user.getEnterId()))
        {
            respBean.setCode(AtdcResultCode.ATDC106.NOT_BELONGTO_ADMIN);
            respBean.setSummary(AtdcResultSummary.ATDC106.NOT_BELONGTO_ADMIN);
            return;
        }

        AttendAppealReq bean = new AttendAppealReq();
        bean.setAttendanceId(reqParam.getAttendanceId());
        AttendExamineEntity entity = null;
        try
        {
            entity = appealDao.queryExamineUid(bean);
        }
        catch (PersistException e)
        {
            logger
                .error(
                    "deleteGroup queryExamineUid query from DB failed,enterId={}|uid={}|phone={}|attendanceId={}",
                    reqParam.getEnterId(), reqParam.getUserInfo().getUid(),
                    reqParam.getUserInfo().getPhone(),
                    reqParam.getAttendanceId(), e);
            respBean.setCode(AtdcResultCode.ATDC107.DATA_PERSISTENCE_ERROR);
            respBean.setSummary(AtdcResultSummary.ATDC107.DATA_PERSISTENCE_ERROR);
            return;
        }

        if (AssertUtil.isNotEmpty(entity))
        {
            long count = 0;
            try
            {
                count = appealDao.queryNotExamineAppeal(entity.getExamineUid(),
                    reqParam.getEnterId(),attendanceId);
            }
            catch (PersistException e)
            {
                logger
                    .error(
                        "deleteGroup queryNotExamineAppeal query from DB failed,enterId={}|uid={}|phone={}|attendanceId={}",
                        reqParam.getEnterId(), reqParam.getUid(), reqParam
                            .getUserInfo().getPhone(), reqParam
                            .getAttendanceId(), e);
                respBean.setCode(AtdcResultCode.ATDC107.DATA_PERSISTENCE_ERROR);
                respBean
                    .setSummary(AtdcResultSummary.ATDC107.DATA_PERSISTENCE_ERROR);
                return;
            }
            if (count > 0)
            {
                // 审批员还有未审核的单存在，不能删除考勤组
                respBean.setCode(AtdcResultCode.ATDC104.GROUP_NOTALLOW_DEL);
                respBean
                    .setSummary(AtdcResultSummary.ATDC104.GROUP_NOTALLOW_DEL);
                return;
            }
        }
    }

    @Override
    public AttendGroupRes updateGroup(AttendGroupReq reqParam)
    {
        AttendGroupRes respBean = new AttendGroupRes();

        UserInfo userInfoCache = reqParam.getUserInfo();
        // 1、校验请求参数
        checkUpdateReqParam(reqParam, respBean, userInfoCache);

        if (!respBean.isSuccess()) {
            logger.info(
                    "updateGroup checkReqParam failed,phone={}|uid={}|sessionEnterId={}|reqParam={}|resultCode={}|resultSummary={}",
                    userInfoCache.getPhone(), userInfoCache.getUid(),
                    userInfoCache.getEnterId(), reqParam, respBean.getCode(),
                    respBean.getSummary());

            return respBean;
        }
        logger.info(
            "updateGroup checkReqParam success,phone={}|uid={}|reqParam={}",
            userInfoCache.getPhone(), userInfoCache.getUid(), reqParam.getAttendanceId());

        //获取企业原有的部门
        List<String> departmentIdList = attendDepartmentDao.queryDepartmentIdByAttendanceId(reqParam.getAttendanceId());
        //logger.info("==================departmentIdList={}",departmentIdList);
        boolean consequence = true;
        //判断获取删除的部门
        judgeDelDepartmentEmployee( consequence,reqParam ,departmentIdList);
        if (!consequence){
            AttendGroupRes groupRes = new AttendGroupRes();
            respBean.setCode(ResultCode.S_ERROR);
            respBean.setSummary(AtdcResultSummary.ATDC102.QYTXL_SESSION_ERROR);
            // 模板内容返回值
            return respBean;
        }
        logger.info(
                "updateGroup Employees={}",
                reqParam.getEmployees());

        // 3、处理考勤组新增、减少成员
        /** 减少成员 */
        List<AttendEmployee> decreaseMember = null;
        /** 更新的成员uid列表(update) */
        List<AttendEmployee> updateIncrease = null;
        /** 新增的成员uid列表(insert) */
        List<AttendEmployee> insertIncrease = null;
        // 如果考勤组成员有变动时则执行下面流程
        decreaseMember = new ArrayList<AttendEmployee>();
        updateIncrease = new ArrayList<AttendEmployee>();
        insertIncrease = new ArrayList<AttendEmployee>();
        // 成员数据处理
        dealInAndDecreaseMembers(reqParam, decreaseMember, updateIncrease, insertIncrease);

        // 3.1 处理考勤组负责人 新增减少成员
        // 查找出原考勤组负责人列表
        List<AttendEmployee> chargeMans_Old = employeeDao.queryChargeMansByAttendanceId(Long.toString(reqParam.getAttendanceId()));
//        logger.info("原考勤组负责人列表,chargeMans_Old={}",chargeMans_Old);
        // 待提交的考勤组负责人列表
        List<UserInfo> chargeMans_New = reqParam.getChargemanList();
//        logger.info("待提交考勤组负责人列表,chargeMans_New={}",chargeMans_New);
        if (AssertUtil.isNotEmpty(chargeMans_Old) && AssertUtil.isNotEmpty(chargeMans_New)) {
            compareChargeManList(chargeMans_Old,chargeMans_New);
        }
//        logger.info("对比筛选后的原考勤组负责人列表,chargeMans_Old={}",chargeMans_Old);
//        logger.info("对比筛选后的待提交考勤组负责人列表,chargeMans_New={}",chargeMans_New);

        //要剔除的考勤组负责人
        if (AssertUtil.isNotEmpty(chargeMans_Old)) {
            List<String> attendanceIds = employeeDao.queryChargeGroupByUid(userInfoCache.getUid());
            if (AssertUtil.isEmpty(attendanceIds)) {
                employeeDao.batchUpdateEmpRoleType(chargeMans_Old, EmployeeType.NormalEmp.getValue());
            }
            employeeDao.batchUpdateEmpChargeStatus(chargeMans_Old, EmployeeChargemanStatus.NoneUse.getValue(),Long.toString(reqParam.getAttendanceId()));
        }
        //待添加考勤组负责人
        if (AssertUtil.isNotEmpty(chargeMans_New)) {
            employeeDao.batchUpdateEmpRoleType(chargeMans_New,EmployeeType.ChargeMan.getValue());
            List<AttendChargemanlistEntity> chargemanlistEntities = new ArrayList<>();
            AttendChargemanlistEntity entity = new AttendChargemanlistEntity();
            for (UserInfo info : chargeMans_New) {
                entity.setUid(info.getUid());
                entity.setAttendanceId(reqParam.getAttendanceId());
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
                chargemanlistEntities.add(entity);
                entity = new AttendChargemanlistEntity();
            }
            employeeDao.batchSaveChargemanList(chargemanlistEntities);
        }
        // 4、处理考勤组基本信息数据
        AttendGroup attendGroup = dealAttendanceInfo(reqParam);
        //获取已经在其他考勤组的部门id
        List<String>departmentIds =  attendDepartmentDao.queryDepartmentInGroups(reqParam.getAttendDepartmentChoosers());
        if (AssertUtil.isEmpty(departmentIds)){
            departmentIds =departmentIdList;
        }
        //添加删除的部门id
        else if (AssertUtil.isNotEmpty(departmentIdList)){
            departmentIds.addAll(departmentIdList);
        }
        AttendGroup oldAttendGroup = getAttendGroupInfoFromCache(
            reqParam.getAttendanceId(), reqParam.getUserInfo().getEnterId());

        // 5、进行数据更新
        boolean result = groupDao.updateGroup(attendGroup,reqParam.getScheduleShifts(), decreaseMember,
            updateIncrease, insertIncrease, userInfoCache,reqParam.getAttendDepartmentChoosers(),departmentIds,reqParam.getAttendClockSites());

        if (!result) {
            logger.error("updateGroup failed,phone={}|uid={}|reqParam={}",
                userInfoCache.getPhone(), userInfoCache.getUid(), reqParam);
            respBean.setCode(AtdcResultCode.ATDC107.DATA_PERSISTENCE_ERROR);
            respBean.setSummary(AtdcResultSummary.ATDC107.DATA_PERSISTENCE_ERROR);
        } else {
            // 删除考勤组信息缓存
            /*String key = AttendanceUtil.getGroupCachekey(reqParam
                .getAttendanceId());
            CachedUtil.delete(key);*/
            /*logger
                .info(
                    "updateGroup and deleteGroupInfo Cached success,phone={}|uid={}|reqParam={}",
                    userInfoCache.getPhone(), userInfoCache.getUid(), reqParam);*/

            // 编辑考勤组信息时，更新消息待发表中的推送目标数据
            MessageUpdateInfo info = new MessageUpdateInfo(oldAttendGroup,
                attendGroup, true, AttendanceUtil.getCguid(), decreaseMember,
                updateIncrease, insertIncrease, false);
            AsynTaskProcess.asynExecTask(new MessageUpdateAsynTask(info));
            logger.info("updateGroup put it to MessageUpdateAsynTask. cguid={}|isUpdate={}|{}",
                    info.getCguid(), info.isUpdate(), info.getNewAttendGroup().getAttendanceId());

        }

        //把考勤组类型传给前端 用来判断是否需要隐藏弹性班制栏
        respBean.setAttendType(reqParam.getAttendType());
        return respBean;
    }

    private void compareChargeManList(List<AttendEmployee> chargeMans_Old,List<UserInfo> chargeMans_New){
        Iterator<UserInfo> iterator = chargeMans_New.iterator();

        Iterator<AttendEmployee> iteratorIn = chargeMans_Old.iterator();
        while (iterator.hasNext()) {
            UserInfo userInfo = iterator.next();
            while (iteratorIn.hasNext()) {
                AttendEmployee userInfoIn = iteratorIn.next();
                if (userInfo.getUid().equals(userInfoIn.getUid())) {
                    iterator.remove();
                    iteratorIn.remove();
                    continue;
                }
            }
        }
    }

    /**
     * 判断获取已经删除的部门进行人员删除
     */

    private void judgeDelDepartmentEmployee(boolean consequence,AttendGroupReq reqParam , List<String> departmentIdList) {
        String enterId = reqParam.getUserInfo().getEnterId();
        List<AttendDepartmentChooser> attendDepartmentChoosers = reqParam.getAttendDepartmentChoosers();
        if (AssertUtil.isEmpty(attendDepartmentChoosers) && AssertUtil.isEmpty(departmentIdList)){
            return;

        }
        if (AssertUtil.isNotEmpty(attendDepartmentChoosers)){

            //遍历删除存在的  剩下的就是准备删除的部门
            for (AttendDepartmentChooser attendDepartmentChooser : attendDepartmentChoosers){
                String departmentId = attendDepartmentChooser.getDepartmentId();
                if (departmentIdList.contains(departmentId)){
                    departmentIdList.remove(departmentId);
                }
            }
        }
        if (AssertUtil.isNotEmpty(departmentIdList)){
            List<String> euserIds = new ArrayList<>();
            Map<String,Object> objectMap = new HashMap<>();
            for (String departmentId : departmentIdList){
                try {
                    objectMap = QytxlUtil.getInstance().gainDepartmentStaff(departmentId,enterId);
                    //调用失败重试
                } catch (Exception e) {
                    logger.info("调用企业通讯录获取直属联系人一次失败 e={}",e);
                    try {
                        objectMap = QytxlUtil.getInstance().gainDepartmentStaff(departmentId,enterId);
                    } catch (Exception e1) {
                        consequence = false;
                        logger.info("调用企业通讯录获取直属联系人二次失败 e={}",e);
                        return;
                    }
                }
                if (0 != (int)objectMap.get("error_code")){
                    consequence = false;
                    logger.error(" gainDepartmentStaff Qytxl error objectMapJson = {}",objectMap);
                    return ;
                }
                List<Map<String,Object>> items =(List<Map<String,Object>>) objectMap.get("items");
                if (AssertUtil.isNotEmpty(items)){
                    for (Map<String,Object> employeeMap : items){
                        String contactId = (String) employeeMap.get("contactId");
                        euserIds.add(contactId);
                    }
                }
            }
            logger.info("judgeDelDepartmentEmployee euserIds={}",euserIds);
            //获取传过来的人员进行迭代器遍历判断上传
            List<UserInfo> employees = reqParam.getEmployees();
            //获取迭代器
            Iterator<UserInfo> iterator = employees.iterator();
            while (iterator.hasNext()){
                UserInfo userInfo = iterator.next();
                if (euserIds.contains(userInfo.getContactId())){
                    iterator.remove();
                }
            }
            reqParam.setEmployees(employees);

        }

    }

    /**
     * 将编辑考勤组请求参数转化成数据库实体
     * @param reqParam
     * @return
     */
    private AttendGroup dealAttendanceInfo(AttendGroupReq reqParam)
    {
        AttendGroup group = new AttendGroup();
        group.setAttendanceId(reqParam.getAttendanceId());
        group.setAmTime(reqParam.getAmTime());
        group.setAttendanceName(reqParam.getAttendanceName());
        group.setAttendanceRange(reqParam.getRange());
        group.setAttendanceOutRange(reqParam.getOutRange());
        group.setDetailAddr(reqParam.getDetailAddr());
        group.setEnterId(reqParam.getEnterId());
        group.setLatitude(reqParam.getLatitude());
        group.setLocation(reqParam.getLocation());
        group.setLongitude(reqParam.getLongitude());
        group.setModifyTime(new Date());
        group.setPmTime(reqParam.getPmTime());
        group.setEnterName(reqParam.getEnterName());
        group.setExamineName(reqParam.getExamineName());
        group.setExamineUid(reqParam.getExamineUid());
        group.setExamineContactId(reqParam.getExamineContactId());
        group.setAttendType(reqParam.getAttendType());
        group.setFixedAttendRule(reqParam.getFixedAttendRule());
        group.setFreeAttendRule(reqParam.getFreeAttendRule());
        group.setAllowLateTime(reqParam.getAllowLateTime());
        group.setRelyHoliday(reqParam.getRelyHoliday());
        if ( AttendGroup.AttendType.Fix.getValue() ==  reqParam.getAttendType() ){
            String fixedAttendRule = reqParam.getFixedAttendRule();
            fixedAttendRule = attendTimeForm(fixedAttendRule);
            group.setFixedAttendRule(fixedAttendRule);
        }
        group.setIsAllowedOutRangeClock(reqParam.getIsAllowedOutRangeClock());
        if (AssertUtil.isNotEmpty(reqParam.getUseFlexibleRule())) {
            group.setUseFlexibleRule(reqParam.getUseFlexibleRule());
            if (reqParam.getUseFlexibleRule()==0) {
                group.setFlexitime(reqParam.getFlexitime());
            }
        }

        return group;
    }

    private void dealInAndDecreaseMembers(AttendGroupReq reqParam,
        List<AttendEmployee> decreaseMember,
        List<AttendEmployee> updateIncrease,
        List<AttendEmployee> insertIncrease)
    {
        // 1、查询当前考勤组所有成员的uid信息
        /*AttendGroup groupInfos = getAttendGroupInfoFromCache(
            reqParam.getAttendanceId(), reqParam.getUserInfo().getEnterId());*/
        List<String> uids = groupDao.queryAttendGroupUid(reqParam.getAttendanceId(),
                GroupStatus.Normal.getValue());
        // 判断缓存中的uid是否存在
       /* if (AssertUtil.isEmpty(groupInfos)
            || AssertUtil.isEmpty(groupInfos.getEmployees()))
        {
            uids = groupDao.queryAttendGroupUid(reqParam.getAttendanceId(),
                GroupStatus.Normal.getValue());
        }
        else
        {
            // 从缓存中获取
            uids = groupInfos.getEmployees();
        }*/

        // 2、循环判断，分离出修改成员中新增和减少的成员
        /** 新增成员（包括全新添加和更新添加） */
        List<UserInfo> increaseMember = new ArrayList<UserInfo>();

        // 获取新增成员(包括考勤员工信息表中已存在和不存在的数据)
        for (UserInfo u : reqParam.getEmployees())
        {
            if (!uids.contains(u.getUid()))
            {
            	u.setEnterName(reqParam.getUserInfo().getEnterName());
                increaseMember.add(u);
            }
        }
        // 获取减少成员
        getDecreaseMembers(uids, reqParam, decreaseMember);

        // 3、获取新增成员的uid信息，查询考勤员工信息表，判断哪些是全新增加（之前库中没存在数据），哪些是修改
        /** 构建查询出新增uid哪些是已存在的员工记录的请求参数 */
        List<AttendEmployee> queryupdateIncrease = new ArrayList<AttendEmployee>();
        if (AssertUtil.isNotEmpty(increaseMember))
        {
            // String uidsStr = buildUidsParams(increaseMember);
            /** 更新的成员uid列表(update) */
            queryupdateIncrease = groupDao.queryUidRecord(increaseMember);

            // 获取全新增加的成员信息（考勤员工信息表中不存在的员工记录添加到考勤组）
            getInsertIncreaseMembers(reqParam, increaseMember,
                queryupdateIncrease, updateIncrease, insertIncrease);

            // 为需要更新的成员添加更新所需数据
            // for (AttendEmployee att : queryupdateIncrease)
            // {
            // att.setModifyTime(new Date());
            // att.setStatus(EmployeeStatus.Normal.getValue());
            // att.setAttendanceId(reqParam.getAttendanceId());
            // updateIncrease.add(att);
            // }
        }
    }

    /**
     * 获得编辑考勤组成员时全新增加的成员信息
     * @param reqParam
     * @param increaseMember
     * @param updateIncrease
     * @param insertIncrease
     */
    private void getInsertIncreaseMembers(AttendGroupReq reqParam,
        List<UserInfo> increaseMember,
        List<AttendEmployee> queryupdateIncrease,
        List<AttendEmployee> updateIncrease, List<AttendEmployee> insertIncrease) {
        for (UserInfo uu : increaseMember) {
            boolean flag = false;
            for (AttendEmployee u : queryupdateIncrease) {
                if (uu.getUid().equals(u.getUid())) {
                    u.setModifyTime(new Date());
                    u.setStatus(EmployeeStatus.Normal.getValue());
                    u.setAttendanceId(reqParam.getAttendanceId());
                    // 重置一下姓名和公司名称，以当前传递过来的用户信息为准
                    u.setEmployeeName(uu.getEmployeeName());
                    u.setEnterName(reqParam.getUserInfo().getEnterName());
                    u.setEnterId(reqParam.getEnterId());
                    u.setContactId(uu.getContactId());
//                    u.setRoleType(uu.getRoleType()==null?0:uu.getRoleType());
                    updateIncrease.add(u);
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                AttendEmployee attend = new AttendEmployee();
                attend.setAttendanceId(reqParam.getAttendanceId());
                attend.setDeptId(uu.getDeptId());
                attend.setDeptName(uu.getDeptName());
                attend.setEmail(uu.getEmail());
                attend.setCreateTime(new Date());
                attend.setModifyTime(new Date());
                attend.setPhone(uu.getPhone());
                attend.setPosition(uu.getPosition());
                attend.setUid(uu.getUid());
                attend.setContactId(uu.getContactId());
                attend.setEmployeeName(uu.getEmployeeName());
                attend.setStatus(EmployeeStatus.Normal.getValue());
//                attend.setRoleType(uu.getRoleType()==null?0:uu.getRoleType());
                attend.setEnterId(reqParam.getEnterId());
                attend.setEnterName(reqParam.getUserInfo().getEnterName());
                insertIncrease.add(attend);
            }
        }
    }

    /**
     * 获得编辑考勤组成员时减少的成员信息
     * @param uids
     * @param reqParam
     * @param decreaseMember
     */
    private void getDecreaseMembers(List<String> uids, AttendGroupReq reqParam,
        List<AttendEmployee> decreaseMember) {
        for (String uid : uids) {
            boolean flag = false;
            for (UserInfo u : reqParam.getEmployees()) {
                if (uid.equals(u.getUid())) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                AttendEmployee employee = new AttendEmployee();
                employee.setUid(uid);
                employee.setModifyTime(new Date());
                employee.setStatus(EmployeeStatus.Abnormal.getValue());
                employee.setAttendanceId(reqParam.getAttendanceId());
//                employee.setRoleType(0);
                decreaseMember.add(employee);
            }
        }
    }

    /**
     * 校验编辑考勤组请求参数
     * @param reqParam
     * @return
     */
    private void checkUpdateReqParam(AttendGroupReq reqParam,
        AttendGroupRes respBean, UserInfo userInfoCache)
    {
        //堵住排班  不予创建排班
        if (AttendGroup.AttendType.Schedule.getValue() == reqParam
                .getAttendType()){
            respBean.setCode(AtdcResultCode.ATDC106.ATTEND_GROUP_MISMATCH);
            respBean.setSummary("暂时不予编辑排班考勤组！");
            return ;
        }
        // 考勤组id为空
        if (reqParam.getAttendanceId() == -1
            || AssertUtil.isEmpty(reqParam.getEnterId())) {
            respBean.setCode(AtdcResultCode.ATDC104.PARAMS_NULL);
            respBean.setSummary(AtdcResultSummary.ATDC104.PARAMS_NULL);
            return;
        }

        // 考勤组id不合法
        if (reqParam.getAttendanceId() <= 0) {
            respBean.setCode(AtdcResultCode.ATDC104.PARAMS_INVALID);
            respBean.setSummary(AtdcResultSummary.ATDC104.PARAMS_INVALID);
            return;
        }
        // 校验是否是管理员
        if (userInfoCache.getIsAdmin() != 1 && userInfoCache.getRoleType() != 1) {
            respBean.setCode(AtdcResultCode.ATDC106.NOT_ADMIN);
            respBean.setSummary(AtdcResultSummary.ATDC106.NOT_ADMIN);
            return;
        }

        // 根据传递进来的考勤组Id查询考勤组信息
        AttendGroup groupInfo = getAttendGroupInfoFromCache(
            reqParam.getAttendanceId(), userInfoCache.getEnterId());

        if (AssertUtil.isEmpty(groupInfo)) {
            // 考勤组不存在
            respBean.setCode(AtdcResultCode.ATDC106.ATTENDANCEGROUP_NOT_EXISTS);
            respBean.setSummary(AtdcResultSummary.ATDC106.ATTENDANCEGROUP_NOT_EXISTS);
            return;
        }

        // 校验考勤组是否属于管理员
        if (!reqParam.getEnterId().equals(userInfoCache.getEnterId()))
        {
            respBean.setCode(AtdcResultCode.ATDC106.NOT_BELONGTO_ADMIN);
            respBean.setSummary(AtdcResultSummary.ATDC106.NOT_BELONGTO_ADMIN);
            return;
        }

        if (AssertUtil.isNotEmpty(reqParam.getAttendanceName()))
        {
            // 1、考勤组名称长度限制规则
            int len = reqParam.getAttendanceName().length();
            if (len > config.getAttendNameLength())
            {
                respBean.setCode(AtdcResultCode.ATDC104.ATTENDANCENAME_LENGTH_ILLEGAL);
                respBean.setSummary(AtdcResultSummary.ATDC104.ATTENDANCENAME_LENGTH_ILLEGAL);
                return;
            }

            // 考勤组名称字符校验 UTF-8 可非法限制表情符号等
            if (AttendanceUtil.isEmoji(reqParam.getAttendanceName()))
            {
                respBean.setCode(AtdcResultCode.ATDC104.PARAMS_INCLUDE_EMOJI);
                respBean.setSummary(AtdcResultSummary.ATDC104.PARAMS_INCLUDE_EMOJI);
                return;
            }

            // 2、同一企业，是否有考勤组名称重复问题
            AttendGroupRes respBeans = checkAttendName(reqParam, true);
            if (!respBeans.isSuccess())
            {
                respBean.setCode(respBeans.getCode());
                respBean.setSummary(respBeans.getSummary());
                return;
            }

        }
        if (reqParam.getAttendType() == AttendGroup.AttendType.Fix.getValue()){
            String fixedAttendRule = reqParam.getFixedAttendRule();
            if (AssertUtil.isEmpty(fixedAttendRule)){
                respBean.setCode(AtdcResultCode.ATDC106.TIME_FORMAT_ERROR);
                respBean.setSummary(AtdcResultSummary.ATDC106.TIME_FORMAT_ERROR);
                return ;
            }
            Map <String,Object>jsonObject = JSON.parseObject(fixedAttendRule);
            Set<String> keys = jsonObject.keySet();
            for (String key : keys ){
                String  time  = (String)jsonObject.get(key);
                String[] times = time.split("-");
                if (!AtdcTimeUtil.isWorkTimeLegal(times[0])
                        || !AtdcTimeUtil.isWorkTimeLegal(times[0]))
                {
                    respBean.setCode(AtdcResultCode.ATDC106.TIME_FORMAT_ERROR);
                    respBean.setSummary(AtdcResultSummary.ATDC106.TIME_FORMAT_ERROR);
                    return ;
                }
                // 对比上班时间和下班时间，目前不支持夜班
                if (AtdcTimeUtil.compareAmtimeAndPmtime(times[0],
                        times[1]) > 0)
                {
                    respBean.setCode(AtdcResultCode.ATDC106.PM_EARLIER_AM);
                    respBean.setSummary(AtdcResultSummary.ATDC106.PM_EARLIER_AM);
                    return ;
                }

                // 校验上、下班时间规则
                if (AssertUtil.isNotEmpty(times[0])
                        || AssertUtil.isNotEmpty(times[1]))
                {
                    // 1、校验时间格式
                    if ((AssertUtil.isNotEmpty(times[0]) && !AtdcTimeUtil
                            .isWorkTimeLegal(times[0]))
                            || (AssertUtil.isNotEmpty(times[1]) && !AtdcTimeUtil
                            .isWorkTimeLegal(times[1])))
                    {
                        respBean.setCode(AtdcResultCode.ATDC106.TIME_FORMAT_ERROR);
                        respBean
                                .setSummary(AtdcResultSummary.ATDC106.TIME_FORMAT_ERROR);
                        return;
                    }

                    // 2、校验上、下班时间 对比上班时间和下班时间，目前不支持夜班
                    // 此处需要增加数据库查询，如果只更改了上午班次或下午班次，需要从库里面查询对应的上或下午班次进行比对

                    if (AssertUtil.isNotEmpty(times[0])
                            && AssertUtil.isNotEmpty(times[1]))
                    {
                        if (AtdcTimeUtil.compareAmtimeAndPmtime(times[0],
                                times[1]) > 0)
                        {
                            respBean.setCode(AtdcResultCode.ATDC106.PM_EARLIER_AM);
                            respBean
                                    .setSummary(AtdcResultSummary.ATDC106.PM_EARLIER_AM);
                            return;
                        }
                    }
                    else
                    {
                        // 上班时间进行了修改，下班时间没用进行修改
                        if (AssertUtil.isNotEmpty(times[0]))
                        {
                            respBean = validWorkTime(respBean, times[0],
                                    times[1]);
                            if (!respBean.isSuccess())
                            {
                                return;
                            }
                        }
                        // 下班时间进行了修改，上班时间没用进行修改
                        else if (AssertUtil.isNotEmpty(times[1]))
                        {
                            respBean = validWorkTime(respBean, times[0],
                                    times[1]);
                            if (!respBean.isSuccess())
                            {
                                return;
                            }
                        }
                    }

                }

            }
        }

        List<AttendClockSite> attendClockSites = reqParam.getAttendClockSites();
        if (AssertUtil.isEmpty(attendClockSites)){
            respBean.setCode(AtdcResultCode.ATDC104.PARAMS_INVALID);
            respBean.setSummary(AtdcResultSummary.ATDC104.PARAMS_INVALID);
            return;
        }
        if (attendClockSites.size() > 10){
            respBean.setCode(AtdcResultCode.ATDC104.PARAMS_INVALID);
            respBean.setSummary(AtdcResultSummary.ATDC104.ATTEND_SITE_SIZE);
            return ;
        }
        //获取第一个考勤范围进行判断
        int attendanceRange = attendClockSites.get(0).getAttendanceRange();
        for (AttendClockSite attendClockSite : attendClockSites){
            //判断范围是否唯一
            if(attendanceRange != attendClockSite.getAttendanceRange()){
                respBean.setCode(AtdcResultCode.ATDC104.PARAMS_INVALID);
                respBean.setSummary(AtdcResultSummary.ATDC104.PARAMS_ILLEGAL_UID);
                return ;
            }
            // 校验经纬度有效性
            if (attendClockSite.getLongitude() <= 0 || attendClockSite.getLatitude() <= 0) {
                respBean.setCode(AtdcResultCode.ATDC104.PARAMS_INVALID);
                respBean.setSummary(AtdcResultSummary.ATDC104.PARAMS_INVALID);
                return;
            }

            // 校验打卡范围有效性(公司内打卡)
            if (attendClockSite.getAttendanceRange() <= 0) {
                respBean.setCode(AtdcResultCode.ATDC104.PARAMS_INVALID);
                respBean.setSummary(AtdcResultSummary.ATDC104.PARAMS_INVALID);
                return;
            }
            // 考勤地点,长度校验
            String location = attendClockSite.getLocation();
            if (AssertUtil.isNotEmpty(location)
                    && location.length() > config.getCommonNameLength()) {
                attendClockSite.setLocation(location.substring(0,
                        config.getCommonNameLength()));
            }
            // 考勤详细地址,长度校验
            String detailAddr = attendClockSite.getDetailAddr();
            if (AssertUtil.isNotEmpty(detailAddr)
                    && detailAddr.length() > config.getDetailAddrLength()) {
                attendClockSite.setDetailAddr(detailAddr.substring(0,
                        config.getDetailAddrLength()));
            }
        }

        // 校验打卡范围有效性(公司外打卡)
        /*if (reqParam.getOutRange() <= 0)
        {
            respBean.setCode(AtdcResultCode.ATDC104.PARAMS_INVALID);
            respBean.setSummary(AtdcResultSummary.ATDC104.PARAMS_INVALID);
            return;
        }*/
        // 校验有效考勤人员列表与部门列表
        if (AssertUtil.isEmpty(reqParam.getEmployees())&& AssertUtil.isEmpty(reqParam.getAttendDepartmentChoosers()))
        {
            logger.warn("employees is empty,check it.userInfo={}", reqParam.getUserInfo());
            respBean.setCode(AtdcResultCode.ATDC104.PARAMS_NULL);
            respBean.setSummary(AtdcResultSummary.ATDC104.PARAMS_NULL);
            return ;
        }

        // 对考勤成员进行处理,并返回无效的用户
        List<UserInfo> unUsefullUser = dealUsefulEmployee(reqParam,
            userInfoCache);
        respBean.setEmployees(unUsefullUser);

        // 企业名称，进行长度校验，超长截断
        String enterName = reqParam.getEnterName();
        if (AssertUtil.isNotEmpty(enterName)
            && enterName.length() > config.getCommonNameLength())
        {
            reqParam.setEnterName(enterName.substring(0,
                config.getCommonNameLength()));
        }

        // 判断考勤组是否已添加了审批员逻辑业务部分
        if (AssertUtil.isEmpty(reqParam.getExamineName()) || AssertUtil.isEmpty(reqParam.getExamineUid()))
        {
            // 审批员基本信息姓名和uid要么都为空，要么都不为空，否则不予通过
            respBean.setCode(AtdcResultCode.ATDC104.PARAMS_INVALID);
            respBean.setSummary(AtdcResultSummary.ATDC104.PARAMS_INVALID);
            return;
        }

       /* AttendAppealReq bean = new AttendAppealReq();
        bean.setAttendanceId(reqParam.getAttendanceId());
        AttendExamineEntity entity = null;
        try
        {
            entity = appealDao.queryExamineUid(bean);
        }
        catch (PersistException e)
        {
            logger
                .error(
                    "updateGroup queryExamineUid query from DB failed,enterId={}|uid={}|phone={}|attendanceId={}",
                    reqParam.getEnterId(), reqParam.getUserInfo().getUid(),
                    reqParam.getUserInfo().getPhone(),
                    reqParam.getAttendanceId(), e);
            respBean.setCode(AtdcResultCode.ATDC107.DATA_PERSISTENCE_ERROR);
            respBean.setSummary(AtdcResultSummary.ATDC107.DATA_PERSISTENCE_ERROR);
            return;
        }*/
      /*  if (AssertUtil.isEmpty(entity))
        {*/
            // 考勤组未添加审批员
            if (AssertUtil.isEmpty(reqParam.getExamineName())
                || AssertUtil.isEmpty(reqParam.getExamineUid()))
            {
                respBean.setCode(AtdcResultCode.ATDC104.ATTEND_NOTADD_EXAMINER);
                respBean
                    .setSummary(AtdcResultSummary.ATDC104.ATTEND_NOTADD_EXAMINER);
                return;
            }
       // }
        /*if (AssertUtil.isNotEmpty(reqParam.getExamineName())
            && AssertUtil.isNotEmpty(reqParam.getExamineUid()))
        {

            if (AssertUtil.isEmpty(entity)
                || (!reqParam.getExamineName().equals(entity.getExamineName()) && !reqParam
                    .getExamineUid().equals(entity.getExamineUid())))
            {
                // 需进行修改，则查询考勤组是否还有未审核的单
                judgeNotExamineAppeal(reqParam, respBean, entity);
                if (!respBean.isSuccess())
                {
                    return;
                }
            }
        }*/

        // 校验排班时间
        if (reqParam.getScheduleShifts().size() > 0)
        {
            for (AttendanceScheduleShift attendanceScheduleShift : reqParam
                .getScheduleShifts())
            {
                if (!AtdcTimeUtil.isWorkTimeLegal(attendanceScheduleShift
                    .getWorkTime()))
                {
                    respBean.setCode(AtdcResultCode.ATDC106.TIME_FORMAT_ERROR);
                    respBean
                        .setSummary(AtdcResultSummary.ATDC106.TIME_FORMAT_ERROR);
                    return;
                }
            }
        }

        /*if (AssertUtil.isNotEmpty(reqParam.getExamineName())
            && AssertUtil.isNotEmpty(reqParam.getExamineUid())
            && AssertUtil.isNotEmpty(entity)
            && reqParam.getExamineName().equals(entity.getExamineName())
            && reqParam.getExamineUid().equals(entity.getExamineUid()))
        {
            // 如果传递的审批员信息与当前的一致，则表明无需更新修改，设定成null,方便具体业务处理sql
            reqParam.setExamineName(null);
            reqParam.setExamineUid(null);
        }*/
    }

    /***
     * 修改考勤组时，判断是否还有审批员存在未审核的单
     * @param reqParam
     * @param respBean
     */
    private void judgeNotExamineAppeal(AttendGroupReq reqParam,
        AttendGroupRes respBean, AttendExamineEntity entity)
    {
        if (AssertUtil.isEmpty(entity))
        {
            return;
        }
        long count = 0;
        try
        {
            count = appealDao.queryNotExamineAppeal(entity.getExamineUid(),
                reqParam.getEnterId(),reqParam.getAttendanceId());
        }
        catch (PersistException e)
        {
            logger
                .error(
                    "updateGroup queryNotExamineAppeal query from DB failed,enterId={}|uid={}|phone={}|attendanceId={}",
                    reqParam.getEnterId(), reqParam.getUid(), reqParam
                        .getUserInfo().getPhone(), reqParam.getAttendanceId(),
                    e);
            respBean.setCode(AtdcResultCode.ATDC107.DATA_PERSISTENCE_ERROR);
            respBean
                .setSummary(AtdcResultSummary.ATDC107.DATA_PERSISTENCE_ERROR);
            return;
        }
        if (count > 0)
        {
            // 审批员还有未审核的单存在
            respBean.setCode(AtdcResultCode.ATDC104.EXAMINER_NOT_APPEAL);
            respBean.setSummary(AtdcResultSummary.ATDC104.EXAMINER_NOT_APPEAL);
            return;
        }
    }

    /**
     * // 校验部门的合法性，剔除并返回无效部门
     * @param
     * @param
     * @return
     */
    private List<AttendDepartmentChooser> dealDepartmentChooser(AttendGroupReq reqParam, UserInfo userCache) {

        List<AttendDepartmentChooser> attendDepartmentChoosers = reqParam.getAttendDepartmentChoosers();

        logger.debug("dealDepartmentChooser attendDepartmentChoosers={}",attendDepartmentChoosers);
        List<AttendDepartmentChooser> unDepartmentChooser = new ArrayList<>();
        if (AssertUtil.isNotEmpty(attendDepartmentChoosers)){
            Iterator<AttendDepartmentChooser> iterator = attendDepartmentChoosers.iterator();
            while (iterator.hasNext()){
                AttendDepartmentChooser department = iterator.next();
                if (AssertUtil.isEmpty(department.getEnterpriseId())
                        || AssertUtil.isEmpty(department.getDepartmentId())
                        || AssertUtil.isEmpty(department.getDepartmentName()))
                {
                    logger.info("Illegal department,ignore it.department={}", department);
                    // 如果必要条件字段不存在，则将其添加到返回列表中，并从请求参数中去除掉
                    unDepartmentChooser.add(department);
                    iterator.remove();
                    continue;
                }

                // 判断企业enterId是否匹配
                if (!userCache.getEnterId().equals(department.getEnterpriseId()))
                {
                    // 兼容搜索联系人的情况，如果联系人属于多企业，选择器会将多企业id返回
                    if (department.getEnterpriseId().indexOf(userCache.getEnterId()) > -1)
                    {
                        department.setEnterpriseId(userCache.getEnterId());
                    }
                    else
                    {
                        logger.info("Illegal department enterId,ignore it.department={}",
                                department);
                        // 如果必要条件字段不存在，则将其添加到返回列表中，并从请求参数中去除掉
                        unDepartmentChooser.add(department);
                        iterator.remove();
                        continue;
                    }

                }

            }
            reqParam.setAttendDepartmentChoosers(attendDepartmentChoosers);
        }
        return unDepartmentChooser;
    }
    /**
     * 对考勤成员进行处理,并返回无效的用户
     * @param reqParam
     * @return
     */
    private List<UserInfo> dealUsefulEmployee(AttendGroupReq reqParam,
        UserInfo userCache)
    {
        List<UserInfo> userInfo = reqParam.getEmployees();
        logger.debug("dealUsefulEmployee userInfo={}",userInfo);
        List<UserInfo> unUsefulUser = new ArrayList<UserInfo>();
        if (AssertUtil.isNotEmpty(userInfo))
        {
            Iterator<UserInfo> iterator = userInfo.iterator();
            while (iterator.hasNext())
            {
                UserInfo user = iterator.next();
                if (AssertUtil.isEmpty(user.getUid())
                    || AssertUtil.isEmpty(user.getEnterId()))
                {
                    logger.info("Illegal user,ignore it.user={}", user);
                    // 如果必要条件字段不存在，则将其添加到返回列表中，并从请求参数中去除掉
                    unUsefulUser.add(user);
                    iterator.remove();
                    continue;
                }
                // 判断企业enterId是否匹配
                if (!userCache.getEnterId().equals(user.getEnterId()))
                {
                    // 兼容搜索联系人的情况，如果联系人属于多企业，选择器会将多企业id返回
                    if (user.getEnterId().indexOf(userCache.getEnterId()) > -1)
                    {
                        user.setEnterId(userCache.getEnterId());
                    }
                    else
                    {
                        logger.info("Illegal user enterId,ignore it.user={}",
                            user);
                        // 如果必要条件字段不存在，则将其添加到返回列表中，并从请求参数中去除掉
                        unUsefulUser.add(user);
                        iterator.remove();
                        continue;
                    }

                }
                // 进一步判断用户名和手机号，两者均为空时，无法展示用户
                if (AssertUtil.isEmpty(user.getEmployeeName()))
                {
                    if (AssertUtil.isEmpty(user.getPhone()))
                    {
                        // 用户名和手机号，两者均为空，并从请求参数中去除掉
                        unUsefulUser.add(user);
                        iterator.remove();
                    }
                    else
                    {
                        // 用户名为空时，取手机号，保证用户展示时信息完整
                        user.setEmployeeName(user.getPhone());
                        logger.info("EmployeeName is empty,use phone.user={}",
                            user);
                    }
                }
            }
            reqParam.setEmployees(userInfo);
        }

        return unUsefulUser;
    }

    /**
     * 获取考勤组信息
     * @param attendanceId
     * @return
     */
    public AttendGroup getAttendGroupInfoFromCache(long attendanceId,
        String enterId)
    {
        String key = AttendanceUtil.getGroupCachekey(attendanceId);
        AttendGroup groupInfo = (AttendGroup) CachedUtil.get(key);
        if (AssertUtil.isEmpty(groupInfo)) {
            groupInfo = groupDao.queryAttendGroupInfo(attendanceId, enterId,
                GroupStatus.Normal.getValue());
        }
        return groupInfo;
    }

    /**
     * 校验上下班时间大小
     * @param respBean
     * @param amTime
     * @param pmTime
     * @return
     */
    private AttendGroupRes validWorkTime(AttendGroupRes respBean,
        String amTime, String pmTime)
    {
        if (AtdcTimeUtil.compareAmtimeAndPmtime(amTime, pmTime) > 0)
        {
            respBean.setCode(AtdcResultCode.ATDC106.PM_EARLIER_AM);
            respBean.setSummary(AtdcResultSummary.ATDC106.PM_EARLIER_AM);
        }
        return respBean;
    }

    @Override
    public AttendGroupRes queryGroupDetail(AttendGroupReq reqParam) {
        AttendGroupRes res = new AttendGroupRes();
        AttendGroup groupInfo = new AttendGroup();
        long attendanceId = reqParam.getAttendanceId();
        logger.info("queryGroupDetail attendanceId={}|userInfo={}",
            attendanceId, reqParam.getUserInfo());
        // 参数检测
        if (attendanceId <= 0) {
            res.setCode(AtdcResultCode.ATDC104.PARAMS_INVALID);
            res.setSummary(AtdcResultSummary.ATDC104.PARAMS_INVALID);
            return res;
        }

        // 管理员权限检测 或考勤组负责人权限检测
        if (reqParam.getUserInfo().getIsAdmin() != 1 && (reqParam.getUserInfo().getRoleType()) != 1) {
            res.setCode(AtdcResultCode.ATDC106.NOT_ADMIN);
            res.setSummary(AtdcResultSummary.ATDC106.NOT_ADMIN);
            return res;
        }
        // 先取缓存 缓存没有再读db
        /*Object object = CachedUtil.get(AttendanceUtil
            .getGroupCachekey(attendanceId));
        if (object != null)
        {
            groupInfo = (AttendGroup) object;
        }
        else
        {*/
            groupInfo = groupDao.queryAttendGroupInfo(attendanceId, reqParam
                .getUserInfo().getEnterId(), GroupStatus.Normal.getValue());
       // }

        logger.debug("groupInfo={}",groupInfo);
        if (AssertUtil.isNotEmpty(groupInfo)) {
            // 查询考勤组用户列表
            List<UserInfo> userList = employeeDao.queryUserListAndWhitelist(attendanceId,
                    EmployeeStatus.Normal.getValue());
            // 查询考勤组负责人列表
            List<AttendEmployee> chargemans = employeeDao.queryChargeMansByAttendanceId(Long.toString(attendanceId));
            //获取部门选择器
            List<AttendDepartmentChooser> attendDepartmentChoosers = groupDao.queryDepartmentChooser(attendanceId);
            //获取考勤组考取地址信息
            List<AttendClockSite> attendClockSites = groupDao.queryClockSite(attendanceId);
            //为空时，说明是旧数据
            if (AssertUtil.isEmpty(attendClockSites)){
                attendClockSites = new ArrayList<>();
                AttendClockSite attendClockSite = new AttendClockSite();
                attendClockSite.setAttendanceRange(groupInfo.getAttendanceRange());
                attendClockSite.setLocation(groupInfo.getLocation());
                attendClockSite.setDetailAddr(groupInfo.getDetailAddr());
                attendClockSite.setLatitude(groupInfo.getLatitude());
                attendClockSite.setLongitude(groupInfo.getLongitude());
                attendClockSites.add(attendClockSite);
            }
            //添加考勤地址
            res.setAttendClockSites(attendClockSites);
            // 查询有结果，组装数据返回
            res.setAttendanceId(reqParam.getAttendanceId());
            res.setAttendanceName(groupInfo.getAttendanceName());
            res.setEnterId(groupInfo.getEnterId());
            res.setEnterName(groupInfo.getEnterName());
            res.setAttendanceOutRange(groupInfo.getAttendanceOutRange());
            res.setAmTime(groupInfo.getAmTime());
            res.setPmTime(groupInfo.getPmTime());
            //添加需要考勤人员
            res.setEmployees(userList);
            //添加考勤组负责人
            res.setChargemanList(chargemans);
            //添加部门选择器
            res.setAttendDepartmentChoosers(attendDepartmentChoosers);
            res.setExamineName(groupInfo.getExamineName());
            res.setExamineUid(groupInfo.getExamineUid());
            res.setExamineContactId(groupInfo.getExamineContactId());
            res.setAttendType(groupInfo.getAttendType());
            res.setFreeAttendRule(groupInfo.getFreeAttendRule());
            res.setFixedAttendRule(groupInfo.getFixedAttendRule());
            res.setRelyHoliday(groupInfo.getRelyHoliday());
            res.setAllowLateTime(groupInfo.getAllowLateTime());
            res.setAdminContactId(groupInfo.getAdminContactId());
            res.setAdminName(groupInfo.getAdminName());
            res.setIsAllowedOutRangeClock(groupInfo.getIsAllowedOutRangeClock());
            res.setUseFlexibleRule(groupInfo.getUseFlexibleRule());
            if (AssertUtil.isNotEmpty(res.getUseFlexibleRule())) {
                if (res.getUseFlexibleRule()==0) {
                    res.setFlexitime(groupInfo.getFlexitime());
                }
            }
            logger.info("queryGroupDetail attendanceId={}|userList Size={}",
                attendanceId, userList.size());
            groupInfo.setEmployees(getEmployeesId(userList));
           /* CachedUtil.set(AttendanceUtil.getGroupCachekey(attendanceId),
                groupInfo, 1200000);*/
        } else {
            logger.warn(
                "queryGroupDetail no data! attendanceId={}|userInfo={}",
                attendanceId, reqParam.getUserInfo());
        }

        return res;
    }

    /**
     * 获取所有用户的id列表
     * @param userList
     * @return
     */
    private List<String> getEmployeesId(List<UserInfo> userList)
    {
        List<String> idList = new ArrayList<String>();
        if (AssertUtil.isNotEmpty(userList))
        {
            for (UserInfo info : userList) {
                idList.add(info.getUid());
            }
        }
        return idList;
    }

    @Override
    public AttendGroupListRes queryGroupList(AttendGroupReq reqParam) {
        AttendGroupListRes res = new AttendGroupListRes();

        String enterId = reqParam.getEnterId();
        logger.info("queryGroupList enterId={}|userInfo={}|roleType={}", enterId, reqParam.getUserInfo(),reqParam.getUserInfo().getRoleType());
        // 参数检测
        if (AssertUtil.isEmpty(enterId)) {
            res.setCode(AtdcResultCode.ATDC104.PARAMS_INVALID);
            res.setSummary(AtdcResultSummary.ATDC104.PARAMS_INVALID);
            return res;
        }
        // 查看考勤组列表 进行管理员或考勤组负责人权限检测
        if (reqParam.getUserInfo().getIsAdmin() != 1
            && reqParam.getUserInfo().getRoleType() != 1) {
            res.setCode(AtdcResultCode.ATDC106.NOT_ADMIN);
            res.setSummary(AtdcResultSummary.ATDC106.NOT_ADMIN);
            return res;
        }

        // 查询考勤组列表 管理员和负责人的回显不一样
        List<AttendGroupWithEmpRes> list = groupDao.queryAttendGroupListOnApp(
            reqParam.getEnterId(),AttendExamineEntity.ExaminerState.Normal.getValue(),reqParam.getUserInfo());

        if (AssertUtil.isEmpty(list))
        {
            res.setCode(AtdcResultCode.S_OK);
            res.setSummary(NO_DATA);
            return res;
        }
        for (AttendGroupWithEmpRes attendGroupWithEmpRes : list){
            List<String> locations = groupDao.queryAttendGroupStringSite(attendGroupWithEmpRes.getAttendanceId());
            if (AssertUtil.isEmpty(locations)){
                locations = new ArrayList<>();
                locations.add(attendGroupWithEmpRes.getLocation());
            }
            List<String> chargeMans = groupDao.queryAttendGroupChargeMansString(attendGroupWithEmpRes.getAttendanceId());
            if (AssertUtil.isEmpty(chargeMans)){
                chargeMans = new ArrayList<>();
            }
            attendGroupWithEmpRes.setChargeMans(chargeMans);
            attendGroupWithEmpRes.setLocations(locations);
        }
        res.setData(list);

//        logger.info("chargeMan={}",res.getData());

        return res;
    }

    @Override
    public AttendUserInGroupRes queryOwnGroup(AttendGroupReq reqParam)
    {
        AttendUserInGroupRes res = new AttendUserInGroupRes();
        try {
            logger.info("queryOwnGroup userInfo={},roleType={}", reqParam.getUserInfo(), reqParam.getUserInfo().getRoleType());
            UserGroupEntity userGroup = employeeDao.queryOwnGroup(reqParam.getUserInfo().getUid());
            if (AssertUtil.isNotEmpty(userGroup)) {
                //以防旧数据没有企业名  使用工作台传送的
                if (AssertUtil.isEmpty(userGroup.getEnterName())){
                    userGroup.setEnterName(reqParam.getUserInfo().getEnterName());
                }
                // 组装返回参数
                userGroup.setIsAdmin(reqParam.getUserInfo().getIsAdmin());
                AttendCalendar attendCalendar = calendarDao.queryAttendCalendarByDate(new Date());

                String[] rowDays = config.getRowDays(TimeUtil.getCurrentYear()).split(AtdcConsts.SEPARATOR.DATE_LIST);
                //查询考勤组时如果当天是补班的情况时 考勤班点按照周六按最后一个工作日周日按第一个工作日的规则
                if (AssertUtil.isNotEmpty(rowDays)) {
                    List<String> strings = Arrays.asList(rowDays);
                    if (strings.contains(TimeUtil.date2String(attendCalendar.getCalendarDate(),"yyyy-MM-dd"))) {
                        userGroup.setWorkdayStatus(0);
                        if (userGroup.getAttendType()==1 && userGroup.getRelyHoliday() == AttendGroup.RelyHoliday.NotRely.getValue() ) {
                            Map jsonObject = JSON.parseObject(userGroup.getFixedAttendRule());
                            List dayNum = new ArrayList(jsonObject.keySet());
                            int week = AtdcTimeUtil.getWeekNum(attendCalendar.getWeek());
                            String workdaytime = "";
                            if (week == 6) {
                                workdaytime = jsonObject.get(dayNum.get(dayNum.size()-1)).toString();
                            } else if (week == 7){
                                workdaytime = jsonObject.get(dayNum.get(0)).toString();
                            }
                            String fixedAttendRule = userGroup.getFixedAttendRule();
                            String newFixed = fixedAttendRule.substring(0,fixedAttendRule.length()-1)
                                +",\""+week+"\":"+workdaytime+"}";
                            userGroup.setFixedAttendRule(newFixed);
                        }
                    } else {
                        userGroup.setWorkdayStatus(attendCalendar.getStatus());
                    }
                } else {
                    userGroup.setWorkdayStatus(attendCalendar.getStatus());
                }
                //获取考勤组考取地址信息
                List<AttendClockSite> attendClockSites = groupDao.queryClockSite(userGroup.getAttendanceId());
                //为空时，说明是旧数据
                if (AssertUtil.isEmpty(attendClockSites)){
                    attendClockSites = new ArrayList<>();
                    AttendClockSite attendClockSite = new AttendClockSite();
                    attendClockSite.setAttendanceRange(userGroup.getAttendanceRange());
                    attendClockSite.setLocation(userGroup.getLocation());
                    attendClockSite.setDetailAddr(userGroup.getDetailAddr());
                    attendClockSite.setLatitude(userGroup.getLatitude());
                    attendClockSite.setLongitude(userGroup.getLongitude());
                    attendClockSites.add(attendClockSite);
                }
                //添加考勤地址
                userGroup.setAttendClockSites(attendClockSites);
            } else {
                userGroup = new UserGroupEntity();
                // 没查到，默认返回用户的uid和企业id
                userGroup.setEnterId(reqParam.getUserInfo().getEnterId());
                userGroup.setUid(reqParam.getUserInfo().getUid());
                userGroup.setEnterName(reqParam.getUserInfo().getEnterName());
            }
            Map<String,Object> paramMap = new HashMap<>();
            paramMap.put("uid",reqParam.getUserInfo().getUid());
            int i = groupDao.queryGroupCharge(paramMap);
            logger.info("isCurrentUserCharge={}",i);
            if (i>0) {
                res.setCharge("1");
            } else {
                res.setCharge("0");
            }
            res.setUserGroup(userGroup);
        }
        catch (PersistException e)
        {
            logger.error("queryOwnGroup data error token={}|isAdmin={}|uid={}",
                reqParam.getToken(), reqParam.getUserInfo().getIsAdmin(),
                reqParam.getUserInfo().getUid(), e);
            res.setCode(AtdcResultCode.S_ERROR);
            res.setSummary("查询个人所在考勤组失败!");
        }

        return res;
    }

    @Override
    public AttendScheduleRsp updateAttendSchedule(
        AttendScheduleReq attendScheduleReq)
    {
        // 校验用户是否为管理员
        AttendScheduleRsp rsp = checkupdateAttendScheduleParam(attendScheduleReq);// 对时间格式做校验
        if (!rsp.isSuccess())
        {
            logger.warn(
                    "updateAttendSchedule checkupdateAttendScheduleParam wrong|attendScheduleReq={}|userInfo={}",
                    attendScheduleReq, attendScheduleReq.getUserInfo());
            return rsp;
        }

        // 查出要软删除的uid attendanceId scheduleMonth
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("attendanceId", attendScheduleReq.getAttendanceId());
        params.put("status", AttendanceSchedule.Status.Normal.getValue());
        params.put("attendMonth", attendScheduleReq.getScheduleMonth());
        List<String> allUidList = attendanceScheduleDao
            .queryScheduleUidByParam(params);

        dellUpdateAttendanceScheduleUidList(allUidList,
            attendScheduleReq.getAllUpdateUid());

        if (!attendanceScheduleDao.updateAttendanceSchedule(attendScheduleReq,
            allUidList, attendScheduleReq.getUserInfo()))
        {
            logger.error("updateAttendSchedule data error token={}|isAdmin={}|uid={}",
                attendScheduleReq.getToken(), attendScheduleReq.getUserInfo()
                    .getIsAdmin(), attendScheduleReq.getUserInfo().getUid());
            rsp.setCode(AtdcResultCode.ATDC107.DATA_PERSISTENCE_ERROR);
            rsp.setSummary("");
        }
        return rsp;

    }

    private void dellUpdateAttendanceScheduleUidList(List<String> allUidList, List<String> updateUidList){

        Iterator<String> iterator = allUidList.iterator();
        while(iterator.hasNext()){
            String uid = iterator.next();
            //如果要更新的uid列表中没有此ID  说明软删除掉了
            if(updateUidList.contains(uid)){
                iterator.remove();
            }
        }
    }

    /**
     * 判断是否为管理员身份
     * @param groupReq
     * @return
     */
    private AttendScheduleRsp checkupdateAttendScheduleParam(
        AttendScheduleReq groupReq)
    {
        AttendScheduleRsp groupRes = new AttendScheduleRsp();
        // 判断是否为管理员身份
        if (groupReq.getUserInfo().getIsAdmin() != 1)
        {
            logger.warn("user is not admin.user={}", groupReq.getUserInfo());
            groupRes.setCode(AtdcResultCode.ATDC106.NOT_ADMIN);
            groupRes.setSummary(AtdcResultCode.ATDC106.NOT_ADMIN);
            return groupRes;
        }
        // 校验格式 yyyy-mm
        if (!AtdcTimeUtil.isAttendanceMonthLegal(groupReq.getScheduleMonth()))
        {
            logger.warn("scheduleMonth format is wrong.scheduleMonth={}",
                groupReq.getScheduleMonth());
            groupRes.setCode(AtdcResultCode.ATDC106.TIME_FORMAT_ERROR);
            groupRes.setSummary(AtdcResultCode.ATDC106.TIME_FORMAT_ERROR);
            return groupRes;
        }

        return groupRes;
    }

    @Override
    public AttendGroupListRes queryGroupFromPc(AttendGroupReq reqParam)
    {
        AttendGroupListRes res = new AttendGroupListRes();

        String enterId = reqParam.getEnterId();
        UserInfo userInfo = reqParam.getUserInfo();
        logger.info("queryGroupList enterId={}|userInfo={},roletype={},aM={}", enterId,
            reqParam.getUserInfo(),reqParam.getUserInfo().getRoleType(),reqParam.getUserInfo().getaM());
        // 参数检测
        if (AssertUtil.isEmpty(enterId))
        {
            res.setCode(AtdcResultCode.ATDC104.PARAMS_INVALID);
            res.setSummary(AtdcResultSummary.ATDC104.PARAMS_INVALID);
            return res;
        }
        // 查看考勤组列表 进行管理员或考勤组负责人权限检测
        if (reqParam.getUserInfo().getIsAdmin() != 1
            && reqParam.getUserInfo().getRoleType() != 1) {
            res.setCode(AtdcResultCode.ATDC106.NOT_ADMIN);
            res.setSummary(AtdcResultSummary.ATDC106.NOT_ADMIN);
            return res;
        }

        // 查询考勤组列表
        List<AttendGroupWithEmpRes> list = groupDao.queryAttendGroupFromPc(
            reqParam.getEnterId(), AttendGroup.GroupStatus.Normal.getValue(), userInfo);
        if (AssertUtil.isEmpty(list)) {
            res.setCode(AtdcResultCode.S_OK);
            res.setSummary(NO_DATA);
            return res;
        }
        for (AttendGroupWithEmpRes attendGroupWithEmpRes : list){
            List<String>locations = groupDao.queryAttendGroupStringSite(attendGroupWithEmpRes.getAttendanceId());
            if (AssertUtil.isEmpty(locations)){
                locations = new ArrayList<>();
                locations.add(attendGroupWithEmpRes.getLocation());
            }
            attendGroupWithEmpRes.setLocations(locations);
            //炒鸡管理员
            if (userInfo.getaM()==1) {
                attendGroupWithEmpRes.setAdminned(1);
            }
        }

        res.setData(list);

        return res;
    }
    

    /**
     * H5 管理员首次进入判断是否存在考勤组
     * @param reqParam
     * @return
     */
    @Override
    public AttendGroupListRes checkoutGroup(AttendGroupReq reqParam) {

        AttendGroupListRes res = new AttendGroupListRes();

        String enterId = reqParam.getEnterId();
        logger.info("checkoutGroup enterId={}|userInfo={}", enterId,
                reqParam.getUserInfo());
        // 参数检测
        if (AssertUtil.isEmpty(enterId))
        {
            res.setCode(AtdcResultCode.ATDC104.PARAMS_INVALID);
            res.setSummary(AtdcResultSummary.ATDC104.PARAMS_INVALID);
            return res;
        }
        // 管理员权限检测
        if (reqParam.getUserInfo().getIsAdmin() != 1)
        {
            res.setCode(AtdcResultCode.ATDC106.NOT_ADMIN);
            res.setSummary(AtdcResultSummary.ATDC106.NOT_ADMIN);
            return res;
        }

        // 查询考勤组列表
        //移动端展示
        List<AttendGroupWithEmpRes> list = groupDao.checkoutGroup(
                reqParam.getEnterId(), 0);

        if (AssertUtil.isEmpty(list))
        {
            res.setCode(AtdcResultCode.S_OK);
            res.setSummary(NO_DATA);
            return res;
        }
        res.setData(list);

        return res;
    }

    @Override
    public AttendGroupRes setWhiteList(AttendGroupReq groupReq) {
        //校验用户是否为管理员 与校验
        AttendGroupRes groupRes = checkIsAdmin(groupReq);

        if (!groupRes.isSuccess()) {
            logger.warn("check employee require admin privilege.groupReq={}|userInfo={}", groupReq, groupReq.getUserInfo());
            return groupRes;
        }


        if (groupReq.getAttendApprovalRestrict() != null){
            groupRes = checkApprovalParam(groupReq);
        }
        if (!groupRes.isSuccess()) {
            logger.warn("check checkApprovalParam.groupReq={}|userInfo={}", groupReq, groupReq.getUserInfo());
            return groupRes;
        }
        String creatorId = groupReq.getUserInfo().getUid();
        if (AssertUtil.isEmpty(creatorId)) {
            groupRes.setCode(AtdcResultCode.ATDC104.EMPLOYEE_IS_NOT_EXIST_CODE);
            groupRes.setSummary(AtdcResultSummary.ATDC104.EMPLOYEE_IS_NOT_EXIST);
            return groupRes;
        }
//        String creator = groupReq.getUserInfo().getEmployeeName();

//        //根据企业id查询所有的白名单人员
//        List<AttendWhitelistEntity> allWhitelist = attendWhitelistDao.queryAttendWhitelist(groupReq.getUserInfo().getEnterId());
//
//        List<UserInfo> userInfoList = groupReq.getEmployees();
//
//        int whiteListStatus = 0;
//
//        //校验用户列表
//        //提交白名单列表是否为空 为空则清空白名单
//        if (AssertUtil.isEmpty(userInfoList)) {
//            if (AssertUtil.isNotEmpty(allWhitelist)) {
//                //白名单里需要移除的员工在白名单表状态改为1
//                whiteListStatus = 1;
//                attendWhitelistDao.batchUpdateEmployeeWhiteListStatus(allWhitelist,whiteListStatus);
//            }
//            groupRes.setCode(AtdcResultCode.S_OK);
//            groupRes.setSummary(AtdcResultSummary.ATDC104.SETTING_SUCCESS);
//            return groupRes;
//        } else {
//            //处理该列表中不合法的用户
//            Iterator<UserInfo> iterator = userInfoList.iterator();
//            while (iterator.hasNext()) {
//                UserInfo userInfo = iterator.next();
//                //必要字段如不存在 则将该用户从列表中剔除
//                if (AssertUtil.isEmpty(userInfo.getUid()) || AssertUtil.isEmpty(userInfo.getEnterId())) {
//                    logger.info("Illegal userInfoList,without uid or enterid,ignore it.userInfo={}", userInfo);
//                    iterator.remove();
//                    continue;
//                }
//                //判断企业ID是否匹配
//                if (!groupReq.getUserInfo().getEnterId().equals(userInfo.getEnterId())) {
//                    logger.info("Illegal userInfoList enterId,enterid is not equal,ignore it.userInfo={}", userInfo);
//                    iterator.remove();
//                    continue;
//                }
//                //判断用户的用户名和手机号 如两者均为空 则为无效用户
//                if (AssertUtil.isEmpty(userInfo.getEmployeeName())) {
//                    if (AssertUtil.isEmpty(userInfo.getPhone())) {
//                        iterator.remove();
//                    } else {
//                        userInfo.setEmployeeName(userInfo.getPhone());
//                        logger.info("EmployeeName is empty,use phone.userInfo={}", userInfo);
//                    }
//                }
//            }
//            groupRes.setEmployees(userInfoList);
//            userInfoList = groupReq.getEmployees();
//        }
//
//        try {
//            //判断该人员列表是否为空，为空则不需要执行里面的修改操作
//            if (AssertUtil.isNotEmpty(allWhitelist)) {
//                //对比数据库中的白名单列表a和本次配置白名单列表b 并分别处理待移除名单allWhitelist和待配置名单userInfoList
//                compareList(allWhitelist,userInfoList);
//                if (AssertUtil.isNotEmpty(allWhitelist)) {
//                    //批量修改这些人员的状态值
//                    //白名单里需要移除的员工在白名单表状态改为1 同时员工表状态改回为正常 0
//                    whiteListStatus = 1;
//                    attendWhitelistDao.batchUpdateEmployeeWhiteListStatus(allWhitelist,whiteListStatus);
//                }
//                if (AssertUtil.isEmpty(userInfoList)) {
//                    groupRes.setCode(AtdcResultCode.S_OK);
//                    groupRes.setSummary(AtdcResultSummary.ATDC104.SETTING_SUCCESS);
//                    return groupRes;
//                }
//            }
//        } catch (Exception e) {
//            logger.error("batchUpdateEmployeeWhiteListStatus failed,allWhitelist = {}",allWhitelist,e);
//        }
//
//        List<AttendWhitelistEntity> list = new ArrayList<>();
//
//        AttendWhitelistEntity entity = new AttendWhitelistEntity();
//
//        for (UserInfo info : userInfoList) {
//            entity.setUid(info.getUid());
//            entity.setContactId(info.getContactId());
//            entity.setEmployeeName(info.getEmployeeName());
//            entity.setPhone(info.getPhone());
//            entity.setEnterId(info.getEnterId());
//            entity.setEnterName(info.getEnterName());
//            entity.setDeptId(info.getDeptId());
//            entity.setDeptName(info.getDeptName());
//            entity.setEmail(info.getEmail());
//            entity.setPosition(info.getPosition());
//            //考勤人员白名单标识 设置为0
//            entity.setStatus(0);
//            entity.setCreateTime(new Date());
//            entity.setModifyTime(new Date());
//            entity.setCreator(creator);
//            entity.setCreatorId(creatorId);
//            list.add(entity);
//            entity = new AttendWhitelistEntity();
//        }

        boolean flag = attendWhitelistDao.setGlobalWhiteList(groupReq,groupRes);
        if (flag) {
            groupRes.setCode(AtdcResultCode.S_OK);
            groupRes.setSummary(AtdcResultSummary.ATDC104.SETTING_SUCCESS);
//            groupRes.setWhitelistEntities(list);
            return groupRes;
        } else {
            groupRes.setCode(AtdcResultCode.ATDC107.DATA_PERSISTENCE_ERROR);
            groupRes.setSummary(AtdcResultSummary.ATDC104.SETTING_FAIL);
            return groupRes;
        }
    }


    /**
     * 独立设置参数
     * @param
     * @return
     */
    @Override
    public AttendGroupRes setApprovalRestrict(AttendGroupReq groupReq) {
        //校验用户是否为管理员 与校验
        AttendGroupRes groupRes = checkIsAdmin(groupReq);

        if (!groupRes.isSuccess()) {
            logger.warn("check setApprovalRestrict require admin privilege.groupReq={}|userInfo={}", groupReq, groupReq.getUserInfo());
            return groupRes;
        }

            groupRes = checkApprovalParam(groupReq);

        if (!groupRes.isSuccess()) {
            logger.warn("setApprovalRestrict checkApprovalParam.groupReq={}|userInfo={}", groupReq, groupReq.getUserInfo());
            return groupRes;
        }
        return appealDao.setApprovalRestrict(groupReq);

    }

    /**
     * 校验 审批限制参数
     * @param groupReq
     * @return
     */
    private AttendGroupRes checkApprovalParam(AttendGroupReq groupReq) {
        AttendGroupRes attendGroupRes = new AttendGroupRes();
        AttendApprovalRestrict attendApprovalRestrict = groupReq.getAttendApprovalRestrict();

            int restrictStatus = attendApprovalRestrict.getRestrictStatus();
            int restrictNumber = attendApprovalRestrict.getRestrictNumber();
            //校验参数
            if ((restrictStatus != 0 && restrictStatus != 1) ||restrictNumber > 31 ){
                attendGroupRes.setCode(AtdcResultCode.ATDC104.PARAMS_INVALID);
                attendGroupRes.setSummary(AtdcResultSummary.ATDC104.PARAMS_INVALID);
                logger.error("checkApprovalParam 请求参数无限attendApprovalRestrict={}|uid={}",attendApprovalRestrict,groupReq.getUid());
                return attendGroupRes;
            }
            //设置用户企业
            attendApprovalRestrict.setEnterId(groupReq.getUserInfo().getEnterId());
            groupReq.setAttendApprovalRestrict(attendApprovalRestrict);
        return attendGroupRes;
    }

    @Override
    public AttendGroupRes queryWhiteList(AttendGroupReq groupReq) {

        AttendGroupRes res = new AttendGroupRes();
        try {

            //获取白名单
            List<AttendWhitelistEntity> attendWhitelistEntityList = attendWhitelistDao.queryAttendWhitelist(groupReq.getUserInfo().getEnterId());
            res.setWhitelistEntities(attendWhitelistEntityList);
            //获取审批限制
            AttendApprovalRestrict attendApprovalRestrict = appealDao.queryApprovalRestrictByEnterId(groupReq.getUserInfo().getEnterId());
            if (AssertUtil.isEmpty(attendApprovalRestrict)){
                attendApprovalRestrict = new AttendApprovalRestrict();
                attendApprovalRestrict.setRestrictStatus(0);
                attendApprovalRestrict.setRestrictNumber(0);
            }
            res.setAttendApprovalRestrict(attendApprovalRestrict);
            res.setCode(AtdcResultCode.S_OK);
        }catch (Exception e){
            e.printStackTrace();
            UserInfo userInfo = groupReq.getUserInfo();
            logger.error("queryWhiteList ApprovalRestrict error uid={}|enterId={}|errorMsg={} ",userInfo.getUid(),userInfo.getEnterId(),e);
            res.setCode(AtdcResultCode.ATDC107.BASE);
            res.setSummary(AtdcResultSummary.ATDC107.S_ERROR);

        }
        return res;
    }

    /**
     * 首次进入企业存在考勤组则检测，个人是否存在考勤组  是否加入考勤组
     * @param
     * @param
     */
    @Override
    public List<AttendDepartmentChooser> detectionJoinGroup(AttendEmployee employee,UserInfo userInfo) {
        String enterId = employee.getEnterId();
        String uid = employee.getUid();

        //不为空直接返回
       /* if (AssertUtil.isNotEmpty(attendEmployee)&&(attendEmployee.getStatus() == EmployeeStatus.Normal.getValue()
                || attendEmployee.getStatus() == EmployeeStatus.Whitelist.getValue() )){
            return null;
            //为空判断 则进行部门选择器操作
        }*/
      /*  if(userInfo.getAttendanceId() != 0 || userInfo.getWhitelistStatus() == 1){
            return null;
        }*/
        if( userInfo.getWhitelistStatus() == 1){
            logger.info("白名单用户直接退出",userInfo);
            return null;
        }

            //获取企业下所有部门的考勤组的部门选择器
            List<AttendDepartmentChooser> attendDepartmentChoosers = attendDepartmentDao.queryEnterDepartmentChooser(enterId);
            //为空说明企业所有考勤组没有启用部门选择器
        logger.info("数据库中的部门列表===>attendDepartmentChoosers={}",attendDepartmentChoosers);
            if (AssertUtil.isEmpty(attendDepartmentChoosers)){
                return null;

                //调用企业通讯录获取用户的所有部门id计划
            }
            Map<String, Object> repMap = new  HashMap<>();
            try {
                repMap = QytxlUtil.getInstance().getDeptIdsByEuserId(enterId, uid);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("getDeptIdsByEuserId error一次失败！enterId={}|uid={}|e={}",enterId,uid ,e);
                try {
                    repMap = QytxlUtil.getInstance().getDeptIdsByEuserId(enterId, uid);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    logger.error("getDeptIdsByEuserId error二次失败！enterId={}|uid={}|e={}",enterId,uid ,e);
                }
            }
            logger.info("getDeptIdsByEuserId return repMap= {}",repMap);
            if ((Integer)repMap.get("error_code") != 0){
                logger.error("getDeptIdsByEuserId return error_code={}|msg={}",repMap.get("error_code"),repMap.get("error_msg"));
                return null;
            }
            List<String>departmentIds = (List<String>) repMap.get("departmentIds");
        logger.info("企业通讯录用户所属部门列表====>>departmentIds={}",departmentIds);

            if (AssertUtil.isEmpty(departmentIds)){
                return null;
            }
            if (departmentIds.indexOf("0") != -1){
                departmentIds.remove(departmentIds.indexOf("0"));
            }
            if (departmentIds.size() == 0){
                return null;
            }
            //创建集合存储符合的部门选择器
            List<AttendDepartmentChooser> conformChoosers = new ArrayList<>();
            //定义set集合自动去重
            Set<Long>attendanceIds = new HashSet<>();
            //循环企业的选择器
            for (AttendDepartmentChooser attendDepartmentChooser : attendDepartmentChoosers ){
                //判断选择器中的部门是否符合用户部门
                if (departmentIds.contains(attendDepartmentChooser.getDepartmentId())){
                    conformChoosers.add(attendDepartmentChooser);
                    //使用set自动去重判断是否存在多个考勤组
                    attendanceIds.add(attendDepartmentChooser.getAttendanceId());
                }
            }
            //没有符合对应的部门
            if (conformChoosers.size() == 0){
                return null;
            }
            //多个考勤组
            if (attendanceIds.size() > 1){
                return conformChoosers;
            }
        //获取考勤组id
        long attendanceId = attendanceIds.iterator().next();
        logger.info("只存在一个部门====>>attendanceId={}",attendanceId);
        //强行回收
        attendanceIds = null;
        //只有一个考勤组 自动加入考勤组
        //查询用户是否在企业白名单中
        //AttendWhitelistEntity attendWhitelistEntity = attendWhitelistDao.queryEnterWhitelistByUid(uid,AttendWhitelistEntity.EmployeeStatus.Normal.getValue());

        /*if (AssertUtil.isNotEmpty(attendWhitelistEntity)){
            employee.setStatus(EmployeeStatus.Whitelist.getValue());
        }else {
            employee.setStatus(EmployeeStatus.Normal.getValue());
        }*/
        //查询是否存在考勤组
        AttendEmployee attendEmployee = employeeDao.queryEmployeeByUidAndWhitelist(uid);
        /*//添加考勤组id
        if (AssertUtil.isNotEmpty(attendEmployee) && attendEmployee.getStatus() == EmployeeStatus.Abnormal.getValue()){
            attendEmployee.setAttendanceId(attendanceId);
            //更新
            attendEmployee.setModifyTime(new Date());
            attendEmployee.setStatus(employee.getStatus());
            //查询是否是考勤组负责人
            //查询负责的考勤组
            List<String> groupIds = groupDao.queryGroupPrincipalByUid(attendEmployee.getUid());
            attendEmployee.setRoleType(groupIds != null && groupIds.size() > 0 ? 1:0);

            employeeDao.updateEmployee(attendEmployee);
            againCacheUser(userInfo);
            return null;
        }*/

        //由于用户跳转过来没有带用户名  所以需要查询企业通讯录获取用户名
        Map<String, Object> itemMap ;
        try {
            itemMap = QytxlUtil.getInstance().getItem(employee.getEnterId(), employee.getContactId());
        } catch (Exception e) {
            logger.error("getItem error 一次 EnterId={}|ContactId()={}|e={}",employee.getEnterId(), employee.getContactId(),e);
            e.printStackTrace();
            try {
                itemMap =  QytxlUtil.getInstance().getItem(employee.getEnterId(),employee.getContactId());
            } catch (Exception e1) {
                e1.printStackTrace();
                logger.error("getItem error 二次 EnterId={}|ContactId()={}|e={}",employee.getEnterId(), employee.getContactId(),e);
                itemMap = null;
            }
        }
        if (AssertUtil.isEmpty(itemMap)|| (Double)itemMap.get("error_code")!= 0){
            return null;
        }
        Map jsonObject = (Map) itemMap.get("item");
        String name = (String) jsonObject.get("name");
        try {
            name = AesUtils.decrypt(name, AttendanceConfig.getInstance()
                .getProperty("attend.qytxl.aes_key",
                    "6af15ca383ee45dd"));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("AES 解密异常 name={}|e={}",name,e);
            return null;
        }

        //如果用户已加入考勤组,更新用户的考勤组信息,否则用户不在考勤组,添加进考勤组
        if (AssertUtil.isNotEmpty(attendEmployee)){
            if(attendEmployee.getStatus() == EmployeeStatus.Abnormal.getValue()||attendEmployee.getAttendanceId() != attendanceId) {
                logger.info("更新考勤组====>>attendanceId={}",attendanceId);
                attendEmployee.setAttendanceId(attendanceId);
                //更新
                attendEmployee.setModifyTime(new Date());
                attendEmployee.setStatus(employee.getStatus());
                attendEmployee.setDeptId((String) itemMap.get("departmentId"));
                attendEmployee.setDeptName((String) itemMap.get("departmentName"));
                //查询是否是考勤组负责人: RoleType为1则为考勤组负责人
                //查询负责的考勤组
                List<String> groupIds = groupDao.queryGroupPrincipalByUid(attendEmployee.getUid());
                attendEmployee.setRoleType(groupIds != null && groupIds.size() > 0 ? 1 : 0);

                //只存在一个考勤组时,将用户考勤组信息更新
                employeeDao.updateEmployee(attendEmployee);

                //第二次缓存用户信息
                againCacheUser(userInfo);
                logger.info("更新部门成功==>attendEmployee={}", attendEmployee);
                return null;
            }

        }

        employee.setEmployeeName(name);
        employee.setAttendanceId(attendanceId);
        employee.setCreateTime(new Date());
        employee.setModifyTime(new Date());
        employee.setDeptId((String) itemMap.get("departmentId"));
        employee.setDeptName((String) itemMap.get("departmentName"));
        //查询是否是考勤组负责人
        //查询负责的考勤组
        List<String> groupIds = groupDao.queryGroupPrincipalByUid(employee.getUid());
        employee.setRoleType(groupIds != null && groupIds.size() > 0 ? 1:0);
        employeeDao.saveEmployee(employee);
        logger.info("登陆时添加进考勤组成功====>employee={}",employee);
        againCacheUser(userInfo);
            return null;
    }

    public void againCacheUser(UserInfo userInfo){
        UserInfo user = loginDao.queryUserInfo(userInfo.getUid(), 0,
                userInfo.getEnterId());
        logger.info("=================user={}",user);
        if (AssertUtil.isEmpty(user)
                || AssertUtil.isEmpty(user.getUid()))
        {
            user = new UserInfo();
            user.setUid(userInfo.getUid());
            user.setEnterId(userInfo.getEnterId());
            // 为空则设定该用户审批员状态为0
            user.setExaminerState(0);
        }
        user.setContactId(userInfo.getContactId());
        user.setToken(userInfo.getToken());
        user.setCacheupdatetime(System.currentTimeMillis());
        user.setLoginupdatetime(System.currentTimeMillis());
        // 和飞信给的数据库数据没有RCS登录的准确 这里设置一下企业名称为登录数据
        user.setEnterName(userInfo.getEnterName());
        user.setPhone(userInfo.getPhone());
        user.setIsAdmin(userInfo.getIsAdmin());
        userInfoCache.save(
                userInfo.getToken(),
                user,
            1800000);
    }

    /**
     * 用户通过选择部门加入考勤组
     * @param req
     * @return
     */
    @Override
    public AttendDepartmentRes joinGroup(AttendDepartmentReq req ) {
        //校验参数
        AttendDepartmentRes res = checkoutParam(req);
        
        if (!res.isSuccess()){
            logger.info("joinGroup checkoutParam failure req={}|uid={}|code={}|summary={}",req,req.getUid(),res.getCode(),res.getSummary());
            return res;
        }
        //封装参数
        UserInfo userInfo = req.getUserInfo();
        //用户状态
        int status = 0;
        AttendWhitelistEntity attendWhitelistEntity = attendWhitelistDao.queryEnterWhitelistByUid(userInfo.getUid(),AttendWhitelistEntity.EmployeeStatus.Normal.getValue());
        if (AssertUtil.isNotEmpty(attendWhitelistEntity)){
            status = EmployeeStatus.Whitelist.getValue();
        }else {
            status = EmployeeStatus.Normal.getValue();
        }
        //查询用户是否存在考勤组
        AttendEmployee employee = employeeDao.queryEmployeeByUidAndWhitelist(userInfo.getUid());
        if (AssertUtil.isNotEmpty(employee)){
            employee.setAttendanceId(req.getAttendanceId());
            employee.setStatus(status);
            //查询是否是考勤组负责人
            //查询负责的考勤组
            List<String> groupIds = groupDao.queryGroupPrincipalByUid(employee.getUid());
            employee.setRoleType(groupIds != null && groupIds.size() > 0 ? 1:0);
            employeeDao.updateEmployee(employee);
            againCacheUser(userInfo);
            return res;
        }

        AttendEmployee attendEmployee = new AttendEmployee();
        attendEmployee.setAttendanceId(req.getAttendanceId());
        attendEmployee.setEnterName(userInfo.getEnterName());
        attendEmployee.setEnterId(userInfo.getEnterId());
        attendEmployee.setPhone(userInfo.getPhone());
        attendEmployee.setDeptId(userInfo.getDeptId());
        attendEmployee.setDeptName(userInfo.getDeptName());
        attendEmployee.setUid(userInfo.getUid());
        attendEmployee.setEmail(userInfo.getEmail());
        attendEmployee.setPosition(userInfo.getPosition());
        attendEmployee.setContactId(userInfo.getContactId());
        attendEmployee.setStatus(status);
        Map<String, Object> itemMap;
        try {
            itemMap = QytxlUtil.getInstance().getItem(userInfo.getEnterId(), userInfo.getContactId());
        } catch (Exception e) {
            logger.error("getItem error 一次 EnterId={}|ContactId()={}|e={}",userInfo.getEnterId(), userInfo.getContactId(),e);
            e.printStackTrace();
            try {
                itemMap =  QytxlUtil.getInstance().getItem(userInfo.getEnterId(),userInfo.getContactId());
            } catch (Exception e1) {
                e1.printStackTrace();
                logger.error("getItem error 二次 EnterId={}|ContactId()={}|e={}",userInfo.getEnterId(), userInfo.getContactId(),e);
                res.setCode(AtdcResultCode.S_ERROR);
                res.setSummary(AtdcResultSummary.ATDC107.QYTXL_OBTAIN_FAIL);
                return res;
            }
        }
        if (AssertUtil.isEmpty(itemMap)|| (Double)(itemMap.get("error_code"))!= 0){
            res.setCode(AtdcResultCode.S_ERROR);
            res.setSummary(AtdcResultSummary.ATDC107.QYTXL_OBTAIN_FAIL);
            return res;
        }
        Map jsonObject = (Map) itemMap.get("item");
        String name = (String) jsonObject.get("name");
        try {
            name = AesUtils.decrypt(name, AttendanceConfig.getInstance()
                    .getProperty("attend.qytxl.aes_key",
                            "6af15ca383ee45dd"));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("AES 解密异常 name={}|e={}",name,e);
            res.setCode(AtdcResultCode.S_ERROR);
            res.setSummary(AtdcResultSummary.ATDC107.QYTXL_OBTAIN_FAIL);
            return res;
        }
        attendEmployee.setEmployeeName(name);
        attendEmployee.setCreateTime(new Date());
        attendEmployee.setModifyTime(new Date());
        //查询是否是考勤组负责人
        //查询负责的考勤组
        List<String> groupIds = groupDao.queryGroupPrincipalByUid(attendEmployee.getUid());
        attendEmployee.setRoleType(groupIds != null && groupIds.size() > 0 ? 1:0);
        employeeDao.saveEmployee(attendEmployee);
        againCacheUser(userInfo);
        //保存
        return res;
    }

    /**
     *手动加入考勤组
     * @param req
     * @param
     * @return
     */
    private AttendDepartmentRes checkoutParam(AttendDepartmentReq req ) {
        AttendDepartmentRes res = new AttendDepartmentRes();
        UserInfo userInfo = req.getUserInfo();
        //判断用户缓存登陆信息
        if (AssertUtil.isEmpty(userInfo)){
            res.setCode(AtdcResultCode.ATDC102.USER_SESSION_ERROR);
            res.setSummary(AtdcResultSummary.ATDC102.USER_SESSION_ERROR);
            return res;
        }
        //判断请求参数
        if (AssertUtil.isEmpty(req.getUid()) || AssertUtil.isEmpty(req.getDepartmentId()) || AssertUtil.isEmpty(req.getAttendanceId())){
            res.setCode(AtdcResultCode.ATDC104.PARAMS_NULL);
            res.setSummary(AtdcResultSummary.ATDC104.PARAMS_NULL);
            return res;
        }
        //判断用户uid是否对应
        if (!userInfo.getUid().equals(req.getUid())){
            res.setCode(AtdcResultCode.ATDC104.PARAMS_ILLEGAL_UID);
            res.setSummary(AtdcResultSummary.ATDC104.PARAMS_ILLEGAL_UID);
            return res;
        }
        //查询数据库是否存在对应的部门选择器
        AttendDepartmentChooser attendDepartmentChooser = attendDepartmentDao.queryDepartmentInDepartmentIdAttendanceId(req.getDepartmentId(),req.getAttendanceId());
        if (AssertUtil.isEmpty(attendDepartmentChooser)){
            res.setCode(AtdcResultCode.ATDC104.PARAMS_ILLEGAL_NORECORD);
            res.setSummary(AtdcResultSummary.ATDC104.PARAMS_ILLEGAL_NORECORD);
            return res;
        }
        //判断部门选择器是否对应企业
       /* if (!attendDepartmentChooser.getEnterpriseId().equals(userInfo.getEnterId())){
            res.setCode(AtdcResultCode.ATDC104.DEPARTMENT_NO_ENTER);
            res.setSummary(AtdcResultSummary.ATDC104.DEPARTMENT_NO_ENTER);
            return res;
        }*/
        return  res;
    }

    @Override
    public AttendGroupRes removeWhiteListItem(AttendGroupReq groupReq) {
        //校验用户是否为管理员
        AttendGroupRes groupRes = checkIsAdmin(groupReq);

        if (!groupRes.isSuccess()) {
            logger.warn("check employee require admin privilege.groupReq={}|userInfo={}", groupReq, groupReq.getUserInfo());
            return groupRes;
        }

        List<UserInfo> userInfoList = groupReq.getEmployees();

        if (AssertUtil.isEmpty(userInfoList)) {
            groupRes.setCode(AtdcResultCode.ATDC104.NO_DATA_NEEDS_DUEL_CODE);
            groupRes.setSummary(AtdcResultSummary.ATDC104.NO_DATA_NEEDS_DUEL);
            return groupRes;
        } else {
            //处理该列表中不合法的用户
            Iterator<UserInfo> iterator = userInfoList.iterator();
            while (iterator.hasNext()) {
                UserInfo userInfo = iterator.next();
                //必要字段如不存在 则将该用户从列表中剔除
                if (AssertUtil.isEmpty(userInfo.getUid()) || AssertUtil.isEmpty(userInfo.getEnterId())) {
                    logger.info("Illegal userInfoList,without uid or enterid,ignore it.userInfo={}", userInfo);
                    iterator.remove();
                    continue;
                }
                //判断企业ID是否匹配
                if (!groupReq.getUserInfo().getEnterId().equals(userInfo.getEnterId())) {
                    logger.info("Illegal userInfoList enterId,enterid is not equal,ignore it.userInfo={}", userInfo);
                    iterator.remove();
                    continue;
                }
                //判断用户的用户名和手机号 如两者均为空 则为无效用户
                if (AssertUtil.isEmpty(userInfo.getEmployeeName())) {
                    if (AssertUtil.isEmpty(userInfo.getPhone())) {
                        iterator.remove();
                    } else {
                        userInfo.setEmployeeName(userInfo.getPhone());
                        logger.info("EmployeeName is empty,use phone.userInfo={}", userInfo);
                    }
                }
            }
            groupRes.setEmployees(userInfoList);
            userInfoList = groupReq.getEmployees();
            if (attendWhitelistDao.batchUpdateEmployeeWhiteListStatus(userInfoList,1)) {
                groupRes.setCode(AtdcResultCode.S_OK);
                groupRes.setSummary(AtdcResultSummary.ATDC104.SETTING_SUCCESS);
                return groupRes;
            } else {
                groupRes.setCode(AtdcResultCode.ATDC107.DATA_PERSISTENCE_ERROR);
                groupRes.setSummary(AtdcResultSummary.ATDC104.SETTING_FAIL);
                return groupRes;
            }
        }
    }

    /**
     * 同步检查通讯录同步删除人员
     * @return
     */
    @Override
    public List attendanceSyncVerify() {
        //查询有效考勤组id
        List<Long>attendanceIds =  groupDao.queryValidAttendanceId();
        if (attendanceIds.isEmpty()){
            return null;
        }
        List<Object> delUsers = new ArrayList<>();
        List<String>delEnter = new ArrayList<>();
        for (Long attendanceId : attendanceIds){
            //根据考勤组id获取考勤人员
           List<AttendEmployee>users =  groupDao.queryPersonnelByAttendanceId(attendanceId);
           logger.info("verify user={}|attendanceId={}",users,attendanceId);
           if (users.isEmpty()){
               continue;
           }
           List<AttendEmployee>delEmployees = new ArrayList<>();
           for (AttendEmployee attendEmployee : users){
               String contactId = attendEmployee.getContactId();
               String enterId = attendEmployee.getEnterId();
               if (StringUtils.isBlank(contactId) || StringUtils.isBlank(enterId)){
                   continue;
               }
               Map<String, Object> itemMap = null;
               try {
                    itemMap = QytxlUtil.getInstance().getItem(enterId, contactId);
               } catch (Exception e) {
                   try {
                       itemMap = QytxlUtil.getInstance().getItem(enterId, contactId);
                   } catch (Exception e1) {

                   }
               }
               if (AssertUtil.isEmpty(itemMap)||
                   ((Double)itemMap.get("error_code")!= 0 && !String.valueOf("企业"+enterId+"不存在").equals(String.valueOf(itemMap.get("error_msg"))))){

                   continue;
               }

               if (itemMap.get("item") == null){
                   attendEmployee.setDeptName(String.valueOf(itemMap.get("error_msg")));
                   delEmployees.add(attendEmployee);
                   if (String.valueOf("企业"+enterId+"不存在").equals(String.valueOf(itemMap.get("error_msg"))) && !delEnter.contains(enterId)){
                       delEnter.add(enterId);
                   }
               }
           }
           //软删除考勤人员
            if (AssertUtil.isNotEmpty(delEmployees)){
                delUsers.addAll(delEmployees);
                enterDao.deleteUser(delEmployees);
            }
        }
        //删除考勤组
        if (delEnter != null  && delEnter.size() > 0) {
            enterDao.delEnterGroup(delEnter);
            delUsers.addAll(delEnter);
        }
        return delUsers;
    }

    @Override
    public AttendGroupRes removeChargeMan(AttendGroupReq groupReq) {
        // 校验用户是否为管理员
        AttendGroupRes groupRes = checkIsAdmin(groupReq);
        if (!groupRes.isSuccess()) {
            logger.warn(
                "createGroup require admin privilege.groupReq={}|userInfo={}",
                groupReq, groupReq.getUserInfo());
            return groupRes;
        }
        List<UserInfo> chargeMans = groupReq.getChargemanList();

        //移除时同步 负责人身份
        List<String> attendanceIds = employeeDao.queryChargeGroupByUid(groupReq.getUid());

        if (AssertUtil.isEmpty(attendanceIds)) {
            employeeDao.batchUpdateEmpRoleType(chargeMans, EmployeeType.NormalEmp.getValue());
        }


        boolean flag = employeeDao.batchUpdateEmpChargeStatus(chargeMans, EmployeeChargemanStatus.NoneUse.getValue(),Long.toString(groupReq.getAttendanceId()));

        if (flag) {
            groupRes.setCode(AtdcResultCode.S_OK);
            groupRes.setSummary(ATDC104.SETTING_SUCCESS);
            return groupRes;
        } else {
            groupRes.setCode(AtdcResultCode.ATDC107.DATA_PERSISTENCE_ERROR);
            groupRes.setSummary(AtdcResultSummary.ATDC104.SETTING_FAIL);
            return groupRes;
        }
    }

    /**
     * 获取考勤组的创建人补充contactId 与创建人名称
     */
    @Override
    public void checkoutGroupContactId() {
        //获取正常使用的考勤组
       List<AttendGroup> groupList =  groupDao.findGroup();
            if (AssertUtil.isEmpty(groupList)) {
                return;
            }
            List<AttendGroup> updateList = new ArrayList<>();
            List<AttendGroup> delList = new ArrayList<>();
            List<String>delEnters  = new ArrayList<>();
            for (AttendGroup  attendGroup : groupList){
                try {
                    Map<String, Object> repMap = QytxlUtil.getInstance().getContactIdByEuserId(attendGroup.getEnterId(), attendGroup.getAdminUid());
                    if(0 == (int)repMap.get("error_code")){
                        List<Map<String,String>> userList= (List<Map<String,String>>)repMap.get("contactInfo");
                        Map<String, String> userMap = userList.get(0);
                        attendGroup.setAdminContactId(userMap.get("contactId"));
                        String adminName  = AesUtils.decrypt(userMap.get("name"), AttendanceConfig.getInstance()
                            .getProperty("attend.qytxl.aes_key",
                                "6af15ca383ee45dd"));
                        attendGroup.setAdminName(adminName);
                       // logger.info("获取后====attendGroup={}",attendGroup);
                        updateList.add(attendGroup);

                        //人员删除
                    }else if (999 == (int)repMap.get("error_code")){
                        delList.add(attendGroup);

                        //企业删除
                    }else if (2004 ==(int)repMap.get("error_code")){
                        if (!delEnters.contains(attendGroup.getEnterId())){
                            delEnters.add(attendGroup.getEnterId());
                        }
                    }
                } catch (Exception e) {
                    logger.error("QytxlUtil.getInstance().getContactIdByEuserId error e={}",e);
                }
                if (updateList.size() == 100){
                    groupDao.checkoutUpdateGroup(updateList);
                    logger.info("checkoutUpdateGroup size={}",updateList.size());
                    updateList.clear();
                }
                if (delList.size() == 100){
                    groupDao.checkoutDelGroupUid(delList);
                    logger.info("checkoutDelGroupUid size={}",delList.size());
                    delList.clear();
                }

            }

            //补充
        if (AssertUtil.isNotEmpty(updateList)){
            groupDao.checkoutUpdateGroup(updateList);
            logger.info("checkoutUpdateGroup size={}",updateList.size());
        }
            //删除创建人id
        if (AssertUtil.isNotEmpty(delList)){
            groupDao.checkoutDelGroupUid(delList);
            logger.info("checkoutDelGroupUid size={}",delList.size());
        }
            //删除企业
        if (AssertUtil.isNotEmpty(delEnters)){
            enterDao.delEnterGroup(delEnters);
            logger.info("delEnterGroup size={}",delEnters.size());
        }
    }


    @Override
    public AttendGroupRes checkEnterGroup(AttendGroupReq req,HttpServletRequest request) {
        AttendGroupRes attendGroupRes = new AttendGroupRes();
        //检测该企业是否是无考勤组 无考勤组则自动填充默认值 1是有考勤组 0是空考勤组
        if (groupDao.queryEnterAllGroups(req.getUserInfo().getEnterId()) > 0) {
            attendGroupRes.setSummary("已有考勤组!");
            attendGroupRes.setIsEmptyAttendance(1);
            return attendGroupRes;
        } else {
            attendGroupRes.setIsEmptyAttendance(0);
            attendGroupRes.setSummary("无考勤组!");
            UserInfo userInfo = loginDao.queryUserInfo(req.getUid(), 0, req.getEnterId());
            List<UserInfo> employeeList = employeeDao.queryEmployeeByEnterid(req.getEnterId());
            if (AssertUtil.isNotEmpty(userInfo)) {
                attendGroupRes.setAttendanceName(userInfo.getEnterName());
                attendGroupRes.setExamineUid(userInfo.getUid());
                attendGroupRes.setExamineContactId(userInfo.getContactId());
                attendGroupRes.setExamineName(userInfo.getEmployeeName());
                if (AssertUtil.isNotEmpty(employeeList)) {
                    attendGroupRes.setEmployees(employeeList);
                }

                return attendGroupRes;
            } else {
                attendGroupRes.setSummary("查无此人!");
                //由于用户跳转过来没有带用户名  所以需要查询企业通讯录获取用户名
                Map<String, Object> itemMap;
                try {
                    itemMap = QytxlUtil.getInstance().getItem(req.getUserInfo().getEnterId(), req.getUserInfo().getContactId());
                } catch (Exception e) {
                    logger.error("getItem error 一次 EnterId={}|ContactId()={}|e={}",req.getUserInfo().getEnterId(), req.getUserInfo().getContactId(),e);
                    e.printStackTrace();
                    try {
                        itemMap =  QytxlUtil.getInstance().getItem(req.getUserInfo().getEnterId(),req.getUserInfo().getContactId());
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        logger.error("getItem error 二次 EnterId={}|ContactId()={}|e={}",req.getUserInfo().getEnterId(), req.getUserInfo().getContactId(),e);
                        itemMap = null;
                    }
                }
                if (AssertUtil.isEmpty(itemMap)|| (Double)itemMap.get("error_code")!= 0){
                    return null;
                }
                Map jsonObject = (Map) itemMap.get("item");
                String name = (String) jsonObject.get("name");
                try {
                    name = AesUtils.decrypt(name, PublicConstant.AES_KEY);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("AES 解密异常 name={}|e={}",name,e);
                    return null;
                }
                attendGroupRes.setAttendanceName(req.getUserInfo().getEnterName());
                attendGroupRes.setExamineUid(req.getUserInfo().getUid());
                attendGroupRes.setExamineContactId(req.getUserInfo().getContactId());
                attendGroupRes.setExamineName(name);
                if (AssertUtil.isEmpty(employeeList)) {
                    try {
                        String items = QytxlUtil.getInstance().getEmployeesByEnterId(req.getUserInfo().getEnterId(),req.getUserInfo().getPhone()==null?"17620868870":req.getUserInfo().getPhone(),req.getUserInfo().getPhone()==null?"17620868870":req.getUserInfo().getPhone());
                        try {
                            items = AesUtils.decrypt(items, PublicConstant.AES_KEY);
                        } catch (Exception e) {
                            e.printStackTrace();
                            logger.error("AES 解密异常 items={}|e={}",items,e);
                            return null;
                        }
                        items = items.substring(1,items.length()-1);
                        Map maps = (Map)JSON.parseObject(items,Map.class);
                        List<UserInfo> infos = new ArrayList<>();
                        UserInfo info = new UserInfo();
                        info.setUid((String) maps.get("euserId"));
                        info.setContactId((String) maps.get("contactId"));
                        String eName = (String) maps.get("name");
                        info.setEmployeeName(eName);
                        info.setEnterId((String) maps.get("enterpriseId"));
                        infos.add(info);
//                        attendGroupRes.setEmployees(infos);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    attendGroupRes.setEmployees(employeeList);
                }
                logger.info("emps={}",attendGroupRes.getEmployees());
                return attendGroupRes;
            }
        }
    }

    /**
     * 审批人同步增加contactId
     */
    @Override
    public void checkoutExamineContactId() {
        //获取正在使用的审批人
        List<AttendExamineEntity> attendExamineEntities = groupDao.findCheckoutExamine();
        if (AssertUtil.isEmpty(attendExamineEntities)){
            return;
        }
        Map<String,String> contactMap = new HashMap<>();
        List<AttendExamineEntity> updateList = new ArrayList<>();
        for (AttendExamineEntity attendExamineEntity: attendExamineEntities){
            try {
                //先从map集合获取
                if (contactMap.get(attendExamineEntity.getEnterId()+attendExamineEntity.getExamineUid()) != null){
                    logger.info("===============集合===================");
                    attendExamineEntity.setExamineContactId(contactMap.get(attendExamineEntity.getEnterId()+attendExamineEntity.getExamineUid()));
                    updateList.add(attendExamineEntity);
                    continue;
                }

                //map 集合没有调用通讯录获取
                Map<String, Object> repMap = QytxlUtil.getInstance().getContactIdByEuserId(attendExamineEntity.getEnterId(), attendExamineEntity.getExamineUid());
                if(0 == (int)repMap.get("error_code")){
                    List<Map<String,String>> userList= (List<Map<String,String>>)repMap.get("contactInfo");
                    Map<String, String> userMap = userList.get(0);
                    attendExamineEntity.setExamineContactId(userMap.get("contactId"));
                    contactMap.put(attendExamineEntity.getEnterId()+attendExamineEntity.getExamineUid(),userMap.get("contactId"));
                    updateList.add(attendExamineEntity);
                }

            } catch (Exception e) {
                logger.error("QytxlUtil.getInstance().getContactIdByEuserId error e={}",e);
            }

            //当集合满100 批量插入一次
            if (updateList.size() == 100){
                groupDao.checkoutUpdateExamine(updateList);
                logger.info("checkoutUpdateExamine size={}",updateList.size());
                updateList.clear();
            }
        }
        if (AssertUtil.isNotEmpty(updateList)){
            groupDao.checkoutUpdateExamine(updateList);
            logger.info("checkoutUpdateExamine size={}",updateList.size());
        }
    }

    /**
     * 考勤人员同步补充ContactId
     */
    @Override
    public void checkoutEmployeeContactId() {
        //获取正在使用contactId为空的考勤人员
        List<AttendEmployee> employeeList = employeeDao.findCheckoutEmployee();
        if (AssertUtil.isEmpty(employeeList)){
            return;
        }
        List<AttendEmployee> updateList = new ArrayList<>();
        List<AttendEmployee> delList = new ArrayList<>();

        for (AttendEmployee attendEmployee : employeeList){
            try {
                Map<String, Object> repMap = QytxlUtil.getInstance().getContactIdByEuserId(attendEmployee.getEnterId(), attendEmployee.getUid());
                if(0 == (int)repMap.get("error_code")){
                    List<Map<String,String>> userList= (List<Map<String,String>>)repMap.get("contactInfo");
                    Map<String, String> userMap = userList.get(0);
                    attendEmployee.setContactId(userMap.get("contactId"));

                    updateList.add(attendEmployee);

                    //人员删除
                }else if (999 == (int)repMap.get("error_code")){
                    delList.add(attendEmployee);

                }
            } catch (Exception e) {
                logger.error("QytxlUtil.getInstance().getContactIdByEuserId error e={}",e);
            }
            //为100 批量处理
            if (updateList.size() == 100){
                employeeDao.checkoutUpdateEmployee(updateList);
                logger.info("checkoutUpdateEmployee size={}",updateList.size());
                updateList.clear();
            }
            //为100 批量处理
            if (delList.size() == 100){
                employeeDao.checkoutDelEmployee(delList);
                logger.info("checkoutDelEmployee size={}",delList.size());
                delList.clear();
            }
        }

        if (AssertUtil.isNotEmpty(updateList)){
            employeeDao.checkoutUpdateEmployee(updateList);
            logger.info("checkoutUpdateEmployee size={}",updateList.size());
        }

        if (AssertUtil.isNotEmpty(delList)){
            employeeDao.checkoutDelEmployee(delList);
            logger.info("checkoutDelEmployee size={}",delList.size());
        }
    }

    /**
     *指定更换企业考勤组审批人与转移审批单
     * @param map
     * @param
     * @return
     */
    @Override
    public String updateApprover(Map<String, String> map) {
        return groupDao.updateApprover(map);
    }

    @Override
    public List<DetailVO> getCurrentDayDate(Map<String, String> map) {
        return groupDao.getCurrentDayDate(map);
    }

    @Override
    public List<String> getAttendanceIdByEnterId(String enterId) {
        return groupDao.getAttendanceIdByEnterId(enterId);
    }

    @Override
    public int getAttendanceIdByEnterIdCount(String enterId) {
        return groupDao.getAttendanceIdByEnterIdCount(enterId);
    }

    @Override
    public int getCurrentDayDateCount(Map<String, String> map) {
        return groupDao.getCurrentDayDateCount(map);
    }

    @Override
    public AttendGroupRes queryEquipmentList(AttendGroupReq attendGroupReq, HttpServletRequest request) {
        logger.info("queryEquipmentList enterId={}|userInfo={}",
            attendGroupReq.getEnterId(), attendGroupReq.getUserInfo());
        AttendGroupRes attendGroupRes = new AttendGroupRes();

        //参数校验
        if (AssertUtil.isEmpty(attendGroupReq.getEnterId())) {
            attendGroupRes.setCode(AtdcResultCode.ATDC104.PARAMS_INVALID);
            attendGroupRes.setSummary(AtdcResultSummary.ATDC104.PARAMS_INVALID);
            return attendGroupRes;
        }

        Map<String,Object> queryParam = new HashMap();
        queryParam.put("enterId",attendGroupReq.getEnterId());
        if (AssertUtil.isNotEmpty(attendGroupReq.getAttendId())) {
            queryParam.put("attendanceId",attendGroupReq.getAttendId());
        }
        if (AssertUtil.isNotEmpty(attendGroupReq.getEmployeeName())) {
            queryParam.put("employeeName",attendGroupReq.getEmployeeName());
        }

        logger.info("queryParam={}",queryParam);
        List<AttendanceEquipment> equipmentList = groupDao.queryEquipmentList(queryParam);
        logger.info("equipmentList.size()={}",equipmentList.size());
        for (AttendanceEquipment equipment:equipmentList) {
            logger.info("equipment={}",equipment.toString());
        }
        Map<String,Object> temp = new HashMap();
        temp.put("enterId",attendGroupReq.getUserInfo().getEnterId());
        AttendanceEquipmentControl attendanceEquipmentControl = groupDao.queryEquipmentStatus(temp);
        AttendanceEquipmentVO equipmentVO = new AttendanceEquipmentVO();
        if (AssertUtil.isNotEmpty(equipmentList)) {
            List<AttendanceEquipmentVO> resultList = new ArrayList<>();
            for (int i=0;i<equipmentList.size();i++) {
                AttendanceEquipment equipment = equipmentList.get(i);
                if (AssertUtil.isEmpty(equipmentVO.getUid())) {
                    equipmentVO.setUid(equipment.getUid());
                    equipmentVO.setAttendanceName(equipment.getAttendanceName());
                    equipmentVO.setEmployeeName(equipment.getEmployeeName());
                    equipmentVO.setContractId(equipment.getContractId());
                    equipmentVO.setEquipmentLimit(attendanceEquipmentControl.getEquipmentLimit());
                    equipmentVO.setFirstEquipmentSerial(equipment.getEquipmentSerial());
                    equipmentVO.setFirstEquipmentDeviceType(equipment.getEquipmentDeviceType());
                    equipmentVO.setFirstEquipmentStatus(equipment.getEquipmentStatus());
                } else {
                    logger.info("isEqual={}",equipment.getUid().equals(equipmentVO.getUid()));
                    if (equipment.getUid().equals(equipmentVO.getUid())) {
                        if (AssertUtil.isNotEmpty(equipmentVO.getFirstEquipmentSerial()) && AssertUtil.isNotEmpty(equipmentVO.getSecondEquipmentSerial())
                            && "0".equals(equipmentVO.getFirstEquipmentStatus()) && "0".equals(equipmentVO.getSecondEquipmentStatus())) {
                            equipmentVO.setThirdEquipmentSerial(equipment.getEquipmentSerial());
                        }
                        if (AssertUtil.isNotEmpty(equipmentVO.getThirdEquipmentSerial())) {
                            equipmentVO.setThirdEquipmentDeviceType(equipment.getEquipmentDeviceType());
                        }
                        if (AssertUtil.isNotEmpty(equipmentVO.getThirdEquipmentSerial())) {
                            equipmentVO.setThirdEquipmentStatus(equipment.getEquipmentStatus());
                        }
                        if (AssertUtil.isNotEmpty(equipmentVO.getFirstEquipmentSerial()) && "0".equals(equipmentVO.getFirstEquipmentStatus()) && AssertUtil.isEmpty(equipmentVO.getThirdEquipmentSerial())) {
                            equipmentVO.setSecondEquipmentSerial(equipment.getEquipmentSerial());
                        }
                        if (AssertUtil.isNotEmpty(equipmentVO.getSecondEquipmentSerial()) && AssertUtil.isEmpty(equipmentVO.getThirdEquipmentSerial())) {
                            equipmentVO.setSecondEquipmentDeviceType(equipment.getEquipmentDeviceType());
                        }
                        if (AssertUtil.isNotEmpty(equipmentVO.getSecondEquipmentSerial()) && AssertUtil.isEmpty(equipmentVO.getThirdEquipmentSerial())) {
                            equipmentVO.setSecondEquipmentStatus(equipment.getEquipmentStatus());
                        }
                    } else {
                        resultList.add(equipmentVO);
                        equipmentVO = new AttendanceEquipmentVO();
                        equipmentVO.setUid(equipment.getUid());
                        equipmentVO.setAttendanceName(equipment.getAttendanceName());
                        equipmentVO.setEmployeeName(equipment.getEmployeeName());
                        equipmentVO.setContractId(equipment.getContractId());
                        equipmentVO.setEquipmentLimit(attendanceEquipmentControl.getEquipmentLimit());
                        equipmentVO.setFirstEquipmentSerial(equipment.getEquipmentSerial());
                        equipmentVO.setFirstEquipmentDeviceType(equipment.getEquipmentDeviceType());
                        equipmentVO.setFirstEquipmentStatus(equipment.getEquipmentStatus());
                    }
                }
                if (i==equipmentList.size()-1) {
                    resultList.add(equipmentVO);
                }
            }
            System.out.println(resultList.size());
            for (AttendanceEquipmentVO equipment : resultList) {
                logger.info("AttendanceEquipmentVO={}",equipment.toString());
            }
            attendGroupRes.setEquipmentLimit(attendanceEquipmentControl.getEquipmentLimit());
            attendGroupRes.setSummary("成功!");
            attendGroupRes.setCode(AtdcResultCode.S_OK);
            attendGroupRes.setEquipmentList(resultList);
        } else {
            attendGroupRes.setEquipmentLimit(attendanceEquipmentControl==null?"0":attendanceEquipmentControl.getEquipmentLimit());
            attendGroupRes.setSummary(AtdcResultSummary.ATDC108.NO_DATA);
            attendGroupRes.setCode(AtdcResultCode.S_OK);
        }
        return attendGroupRes;
    }

    @Override
    public AttendGroupRes removeEquipment(AttendGroupReq attendGroupReq, HttpServletRequest request) {
        logger.info("removeEquipment enterId={}|userInfo={}",
            attendGroupReq.getEnterId(), attendGroupReq.getUserInfo());
        AttendGroupRes attendGroupRes = new AttendGroupRes();

        //参数校验
        if (AssertUtil.isEmpty(attendGroupReq.getEnterId()) || AssertUtil.isEmpty(attendGroupReq.getUid())) {
            attendGroupRes.setCode(AtdcResultCode.ATDC104.PARAMS_INVALID);
            attendGroupRes.setSummary(AtdcResultSummary.ATDC104.PARAMS_INVALID);
            return attendGroupRes;
        }

        Map<String,Object> removeParam = new HashMap();
        removeParam.put("enterId",attendGroupReq.getEnterId());
        removeParam.put("uid",attendGroupReq.getUid());
        removeParam.put("updateTime",new Date());
        if (AssertUtil.isNotEmpty(attendGroupReq.getEquipmentSerial())) {
            removeParam.put("equipmentSerial",attendGroupReq.getEquipmentSerial());
        }

        logger.info("removeParam={}",removeParam);
        int result = groupDao.removeEquipment(removeParam);

        if (result<0) {
            attendGroupRes.setSummary("失败!");
            attendGroupRes.setCode(AtdcResultCode.S_ERROR);
        } else {
            attendGroupRes.setSummary("成功!");
            attendGroupRes.setCode(AtdcResultCode.S_OK);
        }
        return attendGroupRes;
    }

    @Override
    public AttendGroupRes setEquipmentLimit(AttendGroupReq attendGroupReq, HttpServletRequest request) {
        logger.info("setEquipmentLimit enterId={}|userInfo={}",
            attendGroupReq.getEnterId(), attendGroupReq.getUserInfo());
        AttendGroupRes attendGroupRes = new AttendGroupRes();

        //参数校验
        if (AssertUtil.isEmpty(attendGroupReq.getEnterId())) {
            attendGroupRes.setCode(AtdcResultCode.ATDC104.PARAMS_INVALID);
            attendGroupRes.setSummary(AtdcResultSummary.ATDC104.PARAMS_INVALID);
            return attendGroupRes;
        }

        Map<String,Object> temp = new HashMap();
        temp.put("enterId",attendGroupReq.getUserInfo().getEnterId());
        AttendanceEquipmentControl attendanceEquipmentControl = groupDao.queryEquipmentStatus(temp);

        temp.put("equipmentLimit",attendGroupReq.getEquipmentLimit());
        temp.put("equipmentUseStatus",attendGroupReq.getEquipmentStatus());

        logger.info("temp={}",temp);
        if (AssertUtil.isEmpty(attendanceEquipmentControl)) {
            if (!groupDao.insertEquipmentUseStatus(temp)) {
                attendGroupRes.setSummary("失败!");
                attendGroupRes.setCode(AtdcResultCode.S_ERROR);
            }
        } else {
            if (!groupDao.updateEquipmentStatus(temp)) {
                attendGroupRes.setSummary("失败!");
                attendGroupRes.setCode(AtdcResultCode.S_ERROR);
            }
        }

        groupDao.floatEquipment(temp);
        attendGroupRes.setSummary("成功!");
        attendGroupRes.setCode(AtdcResultCode.S_OK);
        return attendGroupRes;
    }

    @Override
    public AttendGroupRes insertEquipment(AttendGroupReq attendGroupReq, HttpServletRequest request) {
        logger.info("insertEquipment userInfo={}", attendGroupReq.getUserInfo());
        AttendGroupRes attendGroupRes = new AttendGroupRes();
        //参数校验
        checkSetEquipmentParam(attendGroupReq,attendGroupRes);

        //根据enterid查得equipmentLimit
        Map<String,Object> temp = new HashMap();
        temp.put("enterId",attendGroupReq.getUserInfo().getEnterId());
        AttendanceEquipmentControl attendanceEquipmentControl = groupDao.queryEquipmentStatus(temp);
        if (AssertUtil.isNotEmpty(attendanceEquipmentControl)) {
            if ("0".equals(attendanceEquipmentControl.getEquipmentUseStatus())) {
                if (AssertUtil.isEmpty(attendGroupReq.getEquipmentSerial())) {
                    attendGroupRes.setCode(AtdcResultCode.S_ERROR);
                    attendGroupRes.setSummary("获取设备号失败，请更新和飞信最新版");
                    return attendGroupRes;
                }
            }
        }
        //获取该企业已录入的设备列表
        List<AttendanceEquipment> equipmentList = groupDao.queryEquipments(temp);
        logger.info("equipmentList={}",equipmentList.toString());
        logger.info("equipmentList.size()={}",equipmentList.size());
        temp.put("uid",attendGroupReq.getUserInfo().getUid());
        //判断该员工的设备是否已经满额
        boolean flag = false;
        //若该列表为空 则说明没有录入过 需要录入
        if (AssertUtil.isEmpty(equipmentList)) {
            flag = true;
        } else {
            for (AttendanceEquipment equipment:equipmentList) {
                logger.info("equipment={}",equipment.toString());
                //判断是否已经有录入过相同的设备 若已经录入则不再录入
                if (attendGroupReq.getEquipmentSerial().equals(equipment.getEquipmentSerial())) {
                    //判端该已有设备是否是本人
                    logger.info("isEqs={}",attendGroupReq.getUserInfo().getUid().equals(equipment.getUid()));
                    if (attendGroupReq.getUserInfo().getUid().equals(equipment.getUid())) {
                        //若已录入的设备被删除则还原
                        if (groupDao.updateEquipmentStatusByUid(temp)) {
                            attendGroupRes.setSummary("成功!");
                            attendGroupRes.setCode(AtdcResultCode.S_OK);
                            return attendGroupRes;
                        }
                    } else {
                        if ("0".equals(equipment.getEquipmentStatus())) {
                            attendGroupRes.setCode(AtdcResultCode.S_ERROR);
                            attendGroupRes.setSummary("当前设备已被他人绑定");
                            return attendGroupRes;
                        }
                        flag = false;
                        break;
                    }
                } else {
                    flag = true;
                }
            }
        }
        if (flag) {
            int i = groupDao.queryEquipmentNumByUid(temp);
            if (Integer.parseInt(attendanceEquipmentControl.getEquipmentLimit()) == i) {
                attendGroupRes.setCode(AtdcResultCode.S_ERROR);
                attendGroupRes.setSummary("超出限制设备数量");
                return attendGroupRes;
            } else {
                Map<String,Object> setEquipmentParam = new HashMap();
                setEquipmentParam.put("enterId",attendGroupReq.getUserInfo().getEnterId());
                setEquipmentParam.put("uid",attendGroupReq.getUserInfo().getUid());
                setEquipmentParam.put("employeeName",attendGroupReq.getUserInfo().getEmployeeName());
                setEquipmentParam.put("contractId",attendGroupReq.getUserInfo().getContactId());
                setEquipmentParam.put("attendanceId",attendGroupReq.getUserInfo().getAttendanceId());
                setEquipmentParam.put("attendanceName",attendGroupReq.getAttendanceName()==null?"":attendGroupReq.getAttendanceName());//
                setEquipmentParam.put("equipmentSerial",attendGroupReq.getEquipmentSerial());
                setEquipmentParam.put("equipmentStatus",0);
                setEquipmentParam.put("equipmentDeviceType",attendGroupReq.getEquipmentDeviceType());
                logger.info("setEquipmentParam={}",setEquipmentParam);
                if (!groupDao.insertEquipment(setEquipmentParam)) {
                    attendGroupRes.setSummary("录入失败!");
                    attendGroupRes.setCode(AtdcResultCode.S_ERROR);
                    return attendGroupRes;
                }
            }
        }
        attendGroupRes.setSummary("成功!");
        attendGroupRes.setCode(AtdcResultCode.S_OK);
        return attendGroupRes;
        }

    private void checkSetEquipmentParam(AttendGroupReq attendGroupReq,AttendGroupRes attendGroupRes){
        if (AssertUtil.isEmpty(attendGroupReq.getEquipmentSerial())) {
            attendGroupRes.setCode(AtdcResultCode.ATDC104.PARAMS_INVALID);
            attendGroupRes.setSummary(AtdcResultSummary.ATDC104.PARAMS_INVALID);
        }
        if (AssertUtil.isEmpty(attendGroupReq.getEquipmentDeviceType())) {
            attendGroupRes.setCode(AtdcResultCode.ATDC104.PARAMS_INVALID);
            attendGroupRes.setSummary(AtdcResultSummary.ATDC104.PARAMS_INVALID);
        }
        if (AssertUtil.isEmpty(attendGroupReq.getAttendanceName())) {
            attendGroupRes.setCode(AtdcResultCode.ATDC104.PARAMS_INVALID);
            attendGroupRes.setSummary(AtdcResultSummary.ATDC104.PARAMS_INVALID);
        }
    }
}
