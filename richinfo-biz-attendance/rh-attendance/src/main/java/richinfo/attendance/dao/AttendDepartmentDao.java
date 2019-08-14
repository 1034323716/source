package richinfo.attendance.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import richinfo.attendance.entity.AttendDepartmentChooser;
import richinfo.attendance.util.AssertUtil;
import richinfo.dbcomponent.exception.PersistException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by qiang on 2018/9/10.
 * 部门dao
 */
public class AttendDepartmentDao extends BaseAttendanceDao {

    private Logger logger = LoggerFactory.getLogger(AttendGroupDao.class);


    /**
     * 查询已经在其他考勤组的DepartmentId列表
     * @param attendDepartmentChoosers
     * @return
     */
    public List<String> queryDepartmentInGroups(List<AttendDepartmentChooser> attendDepartmentChoosers)  {
        if (AssertUtil.isEmpty(attendDepartmentChoosers))
        {
            logger.warn("attendDepartmentChooser List is empty.");
            return null;
        }

        @SuppressWarnings("unchecked")
        List<String> departmentIds= null;
        try {
            departmentIds = attendanceDao.queryForList(
                    "attendance.queryDepartmentInGroups", attendDepartmentChoosers);
        } catch (PersistException e) {
            e.printStackTrace();
            logger.error("queryDepartmentInGroups error attendDepartmentChoosers={}|e={}",attendDepartmentChoosers,e);
        }

        return departmentIds;
    }

    /**
     * 根据企业id 获取所有考勤组的部门选择器
     * @param enterId
     * @return
     */
    public List<AttendDepartmentChooser> queryEnterDepartmentChooser(String enterId) {
        try
        {
            return attendanceDao.queryForList("attendance.queryEnterDepartmentChooser", enterId);
        }
        catch (PersistException e)
        {
            logger
                    .error("queryEnterDepartmentChooser error.enterId={}", enterId, e);
            return null;
        }
    }

    /**
     * 根据部门id 考勤组id获取部门选择器
     * @param departmentId
     * @param attendanceId
     * @return
     */
    public AttendDepartmentChooser queryDepartmentInDepartmentIdAttendanceId(String departmentId, long attendanceId) {
        Map<String,Object>map = new HashMap<>();
        map.put("departmentId",departmentId);
        map.put("attendanceId",attendanceId);
        try {
          return (AttendDepartmentChooser)attendanceDao.queryForObject("attendance.queryDepartmentInDepartmentIdAttendanceId",map);
        } catch (PersistException e) {
            e.printStackTrace();
            logger.error("queryDepartmentInDepartmentIdAttendanceId error departmentId={}|attendanceId={}|e={}",departmentId,attendanceId,e);
        }
        return null;
    }

    /**
     * 根据考勤组id获取原有的部门id
     * @param attendanceId
     * @return
     */
    public List<String> queryDepartmentIdByAttendanceId(long attendanceId) {
        try {
            return attendanceDao.queryForList("attendance.queryDepartmentIdByAttendanceId",attendanceId);
        } catch (PersistException e) {
            e.printStackTrace();
            logger.error("queryDepartmentIdByAttendanceId error attendanceId={}|e={}",attendanceId,e);
        }
        return null;
    }
}
