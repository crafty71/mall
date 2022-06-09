package com.imooc.mall.controller;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.common.ApiRestResponse;
import com.imooc.mall.model.pojo.Product;
import com.imooc.mall.model.request.ProductListReq;
import com.imooc.mall.service.productService;
import io.swagger.annotations.ApiOperation;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {

  @Resource
  productService productService;

  @ApiOperation("商品详情")
  @GetMapping("product/detail")
  @ResponseBody
  public ApiRestResponse detail(@RequestParam Integer id) {
    Product product = productService.detail(id);
    return ApiRestResponse.success(product);
  }

  @ApiOperation("商品列表")
  @GetMapping("product/list")
  public ApiRestResponse list(ProductListReq productListReq) {
    PageInfo list = productService.list(productListReq);
    return ApiRestResponse.success(list);
  }
}
