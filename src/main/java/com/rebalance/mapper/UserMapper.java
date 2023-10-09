package com.rebalance.mapper;

import com.rebalance.dto.response.UserResponse;
import com.rebalance.entities.ApplicationUser;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {
    UserResponse userToResponse(ApplicationUser user);
}
