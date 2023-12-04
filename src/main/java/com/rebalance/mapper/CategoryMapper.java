package com.rebalance.mapper;

import com.rebalance.dto.response.CategoryResponse;
import com.rebalance.dto.response.SumByCategoryResponse;
import com.rebalance.entity.Category;
import com.rebalance.mapper.converter.DecimalConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(uses = DecimalConverter.class)
public interface CategoryMapper {
    CategoryResponse categoryToResponse(Category category);

    @Mapping(target = "sum", source = "sum", qualifiedByName = "bigDecimalToDouble")
    SumByCategoryResponse sumByCategoryToResponse(String category, BigDecimal sum);
}
