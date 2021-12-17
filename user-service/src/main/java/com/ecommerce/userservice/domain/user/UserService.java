package com.ecommerce.userservice.domain.user;

import com.ecommerce.userservice.entity.Address;

import java.util.List;
import java.util.Optional;

public interface UserService {

    UserDto createUser(UserDto userDto);

    List<UserDto> findUserAll();

    Optional<UserDto> findUserByEmail(String email);

    UserDto modifyUserAddress(String email, Address address);

    void deleteUserByEmail(String email);

    UserDto findUserById(Long id);
}
