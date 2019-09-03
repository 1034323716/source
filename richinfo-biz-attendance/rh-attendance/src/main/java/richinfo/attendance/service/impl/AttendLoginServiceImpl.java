/**
 * 文件名：AttendLoginServiceImpl.java
 * 创建日期： 2017年6月5日
 * 作者：     yylchhy
 * Copyright (c) 2009-2011 邮箱产品开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月5日
 *   修改人：yylchhy
 *   修改内容：
 */
package richinfo.attendance.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import richinfo.attendance.SMS.AESUtil;
import richinfo.attendance.bean.*;
import richinfo.attendance.cache.UserInfoCache;
import richinfo.attendance.common.AtdcResultCode;
import richinfo.attendance.common.AtdcResultSummary;
import richinfo.attendance.common.ResultCode;
import richinfo.attendance.dao.AttendEmployeeDao;
import richinfo.attendance.dao.AttendGroupDao;
import richinfo.attendance.dao.AttendLoginDao;
import richinfo.attendance.dao.AttendReportDao;
import richinfo.attendance.entity.*;
import richinfo.attendance.entity.AttendEmployee.EmployeeType;
import richinfo.attendance.service.AttendGroupService;
import richinfo.attendance.service.AttendLoginService;
import richinfo.attendance.service.AttendService;
import richinfo.attendance.util.*;
import richinfo.bcomponet.cache.CachedUtil;
import richinfo.bcomponet.cache.comm.CacheKey;
import richinfo.bcomponet.resource.BComponetConfig;

import java.util.*;

/**
 * 功能描述： 考勤系统登录接口实现类
 */
public class AttendLoginServiceImpl implements AttendLoginService
{
    private Logger logger = LoggerFactory
        .getLogger(AttendLoginServiceImpl.class);
    private UserInfoCache userInfoCache = UserInfoCache.getInstance();
    AttendLoginDao loginDao = new AttendLoginDao();
    private AttendReportDao attendReportDao = new AttendReportDao();
    private AttendEmployeeDao employeeDao = new AttendEmployeeDao();
    private AttendGroupDao groupDao = new AttendGroupDao();
    private AttendService attednService = new AttendServiceImpl();
    private AttendGroupService groupService = new AttendGroupServiceImpl();
    /** 用管凭证校验请求参数密钥 */
    private static final String ARTIFACT_SECRET = "3*7-AB3S$69@94a0";

    private static long cacheTime =AttendanceConfig.getInstance()
        .getPropertyLong("attend.user.cacheTime", 1800000);
    private static String jumpUrl = AttendanceConfig.getInstance()
        .getProperty("attend.login.successUrl", "");

