package com.example.order_service.service;

import com.example.common.dto.UserResponseDTO;
import com.example.common.events.OrderCreatedEvent;
import com.example.common.events.OrderItemEvent;
import com.example.common.exception.UserNotFoundException;
import com.example.order_service.dto.OrderItemDTO;
import com.example.order_service.dto.OrderRequestDTO;
import com.example.order_service.dto.OrderResponseDTO;
import com.example.order_service.exception.InvalidOrderQuantityException;
import com.example.order_service.exception.OutOfStockException;
import com.example.order_service.model.OrderIdAndInventoryItemIdPK;
import com.example.order_service.model.OrderItemModel;
import com.example.order_service.model.OrderModel;
import com.example.order_service.model.OrderStatus;
import com.example.order_service.repository.OrderItemsRepository;
import com.example.order_service.repository.OrdersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final WebClient inventoryWebClient; // 1. Объявление
    private final WebClient userWebClient; // 1. Объявление
    OrdersRepository ordersRepository;
    OrderItemsRepository orderItemsRepository;

    public OrderService(OrdersRepository ordersRepository, OrderItemsRepository orderItemsRepository, KafkaTemplate<String, Object> kafkaTemplate, @Qualifier("inventoryWebClient") WebClient inventoryWebClient, @Qualifier("userWebClient") WebClient userWebClient) {
        this.ordersRepository = ordersRepository;
        this.orderItemsRepository = orderItemsRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.inventoryWebClient = inventoryWebClient;
        this.userWebClient = userWebClient;
    }

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO) {
        log.info("Start of creating order operation");

        log.debug("check if user exist");

        UserResponseDTO user = userWebClient.get().uri("/users/{id}", orderRequestDTO.userId()).retrieve().onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(new UserNotFoundException("User not found"))).bodyToMono(UserResponseDTO.class).block();

        log.debug("user exist");

        for (OrderItemDTO orderItemDTO : orderRequestDTO.items()) {
            if (orderItemDTO.quantity() < 1) {
                log.error("Wanted quantity of product less than 1");
                throw new InvalidOrderQuantityException("Invalid quantity for product " + orderItemDTO.name() + ". Should be > 0");
            }
            log.debug("check item quantity");
            Integer quantityAvailable = inventoryWebClient.get().uri("/inventory/{id}", orderItemDTO.itemId()) // {id} заменится на productId
                    .retrieve().onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(new NoSuchElementException("Not existing item id " + orderItemDTO.itemId()))).bodyToMono(Integer.class).block();
            log.debug("got item quantity");

            if (quantityAvailable < orderItemDTO.quantity()) {
                log.error("Not enough stock for product");

                throw new OutOfStockException("Not enough stock for product " + orderItemDTO.name() + ". Available = " + quantityAvailable);
            }
        }

        OrderModel order = new OrderModel();
        order.setUserId(orderRequestDTO.userId());
        ordersRepository.save(order);

        List<OrderItemEvent> orderItemEventList = new ArrayList<>(orderRequestDTO.items().size());

        for (OrderItemDTO orderItemDTO : orderRequestDTO.items()) {
            OrderItemModel orderItem = new OrderItemModel();
            OrderIdAndInventoryItemIdPK pk = new OrderIdAndInventoryItemIdPK(order.getOrderId(), orderItemDTO.itemId());
            orderItem.setOrder(order);
            orderItem.setIdAndInventoryItemIdPK(pk);
            orderItem.setQuantity(orderItemDTO.quantity());

            orderItem.setName(orderItemDTO.name());
            orderItemsRepository.save(orderItem);

            OrderItemEvent orderItemEvent = new OrderItemEvent(orderItemDTO.itemId(), orderItemDTO.quantity(), orderItemDTO.price());
            orderItemEventList.add(orderItemEvent);
        }

        OrderCreatedEvent event = new OrderCreatedEvent(order.getOrderId(), order.getUserId(), orderItemEventList);
        kafkaTemplate.send("orders-topic", event.getOrderId(), event);

        log.info("Successfully created order");

        return new OrderResponseDTO(order.getOrderId(), order.getUserId(), order.getStatus(), orderRequestDTO.items());
    }

    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderById(String id) {
        log.info("Start of getOrderById() method");
        Optional<OrderModel> orderModel = ordersRepository.findById(id);
        orderModel.orElseThrow(() -> new NoSuchElementException("Product with id = " + id + " not found"));
        return new OrderResponseDTO(orderModel.get().getOrderId(), orderModel.get().getUserId(), orderModel.get().getStatus(), orderModel.get().getItems().stream().map(x -> new OrderItemDTO(x.getIdAndInventoryItemIdPK().getInventoryItemId(), x.getQuantity(), x.getName(), -1)).toList());
    }

    public List<OrderResponseDTO> getAllOrders() {
        log.info("Start of getAllOrders() method");
        return ordersRepository.findAll().stream().map(x -> new OrderResponseDTO(x.getOrderId(), x.getUserId(), x.getStatus(), new ArrayList<>())).toList();
    }


    public String cancelOrder(String id) {
        log.info("Start of cancelOrder() method");

        Optional<OrderModel> orderModel = ordersRepository.findById(id);
        orderModel.orElseThrow().setStatus(OrderStatus.CANCELLED);
        return orderModel.get().getUserId();

    }

    @Transactional
    public String confirmOrder(String id) {
        log.info("Start of confirmOrder() method");
        Optional<OrderModel> orderModel = ordersRepository.findById(id);
        orderModel.orElseThrow().setStatus(OrderStatus.CONFIRMED);
        return orderModel.get().getUserId();
    }


    private OrderStatus parseStringToOrderStatus(String string) {
        return switch (string) {
            case "CREATED" -> OrderStatus.CREATED;
            case "CONFIRMED" -> OrderStatus.CONFIRMED;
            case "CANCELLED" -> OrderStatus.CANCELLED;
            default -> throw new IllegalStateException("Unexpected value: " + string);
        };
    }
}
