package com.rebalance.mapper;

import com.rebalance.dto.request.GroupCreateRequest;
import com.rebalance.dto.response.GroupResponse;
import com.rebalance.entity.Group;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface GroupMapper {
    GroupResponse groupToResponse(Group group);

    @Mapping(target = "creator.id", source = "creatorId")
    Group createRequestToGroup(GroupCreateRequest request);
}