    @Override
    public AttendLoginRes ssoAttendance(AttendLoginReq req)
    {
        AttendLoginRes res = new AttendLoginRes();
        String token = req.getToken();
        String isAdmin = req.getIsAdmin();
        String uid = req.getUid();

        // token为空则返回参数校验失败
        if (AssertUtil.isEmpty(uid) || AssertUtil.isEmpty(token))
        {
            res.setCode(AtdcResultCode.ATDC104.PARAMS_NULL);
            res.setSummary("token或者uid参数为空!");
            res.setJumpUrl(AttendanceConfig.getInstance().getProperty(
                "attend.login.failUrl", ""));
            return res;
        }

        // 企业id信息，优先取企业部门enterDeptId，以便记录完整企业id信息
        String enterId = req.getEnterId();
        if (AssertUtil.isNotEmpty(req.getEnterDeptId()))
        {
            enterId = req.getEnterDeptId();
        }
        // 前往统一认证平台校验token
        UMCResBean resBean = UMCUtil.getInstance().checkTokenByUMC(token, uid,
            enterId);
        // 103000 代表鉴权成功
        if (AssertUtil.isNotEmpty(resBean.getHeader())
            && "103000".equals(resBean.getHeader().getResultcode()))
        {
            // 登录成功，生存用户会话保存 先查询用户信息
            try
            {
                String phone = null;
                String usessionid = EncryptionUtil.getMD5ByUtf8(resBean
                    .getBody().getUsessionid());
                if (AssertUtil.isEmpty(usessionid))
                {
                    usessionid = UUID.randomUUID().toString();
                    logger
                        .info(
                            "UMC usessionid is null, create usessionid={}|uid={}|UMCToken={}",
                            usessionid, req.getUid(), token);
                }

                // 由于之前此处查询是空的，也无影响，在此处查询做关联审批员角色查询
                UserInfo userInfo = loginDao.queryUserInfo(req.getUid(), 0, enterId);
                // 第一次单点登录的情况，考勤系统并没有用户数据，需要做下兼容
                if (AssertUtil.isEmpty(userInfo)
                    || AssertUtil.isEmpty(userInfo.getUid())) {
                    userInfo = new UserInfo();
                    userInfo.setUid(req.getUid());
                    userInfo.setEnterId(enterId);
                    // 为空则设定该用户审批员状态为0
                    userInfo.setExaminerState(0);
                }
                userInfo.setContactId(req.getContactId());
                userInfo.setToken(usessionid);
                userInfo.setCacheupdatetime(System.currentTimeMillis());
                userInfo.setLoginupdatetime(System.currentTimeMillis());
                if (AssertUtil.isEmpty(userInfo.getEmployeeName()) || AssertUtil.isEmpty(userInfo.getContactId())){
                    Map<String, Object> repMap = QytxlUtil.getInstance().getContactIdByEuserId(enterId, req.getUid());
                    if(0 == (int)repMap.get("error_code")){
                        List<Map<String,String>> userList= (List<Map<String,String>>)repMap.get("contactInfo");
                        //获取用户contactId
                        if (AssertUtil.isEmpty(userInfo.getContactId())){
                            userInfo.setContactId(userList.get(0).get("contactId"));
                        }
                        //获取用户名
                        if (AssertUtil.isEmpty(userInfo.getEmployeeName())){
                            String adminName  = AesUtils.decrypt(userList.get(0).get("name"), AttendanceConfig.getInstance()
                                .getProperty("attend.qytxl.aes_key",
                                    "6af15ca383ee45dd"));
                            userInfo.setEmployeeName(adminName);
                        }
                    }
                }
                List<String> attendanceIds = employeeDao.queryChargeGroupByUid(userInfo.getUid());
                if (AssertUtil.isNotEmpty(attendanceIds)) {
                    userInfo.setRoleType(EmployeeType.ChargeMan.getValue());
                } else {
                    userInfo.setRoleType(EmployeeType.NormalEmp.getValue());
                }

                //根据enterid uid查询设备表
                Map<String,Object> temp = new HashMap();
                temp.put("enterId",userInfo.getEnterId());
                AttendanceEquipmentControl attendanceEquipment = groupDao.queryEquipmentStatus(temp);
                if (AssertUtil.isNotEmpty(attendanceEquipment)) {
                    if ("0".equals(attendanceEquipment.getEquipmentUseStatus())) {
                        res.setUseEquipmentClock("0");
                    } else {
                        res.setUseEquipmentClock("1");
                    }
                } else {
                    res.setUseEquipmentClock("1");
                }

                //查询白名单
                AttendWhitelistEntity whitelistUser = loginDao.queryWhitelistUserInfo(req.getUid(), 0, enterId);
                logger.debug("校验白名单用户=============whitelistUser{}",whitelistUser);
                //校验白名单用户
                if (whitelistUser != null && StringUtils.isNotBlank(whitelistUser.getUid())){
                    res.setWhitelistStatus(1);
                    userInfo.setWhitelistStatus(1);
                }
                // 和飞信给的数据库数据没有RCS登录的准确 这里设置一下企业名称为登录数据
                if (AssertUtil.isNotEmpty(req.getEnterName()))
                {
                    userInfo.setEnterName(req.getEnterName());
                }
                phone = resBean.getBody().getMsisdn();
                if (AssertUtil.isNotEmpty(phone)) {
                    userInfo.setPhone(phone);
                }


                // 管理员标识处理 ,1是管理员 非1则置为普通员工
                if ("1".equals(isAdmin) || StringUtils.isBlank(isAdmin)) {
                    // 是管理员，则走RCS检验保证权限安全
                    boolean result = UMCUtil.getInstance().checkAdminByRCS(
                            enterId, req.getFirstDeptId(), "1",
                            resBean.getBody().getMsisdn(), uid);
                    logger.info("h5 管理员认证返回result={}",result);
                    if (result)
                    {
                        isAdmin = "1";
                    }else {
                        isAdmin = "0";
                    }
                } else
                {
                    isAdmin = "0";

                }
                userInfo.setIsAdmin(ConverUtil.string2Int(isAdmin));
                res.setStatus(isAdmin);
                res.setRoleType(userInfo.getRoleType());
                res.setExaminerState(userInfo.getExaminerState());
                res.setCode(ResultCode.S_OK);
                res.setUsessionid(usessionid);
                res.setPhone(phone);
                res.setEnterName(userInfo.getEnterName());
                res.setJumpUrl(jumpUrl);
                // 缓存用户信息 默认缓存半小时
                userInfoCache.save(
                    usessionid,
                    userInfo,
                    cacheTime);

                // 用户首次登录标识
                String firstLogin = checkFirstLogin(uid);
                res.setFirstLogin(firstLogin);
//                if (uid.equals("A11275A1A064AFB7CAE7E38ED6CA3B89") || uid.equals("665036D7B3DDB92A2C2FC3B3CCECF5CD")){
//                    res.setFirstLogin("1");
//                }
            }
            catch (Exception e)
            {
                logger.error(
                    "ssoAttendance data error token={}|isAdmin={}|uid={}",
                    token, isAdmin, req.getUid(), e);
                res.setCode(AtdcResultCode.S_ERROR);
                res.setSummary(AssertUtil.isNotEmpty(resBean.getHeader()) ? resBean
                    .getHeader().getResultcode() : "UMC check token error!");
                res.setJumpUrl(AttendanceConfig.getInstance().getProperty(
                    "attend.login.failUrl", ""));
            }

        }
        else
        {
            res.setCode(AtdcResultCode.S_ERROR);
            res.setSummary(AssertUtil.isNotEmpty(resBean.getHeader()) ? resBean
                .getHeader().getResultcode() : "UMC check token error!");
            res.setJumpUrl(AttendanceConfig.getInstance().getProperty(
                "attend.login.failUrl", ""));
            return res;
        }

        return res;
    }

