package com.rebalance.mapper;

import com.rebalance.dto.request.GroupCreateRequest;
import com.rebalance.dto.response.GroupResponse;
import com.rebalance.entity.Group;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface GroupMapper {
    @Mapping(target = "favorite", expression = "java(group.getUsers().stream().findFirst().map(userGroup -> userGroup.getFavorite()).orElse(false))")
    GroupResponse groupToResponse(Group group);

    Group createRequestToGroup(GroupCreateRequest request);
}
