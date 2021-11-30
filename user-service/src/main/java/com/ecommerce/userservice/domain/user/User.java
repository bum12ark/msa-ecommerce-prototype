package com.ecommerce.userservice.domain.user;

import com.ecommerce.userservice.entity.Address;
import com.ecommerce.userservice.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User extends BaseEntity {

    @Id @GeneratedValue
    private Long id;
    private String name;
    @Embedded
    private Address address;
    @Enumerated(EnumType.STRING)
    private MemberType memberType;
}
