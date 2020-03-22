package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.CommunityConstant;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.LikeService;
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
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

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

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @LoginRequired
    @RequestMapping(path = "/setting",method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
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
    //个人设置页面修改密码功能
    //这里形参用Model类和User类即可，SpringMVC会把传入内容按照User属性填入user
    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.POST)
    public String updatePassword(Model model, String password,String newPassword,String confirmPassword) {
       if(StringUtils.isBlank(password)){
           model.addAttribute("passwordMsg","请输入原始密码！");
           return "/site/setting";
       }
        if(StringUtils.isBlank(newPassword)){
            model.addAttribute("newPasswordMsg","请输入新密码！");
            return "/site/setting";
        }
        if(StringUtils.isBlank(confirmPassword)){
            model.addAttribute("confirmPasswordMsg","请再次输入新密码！");
            return "/site/setting";
        }
        if(!confirmPassword.equals(newPassword)){
            model.addAttribute("newPasswordMsg","两次输入的新密码不相同！");
            return "/site/setting";
        }
        User user=hostHolder.getUser();
        Map<String, Object> map = userService.updatePassword(password,newPassword,user.getId());
        if (map == null || map.isEmpty()) {
            //传给templates改密码成功
            model.addAttribute("msg", "密码修改成功");
            //跳到个人设置面
            model.addAttribute("target", "/user/setting");
            return "/site/operate-result";
        }else {
            //失败了传失败信息，跳到到原来的页面
            model.addAttribute("passwordMsg","输入的原始密码错误！");
            return "/site/setting";
        }


    }

    @LoginRequired
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
        //传给templates改密码成功
        model.addAttribute("msg", "头像修改成功");
        //跳到个人设置面
        model.addAttribute("target", "/user/setting");
        return "/site/operate-result";
    }

    //个人主页
    @RequestMapping(path = "/profile/{userId}",method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId,Model model){
        User user = userService.findUserById(userId);
        if (user==null){
            throw new RuntimeException("该用户不存在！");
        }
        //用户
        model.addAttribute("user",user);
        //点赞数
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",likeCount);

        //关注数量
        long followeeCount = followService.findFolloweeCount(userId,ENTITY_TYPE_USER);
        model.addAttribute("followeeCount",followeeCount);
        //粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER,userId);
        model.addAttribute("followerCount",followerCount);
        //是否已经关注
        boolean hasFollowed = false;
        if(hostHolder.getUser()!=null){
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);
        }
        model.addAttribute("hasFollowed",hasFollowed);

        return "/site/profile";
    }

}
