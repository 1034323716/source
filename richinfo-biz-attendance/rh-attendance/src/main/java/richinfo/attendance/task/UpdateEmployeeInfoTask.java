package richinfo.attendance.task;


import richinfo.attendance.dao.AttendEmployeeDao;
import richinfo.attendance.entity.AttendEmployee;
import richinfo.attendance.util.AssertUtil;
import richinfo.attendance.util.AttendanceConfig;
import richinfo.attendance.util.AttendanceUtil;
import richinfo.attendance.util.QytxlUtil;
import richinfo.bcomponet.tasks.Task;
import richinfo.bcomponet.tasks.TaskContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UpdateEmployeeInfoTask extends Task {

    private AttendEmployeeDao employeeDao = new AttendEmployeeDao();


    @Override
    public void repeat(TaskContext context) {

        if (!AttendanceConfig.getInstance().isTaskServer()) {
            logger.info("not task server,ignore .UpdateEmployeeInfoTask");
            return;
        }
        logger.debug("人员信息更新定时任务启动");

        updateEmployeeInfo();

        logger.debug("人员信息更新定时任务结束");

    }

    private boolean updateEmployeeInfo() {

        List<AttendEmployee> attendEmployees = employeeDao.queryEmployeeDeptNull();
        logger.info("查询需修改人员数量:{}",attendEmployees.size());
        int count = 0;
        for (AttendEmployee emp : attendEmployees) {
            Map<String, Object> empMap = null;
            try {
                empMap = QytxlUtil.getInstance().getItem(emp.getEnterId(), emp.getContactId());
                logger.info("通讯录人员查询成功");
            } catch (Exception e) {
                logger.info("查询通讯录人员信息失败");
                continue;
            }
            if ((double) empMap.get("error_code") != 0.0) {
                logger.info("企业不存在{}", empMap);
                continue;
            }
            if (AssertUtil.isEmpty((Map<String, Object>) empMap.get("item"))){
                logger.info("通讯录此人员不存在{}", empMap);
                continue;
            }
            Map<String, Object> item = (Map<String, Object>) empMap.get("item");
            emp.setDeptId((String) item.get("departmentId"));
            emp.setDeptName((String) item.get("departmentName"));
            employeeDao.updateEmployeeDept(emp);
            count++;
        }
        logger.debug("修改人数:{} 修改成功人数{}", attendEmployees.size(),count);
        return true;
    }

}
