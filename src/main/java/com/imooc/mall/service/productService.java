package com.imooc.mall.service;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.model.pojo.Product;
import com.imooc.mall.model.request.AddProductReq;
import com.imooc.mall.model.request.ProductListReq;

public interface productService {

  Product detail(Integer id);

  Product add(AddProductReq addProductReq);

  Product update(Product product);

  PageInfo listForAdmin(Integer pageNum, Integer pageSize);

  void batchUpdateSellStatus(Integer[] ids, Integer sellStatus);

  PageInfo list(ProductListReq productListReq);
}
