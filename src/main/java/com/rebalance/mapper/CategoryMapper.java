package com.rebalance.mapper;

import com.rebalance.dto.response.CategoryResponse;
import com.rebalance.entity.Category;
import org.mapstruct.Mapper;

@Mapper
public interface CategoryMapper {
    CategoryResponse categoryToResponse(Category category);
}
