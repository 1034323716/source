package richinfo.attendance.SMS;

/**
 * 类名称:SendResultInfo
 * 类描述:短信发送结果
 * 创建人:hetianhuai
 * 创建时间:2019/6/28 14:09
 * Version 1.0
 */
public class SendResultInfo {

    private boolean result;
    private String retryContacts;

    public SendResultInfo() {
    }

    public SendResultInfo(boolean result, String retryContacts) {
        this.result = result;
        this.retryContacts = retryContacts;
    }

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getRetryContacts() {
        return retryContacts;
    }

    public void setRetryContacts(String retryContacts) {
        this.retryContacts = retryContacts;
    }
}