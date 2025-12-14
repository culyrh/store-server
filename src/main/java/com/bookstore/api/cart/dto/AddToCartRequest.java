package com.bookstore.api.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "장바구니 추가 요청")
public class AddToCartRequest {
    @NotNull
    private Long bookId;

    @NotNull
    @Min(1)
    private Integer quantity;
}