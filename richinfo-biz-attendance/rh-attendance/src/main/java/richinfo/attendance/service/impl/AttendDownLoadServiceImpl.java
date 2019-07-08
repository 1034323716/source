/**
 * 文件名：AttendDownLoadServiceImpl.java
 * 创建日期： 2018年2月11日
 * 作者：     wangjin
 * Copyright (c) 2009-2011 无线开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2018年2月11日
 *   修改人：wangjin
 *   修改内容：
 */
package richinfo.attendance.service.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import richinfo.attendance.bean.DownLoadRes;
import richinfo.attendance.common.AtdcConsts;
import richinfo.attendance.common.AtdcResultCode;
import richinfo.attendance.common.AtdcResultSummary;
import richinfo.attendance.service.AttendDownLoadService;
import richinfo.attendance.util.AttendanceConfig;
import richinfo.attendance.util.Base64Coder;
import richinfo.attendance.util.AssertUtil;
import richinfo.tools.file.FileUtil;
import richinfo.tools.io.StreamUtil;

/**
 * 功能描述：考勤报表下载接口实现类
 *
 */
public class AttendDownLoadServiceImpl implements AttendDownLoadService
{
    private Logger logger = LoggerFactory
        .getLogger(AttendDownLoadServiceImpl.class);

    private AttendanceConfig config = AttendanceConfig.getInstance();

    @Override
    public DownLoadRes getDownLoadResource(String contendDirId,
        String fileName, String clientIp)
    {
        DownLoadRes res = new DownLoadRes();
        checkDownLoadResourceParam(contendDirId, fileName, res);
        if (!res.isSuccess())
        {
            logger
                .info(
                    "getDownLoadResource checkParam failed,contendDirId={}|fileName={}|clientIp={}|code={}|summar={}",
                    contendDirId, fileName, clientIp, res.getCode(),
                    res.getSummary());
            return res;
        }
        logger
            .info(
                "getDownLoadResource checkParam success,contendDirId={}|fileName={}|clientIp={}",
                contendDirId, fileName, clientIp);
        // 文件存储的基础目录
        String baseDir = config.getDownLoadTempBaseDir();
        // 解析出文件的分层存储目录
        String decoderDir = null;
        // 解析出文件名
        String decoderFileName = null;
        try
        {
            decoderDir = Base64Coder.decodeString(contendDirId);
            decoderFileName = Base64Coder.decodeString(fileName);
        }
        catch (Exception e)
        {
            logger
                .error(
                    "getDownLoadResource base64Decoder failed,contendDirId={}|fileName={}|clientIp={}",
                    contendDirId, fileName, clientIp, e);
            res.setCode(AtdcResultCode.ATDC108.PARAM_PARSE_FAILED);
            res.setSummary(AtdcResultSummary.ATDC108.PARAM_PARSE_FAILED);
            return res;
        }
        // 文件真实地址
        String filePath = baseDir + decoderDir + File.separator
            + decoderFileName;
        boolean flag = FileUtil.isExists(filePath);
        if (!flag)
        {
            res.setCode(AtdcResultCode.ATDC108.FILE_NOT_FOUND);
            res.setSummary(AtdcResultSummary.ATDC108.FILE_NOT_FOUND);
            return res;
        }
        byte[] bytes = toByteArray(filePath);
        if (AssertUtil.isEmpty(bytes))
        {
            res.setCode(AtdcResultCode.ATDC108.NO_DATA);
            res.setSummary(AtdcResultSummary.ATDC108.NO_DATA);
            return res;
        }
        res.setBytes(bytes);
        // 获得真实的fileName
        String realFileName = decoderFileName
            .split(AtdcConsts.ATTEND_BIZ.DOWNLOAD_FILENAME_SEPARATOR)[1];
        res.setFileName(realFileName);
        res.setFilePath(filePath);
        return res;
    }

    /**
     * 请求参数校验
     * @param contendDirId
     * @param fileName
     * @param res
     */
    private void checkDownLoadResourceParam(String contendDirId,
        String fileName, DownLoadRes res)
    {
        if (AssertUtil.isEmpty(contendDirId) || AssertUtil.isEmpty(fileName))
        {
            res.setCode(AtdcResultCode.ATDC104.PARAMS_NULL);
            res.setSummary(AtdcResultSummary.ATDC104.PARAMS_NULL);
            return;
        }
    }

    /**
     * 读取文件到字节数组中
     * @param filePath
     * @return
     */
    public byte[] toByteArray(String filePath)
    {
        ByteArrayOutputStream bos = null;
        FileInputStream fin = null;
        BufferedInputStream in = null;
        try
        {
            File f = new File(filePath);
            bos = new ByteArrayOutputStream((int) f.length());
            fin = new FileInputStream(f);
            in = new BufferedInputStream(fin);
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while ((len = in.read(buffer, 0, buf_size)) != -1)
            {
                bos.write(buffer, 0, len);
            }
            bos.flush();
            logger
                .debug(
                    "toByteArray fileSize={}|byteSize={}|filePath={}",
                    (int) f.length(),
                    AssertUtil.isNotEmpty(bos.toByteArray()) ? bos
                        .toByteArray().length : 0, filePath);
            return bos.toByteArray();
        }
        catch (Exception e)
        {
            logger.error(
                "attendDownload file convertTo byte failed,filePath={}",
                filePath, e);
        }
        finally
        {
            StreamUtil.close(bos);
            StreamUtil.close(in);
            StreamUtil.close(fin);
        }
        return null;
    }

}
