package com.contract.system.controller;


import com.contract.system.bean.User;
import com.contract.system.mapper.UserMapper;
import com.contract.system.util.JsonUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class LoginAndRegistController {


    @Resource
    public UserMapper userMapper;

    /**
     * 登陆页面
     * @return
     */
    @RequestMapping("/loginHtml.do")
    public String login(){
        return "login";
    }


    /**
     * 登陆验证
     * @param session
     * @return
     */
    @ResponseBody
    @RequestMapping("/login.do")
    public String login(@RequestBody String array[],
                        HttpSession session){
        List<User> users = userMapper.getByName(array[0]);//根据用户name查询用户
        if (users.size() == 0){
            return JsonUtil.toJson("当前用户没注册");
        }
        User user = users.get(0);
        String pas = user.password;
        if(pas.equals(array[1])){
            session.setAttribute("loginUser",array[0]);//把登陆用户设置到session中
            Integer pos = user.pos;
            if(pos==2){
                return JsonUtil.toJson("欢迎管理员登陆");//返回管理端
            }
            else {
                return JsonUtil.toJson("欢迎用户登陆");//返回首页
            }
        }
        else {
            return JsonUtil.toJson("密码错误");
        }
    }



    /**
     * 退出登陆
     * @param session
     * @return
     */
    @RequestMapping("/quit.do")
    public String back(HttpSession session){
        session.removeAttribute("loginUser");
        return "login";

    }
}
