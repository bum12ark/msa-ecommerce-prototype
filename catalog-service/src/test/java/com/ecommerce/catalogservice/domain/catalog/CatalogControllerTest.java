package com.ecommerce.catalogservice.domain.catalog;

import com.ecommerce.catalogservice.config.TestConfig;
import com.ecommerce.catalogservice.domain.category.CategoryDto;
import com.ecommerce.catalogservice.domain.category.CategoryService;
import com.ecommerce.catalogservice.exception.ErrorEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CatalogController.class)
@Import(TestConfig.class)
@AutoConfigureRestDocs(uriHost = "127.0.0.1", uriPort = 8000)
class CatalogControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CatalogService catalogService;

    @MockBean
    private CategoryService categoryService;

    @Test
    @DisplayName("카탈로그 생성")
    public void createCatalog() throws Exception {
        // GIVEN
        Long requestCatalogId = 4L;
        String requestName = "힘이 불끈 쉐이커";
        Integer requestPrice = 10_000;
        Integer requestStockQuantity = 100;

        CatalogController.RequestCatalog requestCatalog =
                new CatalogController.RequestCatalog(requestName, requestPrice, requestStockQuantity, requestCatalogId);

        CatalogDto catalogDto = requestCatalog.toCreateCatalogDto();
        given(catalogService.createCatalog(catalogDto, requestCatalog.getCategoryId()))
                .willReturn(catalogDto);

        String categoryName = "쉐이커&물통&케이스";
        given(categoryService.findById(requestCatalogId))
                .willReturn(new CategoryDto(requestCatalogId, categoryName, 0L));

        // WHEN
        ResultActions resultActions = mockMvc.perform(post("/catalog")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(requestCatalog)));

        // THEN
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("name").value(requestName))
                .andExpect(jsonPath("price").value(requestPrice))
                .andExpect(jsonPath("stockQuantity").value(requestStockQuantity))
                .andExpect(jsonPath("categoryId").value(requestCatalogId))
                .andExpect(jsonPath("categoryName").value(categoryName))
                .andDo(print())
                .andDo(document("post-catalog",
                        requestFields(
                                getRequestFieldDescriptor()
                        ),
                        responseFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("카탈로그 이름"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("가격"),
                                fieldWithPath("stockQuantity").type(JsonFieldType.NUMBER).description("수량"),
                                fieldWithPath("categoryId").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                                fieldWithPath("categoryName").type(JsonFieldType.STRING).description("카테고리 이름")
                        )))
        ;
    }



    @Test
    @DisplayName("카탈로그 생성 - 잘못된 파라미터")
    public void createCatalogInvalidParameter() throws Exception {
        // GIVEN
        CatalogController.RequestCatalog requestCatalog =
                new CatalogController.RequestCatalog("", -20, -1, 0L);

        // WHEN
        ResultActions resultActions = mockMvc.perform(post("/catalog")
                .content(objectMapper.writeValueAsString(requestCatalog))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // THEN
        resultActions.andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(document("catalog-post-invalidParameter",
                        requestFields(getRequestFieldDescriptor())));
    }
    
    @Test
    @DisplayName("카탈로그 생성 - 없는 카테고리 번호")
    public void createCatalogNotExistCategoryId() throws Exception {
        // GIVEN
        Long requestCatalogId = -1L;
        String requestName = "힘이 불끈 쉐이커";
        Integer requestPrice = 10_000;
        Integer requestStockQuantity = 100;

        CatalogController.RequestCatalog requestCatalog =
                new CatalogController.RequestCatalog(requestName, requestPrice, requestStockQuantity, requestCatalogId);

        given(catalogService.createCatalog(requestCatalog.toCreateCatalogDto(), requestCatalogId))
                .willThrow(new NotExistCategoryException());

        // WHEN
        ResultActions resultActions = mockMvc.perform(post("/catalog")
                .content(objectMapper.writeValueAsString(requestCatalog))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
        
        // THEN
        resultActions.andExpect(status().isConflict())
                .andExpect(jsonPath("message").value(ErrorEnum.NOT_EXIST_CATEGORY.getMessage()))
                .andDo(print())
                .andDo(document("catalog-post-notExistCategoryId",
                        requestFields(getRequestFieldDescriptor()),
                        responseFields(getErrorDescription())
                        ));
    }

    @Test
    @DisplayName("메인 카탈로그 가져오기")
    public void getMainCatalogs() throws Exception {
        // Given
        Long lastCatalogId = 6L;
        Long categoryId = 5L;
        String catalogName = "홈트레이닝";
        CatalogSearchCondition condition = new CatalogSearchCondition(categoryId, catalogName);

        given(catalogService.findCatalogSearch(any(CatalogSearchCondition.class), anyLong()))
                .willReturn(getWillReturnMainCatalogs());

        // When
        ResultActions resultActions = mockMvc.perform(get("/catalog/main")
                .param("categoryId", condition.getCategoryId().toString())
                .param("catalogName", condition.getCatalogName())
                .param("lastCatalogId", lastCatalogId.toString())
        );

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("count").exists())
                .andExpect(jsonPath("data").exists())
                .andDo(print())
                .andDo(document("catalog-get-main",
                        requestParameters(
                                parameterWithName("categoryId").optional().description("검색 카테고리 아이디"),
                                parameterWithName("catalogName").optional().description("검색 카탈로그 이름"),
                                parameterWithName("lastCatalogId").optional().description("마지막 카탈로그 아이디")
                        ),
                        responseFields(
                                fieldWithPath("count").type(JsonFieldType.NUMBER).description("data 총 개수")
                        )
                                .and(subsectionWithPath("data[]")
                                        .type(JsonFieldType.ARRAY).description("카탈로그 데이터")
                        )
                        ));
    }

    private List<CatalogCategoryDto> getWillReturnMainCatalogs() {
        CatalogCategoryDto dto1 =
                new CatalogCategoryDto(5L, "홈트레이닝2", 20000, 200, 5L);
        CatalogCategoryDto dto2 =
                new CatalogCategoryDto(4L, "홈트레이닝1", 20000, 200, 5L);
        return List.of(dto1, dto2);
    }

    @Test
    @DisplayName("카탈로그 조회 - catalogId IN Query")
    public void getCatalogIn() throws Exception {
        // GIVEN
        given(catalogService.findCatalogIn(anyList())).willReturn(getWillReturnCatalogIn());

        // WHEN
        ResultActions resultActions =
                mockMvc.perform(get("/catalog/{catalogIds}", "1,2,3"));

        // THEN
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("count").value(3))
                .andExpect(jsonPath("data").exists())
                .andDo(print())
                .andDo(document("catalog-get-in",
                        pathParameters(
                                parameterWithName("catalogIds").description("카탈로그 아이디들")
                        ),
                        responseFields(
                                fieldWithPath("count").type(JsonFieldType.NUMBER).description("data 총 개수")
                        )
                                .and(subsectionWithPath("data[]")
                                        .type(JsonFieldType.ARRAY).description("카탈로그 데이터")
                                )
                        ))
        ;
    }

    private List<CatalogDto> getWillReturnCatalogIn() {
        CatalogDto dto1 = CatalogDto.builder().catalogId(1L).name("쉐이커1").price(10_000).stockQuantity(100).build();
        CatalogDto dto2 = CatalogDto.builder().catalogId(2L).name("홈트레이닝1").price(20_000).stockQuantity(200).build();
        CatalogDto dto3 = CatalogDto.builder().catalogId(3L).name("티셔츠1").price(30_000).stockQuantity(300).build();
        return List.of(dto1, dto2, dto3);
    }

    private FieldDescriptor[] getRequestFieldDescriptor() {
        return new FieldDescriptor[]{fieldWithPath("name").type(JsonFieldType.STRING).description("카탈로그 이름"),
                fieldWithPath("price").type(JsonFieldType.NUMBER).description("가격"),
                fieldWithPath("stockQuantity").type(JsonFieldType.NUMBER).description("수량"),
                fieldWithPath("categoryId").type(JsonFieldType.NUMBER).description("카테고리 아이디")};
    }

    private FieldDescriptor getErrorDescription() {
        return fieldWithPath("message").description("에러 메시지");
    }
}
