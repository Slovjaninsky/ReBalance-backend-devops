package com.rebalance.mapper;

import com.rebalance.dto.request.GroupCreateRequest;
import com.rebalance.dto.response.GroupResponse;
import com.rebalance.entities.ExpenseGroup;
import org.mapstruct.Mapper;

@Mapper
public interface GroupMapper {
    GroupResponse groupToResponse(ExpenseGroup group);

    ExpenseGroup createRequestToGroup(GroupCreateRequest request);
}
