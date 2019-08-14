/**
 * 文件名：AttendanceConfig.java
 * 创建日期： 2017年6月5日
 * 作者：     yylchhy
 * Copyright (c) 2009-2011 邮箱产品开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月5日
 *   修改人：yylchhy
 *   修改内容：
 */
package richinfo.attendance.util;

import richinfo.bcomponet.resource.BaseConfig;

/**
 * 功能描述： 考勤系统配置类
 */
public class AttendanceConfig extends BaseConfig
{
    private static volatile AttendanceConfig conf;

    private AttendanceConfig()
    {
        super();
        loadPropertiesFile("attendance.properties");
    }

    public static AttendanceConfig getInstance()
    {
        if (conf == null)
        {
            synchronized (AttendanceConfig.class)
            {
                if (conf == null)
                {
                    conf = new AttendanceConfig();
                }
            }
        }
        return conf;
    }

    @Override
    public void reLoadData()
    {
        loadPropertiesFile("attendance.properties");
    }

    /**
     * 获取已经配置节假日信息的年份，多个年份，逗号(,)分隔，格式YYYY
     * @return
     */
    public String getCalenderConfigYear()
    {
        return getProperty("calender.config.year", "2017,2018");
    }

    /**
     * 获取即将保存的日历信息的年份，单个年份，格式YYYY
     * @return
     */
    public String getCalenderSaveYear()
    {
        return getProperty("calender.save.year", "2017");
    }

    /**
     * 获取该年份周末的日期列表，排除与节假日重复的周末。eg:2017-06-10,2017-06-11
     * @param year 年份，YYYY,eg:2017
     * @return 周末日期列表，格式：YYYY-MM-DD
     */
    public String getWeekend(String year)
    {
        return getProperty("calender.weekend." + year);
    }

    /**
     * 获取该年份周末的法定节假日的补班日期列表。eg:2017-06-10,2017-06-11
     * @param year 年份，YYYY,eg:2017
     * @return 周末日期列表，格式：YYYY-MM-DD
     */
    public String getRowDays(String year)
    {
        return getProperty("calender.rowday." + year);
    }

    public String getConfigInfo(String name)
    {
        return getProperty(name);
    }

    /**
     * 获取该年份法定节假日的 日期-假日 列表，逗号(,)分隔。eg:2017-05-28:端午节,2017-05-29:端午节
     * @param year 年份，YYYY,eg:2017
     * @return 日期-假日 列表，格式:YYYY-MM-DD:节日,YYYY-MM-DD:节日等
     */
    public String getHoilday(String year)
    {
        return getProperty("calender.holiday." + year);
    }

    /**
     * 判断非工作日是否使用默认的备注描述。0：不使用；1：使用。默认值为0.
     * @return
     */
    public int isUseRestdayDefaultRemark()
    {
        return getPropertyInt("calender.restday.default.remark", 0);
    }

    /**
     * 考勤组名称的长度，默认设为20
     * @return
     */
    public int getAttendNameLength()
    {
        return getPropertyInt("attendance.name.length", 20);
    }

    /**
     * 通用名称长度，默认值为60
     * @return
     */
    public int getCommonNameLength()
    {
        return getPropertyInt("common.name.length", 60);
    }

    /**
     * 考勤详细地址长度，默认值设为100
     * @return
     */
    public int getDetailAddrLength()
    {
        return getPropertyInt("detail.addr.length", 100);
    }

    /**
     * 判断是否是执行定时任务的机器。key对应的默认值为0，不执行；定时任务机器，配值为1，仅一台机器需配置
     * @return
     */
    public boolean isTaskServer()
    {
        return getPropertyInt("attendance.task.server", 0) == 1;
    }

    /**
     * 获取测试的管理员账号列表，支持存企业ID和uid，开始和结尾均用逗号（,）
     * @return
     */
    public String getTestAdminId()
    {
        // attendance.eid.list=,483460,36301,
        return getProperty("attendance.eid.list", ",,");
    }

    /**
     * 考勤详细地址长度，默认值设为100
     * @return
     */
    public int getAppealReasonLength()
    {
        return getPropertyInt("appeal.reason.length", 100);
    }

    /**
     * 获取考勤组上班前消息推送提醒时间（默认5分钟）
     * @return
     */
    public long getRemindTime()
    {
        return getPropertyLong("attendance.hfx.msg.remindTime", 10 * 60 * 1000L);
    }

    /**
     * 获取更新待发消息的间隔时间（默认30分钟）
     * @return
     */
    public long getUpdateMsgIntervalTime()
    {
        return getPropertyLong("attendance.update.msg.intervalTime",
            30 * 60 * 1000L);
    }