    /**
     * 缓存用户首次登录标识，临时写缓存，后续考虑写库方案
     * @param uid
     * @return
     */
    private String checkFirstLogin(String uid)
    {
        String firstLoginKey = String.format(
            CacheKey.Attendance.ATTENDANCE_USER_FIRST_LOGIN_U, uid);
        Object object = CachedUtil.get(firstLoginKey);
        logger.debug("checkFirstLogin firstLoginKey====================={}",firstLoginKey);
        // 默认是首次登录，如果缓存中有值，则使用缓存中的值
        String firstLogin = "1";
        if (object != null)
        {
            firstLogin = (String) object;
        }

        // 缓存20天
        CachedUtil.set(firstLoginKey, "0", 20 * 24 * 3600 * 1000);

        return firstLogin;
    }

    @Override
    public AttendLoginRes ssoAttendancePc(AttendLoginReq req)
    {
        AttendLoginRes res = new AttendLoginRes();
        String token = req.getToken();
        String isAdmin = req.getIsAdmin();
        String Am = req.getaM();
        // TODO 由于RCS WEB单点登录带管理员参数有问题，先统一放开，后续改造再处理
       // isAdmin = "1";
        String uid = req.getUid();
        // 企业id信息，优先取企业部门enterDeptId，以便记录完整企业id信息,此处兼容暂时没传enterDeptId的情景
        String enterId = req.getEnterId();
        if (AssertUtil.isNotEmpty(req.getEnterDeptId()))
        {
            enterId = req.getEnterDeptId();
        }
        else if (AssertUtil.isNotEmpty(req.getFirstDeptId())
            && !"0".equals(req.getFirstDeptId()))
        {
            enterId = req.getEnterId() + "-" + req.getFirstDeptId();
        }

        // 统一认证的必要请求参数为空则直接返回参数校验失败
        if (AssertUtil.isEmpty(uid) || validReqParam(req))
        {
            res.setCode(AtdcResultCode.ATDC104.PARAMS_NULL);
            res.setSummary("凭证校验必选参数或uid参数为空!");
            res.setJumpUrl(AttendanceConfig.getInstance().getProperty(
                "attend.login.pcFailUrl", ""));
            return res;
        }

        // 校验check签名参数，判断请求是否有效
        if (!validCheckParam(req))
        {
            res.setCode(AtdcResultCode.ATDC108.ARTIFACT_SIGN_VALID);
            res.setSummary(AtdcResultSummary.ATDC108.ARTIFACT_SIGN_VALID);
            res.setJumpUrl(AttendanceConfig.getInstance().getProperty(
                "attend.login.pcFailUrl", ""));
            return res;
        }

        // 前往统一认证平台校验token
        UmcArtifactValidRes resBean = UMCUtil.getInstance().checkArtifactByUmc(req);
        if (resBean.isSuccess())
        {
            // 登录成功，生存用户会话保存 先查询用户信息
            try
            {
                String phone = null;
                // 为防止用户同时登录和飞信和管理后台，导致会话被置换掉，PC端管理后台追加"RCSPC"
                String usessionid = EncryptionUtil.getMD5ByUtf8(resBean
                    .getUid() + "RCSPC");
                if (AssertUtil.isEmpty(usessionid))
                {
                    usessionid = UUID.randomUUID().toString();
                    logger
                        .info(
                            "UMC usessionid is null, create usessionid={}|uid={}|UMCToken={}",
                            usessionid, req.getUid(), token);
                }
                // 由于之前此处查询是空的，也无影响，在此处查询做关联审批员角色查询
                UserInfo userInfo = loginDao.queryUserInfo(req.getUid(), 0, enterId);
                // 第一次单点登录的情况，考勤系统并没有用户数据，需要做下兼容
                if (AssertUtil.isEmpty(userInfo)
                    || AssertUtil.isEmpty(userInfo.getUid()))
                {
                    userInfo = new UserInfo();
                    userInfo.setUid(req.getUid());
                    userInfo.setEnterId(enterId);
                    // 为空则设定该用户审批员状态为0
                    userInfo.setExaminerState(0);
                }
                if (AssertUtil.isEmpty(userInfo.getContactId())){
                    Map<String, Object> repMap = QytxlUtil.getInstance().getContactIdByEuserId(enterId, req.getUid());
                    if(0 == (int)repMap.get("error_code")){
                        List<Map<String,String>> userList= (List<Map<String,String>>)repMap.get("contactInfo");
                        userInfo.setContactId(userList.get(0).get("contactId"));
                        String adminName  = AesUtils.decrypt(userList.get(0).get("name"), AttendanceConfig.getInstance()
                            .getProperty("attend.qytxl.aes_key",
                                "6af15ca383ee45dd"));
                        userInfo.setEmployeeName(adminName);
                    }

                }
                userInfo.setToken(usessionid);
                userInfo.setIsAdmin(ConverUtil.string2Int(isAdmin));
                userInfo.setaM(ConverUtil.string2Int(Am));
                userInfo.setCacheupdatetime(System.currentTimeMillis());
                userInfo.setLoginupdatetime(System.currentTimeMillis());
                res.setRoleType(userInfo.getRoleType()==null?0:userInfo.getRoleType());
                //判断超级管理员，如是超级管理员的话则赋予其所在企业下的所有考勤组负责人的身份
                logger.info("userInfo={}|req={}",userInfo.getaM(),req.getaM());
                if (userInfo.getaM()==1) {
                    logger.info("超级管理员进来了，userInfo = {}",userInfo);
                    //该管理员还不是负责人时 赋予负责人身份
                    if (res.getRoleType()!=1) {
                        List users = new ArrayList();
                        users.add(userInfo);
                        employeeDao.batchUpdateEmpRoleType(users,1);
                    }
                    //校验基本信息
//                    checkUserInfoParam(userInfo);
//                    //判断该管理员是否已经获得了所有考勤组的权限 已获得则不再重新赋予
//                    int attendanceIdByEnterId = employeeDao.queryAttendanceIdByEnterId(userInfo.getEnterId());
//                    int attendanceIdByUid = employeeDao.queryAttendanceIdByUid(userInfo.getUid());
//                    logger.info("attendanceIdByEnterId={}",attendanceIdByEnterId);
//                    logger.info("attendanceIdByUid={}",attendanceIdByUid);
//                    if (attendanceIdByEnterId != attendanceIdByUid) {
//                        logger.info("需要赋予管理员全局权限的日志记录!");
//                        //为避免全局插入语句时出现duplicate_key错误 需要先把原来的数据物理删除
//                        employeeDao.removeChargeManRole(userInfo);
//                        //设置企业全局考勤组负责人
//                        employeeDao.setEnterChargeMan(userInfo);
//                    }
                }
                // 和飞信给的数据库数据没有RCS登录的准确 这里设置一下企业名称为登录数据
                if (AssertUtil.isNotEmpty(req.getEnterName()))
                {
                    userInfo.setEnterName(req.getEnterName());
                }
                phone = resBean.getMobileNumber();
                if (AssertUtil.isNotEmpty(phone))
                {
                    userInfo.setPhone(phone);
                }
                // TODO 收敛管理员权限时，此处也需放开
                /*
                 * // 管理员标识处理 ,1是管理员 非1则置为普通员工 if (!"1".equals(isAdmin)) {
                 * isAdmin = "0"; } else { // 是管理员，则走RCS检验保证权限安全 boolean result
                 * = UMCUtil.getInstance().checkAdminByRCS( enterId,
                 * req.getFirstDeptId(), "1", resBean.getMobileNumber(), uid);
                 * if (!result) { isAdmin = "0"; } }
                 */
                res.setStatus(isAdmin);
                res.setAmStatus(Am);
                res.setExaminerState(userInfo.getExaminerState());
                res.setCode(ResultCode.S_OK);
                res.setUsessionid(usessionid);
                res.setPhone(phone);
                res.setJumpUrl(AttendanceConfig.getInstance().getProperty(
                    "attend.login.pcSuccessUrl", ""));
                userInfo.setUmcArtifactValidRes(resBean);
                // 缓存用户信息 默认缓存半小时
                userInfoCache.save(
                    usessionid,
                    userInfo,
                    AttendanceConfig.getInstance().getPropertyLong(
                        "attend.user.cacheTime", 1800000));

                // 用户首次登录标识
                String firstLogin = checkFirstLogin(uid);
                res.setFirstLogin(firstLogin);
            }
            catch (Exception e)
            {
                logger.error(
                    "ssoAttendPc data error token={}|isAdmin={}|uid={}", token,
                    isAdmin, req.getUid(), e);
                res.setCode(AtdcResultCode.S_ERROR);
                res.setSummary(resBean.getCode() + ":UMC check token error!");
                res.setJumpUrl(AttendanceConfig.getInstance().getProperty(
                    "attend.login.pcFailUrl", ""));
            }

        }
        else
        {
            res.setCode(AtdcResultCode.S_ERROR);
            res.setSummary(resBean.getCode() + ":UMC check token error!");
            res.setJumpUrl(AttendanceConfig.getInstance().getProperty(
                "attend.login.pcFailUrl", ""));
            return res;
        }
        return res;
    }
    
    
    
