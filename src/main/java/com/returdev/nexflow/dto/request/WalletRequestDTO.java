package com.returdev.nexflow.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


/**
 * Data Transfer Object for creating or updating a wallet configuration.
 * <p>
 * This record captures the user-defined parameters for a wallet, ensuring
 * strict validation for naming and currency formatting.
 *
 * @param name           the desired name for the wallet. Must not be blank.
 * @param currencyCode   the mandatory 3-character ISO currency code.
 *                       Must be exactly 3 characters.
 * @param overdraftLimit the assigned overdraft limit in cents. Must be provided,
 *                       use 0 for no overdraft.
 */
public record WalletRequestDTO(
        @NotBlank(message = "{validation.not_blank.message}")
        @Size(max = 50, message = "{validation.max_size.message}")
        String name,
        @Size(min = 3, max = 3, message = "{validation.fix_size.message}")
        @NotBlank(message = "{validation.not_blank.message}")
        String currencyCode,
        @Min(value = 0, message = "validation.min_value.message")
        @NotNull(message = "{validation.not_null.message}")
        Long overdraftLimit
) {
}
