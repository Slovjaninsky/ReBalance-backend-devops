package com.rebalance.mapper;

import com.rebalance.dto.request.UserCreateRequest;
import com.rebalance.dto.response.GroupUserResponse;
import com.rebalance.dto.response.UserGroupResponse;
import com.rebalance.dto.response.UserResponse;
import com.rebalance.entity.User;
import com.rebalance.entity.UserGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserMapper {
    @Mapping(target = "personalGroupId", expression = "java(user.getCreatedGroups().stream().filter(Group::getPersonal).findFirst().get().getId())")
    UserResponse userToResponse(User user);

    GroupUserResponse userToGroupResponse(User user);

    User createRequestToUser(UserCreateRequest request);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "groupId", source = "group.id")
    UserGroupResponse userGroupToResponse(UserGroup userGroup);
}
