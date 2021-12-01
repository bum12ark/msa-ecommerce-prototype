package com.ecommerce.userservice.domain.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final ModelMapper modelMapper;
    private final UserService userService;

    @PostMapping("/users")
    public ResponseEntity<ResponseUser> createUser(@RequestBody @Valid RequestUser requestUser) {
        UserDto requestUserDto = modelMapper.map(requestUser, UserDto.class);

        UserDto createdUserDto = userService.createUser(requestUserDto);

        ResponseUser responseUser = new ResponseUser(createdUserDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

    @GetMapping("/users")
    public Result<List<ResponseUser>> getUsers() {

        List<ResponseUser> responseUsers = userService.findUserAll()
                .stream()
                .map(ResponseUser::new)
                .collect(Collectors.toList());

        return new Result<>(responseUsers.size(), responseUsers);
    }

    @GetMapping("/users/{email}")
    public ResponseEntity<ResponseUser> getUser(@PathVariable("email") String email) {

        Optional<UserDto> findUser = userService.findUserByEmail(email);

        return findUser.map(userDto -> ResponseEntity.status(HttpStatus.OK).body(new ResponseUser(userDto)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.OK).build());
    }

    @Data @AllArgsConstructor
    static class Result<T> {
        private Integer count;
        private T data;
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

        public ResponseUser(UserDto userDto) {
            this.email = userDto.getEmail();
            this.name = userDto.getName();
            this.city = userDto.getCity();
            this.street = userDto.getStreet();
            this.zipcode = userDto.getZipcode();
            this.memberType = userDto.getMemberType();
        }
    }
}
