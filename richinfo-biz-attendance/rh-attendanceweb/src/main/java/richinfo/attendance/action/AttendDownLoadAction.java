/**
 * 文件名：AttendDownLoadAction.java
 * 创建日期： 2018年2月11日
 * 作者：     wangjin
 * Copyright (c) 2009-2011 无线开发室
 * All rights reserved.
 
 * 修改记录：
 * 	1.修改时间：2018年2月11日
 *   修改人：wangjin
 *   修改内容：
 */
package richinfo.attendance.action;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import richinfo.attendance.bean.DownLoadRes;
import richinfo.attendance.service.AttendDownLoadService;
import richinfo.attendance.service.impl.AttendDownLoadServiceImpl;
import richinfo.tools.file.FileUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

/**
 * 功能描述：考勤下载Action
 *
 */
@Controller
@RequestMapping("/downLoad")
public class AttendDownLoadAction extends BaseAttendanceAction {
    private AttendDownLoadService downLoadService = new AttendDownLoadServiceImpl();

    @RequestMapping(value = "/downLoadAttendRept", method = RequestMethod.GET)
    public void downLoadAttendRept(
        @RequestParam("contendDirId") String contendDirId,
        @RequestParam("fileName") String fileName, HttpServletRequest request,
        HttpServletResponse response) {
        // URL传递时会将BASE64中有效的"+"变成空格，需要替换下
        contendDirId = contendDirId.replaceAll("\\s", "+");
        fileName = fileName.replaceAll("\\s", "+");
        String clientIp = getClientIp(request);
        DownLoadRes res = downLoadService.getDownLoadResource(contendDirId,
            fileName, clientIp);
        if (!res.isSuccess()) {
            logger.info(
                    "downLoadAttendRept operate failed,contendDirId={}|fileName={}|clientIp={}|code={}|summary={}",
                    contendDirId, fileName, clientIp, res.getCode(),
                    res.getSummary());
            // 如果获取数据失败，直接返回404
            response.setStatus(404);
            return;
        }
        outputFile(res, response);
    }

    /**
     * 输出excel文件
//     * @param path
     * @param response
//     * @param fileName
     */
    private void outputFile(DownLoadRes res, HttpServletResponse response) {
        OutputStream out = null;
        try {
            // 防止乱码
            String fileName = URLEncoder.encode(res.getFileName(), "utf-8");
            // 处理文件名中的空格，否则会转成"+"
            fileName = fileName.replace("+", "%20");
            // 设置响应头，控制浏览器下载该文件
            response.setHeader("content-disposition", "attachment;filename="
                + fileName);
            // 设置响应头，浏览器弹出下载框(针对excel文件)
            response.setContentType("application/octet-stream;charset=utf-8");
            out = response.getOutputStream();
            out.write(res.getBytes(), 0, res.getBytes().length);
            out.flush();
            // 下载后删除临时数据
            boolean isDelete = false;
            isDelete = FileUtil.delete(res.getFilePath());
            logger.info(
                    "downLoadAttendRept success,fileName={}|filePath={}|fileSize={}|isDelete={}",
                    res.getFileName(), res.getFilePath(),
                    res.getBytes().length, isDelete);
        } catch (Exception e) {
            logger.error("outputFile failed,fileName={}|filePath={}",
                res.getFileName(), res.getFilePath(), e);
        } finally {
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {}
            }
        }
    }
}
