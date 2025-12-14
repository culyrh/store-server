package com.bookstore.api.cart.service;

import com.bookstore.api.book.entity.Book;
import com.bookstore.api.book.repository.BookRepository;
import com.bookstore.api.cart.dto.*;
import com.bookstore.api.cart.entity.Cart;
import com.bookstore.api.cart.repository.CartRepository;
import com.bookstore.api.common.exception.BusinessException;
import com.bookstore.api.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {
    private final CartRepository cartRepository;
    private final BookRepository bookRepository;

    @Transactional
    public CartResponse addToCart(Long userId, AddToCartRequest request) {
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        Cart cart = cartRepository.findByUserIdAndBookId(userId, request.getBookId())
                .map(c -> { c.increaseQuantity(request.getQuantity()); return c; })
                .orElseGet(() -> cartRepository.save(Cart.builder()
                        .userId(userId).bookId(request.getBookId()).quantity(request.getQuantity()).build()));

        return convertToResponse(cart, book);
    }

    public List<CartResponse> getMyCart(Long userId) {
        return cartRepository.findByUserId(userId).stream()
                .map(cart -> convertToResponse(cart,
                        bookRepository.findById(cart.getBookId()).orElseThrow(() ->
                                new BusinessException(ErrorCode.RESOURCE_NOT_FOUND))))
                .collect(Collectors.toList());
    }

    @Transactional
    public CartResponse updateCartQuantity(Long userId, Long cartId, UpdateCartRequest request) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        if (!cart.getUserId().equals(userId))
            throw new BusinessException(ErrorCode.FORBIDDEN);
        cart.updateQuantity(request.getQuantity());
        return convertToResponse(cart, bookRepository.findById(cart.getBookId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND)));
    }

    @Transactional
    public void removeFromCart(Long userId, Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        if (!cart.getUserId().equals(userId))
            throw new BusinessException(ErrorCode.FORBIDDEN);
        cartRepository.delete(cart);
    }

    @Transactional
    public void clearCart(Long userId) {
        cartRepository.deleteByUserId(userId);
    }

    public long getCartCount(Long userId) {
        return cartRepository.countByUserId(userId);
    }

    private CartResponse convertToResponse(Cart cart, Book book) {
        return CartResponse.builder()
                .id(cart.getId()).userId(cart.getUserId()).bookId(cart.getBookId())
                .bookTitle(book.getTitle()).bookAuthor(book.getAuthor()).bookPrice(book.getPrice())
                .quantity(cart.getQuantity())
                .totalPrice(book.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())))
                .createdAt(cart.getCreatedAt()).updatedAt(cart.getUpdatedAt()).build();
    }
}