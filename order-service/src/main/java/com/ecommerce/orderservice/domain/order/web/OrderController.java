package com.ecommerce.orderservice.domain.order.web;

import com.ecommerce.orderservice.domain.order.dto.DeliveryDto;
import com.ecommerce.orderservice.domain.order.dto.OrderDto;
import com.ecommerce.orderservice.domain.order.dto.OrderLineDto;
import com.ecommerce.orderservice.domain.order.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor @Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/order")
    public ResponseEntity createOrder(@RequestBody @Valid RequestOrder requestOrder) {
        log.debug("requestCreateOrder = {}", requestOrder);

        OrderDto createOrderDto = orderService.createOrder(requestOrder.toCreateOrderDto());

        return ResponseEntity.status(HttpStatus.OK)
                .body(new RequestOrder(createOrderDto));
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    static class RequestOrder {
        private Long userId;
        private RequestDelivery delivery;
        private List<RequestOrderLine> orderLines;

        public RequestOrder(OrderDto dto) {
            List<RequestOrderLine> orderLines = dto.getOrderLineDtoList()
                    .stream()
                    .map(RequestOrderLine::new)
                    .collect(Collectors.toList());

            this.userId = dto.getUserId();
            this.delivery = new RequestDelivery(dto.getDeliveryDto());
            this.orderLines = orderLines;
        }

        public OrderDto toCreateOrderDto() {
            List<OrderLineDto> orderLineDtoList = orderLines.stream()
                    .map(RequestOrderLine::toCreateDeliveryDto)
                    .collect(Collectors.toList());

            return OrderDto.builder()
                    .userId(this.userId)
                    .deliveryDto(delivery.toCreateDeliveryDto())
                    .orderLineDtoList(orderLineDtoList)
                    .build();
        }
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    static class RequestDelivery {
        private String city;
        private String street;
        private String zipcode;

        public RequestDelivery(DeliveryDto dto) {
            this.city = dto.getCity();
            this.street = dto.getStreet();
            this.zipcode = dto.getZipcode();
        }

        public DeliveryDto toCreateDeliveryDto() {
            return DeliveryDto.builder()
                    .city(this.city).street(this.street).zipcode(this.zipcode)
                    .build();
        }
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    static class RequestOrderLine {
        private Long catalogId;
        private Integer count;
        private Integer orderPrice;

        public RequestOrderLine(OrderLineDto dto) {
            this.catalogId = dto.getCatalogId();
            this.count = dto.getCount();
            this.orderPrice = dto.getOrderPrice();
        }

        public OrderLineDto toCreateDeliveryDto() {
            return OrderLineDto.builder()
                    .catalogId(this.catalogId).count(this.count).orderPrice(this.orderPrice)
                    .build();
        }
    }
}
