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
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
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
}