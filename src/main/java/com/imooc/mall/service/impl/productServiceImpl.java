package com.imooc.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.mall.common.Constant.ProductListOrderBy;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.model.dao.ProductMapper;
import com.imooc.mall.model.pojo.Product;
import com.imooc.mall.model.query.ProductListQuery;
import com.imooc.mall.model.request.AddProductReq;
import com.imooc.mall.model.request.ProductListReq;
import com.imooc.mall.model.vo.CategoryVO;
import com.imooc.mall.service.CategoryService;
import com.imooc.mall.service.productService;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class productServiceImpl implements productService {

  @Resource
  ProductMapper productMapper;

  @Resource
  CategoryService categoryService;

  @Override
  public Product detail(Integer id) {
    return productMapper.selectByPrimaryKey(id);
  }

  @Override
  public Product add(AddProductReq addProductReq) {
    Product product = new Product();
    BeanUtils.copyProperties(addProductReq, product);
    Product productOld = productMapper.selectByName(addProductReq.getName());
    if (productOld != null) {
      throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
    }
    int count = productMapper.insertSelective(product);
    Product product1 = productMapper.selectByName(product.getName());
    if (count == 0) {
      throw new ImoocMallException(ImoocMallExceptionEnum.CREATE_FAILED);
    }
    return product1;
  }

  @Override
  public Product update(Product product) {
    Product productOld = productMapper.selectByName(product.getName());
    if (productOld != null && !productOld.getId().equals(product.getId())) {
      throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
    }
    int count = productMapper.updateByPrimaryKeySelective(product);
    if (count == 0) {
      throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
    }
    return productMapper.selectByPrimaryKey(product.getId());
  }

  @Override
  public PageInfo listForAdmin(Integer pageNum, Integer pageSize) {
    PageHelper.startPage(pageNum, pageSize);
    List<Product> products = productMapper.selectListForAdmin();
    PageInfo<Product> productPageInfo = new PageInfo<>(products);
    return productPageInfo;
  }

  @Override
  public void batchUpdateSellStatus(Integer[] ids, Integer sellStatus) {
    productMapper.batchUpdateSellStatus(ids, sellStatus);
  }

  @Override
  public PageInfo list(ProductListReq productListReq) {
    //构建Query对象
    ProductListQuery productListQuery = new ProductListQuery();

    //搜索处理
    if (!StringUtils.isEmpty(productListReq.getKeyword())) {
      String keyword = "%" + productListReq.getKeyword()
          + "%";
      productListQuery.setKeyword(keyword);
    }

    //目录处理：如果查某个目录下的商品，不仅是需要查出该目录下的，还要把所有子目录的所有商品都查出来，所以要拿到一个目录id的List
    if (productListReq.getCategoryId() != null) {
      List<CategoryVO> categoryVOList = categoryService
          .listCategoryForCustomer(productListReq.getCategoryId());
      ArrayList<Integer> categoryIds = new ArrayList<>();
      categoryIds.add(productListReq.getCategoryId());
      getCategoryIds(categoryVOList, categoryIds);
      productListQuery.setCategoryIds(categoryIds);
    }

    //排序处理
    String orderBy = productListReq.getOrderBy();
    if (ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
      PageHelper
          .startPage(productListReq.getPageNum(), productListReq.getPageSize(), orderBy);
    } else {
      PageHelper
          .startPage(productListReq.getPageNum(), productListReq.getPageSize());
    }

    List<Product> productList = productMapper.selectList(productListQuery);
    return new PageInfo<>(productList);
  }

  private void getCategoryIds(List<CategoryVO> categoryVOList, ArrayList<Integer> categoryIds) {
    for (CategoryVO categoryVO : categoryVOList) {
      if (categoryVO != null) {
        categoryIds.add(categoryVO.getId());
        getCategoryIds(categoryVO.getChildCategory(), categoryIds);
      }
    }
  }

}
