package com.ecommerce.userservice.domain.user;

import com.ecommerce.userservice.entity.Address;
import com.ecommerce.userservice.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
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

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Embedded
    private Address address;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberType memberType;

    @Builder
    public User(String email, String name, Address address, MemberType memberType) {
        this.email = email;
        this.name = name;
        this.address = address;
        this.memberType = memberType;
    }
}
