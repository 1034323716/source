package richinfo.attendance.entity;

import org.apache.commons.codec.digest.DigestUtils;
import richinfo.attendance.bean.UmcArtifactValidRes;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * 
 * 功能描述：考勤系统用户对象Attendance User
 * 
 */
public class UserInfo implements Serializable
{

    private static final long serialVersionUID = -9135407930745028444L;

    /** 用户唯一Id，对应和通讯录系统唯一企业联系人id */
    private String uid;
    
    /** 用户联系人contactId，对应和通讯录企业联系人contactId */
    private String contactId;

    /** 用户token 用户经过统一认证验证后的会话usersessionId */
    private String token;

    /** 考勤组Id，显示设置默认值为0，判断考勤人员是否重复时，不在考勤组时，返回ID为0 */
    private long attendanceId = 0L;

    /** 企业Id */
    private String enterId;

    /** 企业名称 */
    private String enterName;

    /** 员工姓名 */
    private String employeeName;

    /** 手机号 */
    private String phone;

    /** 部门编号 */
    private String deptId;

    /** 部门名称 */
    private String deptName;

    /** 邮箱 */
    private String email;

    /** 职位 */
    private String position;

    /** 状态标识 0：正常 */
    private int status;

    /** 管理员标识 1是0否 */
    private int isAdmin;

    /** 审批员标识 1：是 0：否 */
    private int examinerState;

    /** 炒鸡管理员标识 1是0否 */
    private int aM;

    /**
     * 白名单用户标识  0否 1是
     * 默认 否0
     */
    private int whitelistStatus = 0;

    /**
     * 登录时间
     */
    private long loginupdatetime;

    /**
     * 缓存更新时间
     */
    private Long cacheupdatetime;
    
    /**
     * PC端校验凭证返回用户节点信息
     */
    private UmcArtifactValidRes umcArtifactValidRes;

    /**
     * 角色类型 1为考勤组负责
     */
    private Integer roleType;

    /**
     * 短信开关设置   0:关闭  1:开启
     */
    private Integer smsSwitch;

    public Integer getSmsSwitch() {
        return smsSwitch;
    }

    public void setSmsSwitch(Integer smsSwitch) {
        this.smsSwitch = smsSwitch;
    }

    public Integer getRoleType() {
        return roleType;
    }

    public void setRoleType(Integer roleType) {
        this.roleType = roleType;
    }

    public int getaM() {
        return aM;
    }

    public void setaM(int aM) {
        this.aM = aM;
    }

    /** 职位 */
    private String attendanceName;

    public String getAttendanceName() {
        return attendanceName;
    }

    public void setAttendanceName(String attendanceName) {
        this.attendanceName = attendanceName;
    }

    public UmcArtifactValidRes getUmcArtifactValidRes() {
		return umcArtifactValidRes;
	}

	public void setUmcArtifactValidRes(UmcArtifactValidRes umcArtifactValidRes) {
		this.umcArtifactValidRes = umcArtifactValidRes;
	}

	public int getExaminerState()
    {
        return examinerState;
    }

    public void setExaminerState(int examinerState)
    {
        this.examinerState = examinerState;
    }

    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }   

    public String getContactId()
    {
        return contactId;
    }

    public void setContactId(String contactId)
    {
        this.contactId = contactId;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public long getAttendanceId()
    {
        return attendanceId;
    }

    public void setAttendanceId(long attendanceId)
    {
        this.attendanceId = attendanceId;
    }

    public String getEnterId()
    {
        return enterId;
    }

    public void setEnterId(String enterId)
    {
        this.enterId = enterId;
    }

    public String getEnterName()
    {
        return enterName;
    }

    public void setEnterName(String enterName)
    {
        this.enterName = enterName;
    }

    public String getEmployeeName()
    {
        return employeeName;
    }

    public void setEmployeeName(String employeeName)
    {
        this.employeeName = employeeName;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getDeptId()
    {
        return deptId;
    }

    public void setDeptId(String deptId)
    {
        this.deptId = deptId;
    }

    public String getDeptName()
    {
        return deptName;
    }

    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPosition()
    {
        return position;
    }

    public void setPosition(String position)
    {
        this.position = position;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public long getLoginupdatetime()
    {
        return loginupdatetime;
    }

    public void setLoginupdatetime(long loginupdatetime)
    {
        this.loginupdatetime = loginupdatetime;
    }

    public Long getCacheupdatetime()
    {
        return cacheupdatetime;
    }

    public void setCacheupdatetime(Long cacheupdatetime)
    {
        this.cacheupdatetime = cacheupdatetime;
    }

    public int getIsAdmin()
    {
        return isAdmin;
    }

    public void setIsAdmin(int isAdmin)
    {
        this.isAdmin = isAdmin;
    }

    public int getWhitelistStatus() {
        return whitelistStatus;
    }

    public void setWhitelistStatus(int whitelistStatus) {
        this.whitelistStatus = whitelistStatus;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
            "uid='" + uid + '\'' +
            ", contactId='" + contactId + '\'' +
            ", token='" + token + '\'' +
            ", attendanceId=" + attendanceId +
            ", enterId='" + enterId + '\'' +
            ", enterName='" + enterName + '\'' +
            ", employeeName='" + employeeName + '\'' +
            ", phone='" + phone + '\'' +
            ", deptId='" + deptId + '\'' +
            ", deptName='" + deptName + '\'' +
            ", email='" + email + '\'' +
            ", position='" + position + '\'' +
            ", status=" + status +
            ", isAdmin=" + isAdmin +
            ", examinerState=" + examinerState +
            ", aM=" + aM +
            ", whitelistStatus=" + whitelistStatus +
            ", loginupdatetime=" + loginupdatetime +
            ", cacheupdatetime=" + cacheupdatetime +
            ", umcArtifactValidRes=" + umcArtifactValidRes +
            ", roleType=" + roleType +
            ", smsSwitch=" + smsSwitch +
            '}';
    }

    public static void main(String[]str){
        HashMap<String,String> Map = new HashMap<String,String>();
        Map.put("app_key","d7eec29775ca42a894ab3ce432667e70");
        Map.put("once","089455837791");
        Map.put("version","2.0");
        Map.put("channel","d7eec29775ca42a894ab3ce432667e70");
        Map.put("sdk_from","java");
        Map.put("enterpriseId","7188935");
        Map.put("departmentId","1024718");
        String signature = getNornmalSignature(Map, "ca7dc22b57fa45a7a6a8eb89a3dc7b49");
        System.out.println(signature);
    }
    public static String getNornmalSignature(HashMap<String, String> params, String appscret) {
        String signature = null;
        Map<String, String> sortedParams = new TreeMap<String, String>(params);
        Set<Map.Entry<String, String>> entrys = sortedParams.entrySet();
        StringBuilder basestring = new StringBuilder();
        for (Map.Entry<String, String> param : entrys) {
            basestring.append(param.getKey())
                    .append("=")
                    .append(param.getValue() == null ? "" : param.getValue());
        }
        basestring.append(appscret);
        try {
            signature = DigestUtils.shaHex(basestring.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return signature;
    }
}
