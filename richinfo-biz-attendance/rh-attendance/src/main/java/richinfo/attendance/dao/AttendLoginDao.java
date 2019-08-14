/**
 * 文件名：AttendLoginDao.java
 * 创建日期： 2017年6月6日
 * 作者：     yylchhy
 * Copyright (c) 2009-2011 邮箱产品开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月6日
 *   修改人：yylchhy
 *   修改内容：
 */
package richinfo.attendance.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import richinfo.attendance.entity.AttendExamineEntity;
import richinfo.attendance.entity.AttendWhitelistEntity;
import richinfo.attendance.entity.UserInfo;
import richinfo.dbcomponent.exception.PersistException;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述： 用户登录 信息查询
 */
public class AttendLoginDao extends BaseAttendanceDao
{

    private Logger logger = LoggerFactory.getLogger(AttendLoginDao.class);
    private AttendEmployeeDao employeeDao = new AttendEmployeeDao();
    public UserInfo queryUserInfo(String uid, int status, String enterId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("uid", uid);
        map.put("status", status);
        map.put("enterId", enterId);

        try {
              UserInfo userInfo = (UserInfo) attendanceDao.queryForObject(
                "attendance.queryUserInfo", map);
            AttendExamineEntity attendExamineEntity = (AttendExamineEntity)attendanceDao.queryForObject(
                "attendance.queryExamineUser", map);
            if (userInfo != null && attendExamineEntity != null){
                userInfo.setExaminerState(attendExamineEntity.getExaminerState());
            }else if (userInfo == null && attendExamineEntity != null){
                userInfo = new UserInfo();
                userInfo.setExaminerState(attendExamineEntity.getExaminerState());
                userInfo.setEmployeeName(attendExamineEntity.getExamineName());
                userInfo.setUid(uid);
                userInfo.setEnterId(enterId);
            }
            logger.info("queryUserInfo  userInfo={}|attendExamineEntity={}|map{}",userInfo,attendExamineEntity,map);
            return userInfo;
        } catch (PersistException e) {
            e.printStackTrace();
        }

        return null;

    }

    /**
     * 查询白名单用户
     * @param uid
     * @param
     * @param enterId
     * @return
     */
    public AttendWhitelistEntity queryWhitelistUserInfo(String uid, int status, String enterId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("uid", uid);
        map.put("status", status);
        map.put("enterId", enterId);

        try {
            return  (AttendWhitelistEntity) attendanceDao.queryForObject(
                "attendance.queryEnterWhitelistByUid", map);
        } catch (PersistException e) {
            e.printStackTrace();
        }

        return null;
    }

    public UserInfo queryUserInfoByUid(String uid, int status) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("uid", uid);
        map.put("status", status);

        try {
            UserInfo userInfo = (UserInfo) attendanceDao.queryForObject(
                "attendance.queryUserInfoByUid", map);
            if (userInfo == null){
                userInfo = new UserInfo();
                userInfo.setUid(uid);
            }
            logger.info("queryUserInfo  userInfo={}||map{}", userInfo, map);
            return userInfo;
        } catch (PersistException e) {
            e.printStackTrace();
            logger.info("queryUserInfo params {},  exception {}", map, e.toString());
        }

        return null;
    }
}
