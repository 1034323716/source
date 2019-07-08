/**
 * 文件名：AtdcResultSummary.java
 * 创建日期： 2017年6月2日
 * 作者：     liuyangfei
 * Copyright (c) 2016-2017 邮箱开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2017年6月2日
 *   修改人：liuyangfei
 *   修改内容：
 */
package richinfo.attendance.common;

/**
 * 功能描述：错误码对应的描述
 * 
 */
public interface AtdcResultSummary
{

    /**
     * 
     * 功能描述：会话类错误，ATDC102001-ATDC102999，用户会话异常等
     * 
     */
    public interface ATDC102
    {
        /** 用户会话异常 */
        String USER_SESSION_ERROR = "用户会话异常";

        String QYTXL_SESSION_ERROR = "连接企业通讯录失败";

    }

    /**
     * 功能描述： 错误码定义，参数类错误，ATDC104001-ATDC104999,用户提交的参数不合法等
     */
    public interface ATDC104
    {
        /** 请求参数无效 */
        String PARAMS_INVALID = "请求参数无效";

        /** 请求参数为空或者缺失 */
        String PARAMS_NULL = "请求参数为空或者缺失";

        /** 考勤人员列表为空 */
        String EMPLOEES_IS_EMPTY = "考勤人员列表为空";

        /** 考勤组已经删除 */
        String PARAMS_GROUP_DEL = "考勤组已经删除，不能提交审批！";

        /** 请求参数日期非法 */
        String PARAMS_DATEFORMAT_ERROR = "请求参数日期非法";

        /** 请求参数日期非法 */
        String PARAMS_ILLEGAL_DATE = "请求参数日期非法";
        /** 异常审批单不能当天提交 */
        String PARAMS_AUTHORIZATION_DATE = "不允许提交当天的考勤异常审批单";

        /** 考勤组名称长度过长 */
        String ATTENDANCENAME_LENGTH_ILLEGAL = "考勤组名称长度超出限制大小";

        /** uid参数传递非法 */
        String PARAMS_ILLEGAL_UID = "用户参数传递非法";

        /** 参数传递非法 包含emoji表情 */
        String PARAMS_INCLUDE_EMOJI = "不支持表情符号，请检查后重试";

        /** 参数传递非法,未查询到记录信息 */
        String PARAMS_ILLEGAL_NORECORD = "未查询到记录信息";

        /** 考勤正常,无需申诉异常 */
        String PARAMS_ATTENDANCE_NORMAL = "考勤记录正常，不需异常申诉";

        /** 考勤组未设定审批员 */
        String ATTENDGROUP_NO_EXAMINER = "考勤组未设定审批员";

        /** 异常申诉单不存在 */
        String APPEAL_NOT_EXISTS = "异常申诉单记录不存在";

        /** 异常申诉单已被审核完成，不能再撤销或再次审核 */
        String APPEAL_ALREADY_EXAMINE = "异常申诉单已完成审核，不能撤销或再次审核";

        /** 异常申诉单已被撤销，不能再次撤销或审核 */
        String APPEAL_ALREADY_CANCEL = "异常申诉单已撤销，不能再次撤销或审核";

        /** 审批员不存在或暂未添加进考勤组 */
        String EXAMINEUID_NOT_EXISTS = "审批员不存在或暂未加入考勤组";

        /** 审批员与考勤组不再同一企业 */
        String EXAMINEUID_NOTMATCH_ENTERID = "不能跨企业选择审批员";

        /** 考勤组未选择审批员 */
        String ATTEND_NOTADD_EXAMINER = "考勤组未选择审批员";

        /** 审批员还有单未审批，不能修改审批员 */
        String EXAMINER_NOT_APPEAL = "审批员还有异常单未审核，不能修改审批员";

        /** 无权审批其他审批员的异常单 */
        String NOT_ALLOW_APPEAL = "无权审核其他审批员考勤异常申诉单";

        /** 非审批员，无权操作审批员功能 */
        String NOT_EXAMINER = "非审批员，无权操作审批员功能 ";

        /** 用户不能撤销其他人申请的异常申诉单 */
        String NOT_CANCEL_OTHER_APPEAL = "无权撤销他人考勤异常申诉单";

        /** 异常申诉班次参数非法 */
        String APPEAL_RECORD_ILLEGAL = "申诉班次参数非法";

        /** 审批理由长度过长 */
        String APPEAL_LENGTH_ILLEGAL = "申诉原因长度过长";

