package com.ecommerce.userservice.domain.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
public class UserDto {
    private String email;
    private String name;
    private String city;
    private String street;
    private String zipcode;
    private MemberType memberType;
}
