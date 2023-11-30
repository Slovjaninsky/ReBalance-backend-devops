package com.rebalance.mapper.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mapper
public interface DecimalConverter {
    @Named("bigDecimalToDouble")
    static Double bigDecimalToDouble(BigDecimal bg) {
        return bg.setScale(2, RoundingMode.HALF_EVEN).doubleValue();
    }
}
