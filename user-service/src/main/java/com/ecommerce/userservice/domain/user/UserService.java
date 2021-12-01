package com.ecommerce.userservice.domain.user;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDto createUser(UserDto userDto);
    List<UserDto> findUserAll();
    Optional<UserDto> findUserByEmail(String email);
}
