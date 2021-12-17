package com.ecommerce.userservice.domain.user;

import com.ecommerce.userservice.entity.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


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

        return new UserDto(saveUser);
    }

    @Override
    public List<UserDto> findUserAll() {
        return userRepository.findAll()
                .stream()
                .map(UserDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDto> findUserByEmail(String email) {
        return userRepository.findByEmail(email).map(UserDto::new);
    }

    @Override
    @Transactional
    public UserDto modifyUserAddress(String email, Address address) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(NotExistUserException::new);

        user.modifyAddress(new Address(address.getCity(), address.getStreet(), address.getZipcode()));

        return new UserDto(user);
    }

    @Override
    @Transactional
    public void deleteUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(NotExistUserException::new);

        userRepository.delete(user);
    }

    @Override
    public UserDto findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(NotExistUserException::new);

        return new UserDto(user);
    }
}
