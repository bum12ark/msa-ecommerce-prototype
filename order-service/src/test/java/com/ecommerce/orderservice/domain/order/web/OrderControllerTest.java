package com.ecommerce.orderservice.domain.order.web;

import com.ecommerce.orderservice.config.TestConfig;
import com.ecommerce.orderservice.domain.order.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import(TestConfig.class)
@AutoConfigureRestDocs(uriHost = "127.0.0.1", uriPort = 8000)
class OrderControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    OrderService orderService;

    @Test
    public void createOrder() throws Exception {
        // GIVEN
        OrderController.RequestOrderLine requestOrderLine1
                = new OrderController.RequestOrderLine(2L, 10, 10_000);
        OrderController.RequestOrderLine requestOrderLine2
                = new OrderController.RequestOrderLine(3L, 20, 20_000);

        OrderController.RequestDelivery requestDelivery
                = new OrderController.RequestDelivery("서울시", "광화문로", "123-456");

        OrderController.RequestOrder requestOrder
                = new OrderController.RequestOrder(1L, requestDelivery, List.of(requestOrderLine1, requestOrderLine2));

        given(orderService.createOrder(any()))
                .willReturn(requestOrder.toCreateOrderDto());

        // WHEN
        ResultActions resultActions = mockMvc.perform(post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(requestOrder))
        );

        // THEN
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("userId").value(1L))
                .andExpect(jsonPath("delivery.city").value("서울시"))
                .andExpect(jsonPath("delivery.street").value("광화문로"))
                .andExpect(jsonPath("delivery.zipcode").value("123-456"))
                .andExpect(jsonPath("orderLines").isArray())
                .andDo(print())
                .andDo(document("order-post",
                        requestFields(
                                fieldWithPath("userId").description("회원 고유번호"),
                                fieldWithPath("delivery.city").description("도시"),
                                fieldWithPath("delivery.street").description("거리"),
                                fieldWithPath("delivery.zipcode").description("우편번호"),
                                fieldWithPath("orderLines[].catalogId").description("주문 카탈로그 아이디"),
                                fieldWithPath("orderLines[].count").description("주문 수량"),
                                fieldWithPath("orderLines[].orderPrice").description("주문 가격")
                        ),
                        responseFields(
                            fieldWithPath("userId").description("회원 고유번호"),
                                fieldWithPath("delivery.city").description("도시"),
                                fieldWithPath("delivery.street").description("거리"),
                                fieldWithPath("delivery.zipcode").description("우편번호"),
                                fieldWithPath("orderLines[].catalogId").description("주문 카탈로그 아이디"),
                                fieldWithPath("orderLines[].count").description("주문 수량"),
                                fieldWithPath("orderLines[].orderPrice").description("주문 가격")
                        )
                        ))
        ;
    }

}