        /** 审批员还有单未审批，不能删除考勤组 */
        String GROUP_NOTALLOW_DEL = "审批员还有未审核单，不能删除考勤组";
        /** 不能加入其它企业的考勤组 */
        String DEPARTMENT_NO_ENTER = "不予操作其它企业考勤组";

		String ATTEND_SITE_SIZE = "只能设置10个考勤地址";

        String EMPLOYEE_IS_NOT_EXIST = "联系人不存在";

        String NO_DATA_NEEDS_DUEL = "请选择人员名单";

        String SETTING_SUCCESS = "设置成功";

        String SETTING_FAIL = "设置失败";

        String NOT_ALLOWED_OVER_DAY = "设置弹性跨度不得超过次日凌晨";

        String NOT_ALLOWED_COVER_AFTERNOON = "设置弹性跨度上午班次不得超过下午班次";
    }

    /**
     * 功能描述： 安全类错误描述
     */
    public interface ATDC106
    {
        /** 考勤组名称冲突 */
        String ATTEND_NAME_CONFLICT = "考勤组名称不能重复";

        /** 下午班次时间早于上午班次时间 */
        String PM_EARLIER_AM = "下班时间不能早于上班时间 ";

        /** 时间格式错误 */
        String TIME_FORMAT_ERROR = "时间格式错误";

        /** 考勤组不属于执行操作的管理员 */
        String NOT_BELONGTO_ADMIN = "考勤组不属于管理员";

        /** 编辑、删除考勤组时，传递的考勤组不存在 */
        String ATTENDANCEGROUP_NOT_EXISTS = "考勤组不存在";

        /** 考勤组不匹配 */
        String ATTEND_GROUP_MISMATCH = "考勤组不匹配，请重选";

        /** 传递的企业Id与会话信息中的企业Id不匹配:不支持跨企业操作，请重选 */
        String ENTERID_NOT_MATCHED = "不支持跨企业操作，请重选";

        /** 非管理员，无权操作 */
        String NOT_ADMIN = "非管理员或负责人,无权操作";

        /** 请求的uid和会话信息的企业ID不匹配:无权操作其他用户信息，请重选 */
        String UID_NOT_MATCHED = "无权操作其他用户信息，请重选";

    }

    /**
     * 
     * 功能描述：系统类错误
     * 
     */
    public interface ATDC107
    {
        /** 系统错误:操作失败，请重试 */
        String S_ERROR = "操作失败，请重试";

        /** 数据持久化异常:系统繁忙，请重试 */
        String DATA_PERSISTENCE_ERROR = "系统繁忙，请重试";

        /** 打卡失败:打卡失败，请重试 */
        String CLOCK_ERROR = "打卡失败，请重试";

        /** 考勤异常单撤销失败 */
        String APPEAL_CANCEL_FAIL = "撤销失败,请重试";

        /** 考勤异常单审核失败 */
        String APPEAL_EXAMINE_FAIL = "审核失败，请重试";

        /** 考勤异常申诉单申诉失败 */
        String ATTEND_APPEAL_CAP = "申诉失败，申诉次数已上限";

        /** 考勤异常申诉单申诉上限 */
        String ATTEND_APPEAL_FAIL = "申诉失败，请重试";
        /** 连接企业通讯录失败 obtain*/
        String QYTXL_OBTAIN_FAIL = "企业通讯录获取失败";
        /**/
        String CLOCK_ERROR_DATE = "30秒内不能再次打卡哦";
    }

    /**
     * 
     * 功能描述：业务数据操作类提示
     * 
     */
    public interface ATDC108
    {
        /** 暂无数据可供导出 */
        String NO_EXPORT_DATA = "对应时间无统计数据不能导出";

        /** 暂无数据可供导出 */
        String EXCEL_FAILURE = "导出报表失败，请稍后再试！";

        /** 暂无异常审批单信息 */
        String NOT_APPEAL_DATA = "暂无异常审批单信息";

        /** 无数据 */
        String NO_DATA = "暂无可查询数据";

        /** 用管凭证校验失败 */
        String ARTIFACT_VALID_FAIL = "用管凭证校验失败";

        /** 用管凭证校验 check请求参数校验失败 */
        String ARTIFACT_SIGN_VALID = "用管凭证签名校验错误";

        /** 下载文件不存在 */
        String FILE_NOT_FOUND = "下载文件不存在";
        
        /** 考勤PC端报表下载，base64参数解析失败，这种情况一般发生在恶意用户故意修改参数想遍历所致 */
        String PARAM_PARSE_FAILED = "文件下载Base64解析参数失败";
        
        /** 导出数据量过大，需分批处理 */
        String SO_MANY_EXPORT_DATA = "导出数据量过大,请分批进行导出";
    }

}
