package com.ecommerce.userservice.domain.user;

import com.ecommerce.userservice.exception.ErrorEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.discovery.converters.Auto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ModelMapper mockModelMapper;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("회원 등록 성공 테스트")
    void createUserSuccess() throws Exception {
        ModelMapper modelMapper = new ModelMapper();
        // given
        UserController.RequestUser requestUser =
                new UserController.RequestUser("testId@gmail.com", "홍길동",
                        "서울시", "광화문로", "111-11");
        String requestJson = objectMapper.writeValueAsString(requestUser);

        UserDto userDto = new UserDto("testId@gmail.com", "홍길동",
                "서울시", "광화문로", "111-11", MemberType.NORMAL);

        given(userService.createUser(userDto)).willReturn(userDto);
        given(mockModelMapper.map(any(), eq(UserDto.class))).willReturn(userDto);
        given(mockModelMapper.map(any(), eq(UserController.ResponseUser.class)))
                .willReturn(modelMapper.map(userDto, UserController.ResponseUser.class));

        // when
        ResultActions actions = mockMvc.perform(post("/users")
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        actions.andExpect(status().isCreated())
                .andExpect(jsonPath("email").value("testId@gmail.com"))
                .andDo(print());
    }

    @Test
    @DisplayName("회원 등록 중복 회원")
    void createUserDuplicate() throws Exception {
        // Given
        UserController.RequestUser requestUser =
                new UserController.RequestUser("testId@gmail.com", "홍길동",
                        "서울시", "광화문로", "111-11");
        String requestJson = objectMapper.writeValueAsString(requestUser);

        given(userService.createUser(any())).willThrow(new ExistUserException());

        // When
        ResultActions actions = mockMvc.perform(post("/users")
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // Then
        actions.andExpect(status().isConflict())
                .andExpect(jsonPath("message").value(ErrorEnum.EXIST_USER.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("회원 등록 파라미터 검증 오류")
    void createUserInvalidParameter() throws Exception {
        // GIVEN
        UserController.RequestUser requestUser =
                new UserController.RequestUser("testId", null,
                        "서울시", "광화문로", "111-11");
        String requestJson = objectMapper.writeValueAsString(requestUser);

        // WHEN
        ResultActions actions = mockMvc.perform(post("/users")
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // THEN
        actions.andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("모든 회원 정보 조회")
    public void getUserAll() throws Exception {
        // GIVEN
        List<UserDto> willReturnList = new ArrayList<>();
        willReturnList.add(new UserDto("testId@gmail.com", "홍길동"
                , "서울시", "광화문로", "111-11", MemberType.NORMAL));
        willReturnList.add(new UserDto("testId@naver.com", "김유신"
                , "전주시", "천마산로", "999-99", MemberType.NORMAL));

        given(userService.findUserAll()).willReturn(willReturnList);

        // WHEN
        ResultActions actions = mockMvc.perform(get("/users"));

        // THEN
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("count").value(2))
                .andExpect(jsonPath("data[0].email").value("testId@gmail.com"))
                .andDo(print());
    }

    @Test
    @DisplayName("단일 회원 조회")
    public void getUserByEmail() throws Exception {
        // GIVEN
        String email = "testId@gmail.com";
        UserDto willReturnUserDto = new UserDto("testId@gmail.com", "홍길동"
                , "서울시", "광화문로", "111-11", MemberType.NORMAL);
        Optional<UserDto> willReturnOptional = Optional.of(willReturnUserDto);

        given(userService.findUserByEmail(email)).willReturn(willReturnOptional);

        // WHEN
        ResultActions actions = mockMvc.perform(get("/users/{email}", email));

        // THEN
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("email").value(willReturnUserDto.getEmail()))
                .andExpect(jsonPath("name").value(willReturnUserDto.getName()))
                .andExpect(jsonPath("city").value(willReturnUserDto.getCity()))
                .andExpect(jsonPath("street").value(willReturnUserDto.getStreet()))
                .andExpect(jsonPath("zipcode").value(willReturnUserDto.getZipcode()))
                .andExpect(jsonPath("memberType").value(willReturnUserDto.getMemberType().toString()))
                .andDo(print());
    }

    @Test
    @DisplayName("단일 회원 조회 - 없는 회원")
    public void getUserByEmailNoSuchUser() throws Exception {
        // GIVEN
        String email = "noSuchEmail@gmail.com";
        Optional<UserDto> willReturnDto = Optional.of(new UserDto());
        given(userService.findUserByEmail(email)).willReturn(willReturnDto);

        // WHEN
        ResultActions actions = mockMvc.perform(get("/users/{email}", email));

        // THEN
        actions.andExpect(status().isOk())
                .andDo(print());

    }
}