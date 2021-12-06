package com.ecommerce.catalogservice.domain.category;

import com.ecommerce.catalogservice.config.TestConfig;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
    @DisplayName("모든 카테고리 조회")
    public void getCategoryAll() throws Exception {
        // GIVEN
        given(categoryService.findCategoryAll()).willReturn(createCategoryAllDto());

        // WHEN
        ResultActions resultActions = mockMvc.perform(get("/category"));

        // THEN
        FieldDescriptor[] categoryDto = {
                fieldWithPath("categoryId").description("카테고리 아이디"),
                fieldWithPath("name").description("이름"),
                fieldWithPath("parentId").description("부모 아이디")
        };

        resultActions.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("get-category-all",
                        responseFields(categoryDto)
                                .and(subsectionWithPath("subCategories[]")
                                        .type(JsonFieldType.ARRAY).description("자식 카테고리"))
                ))
        ;
    }

    private CategoryDto createCategoryAllDto() {
        CategoryDto rootNode = CategoryDto.createRootNode();

        CategoryDto subCategory1 = new CategoryDto(1L, "헬스용품", 0L);
        CategoryDto subCategory11 = new CategoryDto(4L, "쉐이커&물통&케이스", 1L);
        CategoryDto subCategory12 = new CategoryDto(5L, "홈트레이닝", 1L);
        subCategory1.setSubCategories(List.of(subCategory11, subCategory12));

        CategoryDto subCategory2 = new CategoryDto(2L, "스포츠웨어", 0L);
        CategoryDto subCategory21 = new CategoryDto(6L, "티셔츠", 2L);
        CategoryDto subCategory22 = new CategoryDto(7L, "나시", 2L);
        CategoryDto subCategory23 = new CategoryDto(8L, "하의", 2L);
        CategoryDto subCategory24 = new CategoryDto(9L, "모자", 2L);
        subCategory2.setSubCategories(List.of(subCategory21, subCategory22, subCategory23, subCategory24));

        CategoryDto subCategory3 = new CategoryDto(3L, "헬스용품", 0L);
        CategoryDto subCategory31 = new CategoryDto(10L, "단백질 보충제", 3L);
        CategoryDto subCategory32 = new CategoryDto(11L, "다이어트", 3L);
        CategoryDto subCategory33 = new CategoryDto(12L, "비타민", 3L);
        subCategory3.setSubCategories(List.of(subCategory31, subCategory32, subCategory33));

        rootNode.setSubCategories(List.of(subCategory1, subCategory2, subCategory3));
        return rootNode;
    }

}