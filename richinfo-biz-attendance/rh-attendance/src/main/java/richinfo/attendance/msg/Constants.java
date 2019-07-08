/**
 * 文件名：Consts.java
 * 创建日期： 2014-2-28
 * 作者：    LZX
 * Copyright (c) 2009-2011 无线开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2011-2-28
 *   修改人：LZX
 *   修改内容：
 */
package richinfo.attendance.msg;


/**
 * 功能描述：其它常量
 * 
 */
public final class Constants
{
    /** 接口版本号 */
    public final static int API_LEVEL = 2;
    /** 接口连接超时时间(毫秒) 默认5秒 */
    public final static int HTTP_CONN_TIMEOUT = 5000;
    /** 接口读取超时时间(毫秒) 默认5秒 */
    public final static int HTTP_READ_TIMEOUT = 5000;
    /** 消息推送最大长度(字节) */
    public final static int CONTENT_MAX_LEN = 2048;
    /** 消息主题最大长度(字节) */
    public final static int TOPIC_MAX_LEN = 1024;
    /** 消息保留字段最大长度(字节) */
    public final static int RESERVED_MAX_LEN = 1024;
    /** 批量消息允许最大的消息数（uid，deviceToken） */
    public final static int BATCH_MESSAGE_MAX_LEN = 200;

    /** 成功返回码 */
    public static final String S_OK = "S_OK";
    /** 失败返回码 */
    public static final String S_FAILED = "S_FAIL";

    /** 查询用户在线的返回错误码 **/
    public static final String S_ERROR = "Error";

    public static final String ENCODING_UTF8 = "UTF-8";

    /**
     * 
     * 功能描述：消息类型
     * 
     */
    public enum MsgType {
        // 1签到消息 //2 签退消息 // 3待审批，//4审批通过//5拒绝审批//5团队日版
        Sign(1), SignOut(2),authorization(3),authorizationPass(4), authorizationReject(5),daily(6),Unknow(-1);

        private int value;

        private MsgType(int value)
        {
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }

        public static MsgType parse(int value)
        {
            switch (value)
            {
            case 1:
                return Sign;
            case 2:
                return SignOut;
            case 3:
                return authorization;
            case 4:
                return authorizationPass;
            case 5:
                return authorizationReject;
            case 6:
                return daily;
            default:
                return Unknow;
            }
        }
    }

}
