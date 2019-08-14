/**
* 文件名：RcsCheckRes.java
* 创建日期： 2017年6月20日
* 作者：     yylchhy
* Copyright (c) 2009-2011 邮箱产品开发室
* All rights reserved.
 
* 修改记录：
* 	1.修改时间：2017年6月20日
*   修改人：yylchhy
*   修改内容：
*/
package richinfo.attendance.bean;

import java.io.Serializable;

import richinfo.attendance.common.BeanObject;

/**
 * 功能描述：
 * RCS管理员校验接口返回对象
 */
public class RcsCheckAdminRes extends BeanObject implements Serializable
{
    private static final long serialVersionUID = -7105622083441995963L;
    
    /**消息体签名*/
    private String msg_signature;
    
    /**时间戳*/
    private String timeStamp;
    
    /**随机字符串*/
    private String nonce;
    
    /**“success”加密字符串 AES加密*/
    private String encrypt;

    public String getMsg_signature()
    {
        return msg_signature;
    }

    public void setMsg_signature(String msg_signature)
    {
        this.msg_signature = msg_signature;
    }

    public String getTimeStamp()
    {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp)
    {
        this.timeStamp = timeStamp;
    }

    public String getNonce()
    {
        return nonce;
    }

    public void setNonce(String nonce)
    {
        this.nonce = nonce;
    }

    public String getEncrypt()
    {
        return encrypt;
    }

    public void setEncrypt(String encrypt)
    {
        this.encrypt = encrypt;
    }
    
}
