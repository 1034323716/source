/**
 * 文件名：AttendAppealService.java
 * 创建日期： 2017年10月13日
 * 作者：     wangjin
 * Copyright (c) 2009-2011 无线开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年10月13日
 *   修改人：wangjin
 *   修改内容：
 */
package richinfo.attendance.service;

import richinfo.attendance.bean.AbnormalAppealRes;
import richinfo.attendance.bean.AttendAppealReq;
import richinfo.attendance.bean.AttendAppealRes;
import richinfo.attendance.bean.AttendExamineRes;
import richinfo.attendance.common.ResBean;

/**
 * 功能描述：考勤异常申诉接口
 *
 */
public interface AttendAppealService
{

    /**
     * 查询单个考勤异常申诉单详情
     * @param reqBean
     * @return
     */
    AttendAppealRes querySingleAppealInfo(AttendAppealReq reqBean);

    /**
     * 查询用户已申诉单列表信息
     * @param reqBean
     * @return
     */
    AttendAppealRes queryUserAppealList(AttendAppealReq reqBean);

    /**
     * 查询管理员待审批单列表信息
     * @param reqBean
     * @return
     */
    AttendAppealRes queryManageAppealList(AttendAppealReq reqBean);

    /**
     * 考勤异常申诉
     * @param reqBean
     * @return
     */
    AbnormalAppealRes abnormalAppeal(AttendAppealReq reqBean);

    /**
     * 查询考勤组对应的审批人员
     * @param reqBean
     * @return
     */
    AttendExamineRes queryExamineUid(AttendAppealReq reqBean);

    /**
     * 撤销考勤异常申诉单
     * @param reqBean
     * @return
     */
    ResBean cancelAppeal(AttendAppealReq reqBean);

    /**
     * 审批员审批申诉单
     * @param reqBean
     * @return
     */
    ResBean dealAppeal(AttendAppealReq reqBean);

}
