package com.returdev.nexflow.dto.request.update;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * DTO for updating Wallet settings.
 *
 * @param name           updated wallet name.
 * @param currencyCode   updated 3-letter ISO currency code.
 * @param overdraftLimit updated overdraft limit in cents (must be non-negative).
 */
public record WalletUpdateDTO(
        @Size(min = 1, max = 50, message = "{validation.size.message}")
        String name,
        @Size(min = 3, max = 3, message = "{validation.fix_size.message}")
        String currencyCode,
        @Min(value = 0, message = "validation.min_value.message")
        Long overdraftLimit
) {
}
