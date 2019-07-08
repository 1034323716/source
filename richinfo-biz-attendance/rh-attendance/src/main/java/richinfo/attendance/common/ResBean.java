package richinfo.attendance.common;

import java.io.Serializable;

/**
 * 功能描述：服务返回对象基类
 * 
 */
public class ResBean extends BeanObject implements Serializable
{
    private static final long serialVersionUID = 257617561564691621L;
    /**
     * 成功的默认编码
     */
    public static final String S_OK = "S_OK";

    /** 返回码,默认成功S_OK */
    private String code = S_OK;

    /** 返回码描述 */
    private String summary;

    /**
     * 是否成功,默认成功
     */
    private boolean success = true;

    private  int clockStatue = 1;

    public int getClockStatue() {
        return clockStatue;
    }

    public void setClockStatue(int clockStatue) {
        this.clockStatue = clockStatue;
    }

    /**
     * 设置错误码,并标记是否成功
     * @param code
     */
    public void setCode(String code)
    {
        if (S_OK.equals(code))
        {
            success = true;
        }
        else
        {
            success = false;
        }

        this.code = code;
    }

    public ResBean()
    {
    }

    public ResBean(String code)
    {
        this.code = code;
    }

    public ResBean(String code, String summary)
    {
        this.code = code;
        this.summary = summary;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public String getCode()
    {
        return code;
    }

    public String getSummary()
    {
        return summary;
    }

    public void setSummary(String summary)
    {
        this.summary = summary;
    }

}