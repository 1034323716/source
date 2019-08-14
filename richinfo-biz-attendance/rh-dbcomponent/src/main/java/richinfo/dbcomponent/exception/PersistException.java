package richinfo.dbcomponent.exception;

/**
 * 持久层统一异常,屏蔽底层的sql异常.
 * 
 * @author zhou gui ping
 *
 */
public class PersistException extends Exception
{
  
    private static final long serialVersionUID = -3334732815077003453L;

    /** 异常信息 */
    @SuppressWarnings("unused")
    private String errorMsg = "";

    /** 异常错误码 */
    @SuppressWarnings("unused")
    private int errorCode = 0;
    
    public PersistException()
    {
        super();
    }

    public PersistException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public PersistException(String message)
    {
        super(message);
    }

    public PersistException(Throwable cause)
    {
        super(cause);
    }

}
