package com.imooc.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.model.dao.CategoryMapper;
import com.imooc.mall.model.pojo.Category;
import com.imooc.mall.model.request.AddCategoryReq;
import com.imooc.mall.model.vo.CategoryVO;
import com.imooc.mall.service.CategoryService;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class CategoryServiceImpl implements CategoryService {

  @Resource
  CategoryMapper categoryMapper;

  @Override
  public Category add(AddCategoryReq addCategoryReq) {
    Category category = new Category();
    BeanUtils.copyProperties(addCategoryReq, category);
    Category categoryOld = categoryMapper.selectByName(addCategoryReq.getName());
    if (categoryOld != null) {
      throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
    }
    int count = categoryMapper.insertSelective(category);
    if (count == 0) {
      throw new ImoocMallException(ImoocMallExceptionEnum.CREATE_FAILED);
    }
    Category categoryNew = categoryMapper.selectByName(category.getName());
    return categoryMapper.selectByPrimaryKey(categoryNew.getId());
  }

  @Override
  public Category update(Category category) {
    if (category.getName() != null) {
      Category category1 = categoryMapper.selectByName(category.getName());
      if (category1 != null && category1.getId().equals(category.getId())) {
        throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
      }
    }
    int count = categoryMapper.updateByPrimaryKeySelective(category);
    if (count == 0) {
      throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
    }
    return categoryMapper.selectByPrimaryKey(category.getId());
  }

  @Override
  public void delete(Integer id) {
    Category categoryOld = categoryMapper.selectByPrimaryKey(id);
    //?????????????????????????????????????????????
    if (categoryOld == null) {
      throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);
    }
    int count = categoryMapper.deleteByPrimaryKey(id);
    if (count == 0) {
      throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);
    }
  }

  @Override
  public PageInfo listFormAdmin(Integer pageNum, Integer pageSize) {
    PageHelper.startPage(pageNum, pageSize, "type, order_num");
    List<Category> categoryList = categoryMapper.selectList();
    return new PageInfo(categoryList);
  }


  @Override
//  @Cacheable(value = "listCategoryForCustomer")
  public ArrayList<CategoryVO> listCategoryForCustomer(Integer categoryId) {
    ArrayList<CategoryVO> categoryVOList = new ArrayList<>();
    recursivelyFindCategories(categoryVOList, 0);
    return categoryVOList;
  }

  private void recursivelyFindCategories(List<CategoryVO> categoryVOList, Integer parentId) {
    //??????????????????????????????????????????????????????????????????
    List<Category> categoryList = categoryMapper.selectCategoriesByParentId(parentId);
    if (!CollectionUtils.isEmpty(categoryList)) {
      for (Category category : categoryList) {
        CategoryVO categoryVO = new CategoryVO();
        BeanUtils.copyProperties(category, categoryVO);
        categoryVOList.add(categoryVO);
        recursivelyFindCategories(categoryVO.getChildCategory(), categoryVO.getId());
      }
    }
  }
}
