package com.rebalance.mapper;

import com.rebalance.dto.request.GroupCreateRequest;
import com.rebalance.dto.response.GroupResponse;
import com.rebalance.dto.response.GroupWithFavoriteResponse;
import com.rebalance.entity.Group;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface GroupMapper {
    GroupResponse groupToResponse(Group group);

    @Mapping(target = "favorite", expression = "java(group.getUsers().stream().findFirst().map(userGroup -> userGroup.getFavorite()).orElse(false))")
    GroupWithFavoriteResponse groupToResponseWithFavorite(Group group);

    Group createRequestToGroup(GroupCreateRequest request);
}
