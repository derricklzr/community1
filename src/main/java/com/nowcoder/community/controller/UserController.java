package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;//上传路径

    @Value("${community.path.domain}")
    private String domain; //域名

    @Value("${server.servlet.context-path}")
    private String contextPath; //项目名

    @Autowired
    private UserService userService;//service

    @Autowired
    private HostHolder hostHolder;//从hostHolder取当前用户

    @RequestMapping(path = "/setting",method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    @RequestMapping(path="/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        //形参为MultipartFile，用于接收图片
        if(headerImage==null){//如果没有图片
            model.addAttribute("error","您还没有选择图片！");
            return "/site/setting";
        }
        //生成新头像的随机名字
        String fileName=headerImage.getOriginalFilename();
        //截取文件名后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件的格式不正确！");
            return "/site/setting";
        }
        //生成随机文件名
        fileName = CommunityUtil.generateUID()+suffix;
        //确定文件存放的路径,存储文件
        File dest = new File(uploadPath+"/"+fileName);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件时"+e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发送异常",e);
        }

        //更新当前用户头像路径(web访问路径)
        //http://localhost:8004/4399/user/header/xxx.png

        //获取当前用户
        User user=hostHolder.getUser();
        String headerUrl = domain+contextPath+"/user/header/"+fileName;
        userService.updateHeader(user.getId(),headerUrl);
        return "redirect:/index";
    }

    //获取头像
    @RequestMapping(path = "/header/{fileName}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        //找服务器存放头像位置
        fileName = uploadPath+"/"+fileName;
        //输出html，先解析后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //设置图片格式类型
        response.setContentType("image/"+suffix);
        //输出图片
        try (
                FileInputStream fis = new FileInputStream(fileName);//输入到这个文件
                OutputStream os = response.getOutputStream();//输出的位置

                ){

            //输出缓冲限速
            byte[] buffer = new byte[1024];//每次1024字节数据
            int b=0;
            while((b=fis.read(buffer))!=-1){//表示读到数据
                os.write(buffer,0,b);

            }
        } catch (IOException e) {
            logger.error("读取头像失败"+e.getMessage());
        }

    }
}