    /**
     * 获取机器编号列表
     * @return
     */
    public String getServerNoList()
    {
        return getProperty("attendance.serverNo.list", "1,2");
    }

    /**
     * 获取当前机器编号（用于扫描待发消息表）
     * @return
     */
    public int getCurrentServerNo()
    {
        return getPropertyInt("attendance.current.serverNo", 0);
    }

    /**
     * 获取消息扫描条数（默认100）
     * @return
     */
    public int getMsgScanLimit()
    {
        return getPropertyInt("attendance.msg.scan.limit", 100);
    }

    /**
     * 获取每次扫描发送消息任务的最大累计发送消息数（默认1000）
     * @return
     */
    public int getMaxMsgTotalCount()
    {
        return getPropertyInt("attendance.max.msg.totalCount", 1000);
    }

    /**
     * 获取每个异步任务下发的待发消息数（默认10）
     * @return
     */
    public int getMsgSplitSize()
    {
        return getPropertyInt("attendance.msg.splitSize", 10);
    }

    /**
     * 签到消息标题
     * @return
     */
    public String getSignMsgTopic()
    {
        return getProperty("attendance.sign.msg.topic", "上班打卡提醒");
    }


    /**
     * 签到消息内容
     * @return
     */
    public String getSignMsgContent()
    {
        return getProperty("attendance.sign.msg.content",
            "还有[@remindMinute@]分钟就要上班了，别忘了打卡哟~");
    }

    /**
     * 签到消息摘要
     * @return
     */
    public String getSignMsgSummary()
    {
        return getProperty("attendance.sign.msg.summary",
            "还有[@remindMinute@]分钟就要上班了，别忘了打卡哟~");
    }

    /**
     * 待批异常审批单-消息标题
     * @return
     */
    public String getAuthorization()
    {
        return getProperty("attendance.authorization.msg.topic", "待批异常审批单");
    }

    /**
     * 待批异常审批单-消息内容
     * @return
     */
    public String getAuthorizationMsgContent()
    {
        return getProperty("attendance.authorization.msg.content",
                "您有新的审批单，请处理！");
    }

    /**
     * 待批异常审批单-消息摘要
     * @return
     */
    public String getAuthorizationMsgSummary()
    {
        return getProperty("attendance.authorization.msg.summary",
                "您有新的审批单，请处理！");
    }
    /**
     * 异常审批单已通过-消息标题
     * @return
     */
    public String getAuthorizationPass()
    {
        return getProperty("attendance.authorizationPass.msg.topic", "异常审批单已通过");
    }

    /**
     * 异常审批单通过-消息内容
     * @return
     */
    public String getAuthorizationPassMsgContent()
    {
        return getProperty("attendance.authorizationPass.msg.content",
                "请查收您的审批单！");
    }

    /**
     * 异常审批单通过-消息摘要
     * @return
     */
    public String getAuthorizationPassMsgSummary()
    {
        return getProperty("attendance.authorizationPass.msg.summary",
                "请查收您的审批单！");
    }

    /**
     * 异常审批单已拒绝-消息标题
     * @return
     */
    public String getAuthorizationReject()
    {
        return getProperty("attendance.authorizationReject.msg.topic", "异常审批单已拒绝");
    }

    /**
     * 异常审批单拒绝-消息内容
     * @return
     */
    public String getAuthorizationRejectMsgContent()
    {
        return getProperty("attendance.authorizationReject.msg.content",
                "请查收您的审批单！");
    }

    /**
     * 异常审批单拒绝-消息摘要
     * @return
     */
    public String getAuthorizationRejectMsgSummary()
    {
        return getProperty("attendance.authorizationReject.msg.summary",
                "请查收您的审批单！");
    }


    /**
     * 今日考勤日报-消息标题
     * @return
     */
    public String getDaily()
    {
        return getProperty("attendance.daily.msg.topic", "今日考勤日报");
    }


    /**
     * 今日考勤日报-消息内容
     * @return
     */
    public String getDailyMsgContent()
    {
        return getProperty("attendance.daily.msg.content",
                "请查收上午团队考勤日报！");
    }

    /**
     * 今日考勤日报-消息摘要
     * @return
     */
    public String getDailyMsgSummary()
    {
        return getProperty("attendance.daily.msg.summary",
                "请查收上午团队考勤日报！");
    }

    /**
     * 签退消息标题
     * @return
     */
    public String getSignOutMsgTopic()
    {
        return getProperty("attendance.signout.msg.topic", "下班打卡提醒");
    }

    /**
     * 签退消息内容
     * @return
     */
    public String getSignOutMsgContent()
    {
        return getProperty("attendance.signout.msg.content", "工作辛苦了，别忘了打卡哟~");
    }

