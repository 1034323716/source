package richinfo.dbcomponent.resourceloader;

/**
 * 
 * 功能描述：文件系统资源加载器.
 * 
 * 创建日期： 2012-9-15 作者： zhou gui ping
 */
public class FileSystemResourceLoader extends DefaultResourceLoader
{

    /**
     * 通过路径获取资源加载器对象.
     * @param path 路径.
     */
    protected ResourceLoader getResourceByPath(String path)
    {
        if (path != null && path.startsWith("/"))
        {
            path = path.substring(1);
        }
        return new FileSystemContextResource(path);
    }

    /**
     * 
     * 功能描述：获取文件系统资源内部类.
     * @author   zhou gui ping
     * @company: 深圳彩讯科技有限公司 
     *
     */
    private static class FileSystemContextResource extends FileSystemResource
        implements ContextResource
    {

        public FileSystemContextResource(String path)
        {
            super(path);
        }

        public String getPathWithinContext()
        {
            return getPath();
        }
    }

}
