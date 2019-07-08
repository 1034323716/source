/**
 * 文件名：QytxlServiceImpl.java
 * 创建日期： 2018年6月9日
 * 作者：     黄学振
 * Copyright (c) 2009-2011 无线开发室
 * All rights reserved.
 
 */
package richinfo.attendance.service.impl;



import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import richinfo.attendance.bean.AttendEnterRes;
import richinfo.attendance.bean.RegisterCallBackResBean;
import richinfo.attendance.dao.EnterDao;
import richinfo.attendance.service.QytxlService;
import richinfo.attendance.util.EnterpriseUtil;
import richinfo.attendance.util.QytxlUtil;
import richinfo.attendance.util.AssertUtil;
import richinfo.attendance.util.JsonUtil;


/**
 * 功能描述：企业通讯录回调接口实现类
 * 
 */
public class QytxlServiceImpl implements QytxlService
{
	private Logger logger = LoggerFactory
	        .getLogger(QytxlServiceImpl.class);
	
	private EnterDao enterDao = new EnterDao();
    @Override
	public void registerAll() {
    	List<AttendEnterRes> enters = enterDao.queryUnregisteredEnters();
    	if(AssertUtil.isNotEmpty(enters)&&enters.size()>0)
    	{
    		for(AttendEnterRes enter : enters)
    		{
    			String result = EnterpriseUtil.getRregisterCallBack(enter.getEnterId());
    			logger.info("getRregisterCallBack result={}",result);
    			RegisterCallBackResBean res = JsonUtil.jsonToBean(result, RegisterCallBackResBean.class);
    			res.setEventType("event_user_remove");
    			if(res.getError_code().equals("0")){
    				enterDao.updateEnterStatus(enter.getEnterId(), 1,res.getEventType());
    			}
    		}
    	}
		
	}

	@Override
	public RegisterCallBackResBean register(String enterId,String method,String eventType ) {
		try {
			RegisterCallBackResBean resBean = QytxlUtil.getInstance().registerCallBack(enterId,method,eventType);
			if(resBean.getError_code().equals("0"))
			{
				if(AssertUtil.isNotEmpty(eventType))
				{
					resBean.setEventType(eventType);
				}else
				{
					resBean.setEventType("event_user_remove");
				}
				logger.info("Qytxl register is Success!|enterId={}",enterId);
			}else{
				logger.error("Qytxl register is Success!|enterId={}|error_Code={}|error_msg={}",
						enterId,resBean.getError_code(),resBean.getError_msg());
			}
			return resBean;
			
		} catch (NoSuchAlgorithmException e) {
			logger.error("Qytxl register is error!|enterId={}",enterId,e);
			return null;
		}
		
	}
	
	@Override
	public void registerQytxl(String enterId) {
			List<AttendEnterRes> attendEnterRes = enterDao.queryEnterByEnterId(enterId);
			logger.info("queryEnterByEnterId query data attendEnterRes={}",attendEnterRes);
			//判断数据库是否有数据
			 if (AssertUtil.isNotEmpty(attendEnterRes)){

                 checkEnterRegister(attendEnterRes,enterId);
             }else {

                    //注册企业删除回调
                     typeCallBack(enterId,"event_corp_remove");
                    //注册部门删除回调
                     typeCallBack(enterId,"event_dept_remove");
                    //注册人员删除回调
                     typeCallBack(enterId,"event_user_remove");
                     //注册人员更新回调
                     typeCallBack(enterId,"event_user_modify");
				}

	}

    /**
     * 循环校验企业是否注册
     *没有注册的  进行注册
     */
	public void checkEnterRegister(List<AttendEnterRes> attendEnterRes,String enterId){

        String  event_corp_remove = null;
        String  event_dept_remove = null;
        String  event_user_remove = null;
        String  event_user_modify = null;
        for (AttendEnterRes attendEnter : attendEnterRes){

            if ("event_corp_remove".equals(attendEnter.getEventType())){
                event_corp_remove = "event_corp_remove";
            }else if ("event_dept_remove".equals(attendEnter.getEventType())){
                event_dept_remove = "event_dept_remove";
            }else if ("event_user_remove".equals(attendEnter.getEventType())){
                event_user_remove = "event_user_remove";
            }else if ("event_user_modify".equals(attendEnter.getEventType())){
                event_user_modify = "event_user_modify";
            }
        }

        if (StringUtils .isBlank(event_corp_remove)){
            //注册企业删除回调
            typeCallBack(enterId,"event_corp_remove");

        }else if (StringUtils .isBlank(event_dept_remove)){
            //注册部门删除回调
            typeCallBack(enterId,"event_dept_remove");
        }
        else if (StringUtils .isBlank(event_user_remove)){
            //注册人员删除回调
            typeCallBack(enterId,"event_user_remove");
        }
        else if (StringUtils .isBlank(event_user_modify)){
            //注册人员更新回调
            typeCallBack(enterId,"event_user_modify");
        }

    }

    /**
     *h 根据类型注册回调
     * @param enterId
     * @param type
     */
    public void typeCallBack(String enterId ,String type){
        try {

            RegisterCallBackResBean registerCallBackResBean = QytxlUtil.getInstance().registerCallBack(enterId, "create", type);
            if (registerCallBackResBean.getError_code().equals("0")) {
                int i = enterDao.insertEnterStatus(enterId, 1, registerCallBackResBean.getEventType());
                logger.info("================{}==============",i);
            }
        } catch (NoSuchAlgorithmException e) {
            logger.error("registerQytxl  is error!|enterId={}",enterId,e);
        }
    }
}