    @Override
    public UmcGetArtifactRes getArtifactPc(UserInfo userInfo)
    {
    	UmcGetArtifactRes res = new UmcGetArtifactRes();

        // 统一认证的必要请求参数为空则直接返回参数校验失败
        if (AssertUtil.isEmpty(userInfo))
        {
            res.setCode(AtdcResultCode.ATDC104.PARAMS_NULL);
            res.setSummary("凭证校验失败!");
            return res;
        }

        res = UMCUtil.getInstance().getArtifactByUmc(
            userInfo);
		return res;
    }

    @Override
    public SMSAttendancInfo ssoAttendanceSMS(AttendLoginReq req) {
        // 前往统一认证平台校验token
        UMCResBean resBean = UMCUtil.getInstance().checkTokenBySMS(req.getToken());
        SMSAttendancInfo attendancInfo = null;
        if (AssertUtil.isNotEmpty(resBean.getHeader())
            && "103000".equals(resBean.getHeader().getResultcode())) {

            attendancInfo = new SMSAttendancInfo();
            //获取登录手机号
            String phone = resBean.getBody().getMsisdn();
            // 由于之前此处查询是空的，也无影响，在此处查询做关联审批员角色查询
            UserInfo userInfo = loginDao.queryUserInfoByUid(req.getUid(), 0);
            logger.info("=========================>> userInfo : {}, uid:{}",userInfo, req.getUid());
            try {
                String desPhone = AESUtil.deCodeAES(phone,AttendanceConfig.getInstance().getProperty("attend.sms.token.phone.secret", "hHMogstx1gcJQLcu"));
                //不存在
                if(null == userInfo) {
                    attendancInfo.setSsoStatus(-1);
                    return attendancInfo;
                }

                if(StringUtils.isEmpty(userInfo.getPhone())) {
                    String remotePhone = getUserMobile(userInfo.getEnterId(), userInfo.getContactId());
                    if(null == remotePhone) {
                        attendancInfo.setSsoStatus(-1);
                        return attendancInfo;
                    }

                    userInfo.setPhone(remotePhone);
                    //入库
                    List<Map<String, String>> phoneList = new ArrayList<>();
                    Map<String, String> phoneItem = new HashMap<>();
                    phoneItem.put("phone",remotePhone);
                    phoneItem.put("uid",userInfo.getUid());
                    phoneList.add(phoneItem);
                    attendReportDao.batchInsertPhone(phoneList);
                }
                logger.info("=========================>> resBean : {}, phoneNum : {}, " +
                    "desPhone : {}, userPhone:{}",resBean,phone,desPhone,userInfo.getPhone());

                //替人打卡
                if(!userInfo.getPhone().equals(desPhone)) {
                    attendancInfo.setSsoStatus(-2);
                    return attendancInfo;
                }

            } catch (Exception e) {
                logger.error("Aes phone error : {}", e.toString());
                attendancInfo.setSsoStatus(-1);
                return attendancInfo;
            }
            String usessionid = UUID.randomUUID().toString();


            userInfo.setCacheupdatetime(System.currentTimeMillis());
            userInfo.setLoginupdatetime(System.currentTimeMillis());
            userInfo.setToken(usessionid);

            // 缓存用户信息 默认缓存半小时
            logger.info("sms login usessionid {}", usessionid);
            userInfoCache.save(
                usessionid,
                userInfo,
                AttendanceConfig.getInstance().getPropertyLong(
                    "attend.user.cacheTime", 1800000));

            //获取打卡数据
            AttendReq employRecordReq = new AttendReq();
            employRecordReq.setUid(req.getUid());
            employRecordReq.setUserInfo(userInfo);
            employRecordReq.setToken(usessionid);
            AttendRes attendRes = attednService.queryEmployRecord(employRecordReq);
            //获取考勤组
            AttendGroupReq reqBean = new AttendGroupReq();
            reqBean.setUserInfo(userInfo);
            AttendUserInGroupRes res = groupService.queryOwnGroup(reqBean);
            attendancInfo.setToken(usessionid);
            if(null != res) {
                attendancInfo.setAllowOutRangeClock(res.getAllowOutRangeClock());
                attendancInfo.setCharge(res.getCharge());
                UserGroupEntity userGroupEntity = res.getUserGroup();
                List<AttendClockSite> attendClockSites = null;
                if(null != userGroupEntity
                    && (attendClockSites = userGroupEntity.getAttendClockSites()) != null) {
                    for (AttendClockSite item : attendClockSites) {
                        item.setRange(item.getAttendanceRange());
                    }
                }
                attendancInfo.setUserGroup(userGroupEntity);
            }

            if(null != attendRes) {
                attendancInfo.setAttendClockVos(attendRes.getAttendClockVos());
                attendancInfo.setAttendRecord(attendRes.getAttendRecord());
            }
        }

        logger.info("attendance data is {}", attendancInfo);
        return attendancInfo;
    }

