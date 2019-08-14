package richinfo.attendance.bean;

import richinfo.attendance.common.ResBean;
import richinfo.attendance.entity.AttendWhitelistEntity;

import java.util.List;

/**
 * Created by Daniel on 2018/9/18.
 */
public class AttendWhiteListRes extends ResBean {

    private List<AttendWhitelistEntity> employees;

    public List<AttendWhitelistEntity> getEmployees() {
        return employees;
    }

    public void setEmployees(List<AttendWhitelistEntity> employees) {
        this.employees = employees;
    }
}
