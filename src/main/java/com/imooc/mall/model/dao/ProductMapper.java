package com.imooc.mall.model.dao;

import com.imooc.mall.model.pojo.Product;
import com.imooc.mall.model.query.ProductListQuery;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductMapper {

  int deleteByPrimaryKey(Integer id);

  int insert(Product record);

  int insertSelective(Product record);

  Product selectByPrimaryKey(Integer id);

  int updateByPrimaryKeySelective(Product record);

  int updateByPrimaryKey(Product record);

  Product selectByName(String name);

  List<Product> selectListForAdmin();

  void batchUpdateSellStatus(Integer[] ids, Integer sellStatus);

  List<Product> selectList(@Param("query") ProductListQuery query);
}