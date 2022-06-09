package com.imooc.mall.controller;

import com.imooc.mall.common.ApiRestResponse;
import com.imooc.mall.common.Constant;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.model.pojo.Product;
import com.imooc.mall.model.pojo.User;
import com.imooc.mall.service.UserService;
import io.swagger.annotations.ApiOperation;
import java.util.HashMap;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 描述：     用户控制器
 */
@Controller
public class UserController {

  @Autowired
  UserService userService;
  @ApiOperation("测试")
  @GetMapping("/test")
  @ResponseBody
  public ApiRestResponse<Product> personalPage() {
    return ApiRestResponse.success(userService.getUser());
  }


  /**
   * 注册
   */
  @ApiOperation("用户注册")
  @PostMapping("/register")
  @ResponseBody
  public ApiRestResponse register(@RequestParam("username") String userName,
      @RequestParam("password") String password) {
    if (StringUtils.isEmpty(userName)) {
      return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_USER_NAME);
    }
    if (StringUtils.isEmpty(password)) {
      return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_PASSWORD);
    }
    //密码长度不能少于8位
    if (password.length() < 8) {
      return ApiRestResponse.error(ImoocMallExceptionEnum.PASSWORD_TOO_SHORT);
    }
    userService.register(userName, password);
    return ApiRestResponse.success();
  }

  /*
    登录
   */
  @ApiOperation("用户登录")
  @PostMapping("/login")
  @ResponseBody
  public ApiRestResponse login(@RequestParam("userName") String userName,
      @RequestParam("password") String password,
      HttpSession session) throws ImoocMallException {
    if (StringUtils.isEmpty(userName)) {
      return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_USER_NAME);
    }
    if (StringUtils.isEmpty(password)) {
      return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_PASSWORD);
    }
    User user = userService.login(userName, password);
    System.out.println(user);
    user.setPassword("想看吗，哎，就不给你看，略略略");
    session.setAttribute(Constant.IMOOC_MALL_USER, user);
    return ApiRestResponse.success(user);
  }


  /*
   更新个性签名
  */
  @ApiOperation("用户更新个性签名")
  @PostMapping("/user/update")
  @ResponseBody
  public ApiRestResponse updateUserInfo(HttpSession session, @RequestParam String signature) {
    User currentUser = (User) session.getAttribute(Constant.IMOOC_MALL_USER);
    if (currentUser == null) {
      return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_LOGIN);
    }

    User user = new User();
    user.setId(currentUser.getId());
    user.setPersonalizedSignature(signature);
    userService.updateInformation(user);
    user.setPassword(null);
    return ApiRestResponse.success(user);
  }

  /*
    退出登陆
   */
  @ApiOperation("退出登录")
  @PostMapping("/user/logout")
  @ResponseBody
  public ApiRestResponse logout(HttpSession session) {
    session.removeAttribute(Constant.IMOOC_MALL_USER);
    HashMap<String, String> map = new HashMap<>();
    map.put("msg", "退出登录成功");
    map.put("notice", "你好像瘦了，头发也变长，背影陌生到让我觉得，见你是上个世纪的事，然后你开口叫我名字，我就想笑，好像自己刚刚放学，只在校门口等了你五分钟而已");
    return ApiRestResponse.success(map);
  }

  /*
  管理员登陆
 */
  @ApiOperation("管理员登陆")
  @PostMapping("/adminLogin")
  @ResponseBody
  public ApiRestResponse adminLogin(@RequestParam("userName") String userName,
      @RequestParam("password") String password,
      HttpSession session) {
    if (StringUtils.isEmpty(userName)) {
      return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_USER_NAME);
    }
    if (StringUtils.isEmpty(password)) {
      return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_PASSWORD);
    }
    User user = userService.login(userName, password);
    if (userService.checkAdminRole(user)) {
      user.setPassword(null);
      session.setAttribute(Constant.IMOOC_MALL_USER, user);
      return ApiRestResponse.success(user);
    } else {
      return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_ADMIN);
    }
  }
}
