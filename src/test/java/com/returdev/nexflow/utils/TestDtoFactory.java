package com.returdev.nexflow.utils;

import com.returdev.nexflow.dto.request.CategoryRequestDTO;
import com.returdev.nexflow.dto.request.UserRequestDTO;
import com.returdev.nexflow.dto.request.WalletRequestDTO;
import com.returdev.nexflow.dto.request.update.CategoryUpdateDTO;
import com.returdev.nexflow.dto.request.update.UserUpdateDTO;
import com.returdev.nexflow.dto.request.update.WalletUpdateDTO;

import java.util.UUID;

public class TestDtoFactory {

    public static CategoryRequestDTO createValidCategoryRequestDTO() {
        return new CategoryRequestDTO("Category", "icon_resource");
    }

    public static CategoryUpdateDTO createValidCategoryUpdateDTO() {
        return new CategoryUpdateDTO("New Category", "new_icon_resource");
    }

    public static UserRequestDTO createValidUserRequestDTO() {
        return new UserRequestDTO(
                "User",
                "Test",
                "email@email.com",
                "password"
        );
    }

    public static UserUpdateDTO createValidUserUpdateDTO() {
        return new UserUpdateDTO(
                "New name",
                "New surname"
        );
    }

    public static WalletRequestDTO createValidWalletRequestDTO(UUID userId) {
        return new WalletRequestDTO(
                "Wallet",
                "EUR",
                100L,
                userId
        );
    }

    public static WalletUpdateDTO createValidWalletUpdateDTO() {
        return new WalletUpdateDTO(
                "New name",
                "ARG",
                100L
        );
    }

}
