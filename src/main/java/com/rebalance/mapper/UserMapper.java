package com.rebalance.mapper;

import com.rebalance.dto.response.UserResponse;
import com.rebalance.entities.User;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {
    UserResponse userToResponse(User user);
}
