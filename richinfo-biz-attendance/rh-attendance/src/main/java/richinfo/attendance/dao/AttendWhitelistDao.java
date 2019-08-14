package richinfo.attendance.dao;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import richinfo.attendance.bean.AttendGroupReq;
import richinfo.attendance.bean.AttendGroupRes;
import richinfo.attendance.entity.AttendApprovalRestrict;
import richinfo.attendance.entity.AttendWhitelistEntity;
import richinfo.attendance.entity.UserInfo;
import richinfo.attendance.util.AssertUtil;
import richinfo.dbcomponent.exception.PersistException;

import java.util.*;

/**
 * Created by Daniel on 2018/9/9.
 */
public class AttendWhitelistDao extends BaseAttendanceDao{
    private Logger logger = LoggerFactory.getLogger(AttendWhitelistDao.class);

    /**
     * 设置全局白名单 与审批限制
     */
    public boolean setGlobalWhiteList(AttendGroupReq groupReq,AttendGroupRes groupRes){
        boolean flag;
        try {
            attendanceDao.startTransaction();

            //设置审批限制
            AttendApprovalRestrict attendApprovalRestrict = groupReq.getAttendApprovalRestrict();
            //不为空说明是pc端提交
            if (attendApprovalRestrict != null){
                try {
                    attendanceDao.insert("attendance.insertOrUpdateApprovalRestrict",attendApprovalRestrict);
                }catch (Exception e){
                    //回滚
                    attendanceDao.rollbackTransaction();
                    logger.error("insertOrUpdateApprovalRestrict error attendApprovalRestrict={}|errorMsg={}",attendApprovalRestrict,e);
                    return false;
                }
            }
            //根据企业id查询所有的白名单人员
            Map<String,Object> whiteListMap = new HashMap<>();
            whiteListMap.put("enterId",groupReq.getUserInfo().getEnterId());
            whiteListMap.put("status",0);
            List<AttendWhitelistEntity> allWhitelist = attendanceDao.queryForList("attendance.queryAttendWhitelist",whiteListMap);

            List<UserInfo> userInfoList = groupReq.getEmployees();

            //校验用户列表
            //提交白名单列表是否为空 为空则清空白名单
            if (AssertUtil.isEmpty(userInfoList)) {
                if (AssertUtil.isNotEmpty(allWhitelist)) {
                    //白名单里需要移除的员工在白名单表状态改为1
                    Map<String,Object> map = new HashMap<>();
                    map.put("users",allWhitelist);
                    map.put("status",1);
                    try {
                        attendanceDao.update("attendance.batchUpdateEmployeeWhiteListStatus",map);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.info("更新员工白名单状态失败，因为{}",e);
                        attendanceDao.rollbackTransaction();
                    }
                }
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
            }

            //判断该人员列表是否为空，为空则不需要执行里面的修改操作
            if (AssertUtil.isNotEmpty(allWhitelist)) {
                //对比数据库中的白名单列表a和本次配置白名单列表b 处理待移除名单allWhitelist
                compareList(allWhitelist,userInfoList);
                if (AssertUtil.isNotEmpty(allWhitelist)) {
                    //批量修改这些人员的状态值
                    //白名单里需要移除的员工在白名单表状态改为1 同时员工表状态改回为正常 0
                    Map<String,Object> map = new HashMap<>();
                    map.put("users",allWhitelist);
                    map.put("status",1);
                    try {
                        attendanceDao.update("attendance.batchUpdateEmployeeWhiteListStatus",map);

                        logger.info("batchUpdateEmployeeWhiteListStatus success,users={}",map.get("users"));
                    } catch (Exception e) {
                        try {
                            attendanceDao.rollbackTransaction();
                        } catch (PersistException e1) {
                            logger.info("batchUpdateEmployeeWhiteListStatus success,users={}",map.get("users"),e1);
                        }
                    }
                }
            }

            if (AssertUtil.isNotEmpty(userInfoList)) {
                List<AttendWhitelistEntity> list = new ArrayList<>();
                AttendWhitelistEntity entity = new AttendWhitelistEntity();

                for (UserInfo info : userInfoList) {
                    entity.setUid(info.getUid());
                    entity.setContactId(info.getContactId());
                    entity.setEmployeeName(info.getEmployeeName());
                    entity.setPhone(info.getPhone());
                    entity.setEnterId(info.getEnterId());
                    entity.setEnterName(info.getEnterName());
                    entity.setDeptId(info.getDeptId());
                    entity.setDeptName(info.getDeptName());
                    entity.setEmail(info.getEmail());
                    entity.setPosition(info.getPosition());
                    //考勤人员白名单标识 设置为0
                    entity.setStatus(0);
                    entity.setCreateTime(new Date());
                    entity.setModifyTime(new Date());
                    entity.setCreator(groupReq.getUserInfo().getEmployeeName());
                    entity.setCreatorId(groupReq.getUserInfo().getUid());
                    list.add(entity);
                    entity = new AttendWhitelistEntity();
                }

                try {
                    flag = attendanceDao.batchInsert("attendance.saveGlobalWhiteList",list);
                    logger.info("saveGlobalWhiteList insert to DB success,count={}",list.size());
                    groupRes.setWhitelistEntities(list);
                    return flag;
                } catch (Exception e) {
                    attendanceDao.rollbackTransaction();
                    logger.error("saveGlobalWhiteList insert to DB failed,list = {}",list);
                }
            }
        }catch (Exception e) {
            logger.error("saveGlobalWhiteList failed ,cause = {}",e);
        }
        finally {
            try {
                attendanceDao.commitTransaction();
            } catch (PersistException e) {
                logger.error("saveGlobalWhiteList commitTransaction failed,",e);
            }
        }
        return true;
    }

