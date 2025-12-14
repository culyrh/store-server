package com.bookstore.api.cart.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCartRequest {
    @NotNull
    @Min(1)
    private Integer quantity;
}