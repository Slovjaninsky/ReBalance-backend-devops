package com.rebalance.mapper;

import com.rebalance.dto.request.GroupCreateRequest;
import com.rebalance.dto.response.GroupResponse;
import com.rebalance.entity.Group;
import org.mapstruct.Mapper;

@Mapper
public interface GroupMapper {
    GroupResponse groupToResponse(Group group);

    Group createRequestToGroup(GroupCreateRequest request);
}
