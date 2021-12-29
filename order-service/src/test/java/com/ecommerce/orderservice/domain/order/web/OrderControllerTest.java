package com.ecommerce.orderservice.domain.order.web;

import com.ecommerce.orderservice.config.TestConfig;
import com.ecommerce.orderservice.domain.order.dto.OrderDto;
import com.ecommerce.orderservice.domain.order.entity.OrderStatus;
import com.ecommerce.orderservice.domain.order.exception.NotExistOrder;
import com.ecommerce.orderservice.domain.order.service.OrderService;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
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
    @DisplayName("주문 생성")
    public void createOrder() throws Exception {
        // GIVEN
        OrderController.RequestOrder requestOrder = getRequestOrder();

        OrderDto willReturnDto = requestOrder.toCreateOrderDto();
        ReflectionTestUtils.setField(willReturnDto, "orderStatus", OrderStatus.PENDING);
        given(orderService.createOrder(any())).willReturn(willReturnDto);

        // WHEN
        ResultActions resultActions = mockMvc.perform(post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(requestOrder))
        );

        // THEN
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("message").value("OK"))
                .andExpect(jsonPath("data.userId").value(1L))
                .andExpect(jsonPath("data.orderStatus").value(OrderStatus.PENDING.toString()))
                .andExpect(jsonPath("data.delivery.city").value("서울시"))
                .andExpect(jsonPath("data.delivery.street").value("광화문로"))
                .andExpect(jsonPath("data.delivery.zipcode").value("123-456"))
                .andExpect(jsonPath("data.orderLines").isArray())
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
                        responseFields(getFieldDescriptors())
                        ))
        ;
    }

    @Test
    @DisplayName("주문 취소")
    public void cancelOrder() throws Exception {
        // GIVEN
        Long orderId = 1L;

        OrderDto willReturnDto = getRequestOrder().toCreateOrderDto();
        ReflectionTestUtils.setField(willReturnDto, "orderStatus", OrderStatus.PENDING);
        given(orderService.cancelOrder(orderId)).willReturn(willReturnDto);

        // WHEN
        ResultActions resultActions = mockMvc.perform(patch("/order/{orderId}/cancel", orderId));

        // THEN
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("message").value("OK"))
                .andExpect(jsonPath("data.userId").value(1L))
                .andExpect(jsonPath("data.orderStatus").value(OrderStatus.PENDING.toString()))
                .andExpect(jsonPath("data.delivery.city").value("서울시"))
                .andExpect(jsonPath("data.delivery.street").value("광화문로"))
                .andExpect(jsonPath("data.delivery.zipcode").value("123-456"))
                .andExpect(jsonPath("data.orderLines").isArray())
                .andDo(print())
                .andDo(document("order-patch-cancel",
                        pathParameters(
                                parameterWithName("orderId").description("상품고유번호")
                        ),
                        responseFields(getFieldDescriptors())
                ))
        ;
    }

    @Test
    @DisplayName("주문 취소 - 존재하지 않는 주문번호")
    public void cancelOrderNotExistOrderId() throws Exception {
        // GIVEN
        Long notExistOrderId = -1L;

        given(orderService.cancelOrder(notExistOrderId))
                .willThrow(new NotExistOrder());

        // WHEN
        ResultActions resultActions = mockMvc.perform(patch("/order/{orderId}/cancel", notExistOrderId));

        // THEN
        resultActions.andExpect(status().isConflict())
                .andDo(print())
                .andDo(document("order-patch-cancel-notExistOrderId",
                            pathParameters(
                                    parameterWithName("orderId").description(notExistOrderId)
                            ),
                            responseFields(
                                    fieldWithPath("message").description("메세지")
                            )
                        ))
        ;
    }

    @Test
    @DisplayName("주문 조회")
    public void getOrderById() throws Exception {
        // GIVEN
        long orderId = 1L;

        OrderDto willReturnDto = getRequestOrder().toCreateOrderDto();
        ReflectionTestUtils.setField(willReturnDto, "orderStatus", OrderStatus.PLACED);
        given(orderService.getOrderById(orderId))
                .willReturn(willReturnDto);

        // WHEN
        ResultActions resultActions = mockMvc.perform(get("/order/{orderId}", orderId));

        // THEN
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("message").value("OK"))
                .andExpect(jsonPath("data.userId").value(1L))
                .andExpect(jsonPath("data.orderStatus").exists())
                .andExpect(jsonPath("data.delivery.city").value("서울시"))
                .andExpect(jsonPath("data.delivery.street").value("광화문로"))
                .andExpect(jsonPath("data.delivery.zipcode").value("123-456"))
                .andExpect(jsonPath("data.orderLines").isArray())
                .andDo(print())
                .andDo(document("order-get-one",
                        pathParameters(
                                parameterWithName("orderId").description("상품고유번호")
                        ),
                        responseFields(getFieldDescriptors())
                ));
    }

    @Test
    @DisplayName("주문 조회 - 존재하지 않는 주문번호")
    public void getOrderByIdNotExistOrder() throws Exception {
        // GIVEN
        long orderId = -1L;

        given(orderService.getOrderById(orderId))
                .willThrow(new NotExistOrder());

        // WHEN
        ResultActions resultActions = mockMvc.perform(get("/order/{orderId}", orderId));

        // THEN
        resultActions.andExpect(status().isConflict())
                .andDo(print())
                .andDo(document("order-get-one-notExistOrderId",
                        pathParameters(
                                parameterWithName("orderId").description(orderId)
                        ),
                        responseFields(
                                fieldWithPath("message").description("메세지")
                        )
                ))
        ;
    }

    private FieldDescriptor[] getFieldDescriptors() {
        return new FieldDescriptor[]{fieldWithPath("message").description("메시지"),
                fieldWithPath("data.userId").description("회원 고유번호"),
                fieldWithPath("data.orderStatus").description("주문 상태"),
                fieldWithPath("data.delivery.city").description("도시"),
                fieldWithPath("data.delivery.street").description("거리"),
                fieldWithPath("data.delivery.zipcode").description("우편번호"),
                fieldWithPath("data.orderLines[].catalogId").description("주문 카탈로그 아이디"),
                fieldWithPath("data.orderLines[].count").description("주문 수량"),
                fieldWithPath("data.orderLines[].orderPrice").description("주문 가격")};
    }

    private OrderController.RequestOrder getRequestOrder() {
        OrderController.RequestOrderLine requestOrderLine1
                = new OrderController.RequestOrderLine(2L, 10, 10_000);
        OrderController.RequestOrderLine requestOrderLine2
                = new OrderController.RequestOrderLine(3L, 20, 20_000);

        OrderController.RequestDelivery requestDelivery
                = new OrderController.RequestDelivery("서울시", "광화문로", "123-456");

        return new OrderController.RequestOrder(1L, requestDelivery, List.of(requestOrderLine1, requestOrderLine2));
    }
}