package com.ecommerce.userservice.domain.user;

import com.ecommerce.userservice.entity.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        // 이메일 중복 체크
        boolean exists = userRepository.existsByEmail(userDto.getEmail());
        if (exists) throw new ExistUserException();

        User user = User.builder()
                .email(userDto.getEmail())
                .name(userDto.getName())
                .address(new Address(userDto.getCity(), userDto.getStreet(), userDto.getZipcode()))
                .memberType(MemberType.NORMAL)
                .build();

        User saveUser = userRepository.save(user);

        Address saveAddress = saveUser.getAddress();
        return new UserDto(saveUser.getEmail(), saveUser.getName(), saveAddress.getCity(),
                saveAddress.getStreet(), saveAddress.getZipcode(), saveUser.getMemberType());
    }
}