    private String getUserMobile(String entpId, String contactId) {
        int retry = 2;
        while (true) {
            try {
                Map<String, Object> repMap = QytxlUtil.getInstance().getItem(entpId, contactId);
                if (0 == (Double) repMap.get("error_code")){
                    Map<String,Object> itemMap = (Map<String, Object>) repMap.get("item");
                    String regMobile =  (String)itemMap.get("regMobile");
                    regMobile =  AesUtils.decrypt(regMobile, AttendanceConfig.getInstance().getProperty("attend.qytxl.aes_key", "6af15ca383ee45dd"));
                    logger.info("decrypt ContactId={}|enterId={}|regMobile={}",contactId, entpId,regMobile);
                    return regMobile;
                }else {
                    logger.error("获取失败==========》ContactId={}|enterId={}|msg={}", contactId, entpId,repMap.get("error_msg"));
                    if(retry < 0) {
                        return null;
                    }
                }
                retry--;
            } catch (Exception e) {
                if(retry < 0) {
                    return null;
                }
                retry--;
            }
        }
    }

    @Override
    public SMSAttendancInfo ssoAttendanceTakeNum(AttendLoginReq req) {
        String usessionid = UUID.randomUUID().toString();
        // 由于之前此处查询是空的，也无影响，在此处查询做关联审批员角色查询
        UserInfo userInfo = loginDao.queryUserInfoByUid(req.getUid(), 0);
        userInfo.setCacheupdatetime(System.currentTimeMillis());
        userInfo.setLoginupdatetime(System.currentTimeMillis());
        userInfo.setToken(usessionid);
        req.setUserInfo(userInfo);
        // 缓存用户信息 默认缓存半小时
        logger.info("take number login usessionid {}", usessionid);
        userInfoCache.save(
            usessionid,
            userInfo,
            AttendanceConfig.getInstance().getPropertyLong(
                "attend.user.cacheTime", 1800000));

        //获取打卡数据
        AttendReq employRecordReq = new AttendReq();
        employRecordReq.setUid(req.getUid());
        employRecordReq.setUserInfo(userInfo);
        employRecordReq.setToken(usessionid);
        AttendRes attendRes = attednService.queryEmployRecord(employRecordReq);
        //获取考勤组
        AttendGroupReq reqBean = new AttendGroupReq();
        reqBean.setUserInfo(userInfo);
        AttendUserInGroupRes res = groupService.queryOwnGroup(reqBean);

        SMSAttendancInfo attendancInfo = new SMSAttendancInfo();
        attendancInfo.setToken(usessionid);
        if(null != res) {
            attendancInfo.setAllowOutRangeClock(res.getAllowOutRangeClock());
            attendancInfo.setCharge(res.getCharge());
            attendancInfo.setUserGroup(res.getUserGroup());
        }

        if(null != attendRes) {
            attendancInfo.setAttendClockVos(attendRes.getAttendClockVos());
            attendancInfo.setAttendRecord(attendRes.getAttendRecord());
        }

        return attendancInfo;
    }

