/**
 * 文件名：DeleteFileUtil.java
 * 创建日期： 2018年2月12日
 * 作者：     wangjin
 * Copyright (c) 2009-2011 无线开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2018年2月12日
 *   修改人：wangjin
 *   修改内容：
 */
package richinfo.attendance.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.EnumSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 功能描述：删除文件下载产生的临时文件工具类
 *
 */
@SuppressWarnings("rawtypes")
public class DeleteFileUtil implements FileVisitor
{
    private static DeleteFileUtil deletefileInstace = DeleteFileUtil
        .getInstace();

    private Logger logger = LoggerFactory.getLogger(DeleteFileUtil.class);

    private AttendanceConfig config = AttendanceConfig.getInstance();

    private String rootPath = config.getDownLoadTempBaseDir();

    private int diffHours = config.getdownLoadDelDiffHours();

    public static DeleteFileUtil getInstace()
    {
        if (deletefileInstace == null)
        {
            synchronized (DeleteFileUtil.class)
            {
                if (deletefileInstace == null)
                {
                    deletefileInstace = new DeleteFileUtil();
                }
            }
        }
        return deletefileInstace;
    }

    /**
     * 
     * 访问目录之前判断该目录是否合法 不合法 直接跳出同级目录
     * @param dir
     * @param attrs
     * @return
     * @throws IOException
     */
    @Override
    public FileVisitResult preVisitDirectory(Object dir,
        BasicFileAttributes attrs) throws IOException
    {
        // 按小时删除图片场景
        if (!deleteByHoursOuttime(diffHours, dir.toString()))
        {
            // 进入该流程直接跳过 不做删除操作
            return FileVisitResult.SKIP_SUBTREE;
        }
        return FileVisitResult.CONTINUE;
    }

    /**
     * 访问文件时 删除文件操作
     * @param file
     * @param attrs
     * @return
     * @throws IOException
     */
    @Override
    public FileVisitResult visitFile(Object file, BasicFileAttributes attrs)
        throws IOException
    {
        // 访问文件时 直接删除文件
        boolean success = deleteFileByFile((Path) file);
        if (!success)
        {
            logger.info(
                "attendRept downLoad tempFile delete failed,filePath={}",
                file.toString());
        }
        return FileVisitResult.CONTINUE;
    }

    /**
     * 
     * 访问文件失败 继续访问
     */
    @Override
    public FileVisitResult visitFileFailed(Object file, IOException exc)
        throws IOException
    {
        return FileVisitResult.CONTINUE;
    }

    /**
     * 访问目录时做的操作
     * @param file
     * @param exc
     * @return
     * @throws IOException
     */
    @Override
    public FileVisitResult postVisitDirectory(Object file, IOException exc)
        throws IOException
    {
        boolean success = false;
        if (exc == null)
        {
            String f = file.toString();
            if (deleteByHoursOuttime(diffHours, file.toString())
                && isEmptyDir(f) && !checkPath(f))
            {
                // 进入该流程表示要删掉空目录
                success = deleteFileByFile((Path) file);
                if (!success)
                {
                    logger
                        .info(
                            "attendRept downLoad tempEmptyDir delete failed,filePath={}",
                            f);
                }

            }
        }
        else
        {
            throw exc;
        }
        return FileVisitResult.CONTINUE;
    }

    /**
     * 删除文件操作
     * @param file
     * @return
     */
    private boolean deleteFileByFile(Path file)
    {
        boolean flag = false;
        try
        {
            flag = Files.deleteIfExists(file);
        }
        catch (Exception e)
        {
            logger.error(
                "attendRept downLoad deleteFileByFile failed,filePath={}",
                file.toString(), e);
        }
        return flag;

    }

    /**
     * 按小时来删除
     * @param hours
     * @param file
     * @return
     */
    private boolean deleteByHoursOuttime(int hours, String file)
    {
        boolean flag = true;
        for (int i = 0; i < hours; i++)
        {
            String getFileNameByHours = getHours(i);
            String nowStr = rootPath + File.separator + getDays(0)
                + File.separator + getFileNameByHours;
            if (file.equals(nowStr))
            { // 不删除
                flag = false;
            }
        }
        // 如果时间为00点，且访问的前一天目录下的23目录存在，也不进行删除
        String currentHour = getHours(0);
        String yestody = getDays(1);
        String yestodyStr = rootPath + File.separator + yestody
            + File.separator + "23";
        if ("00".equals(currentHour) && file.equals(yestodyStr))
        {
            if (!isEmptyDir(file))
            {
                flag = false;
            }
        }
        return flag;
    }

    /**
     * 获取当前时间的前几天时间
     * @param days
     * @return 返回yyyyMMdd时间格式的文件名
     */
    private String getDays(int days)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -days);
        SimpleDateFormat sFormat = new SimpleDateFormat("yyyyMMdd");
        return sFormat.format(calendar.getTime());
    }

    /**
     * 检查当前文件目录是否登录设置的根目录 ，如果相等 则不删除
     * @param filePath
     * @return
     */
    private boolean checkPath(String filePath)
    {
        // 根目录为空也不进行删除
        String subLastStr = rootPath.substring(0, rootPath.length() - 1);
        if (filePath.equals(rootPath) || filePath.equals(subLastStr))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * 是否是空文件夹
     * @param path
     * @return
     */
    private boolean isEmptyDir(String path)
    {
        File files = new File(path);
        if (files.exists() && files.isDirectory())
        {
            File[] listFile = files.listFiles();
            if (AssertUtil.isNotEmpty(listFile) && listFile.length > 0)
            {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取当前时间的前几小时时间
     * @param days
     * @return 返回yyyyMMdd时间格式的文件名
     */
    private String getHours(int hours)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -hours);
        SimpleDateFormat sFormat = new SimpleDateFormat("HH");
        return sFormat.format(calendar.getTime());
    }

    /**
     * 
     * 按小时删除临时文件
     * @param rootPath
     * @param scenceIds
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public void deleteFile(String cguid) throws Exception
    {
        Path directory = null;
        // 参数判断
        if (AssertUtil.isEmpty(rootPath))
        {
            logger.info("rootPath is null,stop execute,cguid={}", cguid);
            return;
        }
        directory = Paths.get(rootPath);
        EnumSet optsLogin = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
        Files.walkFileTree(directory, optsLogin, Integer.MAX_VALUE,
            deletefileInstace);
    }

    /*
     * public static void main(String[] args) { deleteFile(); }
     */
}
