package com.rebalance.mapper;

import com.rebalance.dto.response.CategoryResponse;
import com.rebalance.dto.response.SumByCategoryResponse;
import com.rebalance.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Map;

@Mapper
public interface CategoryMapper {
    CategoryResponse categoryToResponse(Category category);

    @Mapping(target = "category", expression = "java(sums.getKey())")
    @Mapping(target = "sum", expression = "java(sums.getValue())")
    SumByCategoryResponse sumByCategoryToResponse(Map.Entry<String, Double> sums);
}