    /**
     * 
     * @param req
     * @return
     */
    private boolean validCheckParam(AttendLoginReq req)
    {
        String str = req.getOptype() + req.getSourceid() + req.getPassid()
            + req.getArtifact() + req.getReqtime() + ARTIFACT_SECRET;
        String encryptStr = EncryptionUtil.getSha1(str);
        if (req.getCheck().equals(encryptStr))
        {
            return true;
        }
        return false;
    }

    /**
     * PC端管理后台 token校验参数
     * @param req
     * @return
     */
    private boolean validReqParam(AttendLoginReq req)
    {
        if (AssertUtil.isEmpty(req.getArtifact()))
        {
            return true;
        }
        if (AssertUtil.isEmpty(req.getPassid()))
        {
            return true;
        }
        if (AssertUtil.isEmpty(req.getSourceid()))
        {
            return true;
        }
        if (AssertUtil.isEmpty(req.getCheck()))
        {
            return true;
        }
        if (AssertUtil.isEmpty(req.getOptype()))
        {
            return true;
        }
        if (AssertUtil.isEmpty(req.getReqtime()))
        {
            return true;
        }
        return false;
    }

    /*
     * 校验人员基本信息
     */
    public static Boolean checkUserInfoParam(UserInfo userInfo) {
        if (AssertUtil.isNotEmpty(userInfo)) {
            if ("".equals(userInfo.getUid()) || userInfo.getUid() == null) {
                return false;
            }
            if ("".equals(userInfo.getEnterId()) || userInfo.getEnterId() == null) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

}
