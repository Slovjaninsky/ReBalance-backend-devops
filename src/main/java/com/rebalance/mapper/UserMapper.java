package com.rebalance.mapper;

import com.rebalance.dto.request.UserCreateRequest;
import com.rebalance.dto.response.*;
import com.rebalance.entity.User;
import com.rebalance.entity.UserGroup;
import com.rebalance.mapper.converter.DecimalConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(uses = DecimalConverter.class)
public interface UserMapper {
    @Mapping(target = "personalGroupId", expression = "java(user.getCreatedGroups().stream().filter(Group::getPersonal).findFirst().get().getId())")
    @Mapping(target = "currency", expression = "java(user.getCreatedGroups().stream().filter(Group::getPersonal).findFirst().get().getCurrency())")
    UserResponse userToResponse(User user);

    @Mapping(target = "personalGroupId", expression = "java(user.getCreatedGroups().stream().filter(Group::getPersonal).findFirst().get().getId())")
    UserWithTokenResponse userToResponseWithToken(User user, String token);

    @Mapping(target = "balance", source = "balance", qualifiedByName = "bigDecimalToDouble")
    GroupUserResponse userToGroupResponse(User user, BigDecimal balance);

    User createRequestToUser(UserCreateRequest request);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "groupId", source = "group.id")
    UserGroupResponse userGroupToResponse(UserGroup userGroup);

    LoginResponse tokenToResponse(String token);
}
