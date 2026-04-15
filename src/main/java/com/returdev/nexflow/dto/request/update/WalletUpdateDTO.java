package com.returdev.nexflow.dto.request.update;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * DTO for updating Wallet settings.
 *
 * @param name           updated wallet name.
 * @param currencyCode   updated 3-letter ISO currency code.
 * @param overdraftLimit updated overdraft limit in cents (must be non-negative).
 */
@Schema(title = "Wallet Update", description = "Schema for updating wallet settings and balance limits.")
public record WalletUpdateDTO(
        @Size(min = 1, max = 50, message = "{validation.size.message}")
        @Schema(
                example = "Main Savings",
                description = "The display name of the wallet.",
                minLength = 1,
                maxLength = 50
        )
        String name,

        @Size(min = 3, max = 3, message = "{validation.fix_size.message}")
        @Schema(
                example = "EUR",
                description = "The 3-letter currency code (ISO 4217 standard).",
                minLength = 3,
                maxLength = 3
        )
        String currencyCode,

        @Min(value = 0, message = "validation.min_value.message")
        @Schema(
                example = "50000",
                description = "Maximum allowed negative balance in cents (e.g., 500€ limit is 50000).",
                minimum = "0"
        )
        Long overdraftLimit
) {
}
