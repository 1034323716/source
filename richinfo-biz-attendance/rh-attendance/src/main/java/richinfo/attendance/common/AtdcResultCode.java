/**
 * 文件名：AtdcResultCode.java
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
 * 功能描述：错误码定义
 * 
 */
public interface AtdcResultCode extends ResultCode
{

    /**
     * 
     * 功能描述：会话类错误
     * 
     */
    public interface ATDC102
    {
        /** 系统类错误头 */
        String BASE = "ATDC102";

        /** 用户会话异常 */
        String USER_SESSION_ERROR = BASE + "001";

    }

    /**
     * 功能描述：参数类错误，ATDC104001-ATDC104999,用户提交的参数不合法等
     */
    public interface ATDC104
    {
        /** 参数类错误头 */
        String BASE = "ATDC104";

        /** 请求参数无效 */
        String PARAMS_INVALID = BASE + "001";

        /** 请求参数为空或者缺失 */
        String PARAMS_NULL = BASE + "002";

        /** 请求参数日期非法 */
        String PARAMS_DATEFORMAT_ERROR = BASE + "003";

        /** 请求参数日期非法 */
        String PARAMS_ILLEGAL_DATE = BASE + "004";

        /** 考勤组名称长度过长 */
        String ATTENDANCENAME_LENGTH_ILLEGAL = BASE + "005";

        /** uid参数传递非法 */
        String PARAMS_ILLEGAL_UID = BASE + "006";

        /** 参数传递非法 包含emoji表情 */
        String PARAMS_INCLUDE_EMOJI = BASE + "007";

        /** 参数传递非法,未查询到记录信息 */
        String PARAMS_ILLEGAL_NORECORD = BASE + "008";

        /** 考勤正常,无需申诉异常 */
        String PARAMS_ATTENDANCE_NORMAL = BASE + "009";

        /** 考勤组未设定审批员 */
        String ATTENDGROUP_NO_EXAMINER = BASE + "010";

        /** 异常申诉单不存在 */
        String APPEAL_NOT_EXISTS = BASE + "011";

        /** 异常申诉单已被审核完成，不能再撤销或再次审核 */
        String APPEAL_ALREADY_EXAMINE = BASE + "012";

        /** 异常申诉单已被撤销，不能再次撤销或审核 */
        String APPEAL_ALREADY_CANCEL = BASE + "013";

        /** 审批员不存在或暂未添加进考勤组 */
        String EXAMINEUID_NOT_EXISTS = BASE + "014";

        /** 审批员与考勤组不再同一企业 */
        String EXAMINEUID_NOTMATCH_ENTERID = BASE + "015";

        /** 考勤组未选择审批员 */
        String ATTEND_NOTADD_EXAMINER = BASE + "016";

        /** 审批员还有单未审批，不能修改审批员 */
        String EXAMINER_NOT_APPEAL = BASE + "017";

        /** 无权审批其他审批员的异常单 */
        String NOT_ALLOW_APPEAL = BASE + "018";

        /** 非审批员，无权操作审批员功能 */
        String NOT_EXAMINER = BASE + "019";

        /** 用户不能撤销其他人申请的异常申诉单 */
        String NOT_CANCEL_OTHER_APPEAL = BASE + "020";

        /** 异常申诉班次参数非法 */
        String APPEAL_RECORD_ILLEGAL = BASE + "021";

        /** 审批理由长度过长 */
        String APPEAL_LENGTH_ILLEGAL = BASE + "022";

        /** 审批员还有单未审批，不能删除考勤组 */
        String GROUP_NOTALLOW_DEL = BASE + "023";
        /** 考勤组已经删除 */
        String GROUP_DEL = BASE + "024";
        /** 部门企业不符合 */
        String DEPARTMENT_NO_ENTER = BASE + "025";

        /**联系人不存在*/
        String EMPLOYEE_IS_NOT_EXIST_CODE = "026";

        /**无待处理名单人员*/
        String NO_DATA_NEEDS_DUEL_CODE = "027";

        /**设置弹性跨度不得超过次日凌晨*/
        String NOT_ALLOWED_OVER_DAY = BASE+"028";

        /**设置弹性跨度上午班次不得超过下午班次*/
        String NOT_ALLOWED_COVER_AFTERNOON= BASE+"029";
    }

    /**
     * 
     * 功能描述：安全类错误
     * 
     */
    public interface ATDC106
    {
        /** 安全类错误头 */
        String BASE = "ATDC106";

        /** 考勤组名称冲突 */
        String ATTEND_NAME_CONFLICT = BASE + "001";

        /** 下午班次时间早于上午班次时间 */
        String PM_EARLIER_AM = BASE + "002";

        /** 时间格式错误 */
        String TIME_FORMAT_ERROR = BASE + "003";

        /** 考勤组不属于执行操作的管理员 */
        String NOT_BELONGTO_ADMIN = BASE + "004";

        /** 编辑、删除考勤组时，传递的考勤组不存在 */
        String ATTENDANCEGROUP_NOT_EXISTS = BASE + "005";

        /** 考勤组不匹配 */
        String ATTEND_GROUP_MISMATCH = BASE + "006";

        /** 传递的企业Id与会话信息中的企业Id不匹配 */
        String ENTERID_NOT_MATCHED = BASE + "007";

        /** 非管理员，无权操作 */
        String NOT_ADMIN = BASE + "008";

        /** 请求的uid和会话信息的企业ID不匹配 */
        String UID_NOT_MATCHED = BASE + "009";

    }

    /**
     * 
     * 功能描述：系统类错误
     * 
     */
    public interface ATDC107
    {
        /** 系统类错误头 */
        String BASE = "ATDC107";

        /** 数据持久化异常 */
        String DATA_PERSISTENCE_ERROR = BASE + "001";

        /** 打卡失败 */
        String CLOCK_ERROR = BASE + "002";

        /** 考勤异常单撤销失败 */
        String APPEAL_CANCEL_FAIL = BASE + "003";

        /** 考勤异常单审核失败 */
        String APPEAL_EXAMINE_FAIL = BASE + "004";

        /** 考勤异常申诉单申诉失败 */
        String ATTEND_APPEAL_FAIL = BASE + "005";

        /** 打卡失败 */
        String CLOCK_ERROR_DATE = BASE + "006";
    }

    /**
     * 
     * 功能描述：业务数据操作类提示
     * 
     */
    public interface ATDC108
    {
        /** 系统类错误头 */
        String BASE = "ATDC108";

        /** 暂无数据可供导出 */
        String NO_EXPORT_DATA = BASE + "001";

        /** 暂无异常审批单信息 */
        String NOT_APPEAL_DATA = BASE + "002";

        /** 无数据 */
        String NO_DATA = BASE + "003";

        /** 用管凭证校验失败 */
        String ARTIFACT_VALID_FAIL = BASE + "004";

        /** 用管凭证校验 check请求参数校验失败 */
        String ARTIFACT_SIGN_VALID = BASE + "005";

        /** 文件不存在 */
        String FILE_NOT_FOUND = BASE + "006";

        /** 考勤PC端报表下载，base64参数解析失败，这种情况一般发生在恶意用户故意修改参数想遍历所致 */
        String PARAM_PARSE_FAILED = BASE + "007";

        /** 导出数据量过大，需分批处理 */
        String SO_MANY_EXPORT_DATA = BASE + "008";
    }
}
