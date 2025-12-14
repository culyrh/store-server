package com.bookstore.api.order.service;

import com.bookstore.api.book.entity.Book;
import com.bookstore.api.book.repository.BookRepository;
import com.bookstore.api.cart.entity.Cart;
import com.bookstore.api.cart.repository.CartRepository;
import com.bookstore.api.common.exception.BusinessException;
import com.bookstore.api.common.exception.ErrorCode;
import com.bookstore.api.order.dto.CreateOrderRequest;
import com.bookstore.api.order.dto.OrderResponse;
import com.bookstore.api.order.entity.Order;
import com.bookstore.api.order.entity.OrderItem;
import com.bookstore.api.order.entity.OrderStatus;
import com.bookstore.api.order.repository.OrderItemRepository;
import com.bookstore.api.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final BookRepository bookRepository;

    /**
     * 주문 생성
     */
    @Transactional
    public OrderResponse createOrder(Long userId, CreateOrderRequest request) {
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        // 장바구니에서 주문
        if (request.getCartIds() != null && !request.getCartIds().isEmpty()) {
            for (Long cartId : request.getCartIds()) {
                Cart cart = cartRepository.findById(cartId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "장바구니 항목을 찾을 수 없습니다"));

                if (!cart.getUserId().equals(userId)) {
                    throw new BusinessException(ErrorCode.FORBIDDEN, "본인의 장바구니만 주문할 수 있습니다");
                }

                Book book = bookRepository.findById(cart.getBookId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "도서를 찾을 수 없습니다"));

                BigDecimal itemTotal = book.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()));
                totalAmount = totalAmount.add(itemTotal);

                orderItems.add(OrderItem.builder()
                        .bookId(book.getId())
                        .quantity(cart.getQuantity())
                        .price(book.getPrice())
                        .build());
            }

            // 장바구니 항목 삭제
            request.getCartIds().forEach(cartRepository::deleteById);
        }

        // 직접 주문
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (CreateOrderRequest.OrderItemRequest item : request.getItems()) {
                Book book = bookRepository.findById(item.getBookId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "도서를 찾을 수 없습니다"));

                BigDecimal itemTotal = book.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                totalAmount = totalAmount.add(itemTotal);

                orderItems.add(OrderItem.builder()
                        .bookId(book.getId())
                        .quantity(item.getQuantity())
                        .price(book.getPrice())
                        .build());
            }
        }

        if (orderItems.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "주문 항목이 없습니다");
        }

        // 주문 생성
        Order order = Order.builder()
                .userId(userId)
                .status(OrderStatus.CREATED)
                .totalAmount(totalAmount)
                .build();

        Order savedOrder = orderRepository.save(order);

        // 주문 항목 생성
        for (OrderItem item : orderItems) {
            OrderItem orderItem = OrderItem.builder()
                    .orderId(savedOrder.getId())
                    .bookId(item.getBookId())
                    .quantity(item.getQuantity())
                    .price(item.getPrice())
                    .status(OrderStatus.CREATED)
                    .build();
            orderItemRepository.save(orderItem);
        }

        log.info("주문 생성 완료: orderId={}, userId={}", savedOrder.getId(), userId);
        return convertToResponse(savedOrder);
    }

    /**
     * 주문 조회
     */
    public OrderResponse getOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "주문을 찾을 수 없습니다"));

        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "본인의 주문만 조회할 수 있습니다");
        }

        return convertToResponse(order);
    }

    /**
     * 내 주문 목록 조회
     */
    public Page<OrderResponse> getMyOrders(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable)
                .map(this::convertToResponse);
    }

    /**
     * 주문 상태 변경 (ADMIN)
     */
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "주문을 찾을 수 없습니다"));

        order.updateStatus(status);

        // 주문 항목 상태도 함께 변경
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        items.forEach(item -> item.updateStatus(status));

        log.info("주문 상태 변경: orderId={}, status={}", orderId, status);
        return convertToResponse(order);
    }

    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "주문을 찾을 수 없습니다"));

        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "본인의 주문만 취소할 수 있습니다");
        }

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "취소할 수 없는 주문 상태입니다");
        }

        order.updateStatus(OrderStatus.CANCELED);

        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        items.forEach(item -> item.updateStatus(OrderStatus.CANCELED));

        log.info("주문 취소: orderId={}", orderId);
    }

    /**
     * Order -> OrderResponse 변환
     */
    private OrderResponse convertToResponse(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());

        List<OrderResponse.OrderItemResponse> itemResponses = items.stream()
                .map(item -> {
                    Book book = bookRepository.findById(item.getBookId())
                            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "도서를 찾을 수 없습니다"));

                    return OrderResponse.OrderItemResponse.builder()
                            .id(item.getId())
                            .bookId(item.getBookId())
                            .bookTitle(book.getTitle())
                            .quantity(item.getQuantity())
                            .price(item.getPrice())
                            .status(item.getStatus())
                            .build();
                })
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .items(itemResponses)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}