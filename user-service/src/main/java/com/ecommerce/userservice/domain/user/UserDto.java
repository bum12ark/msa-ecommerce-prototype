package com.ecommerce.userservice.domain.user;

import com.ecommerce.userservice.entity.Address;
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

    public UserDto(User user) {
        Address address = user.getAddress();

        this.email = user.getEmail();
        this.name = user.getName();
        this.city = address.getCity();
        this.street = address.getStreet();
        this.zipcode = address.getZipcode();
        this.memberType = user.getMemberType();
    }
}
