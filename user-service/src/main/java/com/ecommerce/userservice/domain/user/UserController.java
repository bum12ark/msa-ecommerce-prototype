package com.ecommerce.userservice.domain.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final ModelMapper modelMapper;
    private final UserService userService;

    @PostMapping("/users")
    public ResponseEntity<ResponseUser> createUser(@RequestBody @Valid RequestUser requestUser) {
        UserDto requestUserDto = modelMapper.map(requestUser, UserDto.class);

        UserDto createdUserDto = userService.createUser(requestUserDto);

        ResponseUser responseUser = modelMapper.map(createdUserDto, ResponseUser.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

    @Data @AllArgsConstructor @NoArgsConstructor
    static class RequestUser {
        @Email @NotNull
        private String email;
        @NotNull
        private String name;
        @NotNull
        private String city;
        @NotNull
        private String street;
        @NotNull
        private String zipcode;
    }

    @Data @AllArgsConstructor @NoArgsConstructor
    static class ResponseUser {
        private String email;
        private String name;
        private String city;
        private String street;
        private String zipcode;
        private MemberType memberType;
    }
}
