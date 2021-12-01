package com.ecommerce.userservice.domain.user;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);
    List<UserDto> findUserAll();
}
