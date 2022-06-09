package com.imooc.mall.controller;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.common.ApiRestResponse;
import com.imooc.mall.common.Constant;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.model.pojo.Product;
import com.imooc.mall.model.request.AddProductReq;
import com.imooc.mall.model.request.UpdateProductReq;
import com.imooc.mall.service.productService;
import io.swagger.annotations.ApiOperation;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 描述：     后台商品管理Controller
 */
@RestController
public class ProductAdminController {

  @Resource
  productService productService;

  @ApiOperation("后台新增商品")
  @PostMapping("/admin/product/add")
  public ApiRestResponse addProduct(@Valid @RequestBody AddProductReq addProductReq) {
    Product product = productService.add(addProductReq);
    return ApiRestResponse.success(product);
  }

  @PostMapping("/admin/upload/file")
  public ApiRestResponse upload(HttpServletRequest httpServletRequest,
      @RequestParam("file") MultipartFile file) {
    String fileName = file.getOriginalFilename();
    assert fileName != null;
    String suffixName = fileName.substring(fileName.lastIndexOf("."));
    //生成文件名称UUID
    UUID uuid = UUID.randomUUID();
    String newFileName = uuid + suffixName;
    //创建文件
    File fileDirectory = new File(Constant.FILE_UPLOAD_DIR);
    File destFile = new File(Constant.FILE_UPLOAD_DIR + newFileName);
    if (!fileDirectory.exists()) {
      if (!fileDirectory.mkdir()) {
        throw new ImoocMallException(ImoocMallExceptionEnum.MKDIR_FAILED);
      }
    }
    try {
      file.transferTo(destFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      return ApiRestResponse
          .success(getHost(new URI(httpServletRequest.getRequestURL() + "")) + "/images/"
              + newFileName);
    } catch (URISyntaxException e) {
      return ApiRestResponse.error(ImoocMallExceptionEnum.UPLOAD_FAILED);
    }
  }

  private URI getHost(URI uri) {
    URI effectiveURI;
    try {
      effectiveURI = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(),
          null, null, null);
    } catch (URISyntaxException e) {
      effectiveURI = null;
    }
    return effectiveURI;
  }

  @ApiOperation("后台更新商品")
  @PostMapping("/admin/product/update")
  public ApiRestResponse updateProduct(@Valid @RequestBody UpdateProductReq updateProductReq) {
    Product product = new Product();
    BeanUtils.copyProperties(updateProductReq, product);
    Product update = productService.update(product);
    return ApiRestResponse.success(update);
  }

  @ApiOperation("后台商品列表接口")
  @PostMapping("/admin/product/list")
  public ApiRestResponse list(@RequestParam Integer pageNum,
      @RequestParam Integer pageSize) {
    PageInfo pageInfo = productService.listForAdmin(pageNum, pageSize);
    return ApiRestResponse.success(pageInfo);
  }

  @ApiOperation("后台批量上下架接口")
  @PostMapping("/admin/product/batchUpdateSellStatus")
  public ApiRestResponse batchUpdateSellStatus(@RequestParam Integer[] ids,
      @RequestParam Integer sellStatus) {
    productService.batchUpdateSellStatus(ids, sellStatus);
    return ApiRestResponse.success();
  }

}
