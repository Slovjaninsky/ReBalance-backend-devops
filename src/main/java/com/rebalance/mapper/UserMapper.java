package com.rebalance.mapper;

import com.rebalance.dto.request.UserCreateRequest;
import com.rebalance.dto.response.UserResponse;
import com.rebalance.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserMapper {
    @Mapping(target = "expenseGroups", source = "groups")
    UserResponse userToResponse(User user);

    User createRequestToUser(UserCreateRequest request);
}
