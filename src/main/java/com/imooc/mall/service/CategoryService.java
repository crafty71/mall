package com.imooc.mall.service;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.model.pojo.Category;
import com.imooc.mall.model.request.AddCategoryReq;
import com.imooc.mall.model.vo.CategoryVO;
import java.util.ArrayList;

/**
 * 描述：     CategoryService
 */
public interface CategoryService {

  Category add(AddCategoryReq addCategoryReq);

  Category update(Category category);

  void delete(Integer id);

  PageInfo listFormAdmin(Integer pageNum, Integer pageSize);

  ArrayList<CategoryVO> listCategoryForCustomer(Integer categoryId);
}