    public List queryAttendWhitelistUid(String enterId){
        try {
            List list = attendanceDao.queryForList("attendance.queryAttendWhitelistUid",enterId);
            logger.info("queryAttendWhitelistUid success from DB,enterId={}",enterId);
            return list;
        }catch (Exception e) {
            logger.error("queryAttendWhitelistUid failed,enterId={}",enterId,e);
        }
        return null;
    }

    public List<AttendWhitelistEntity> queryAttendWhitelist(String enterId) throws Exception{
        Map<String,Object> map = new HashMap<>();
        map.put("enterId",enterId);
        map.put("status",0);
        try {
            List<AttendWhitelistEntity> list = attendanceDao.queryForList("attendance.queryAttendWhitelist",map);
            logger.info("queryAttendWhitelist success from DB,enterId={}",enterId);
            return list;
        }catch (Exception e) {
            logger.error("queryAttendWhitelist failed,enterId={}",enterId,e);
            throw new Exception(e);
        }
    }

    /**
     * 查询企业用户白名单信息
     * @param uid
     * @return
     */
    public AttendWhitelistEntity queryEnterWhitelistByUid(String uid,int status) {

        try {
            Map<String,Object> map = new HashMap<>();
            map.put("uid",uid);
            map.put("status",status);
             return  (AttendWhitelistEntity) attendanceDao.queryForObject("attendance.queryEnterWhitelistByUid",map);
        }catch (Exception e) {
            logger.error("queryEnterWhitelistByUid error failed,uid={}",uid);
        }
        return null;
    }

    /**
     * 根据uid批量修改员工状态
     * @return
     */
    public boolean batchUpdateEmployeeWhiteListStatus(List users,int status){
        int result = 0;

        Map<String,Object> map = new HashMap<>();
        map.put("users",users);
        map.put("status",status);

        try {
            attendanceDao.startTransaction();

            result = attendanceDao.update("attendance.batchUpdateEmployeeWhiteListStatus",map);

            logger.info("batchUpdateEmployeeWhiteListStatus success,users={}|result={}",map.get("users"),result);
            return true;
        } catch (Exception e) {
            try {
                attendanceDao.rollbackTransaction();
            } catch (PersistException e1) {
                logger.info("batchUpdateEmployeeWhiteListStatus success,users={}|result={}",map.get("users"),result,e1);
            }
        }
        finally {
            try {
                attendanceDao.commitTransaction();
            } catch (PersistException e) {
                logger.info("batchUpdateEmployeeWhiteListStatus success,users={}|result={}",map.get("users"),result,e);            }
            }
        return false;
    }


    /**
     * 获取企业白名单uid列表
     * @param enterId
     * @return
     */
    public List<String> queryUidInEnter(String enterId) {
        try {
           return attendanceDao.queryForList("attendance.queryAttendWhitelistUid",enterId);
        } catch (PersistException e) {
           logger.error("queryUidInEnter query data error enterId={}|e={}",enterId,e);
        }
    return new ArrayList<String>();
    }

    private static void compareList(List<AttendWhitelistEntity> allWhitelist,List<UserInfo> userInfoList){
        Iterator<UserInfo> iterator = userInfoList.iterator();
        while (iterator.hasNext()) {
            UserInfo userInfo = iterator.next();
            if (allWhitelist.contains(userInfo.getUid())) {
                allWhitelist.remove(userInfo.getUid());
                iterator.remove();
            }
        }
    }
}