    /**
     * 签退消息摘要
     * @return
     */
    public String getSignOutMsgSummary()
    {
        return getProperty("attendance.signout.msg.summary", "工作辛苦了，别忘了打卡哟~");
    }

    /**
     * 创新平台消息推送接口consumerKey
     * @return
     */
    public String getRcsConsumerKey()
    {
        return getProperty("attendance.rcs.consumerKey", "9");
    }

    /**
     * 创新平台消息推送接口consumerSecret
     * @return
     */
    public String getRcsConsumerSecret()
    {
        return getProperty("attendance.rcs.consumerSecret",
            "ba327bca4fa15b0b0f4207d1bc13def4");
    }

    /**
     * 创新平台消息推送接口appId
     * @return
     */
    public String getRcsAppId()
    {
        return getProperty("attendance.rcs.appId", "166");
    }

    /**
     * 创新平台消息推送接口appSecret
     * @return
     */
    public String getRcsAppSecret()
    {
        return getProperty("attendance.rcs.appSecret",
            "xq119bos15L0N91657I094NQD091R6c1");
    }

    /**
     * 获取创新平台消息推送接口接入码
     * @return
     */
    public String getRcsAccessNo()
    {
        return getProperty("attendance.rcs.accessNo", "125600400000047");
    }

    /**
     * 创新平台消息调用凭证缓存时间（默认1小时）
     * @return
     */
    public long getRcsMsgAccessTokenCacheTime()
    {
        return getPropertyLong("attendance.rcs.msg.accessToken.cachetime",
            60 * 60 * 1000L);
    }

    /**
     * 获取创新平台消息token申请接口的调用地址
     * @return
     * 线上  http://120.196.212.5/rcsoaplus-gateway/message/token
     */
    public String getRcsMsgAccessTokenUrl()
    {
//        return getProperty("attendance.rcs.msg.accessToken.url",
//            "http://58.248.29.7:8088/rcsoaplus-gateway/message/token");
        return getProperty("attendance.rcs.msg.accessToken.url",
                "http://120.196.212.5/rcsoaplus-gateway/message/token");
    }

    /**
     * 获取创新平台消息推送接口的调用地址
     * @return
     * 线上   http://120.196.212.5/rcsoaplus-gateway/message/sendByEuserId
     */
    public String getRcsMsgSendUrl()
    {
//        return getProperty("attendance.rcs.msg.send.url",
//            "http://58.248.29.7:8088/rcsoaplus-gateway/message/sendByEuserId");
        return getProperty("attendance.rcs.msg.send.url",
                "http://120.196.212.5/rcsoaplus-gateway/message/sendByEuserId");
    }

    /**  http://120.196.212.78:8080/atdc/login/ssoAttendance
     * 获取消息跳转详情页地址 http://120.196.212.78:8080
     * @return   http://120.196.212.78:8080
     */
    public String getRcsMsgDetailsPageUrl()
    {
        return getProperty("attendance.rcs.msg.details.pageurl",
            "http://120.196.212.78:8080/atdc/login/ssoAttendance");
    }

    /**
     * 获取每次批量生成的待发消息数（默认100）
     * @return
     */
    public int getGenerateMsgSplitSize()
    {
        return getPropertyInt("attendance.generate.msg.splitSize", 100);
    }

    /**
     * 获取文件下载临时基础目录
     * @return
     */
    public String getDownLoadTempBaseDir()
    {
        return getProperty("attend.download.file.dir",
            "/home/RCS/downLoadTempFile/");
    }

    /**
     * 获取文件下载接口服务的URL地址
     * @return
     */
    public String getDownLoadServerUrl()
    {
        return getProperty("attend.download.serverUrl",
            "http://121.15.167.235:10721/atdc/downLoad/downLoadAttendRept?");
    }

    /**
     * 按小时删除临时文件时，与当前时间相隔的小时内都不会进行删除
     * @return
     */
    public int getdownLoadDelDiffHours()
    {
        return getPropertyInt("attend.downLoadDelDiffHours", 2);
    }

    /**
     * 导出的最大数据量
     * @return
     */
    public int getMaxExportCount()
    {
        return getPropertyInt("attend.maxExportCount", 5000);
    }
    
    
    
    /**
     * 企业通讯录批量注册接口开关
     * @return
     */
    public int getQytxlRegisterFlag()
    {
        return getPropertyInt("attendance.qytxl.resFlag", 1);
    }
    /**
     * 统计线程池数
     * @return
     */
    public int getMultiThreadedPool()
    {
        return getPropertyInt("attendance.statistics.threaded.number", 20);
    }

    /**
     * 信息推送类型
     * @return
     */
    public String getMsgPullType()
    {
        return getProperty("msg.pull.type", "");
    }

}
