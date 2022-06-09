package com.imooc.mall.controller;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.common.ApiRestResponse;
import com.imooc.mall.common.Constant;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.model.pojo.Category;
import com.imooc.mall.model.pojo.User;
import com.imooc.mall.model.request.AddCategoryReq;
import com.imooc.mall.model.request.UpdateCategoryReq;
import com.imooc.mall.model.vo.CategoryVO;
import com.imooc.mall.service.CategoryService;
import com.imooc.mall.service.UserService;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CategoryController {

  @Resource
  UserService userService;

  @Resource
  CategoryService categoryService;

  /**
   * 后台添加目录
   */
  @ApiOperation("后台添加目录")
  @PostMapping("admin/category/add")
  @ResponseBody
  public ApiRestResponse addCategory(HttpSession session,
      @Valid @RequestBody AddCategoryReq addCategoryReq) {
    User attribute = (User) session.getAttribute(Constant.IMOOC_MALL_USER);
    if (attribute == null) {
      return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_LOGIN);
    }

    boolean adminRole = userService.checkAdminRole(attribute);
    if (adminRole) {
      Category category = categoryService.add(addCategoryReq);
      return ApiRestResponse.success(category);
    } else {
      return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_ADMIN);
    }
  }

  @ApiOperation("后台更新目录")
  @PostMapping("admin/category/update")
  @ResponseBody

  public ApiRestResponse updateCategory(@Valid @RequestBody UpdateCategoryReq updateCategoryReq,
      HttpSession session) {
    User attribute = (User) session.getAttribute(Constant.IMOOC_MALL_USER);
    if (attribute == null) {
      return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_LOGIN);
    }

    boolean adminRole = userService.checkAdminRole(attribute);
    if (adminRole) {
      Category category = new Category();
      BeanUtils.copyProperties(updateCategoryReq, category);
      Category update = categoryService.update(category);
      return ApiRestResponse.success(update);
    } else {
      return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_ADMIN);
    }
  }

  @ApiOperation("后台删除目录")
  @PostMapping("admin/category/delete")
  @ResponseBody

  public ApiRestResponse deleteCategory(@RequestParam("id") Integer id) {
    categoryService.delete(id);
    return ApiRestResponse.success();
  }

  @ApiOperation("后台目录列表")
  @PostMapping("admin/category/list")
  @ResponseBody

  public ApiRestResponse listCategoryForAdmin(@RequestParam Integer pageNum,
      @RequestParam Integer pageSize) {
    PageInfo pageInfo = categoryService.listFormAdmin(pageNum, pageSize);
    return ApiRestResponse.success(pageInfo);
  }

  @ApiOperation("前台目录列表")
  @PostMapping("category/list")
  @ResponseBody
  public ApiRestResponse listCategoryForCustomer() {
    List<CategoryVO> categoryVOS = categoryService.listCategoryForCustomer(0);
    return ApiRestResponse.success(categoryVOS);
  }
}
