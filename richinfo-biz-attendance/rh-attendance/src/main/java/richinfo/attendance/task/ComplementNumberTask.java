package richinfo.attendance.task;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import richinfo.attendance.dao.AttendEmployeeDao;
import richinfo.attendance.dao.AttendGroupDao;
import richinfo.attendance.entity.AttendEmployee;
import richinfo.attendance.entity.AttendGroup;
import richinfo.attendance.util.*;
import richinfo.bcomponet.tasks.Task;
import richinfo.bcomponet.tasks.TaskContext;
import richinfo.bcomponet.tasks.exception.TaskException;

import java.util.*;

/**
 * Created by qiang on 2019/4/22.
 */
public class ComplementNumberTask extends Task {
    private Logger logger = LoggerFactory.getLogger(ComplementNumberTask.class);
    private AttendEmployeeDao attendEmployeeDao = new AttendEmployeeDao();
    private AttendGroupDao attendGroupDao = new AttendGroupDao();
    @Override
    public void repeat(TaskContext context) throws TaskException {
        logger.info("ComplementNumberTask  start {}", TimeUtil.date2String(new Date(),TimeUtil.BASE_DATETIME_FORMAT));
        complementNumber();
    }

    /**
     * 补充号码
     */
    private void complementNumber()   {

        logger.info("complementNumber start >>>>>>>>");
        //设置一个结果
        boolean  result = true;
        int startNumber = 0;
        int endNumber = 10;
        while (result) {
            Map<String,Integer> paramMap = new HashMap<>();
            paramMap.put("startNumber",startNumber);
            paramMap.put("endNumber",endNumber);
            //获取正在使用的考勤组
            List<String> groups = attendGroupDao.findGroupInNormal(paramMap);
            if (groups == null ||  groups.size() == 0){
                result = false;
                logger.info("complementNumber return finish  ========={}  ",result);
                break;
            }

            if (groups.size() < endNumber){
                logger.info("findEmployeeNoPhone return group size={}", groups.size());
                result = false;
            }
            //设置下一分页查询
            startNumber += endNumber;

            for (String  attendanceId : groups){
                //根据考勤组获取没有手机号码的考勤人员
                List<AttendEmployee> attendEmployees = attendEmployeeDao.findEmployeeNoPhone(attendanceId);
                //为空停止查询
                if (attendEmployees == null || attendEmployees.size() == 0) {
                    continue;
                }
                logger.info("findEmployeeNoPhone return size={}", attendEmployees.size());
                List<AttendEmployee> complementEmployees = new ArrayList<>();

                for (AttendEmployee attendEmployee : attendEmployees) {
                    //contactId 为空
                    if (StringUtils.isBlank(attendEmployee.getContactId())) {
                        try {
                            Map<String, Object> repMap = QytxlUtil.getInstance().getContactIdByEuserId(attendEmployee.getEnterId(), attendEmployee.getUid());
                            if (0 == (int) repMap.get("error_code")) {
                                List<Map<String, String>> userList = (List<Map<String, String>>) repMap.get("contactInfo");
                                //获取用户contactId
                                String contactId = userList.get(0).get("contactId");
                                logger.info("getContactIdByEuserId uid={}|enterId={}|contactId={}",attendEmployee.getUid(),attendEmployee.getEnterId(),attendEmployee.getContactId());
                                attendEmployee.setContactId(contactId);
                                //获取手机号码
                                String regMobile = getQytxlItem(attendEmployee);
                                if (StringUtils.isBlank(regMobile)) {
                                    continue;
                                }
                                attendEmployee.setPhone(regMobile);
                                complementEmployees.add(attendEmployee);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            logger.error("getItem/getContactIdByEuserId==========》uid={}|ContactId={}|enterId={}|msg={}", attendEmployee.getUid(),attendEmployee.getContactId(), attendEmployee.getEnterId(), e.getMessage());

                        }
                        //获取手机号码
                    } else {
                        try {
                            String regMobile = getQytxlItem(attendEmployee);
                            if (StringUtils.isBlank(regMobile)) {
                                continue;
                            }
                            attendEmployee.setPhone(regMobile);
                            complementEmployees.add(attendEmployee);


                        } catch (Exception e) {
                            e.printStackTrace();
                            logger.error("getItem==========》ContactId={}|enterId={}|msg={}", attendEmployee.getContactId(), attendEmployee.getEnterId(), e.getMessage());

                        }
                    }
                }
                if (complementEmployees.size() != 0){
                    attendEmployeeDao.batchSaveComplementEmployee(complementEmployees);
                }
            }
        }
        logger.info("complementNumber  end >>>>>>>>>>" );
    }

    private String getQytxlItem(AttendEmployee attendEmployee) throws Exception {
        Map<String, Object> repMap = QytxlUtil.getInstance().getItem(attendEmployee.getEnterId(), attendEmployee.getContactId());
        if (0 == (Double) repMap.get("error_code")){
            Map<String,Object> itemMap = (Map<String, Object>) repMap.get("item");
            String regMobile =  (String)itemMap.get("regMobile");
            regMobile =  AesUtils.decrypt(regMobile, AttendanceConfig.getInstance().getProperty("attend.qytxl.aes_key", "6af15ca383ee45dd"));
            logger.info("decrypt    ContactId={}|enterId={}|regMobile={}",attendEmployee.getContactId(), attendEmployee.getEnterId(),regMobile);
            return regMobile;
        }else {
            logger.error("获取失败==========》ContactId={}|enterId={}|msg={}", attendEmployee.getContactId(), attendEmployee.getEnterId(),repMap.get("error_msg"));
            return null;
        }
    }

    public static void  main(String[]sges){
        System.out.print(0 == 0.0);
    }
}
