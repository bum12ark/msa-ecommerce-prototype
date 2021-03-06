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
    @DisplayName("?????? ??????")
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
                .andExpect(jsonPath("data.delivery.city").value("?????????"))
                .andExpect(jsonPath("data.delivery.street").value("????????????"))
                .andExpect(jsonPath("data.delivery.zipcode").value("123-456"))
                .andExpect(jsonPath("data.orderLines").isArray())
                .andDo(print())
                .andDo(document("order-post",
                        requestFields(
                                fieldWithPath("userId").description("?????? ????????????"),
                                fieldWithPath("delivery.city").description("??????"),
                                fieldWithPath("delivery.street").description("??????"),
                                fieldWithPath("delivery.zipcode").description("????????????"),
                                fieldWithPath("orderLines[].catalogId").description("?????? ???????????? ?????????"),
                                fieldWithPath("orderLines[].count").description("?????? ??????"),
                                fieldWithPath("orderLines[].orderPrice").description("?????? ??????")
                        ),
                        responseFields(getFieldDescriptors())
                        ))
        ;
    }

    @Test
    @DisplayName("?????? ??????")
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
                .andExpect(jsonPath("data.delivery.city").value("?????????"))
                .andExpect(jsonPath("data.delivery.street").value("????????????"))
                .andExpect(jsonPath("data.delivery.zipcode").value("123-456"))
                .andExpect(jsonPath("data.orderLines").isArray())
                .andDo(print())
                .andDo(document("order-patch-cancel",
                        pathParameters(
                                parameterWithName("orderId").description("??????????????????")
                        ),
                        responseFields(getFieldDescriptors())
                ))
        ;
    }

    @Test
    @DisplayName("?????? ?????? - ???????????? ?????? ????????????")
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
                                    fieldWithPath("message").description("?????????")
                            )
                        ))
        ;
    }

    @Test
    @DisplayName("?????? ??????")
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
                .andExpect(jsonPath("data.delivery.city").value("?????????"))
                .andExpect(jsonPath("data.delivery.street").value("????????????"))
                .andExpect(jsonPath("data.delivery.zipcode").value("123-456"))
                .andExpect(jsonPath("data.orderLines").isArray())
                .andDo(print())
                .andDo(document("order-get-one",
                        pathParameters(
                                parameterWithName("orderId").description("??????????????????")
                        ),
                        responseFields(getFieldDescriptors())
                ));
    }

    @Test
    @DisplayName("?????? ?????? - ???????????? ?????? ????????????")
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
                                fieldWithPath("message").description("?????????")
                        )
                ))
        ;
    }

    private FieldDescriptor[] getFieldDescriptors() {
        return new FieldDescriptor[]{fieldWithPath("message").description("?????????"),
                fieldWithPath("data.userId").description("?????? ????????????"),
                fieldWithPath("data.orderStatus").description("?????? ??????"),
                fieldWithPath("data.delivery.city").description("??????"),
                fieldWithPath("data.delivery.street").description("??????"),
                fieldWithPath("data.delivery.zipcode").description("????????????"),
                fieldWithPath("data.orderLines[].catalogId").description("?????? ???????????? ?????????"),
                fieldWithPath("data.orderLines[].count").description("?????? ??????"),
                fieldWithPath("data.orderLines[].orderPrice").description("?????? ??????")};
    }

    private OrderController.RequestOrder getRequestOrder() {
        OrderController.RequestOrderLine requestOrderLine1
                = new OrderController.RequestOrderLine(2L, 10, 10_000);
        OrderController.RequestOrderLine requestOrderLine2
                = new OrderController.RequestOrderLine(3L, 20, 20_000);

        OrderController.RequestDelivery requestDelivery
                = new OrderController.RequestDelivery("?????????", "????????????", "123-456");

        return new OrderController.RequestOrder(1L, requestDelivery, List.of(requestOrderLine1, requestOrderLine2));
    }
}