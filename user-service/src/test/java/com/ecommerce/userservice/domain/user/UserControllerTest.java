package com.ecommerce.userservice.domain.user;

import com.ecommerce.userservice.config.TestConfig;
import com.ecommerce.userservice.entity.Address;
import com.ecommerce.userservice.exception.ErrorEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(TestConfig.class)
@AutoConfigureRestDocs(uriHost = "127.0.0.1", uriPort = 8000)
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
                .andDo(print())
                .andDo(document("user-post",
                        requestFields(
                                getRequestUserFieldDescriptors()
                        ),
                        responseFields(
                                getResponseUserFieldDescriptors()
                        )
                ))
        ;
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
                .andDo(print())
                .andDo(document("user-post-duplicate",
                        requestFields(
                                getRequestUserFieldDescriptors()
                        ),
                        responseFields(
                                getErrorDescription()
                        )))
        ;
    }

    @Test
    @DisplayName("회원 등록 파라미터 검증 오류")
    void createUserInvalidParameter() throws Exception {
        // GIVEN
        UserController.RequestUser requestUser =
                new UserController.RequestUser("testId", "",
                        "서울시", "광화문로", "111-11");
        String requestJson = objectMapper.writeValueAsString(requestUser);

        // WHEN
        ResultActions actions = mockMvc.perform(post("/users")
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // THEN
        actions.andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(document("user-post-invalidParameter",
                        requestFields(getRequestUserFieldDescriptors())))
        ;
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
                .andDo(print())
                .andDo(document("user-get-allUser",
                        responseFields(
                                fieldWithPath("count").type(JsonFieldType.NUMBER).description("data 총 개수"),
                                fieldWithPath("data[].email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("data[].name").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("data[].city").type(JsonFieldType.STRING).description("도시"),
                                fieldWithPath("data[].street").type(JsonFieldType.STRING).description("거리"),
                                fieldWithPath("data[].zipcode").type(JsonFieldType.STRING).description("우편번호"),
                                fieldWithPath("data[].memberType").type(JsonFieldType.STRING).description("회원 등급")
                        )))
        ;
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
                .andDo(print())
                .andDo(document("user-get",
                        pathParameters(parameterWithName("email").description("유저 이메일")),
                        responseFields(getResponseUserFieldDescriptors())
                        ))
        ;
    }

    @Test
    @DisplayName("단일 회원 조회 - 없는 회원")
    public void getUserByEmailNoSuchUser() throws Exception {
        // GIVEN
        String email = "noSuchEmail@gmail.com";
        given(userService.findUserByEmail(email)).willReturn(Optional.empty());

        // WHEN
        ResultActions actions = mockMvc.perform(get("/users/{email}", email));

        // THEN
        actions.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("user-get-notExist",
                        pathParameters(parameterWithName("email").description("유저 이메일"))
                        ))
        ;

    }

    @Test
    @DisplayName("회원 수정")
    public void modifyUserByEmail() throws Exception {
        // Given
        String email = "testId@gmail.com";
        Address address = new Address("전주시", "오송로", "999-99");
        String requestJson = objectMapper.writeValueAsString(
                new UserController.RequestModifyUser(address.getCity(), address.getStreet(), address.getZipcode())
        );
        UserDto willReturnDto = new UserDto("testId@gmail.com", "홍길동",
                address.getCity(), address.getStreet(), address.getZipcode(), MemberType.NORMAL);

        given(userService.modifyUserAddress(anyString(), any())).willReturn(willReturnDto);

        // When
        ResultActions actions = mockMvc.perform(patch("/users/{email}", email)
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // Then
        actions.andExpect(status().isCreated())
                .andExpect(jsonPath("email").value(willReturnDto.getEmail()))
                .andExpect(jsonPath("name").value(willReturnDto.getName()))
                .andExpect(jsonPath("city").value(willReturnDto.getCity()))
                .andExpect(jsonPath("street").value(willReturnDto.getStreet()))
                .andExpect(jsonPath("zipcode").value(willReturnDto.getZipcode()))
                .andExpect(jsonPath("memberType").value(willReturnDto.getMemberType().toString()))
                .andDo(print())
                .andDo(document("user-patch",
                        pathParameters( // pathVariable
                                parameterWithName("email").description("유저 이메일")
                        ),
                        requestFields( // 요청 필드
                                fieldWithPath("city").type(JsonFieldType.STRING).description("회원 도시"),
                                fieldWithPath("street").type(JsonFieldType.STRING).description("회원 거리"),
                                fieldWithPath("zipcode").type(JsonFieldType.STRING).description("회원 우편번호")
                        ),
                        responseFields( // 응답필드
                                getResponseUserFieldDescriptors()
                        )))
        ;
    }

    @Test
    @DisplayName("회원 수정 -  존재하지 않는 회원")
    public void modifyUserNotExistUser() throws Exception {
        // Given
        String email = "notExistUser@gmail.com";
        Address address = new Address("전주시", "오송로", "999-99");
        String requestJson = objectMapper.writeValueAsString(
                new UserController.RequestModifyUser(address.getCity(), address.getStreet(), address.getZipcode())
        );

        given(userService.modifyUserAddress(anyString(), any())).willThrow(new NotExistUserException());

        // When
        ResultActions actions = mockMvc.perform(patch("/users/{email}", email)
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // Then
        actions.andExpect(status().isConflict())
                .andExpect(jsonPath("message").value(ErrorEnum.NOT_EXIST_USER.getMessage()))
                .andDo(print())
                .andDo(document("user-patch-notExist",
                        pathParameters(parameterWithName("email").description("이메일")),
                        requestFields(
                                fieldWithPath("city").type(JsonFieldType.STRING).description("회원 도시"),
                                fieldWithPath("street").type(JsonFieldType.STRING).description("회원 거리"),
                                fieldWithPath("zipcode").type(JsonFieldType.STRING).description("회원 우편번호")
                        ),
                        responseFields(getErrorDescription())
                        ))
        ;
    }

    @Test
    @DisplayName("회원 삭제 성공")
    public void deleteUserByEmail() throws Exception {
        // Given
        String email = "testId@gmail.com";

        // When
        ResultActions actions = mockMvc.perform(delete("/users/{email}", email));

        // Then
        actions.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("user-delete",
                        pathParameters(parameterWithName("email").description("이메일")))
                        )
        ;
    }

    @Test
    @DisplayName("회원 삭제 - 존재하지 않는 회원")
    public void deleteUserByEmailNotExistUser() throws Exception {
        // Given
        String email = "notExistUser@gmail.com";
        willThrow(new NotExistUserException()).given(userService).deleteUserByEmail(email);

        // When
        ResultActions actions = mockMvc.perform(delete("/users/{email}", email));

        // Then
        actions.andExpect(status().isConflict())
                .andDo(print())
                .andDo(document("user-delete-notExist",
                        pathParameters(
                            parameterWithName("email").description("유저 이메일")
                        ),
                        responseFields(getErrorDescription())
                ))
        ;
    }

    private FieldDescriptor getErrorDescription() {
        return fieldWithPath("message").description("에러 메시지");
    }

    private FieldDescriptor[] getRequestUserFieldDescriptors() {
        return new FieldDescriptor[]{fieldWithPath("email").description("회원 이메일"),
                fieldWithPath("name").type(JsonFieldType.STRING).description("회원 이름"),
                fieldWithPath("city").type(JsonFieldType.STRING).description("회원 도시"),
                fieldWithPath("street").type(JsonFieldType.STRING).description("회원 거리"),
                fieldWithPath("zipcode").type(JsonFieldType.STRING).description("회원 우편번호")};
    }

    private FieldDescriptor[] getResponseUserFieldDescriptors() {
        return new FieldDescriptor[]{fieldWithPath("email").description("회원 이메일"),
                fieldWithPath("name").type(JsonFieldType.STRING).description("회원 이름"),
                fieldWithPath("city").type(JsonFieldType.STRING).description("회원 도시"),
                fieldWithPath("street").type(JsonFieldType.STRING).description("회원 거리"),
                fieldWithPath("zipcode").type(JsonFieldType.STRING).description("회원 우편번호"),
                fieldWithPath("memberType").type(JsonFieldType.STRING).description("회원 등급")};
    }
}