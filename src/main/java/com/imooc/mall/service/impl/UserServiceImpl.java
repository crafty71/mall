package com.imooc.mall.service.impl;

import com.imooc.mall.common.Constant;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.model.dao.ProductMapper;
import com.imooc.mall.model.dao.UserMapper;
import com.imooc.mall.model.pojo.Product;
import com.imooc.mall.model.pojo.User;
import com.imooc.mall.service.UserService;
import com.imooc.mall.utils.MD5Utils;
import java.security.NoSuchAlgorithmException;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 描述：     UserService实现类
 */
@Service
public class UserServiceImpl implements UserService {

  @Resource
  UserMapper userMapper;

  @Resource
  ProductMapper productMapper;

  @Override
  public Product getUser() {
    return productMapper.selectByPrimaryKey(2);
  }

  @Override
  public void register(String userName, String password) throws ImoocMallException {
    //查询用户名是否存在，不允许重名
    User result = userMapper.selectByName(userName);
    if (result != null) {
      throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
    }

    //写到数据库
    User user = new User();
    user.setUsername(userName);
    try {
      user.setPassword(MD5Utils.getMD5Str(password, Constant.ICODE));
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    int count = userMapper.insertSelective(user);
    if (count == 0) {
      throw new ImoocMallException(ImoocMallExceptionEnum.INSERT_FAILED);
    }
  }

  @Override
  public User login(String userName, String password) throws ImoocMallException {
    String md5Password = null;
    try {
      md5Password = MD5Utils.getMD5Str(password, Constant.ICODE);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }

    User user = userMapper.selectLogin(userName, md5Password);

    if (user == null) {
      throw new ImoocMallException(ImoocMallExceptionEnum.WRONG_PASSWORD);
    }

    return user;
  }

  @Override
  public void updateInformation(User user) throws ImoocMallException {
    int count = userMapper.updateByPrimaryKeySelective(user);
    if (count > 1) {
      throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
    }
  }

  @Override
  public boolean checkAdminRole(User user) {
    //1是普通用户，2是管理员
    return user.getRole().equals(2);
  }
}
