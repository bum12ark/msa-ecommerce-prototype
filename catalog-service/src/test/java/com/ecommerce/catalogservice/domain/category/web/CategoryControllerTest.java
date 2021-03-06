package com.ecommerce.catalogservice.domain.category.web;

import com.ecommerce.catalogservice.domain.category.dto.CategoryDto;
import com.ecommerce.catalogservice.domain.category.service.CategoryService;
import com.ecommerce.catalogservice.global.config.TestConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@Import(TestConfig.class)
@AutoConfigureRestDocs(uriHost = "127.0.0.1", uriPort = 8000)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @Test
    @DisplayName("?????? ???????????? ??????")
    public void getCategoryAll() throws Exception {
        // GIVEN
        given(categoryService.findCategoryAll()).willReturn(createCategoryAllDto());

        // WHEN
        ResultActions resultActions = mockMvc.perform(get("/category"));

        FieldDescriptor[] categoryDto = getCategoryFieldDescriptor();

        resultActions.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("get-category-all",
                        responseFields(categoryDto)
                                .and(subsectionWithPath("subCategories[]")
                                        .type(JsonFieldType.ARRAY).description("?????? ????????????"))
                ))
        ;
    }

    private CategoryDto createCategoryAllDto() {
        CategoryDto rootNode = CategoryDto.createRootNode();

        CategoryDto subCategory1 = new CategoryDto(1L, "????????????", 0L);
        CategoryDto subCategory11 = new CategoryDto(4L, "?????????&??????&?????????", 1L);
        CategoryDto subCategory12 = new CategoryDto(5L, "???????????????", 1L);
        subCategory1.setSubCategories(List.of(subCategory11, subCategory12));

        CategoryDto subCategory2 = new CategoryDto(2L, "???????????????", 0L);
        CategoryDto subCategory21 = new CategoryDto(6L, "?????????", 2L);
        CategoryDto subCategory22 = new CategoryDto(7L, "??????", 2L);
        CategoryDto subCategory23 = new CategoryDto(8L, "??????", 2L);
        CategoryDto subCategory24 = new CategoryDto(9L, "??????", 2L);
        subCategory2.setSubCategories(List.of(subCategory21, subCategory22, subCategory23, subCategory24));

        CategoryDto subCategory3 = new CategoryDto(3L, "????????????", 0L);
        CategoryDto subCategory31 = new CategoryDto(10L, "????????? ?????????", 3L);
        CategoryDto subCategory32 = new CategoryDto(11L, "????????????", 3L);
        CategoryDto subCategory33 = new CategoryDto(12L, "?????????", 3L);
        subCategory3.setSubCategories(List.of(subCategory31, subCategory32, subCategory33));

        rootNode.setSubCategories(List.of(subCategory1, subCategory2, subCategory3));
        return rootNode;
    }

    @Test
    public void getParentCategories() throws Exception {
        // GIVEN
        Long leafCategoryId = 4L;
        given(categoryService.findParentCategories(leafCategoryId)).willReturn(createParentCategories());

        // WHEN
        ResultActions resultActions = mockMvc.perform(get("/category/{leafCategoryId}", leafCategoryId));

        // THEN
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("categoryId").value(0))
                .andExpect(jsonPath("name").value("???"))
                .andExpect(jsonPath("parentId").isEmpty())
                .andExpect(jsonPath("subCategories").isArray())
                .andDo(print())
                .andDo(document("get-category-parent",
                        pathParameters(
                                parameterWithName("leafCategoryId").description("???????????? ?????????")
                        ),
                        responseFields(getCategoryFieldDescriptor())
                                .and(subsectionWithPath("subCategories[]")
                                .type(JsonFieldType.ARRAY).description("?????? ????????????"))
                ))
        ;
    }

    private CategoryDto createParentCategories() {
        CategoryDto rootNode = CategoryDto.createRootNode();

        CategoryDto subCategory1 = new CategoryDto(1L, "????????????", 0L);
        CategoryDto subCategory11 = new CategoryDto(4L, "?????????&??????&?????????", 1L);

        subCategory1.addSubCategories(subCategory11);
        rootNode.addSubCategories(subCategory1);

        return rootNode;
    }

    private FieldDescriptor[] getCategoryFieldDescriptor() {
        // THEN
        return new FieldDescriptor[]{
                fieldWithPath("categoryId").description("???????????? ?????????"),
                fieldWithPath("name").description("??????"),
                fieldWithPath("parentId").description("?????? ?????????")
        };
    }

}