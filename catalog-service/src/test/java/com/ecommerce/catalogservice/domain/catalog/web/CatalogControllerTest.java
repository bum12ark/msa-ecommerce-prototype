package com.ecommerce.catalogservice.domain.catalog.web;

import com.ecommerce.catalogservice.domain.catalog.dto.CatalogCategoryDto;
import com.ecommerce.catalogservice.domain.catalog.dto.CatalogDto;
import com.ecommerce.catalogservice.domain.catalog.dto.CatalogSearchCondition;
import com.ecommerce.catalogservice.domain.catalog.exception.NotExistCatalogException;
import com.ecommerce.catalogservice.domain.catalog.exception.NotExistCategoryException;
import com.ecommerce.catalogservice.domain.catalog.service.CatalogService;
import com.ecommerce.catalogservice.domain.category.dto.CategoryDto;
import com.ecommerce.catalogservice.domain.category.service.CategoryService;
import com.ecommerce.catalogservice.global.config.TestConfig;
import com.ecommerce.catalogservice.global.exception.ErrorEnum;
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
    @DisplayName("???????????? ??????")
    public void createCatalog() throws Exception {
        // GIVEN
        Long requestCatalogId = 4L;
        String requestName = "?????? ?????? ?????????";
        Integer requestPrice = 10_000;
        Integer requestStockQuantity = 100;

        CatalogController.RequestCatalog requestCatalog =
                new CatalogController.RequestCatalog(requestName, requestPrice, requestStockQuantity, requestCatalogId);

        CatalogDto catalogDto = requestCatalog.toCreateCatalogDto();
        given(catalogService.createCatalog(catalogDto, requestCatalog.getCategoryId()))
                .willReturn(catalogDto);

        String categoryName = "?????????&??????&?????????";
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
                                fieldWithPath("name").type(JsonFieldType.STRING).description("???????????? ??????"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("??????"),
                                fieldWithPath("stockQuantity").type(JsonFieldType.NUMBER).description("??????"),
                                fieldWithPath("categoryId").type(JsonFieldType.NUMBER).description("???????????? ?????????"),
                                fieldWithPath("categoryName").type(JsonFieldType.STRING).description("???????????? ??????")
                        )))
        ;
    }



    @Test
    @DisplayName("???????????? ?????? - ????????? ????????????")
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
    @DisplayName("???????????? ?????? - ?????? ???????????? ??????")
    public void createCatalogNotExistCategoryId() throws Exception {
        // GIVEN
        Long requestCatalogId = -1L;
        String requestName = "?????? ?????? ?????????";
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
    @DisplayName("?????? ???????????? ????????????")
    public void getMainCatalogs() throws Exception {
        // Given
        Long lastCatalogId = 6L;
        Long categoryId = 5L;
        String catalogName = "???????????????";
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
                                parameterWithName("categoryId").optional().description("?????? ???????????? ?????????"),
                                parameterWithName("catalogName").optional().description("?????? ???????????? ??????"),
                                parameterWithName("lastCatalogId").optional().description("????????? ???????????? ?????????")
                        ),
                        responseFields(
                                fieldWithPath("count").type(JsonFieldType.NUMBER).description("data ??? ??????")
                        )
                                .and(subsectionWithPath("data[]")
                                        .type(JsonFieldType.ARRAY).description("???????????? ?????????")
                        )
                        ));
    }

    private List<CatalogCategoryDto> getWillReturnMainCatalogs() {
        CatalogCategoryDto dto1 =
                new CatalogCategoryDto(5L, "???????????????2", 20000, 200, 5L);
        CatalogCategoryDto dto2 =
                new CatalogCategoryDto(4L, "???????????????1", 20000, 200, 5L);
        return List.of(dto1, dto2);
    }

    @Test
    @DisplayName("???????????? ?????? - catalogId IN Query")
    public void getCatalogIn() throws Exception {
        // GIVEN
        given(catalogService.findCatalogIn(anyList())).willReturn(getWillReturnCatalogIn());

        // WHEN
        ResultActions resultActions =
                mockMvc.perform(get("/catalogs/{catalogIds}", "1,2,3"));

        // THEN
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("count").value(3))
                .andExpect(jsonPath("data").exists())
                .andDo(print())
                .andDo(document("catalog-get-in",
                        pathParameters(
                                parameterWithName("catalogIds").description("???????????? ????????????")
                        ),
                        responseFields(
                                fieldWithPath("count").type(JsonFieldType.NUMBER).description("data ??? ??????")
                        )
                                .and(subsectionWithPath("data[]")
                                        .type(JsonFieldType.ARRAY).description("???????????? ?????????")
                                )
                        ))
        ;
    }

    private List<CatalogDto> getWillReturnCatalogIn() {
        CatalogDto dto1 = CatalogDto.builder().catalogId(1L).name("?????????1").price(10_000).stockQuantity(100).build();
        CatalogDto dto2 = CatalogDto.builder().catalogId(2L).name("???????????????1").price(20_000).stockQuantity(200).build();
        CatalogDto dto3 = CatalogDto.builder().catalogId(3L).name("?????????1").price(30_000).stockQuantity(300).build();
        return List.of(dto1, dto2, dto3);
    }

    @Test
    @DisplayName("???????????? ?????? - ???????????? ?????????")
    public void getCatalogByCatalogId() throws Exception {
        // Given
        Long catalogId = 1L;

        given(catalogService.getCatalogByCatalogId(catalogId))
                .willReturn(new CatalogDto(1L, "??????1", 10_000, 100));

        // When
        ResultActions resultActions = mockMvc.perform(get("/catalog/{catalogId}", catalogId));

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("catalogId").value(1))
                .andExpect(jsonPath("name").value("??????1"))
                .andExpect(jsonPath("price").value(10_000))
                .andExpect(jsonPath("stockQuantity").value(100))
                .andDo(print())
                .andDo(document("catalog-get-byId",
                        pathParameters(
                                parameterWithName("catalogId").description("???????????? ?????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("catalogId").description("???????????? ????????????"),
                                fieldWithPath("name").description("???????????? ??????"),
                                fieldWithPath("price").description("??????"),
                                fieldWithPath("stockQuantity").description("??????")
                        )
                ));
    }

    @Test
    @DisplayName("???????????? ?????? - ???????????? ????????? (DB ?????? ???????????? ??????)")
    public void getCatalogByCatalogIdNotExistCatalogException() throws Exception {
        // Given
        Long notExistId = 100L;

        given(catalogService.getCatalogByCatalogId(notExistId))
                .willThrow(new NotExistCatalogException());

        // When
        ResultActions resultActions = mockMvc.perform(get("/catalog/{catalogId}", notExistId));

        // Then
        resultActions.andExpect(status().isConflict())
                .andDo(print())
                .andDo(document("catalog-get-byId-NotExistException",
                        pathParameters(
                                parameterWithName("catalogId").description("???????????? ?????? ??????")
                        ),
                        responseFields(
                                getErrorDescription()
                        )
                ));
    }


    private FieldDescriptor[] getRequestFieldDescriptor() {
        return new FieldDescriptor[]{fieldWithPath("name").type(JsonFieldType.STRING).description("???????????? ??????"),
                fieldWithPath("price").type(JsonFieldType.NUMBER).description("??????"),
                fieldWithPath("stockQuantity").type(JsonFieldType.NUMBER).description("??????"),
                fieldWithPath("categoryId").type(JsonFieldType.NUMBER).description("???????????? ?????????")};
    }

    private FieldDescriptor getErrorDescription() {
        return fieldWithPath("message").description("?????? ?????????");
    }
}
