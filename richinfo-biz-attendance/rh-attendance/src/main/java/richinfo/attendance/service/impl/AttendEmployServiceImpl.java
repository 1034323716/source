package richinfo.attendance.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import richinfo.attendance.dao.AttendEmployeeDao;
import richinfo.attendance.dao.MessageDao;
import richinfo.attendance.service.AttendEmployService;

/**
 * 功能描述：考勤员工功能逻辑实现类
 */
public class AttendEmployServiceImpl implements AttendEmployService {
    private Logger logger = LoggerFactory
        .getLogger(AttendAppealServiceImpl.class);

    private AttendEmployeeDao employeeDao = new AttendEmployeeDao();
    private MessageDao messageDao = new MessageDao();

    @Override
    public boolean settingSMSRemider(String uid, int status) {
        int modifyLine = employeeDao.updateEmployeeSMSSwitch(uid, status);
        //需要修改待发消息表对应的数据
        if(modifyLine == 1) {
            messageDao.updateMessageBySSMSwitch(uid, status);
        }
        return modifyLine == 1 ? true : false;
    }

    @Override
    public int getSMSRemiderStatus(String uid) {
        return employeeDao.getEmployeeSMSSwitch(uid);
    }
}
