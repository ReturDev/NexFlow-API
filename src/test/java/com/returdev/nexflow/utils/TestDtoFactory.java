package com.returdev.nexflow.utils;

import com.returdev.nexflow.dto.request.CategoryRequestDTO;
import com.returdev.nexflow.dto.request.UserRequestDTO;
import com.returdev.nexflow.dto.request.update.CategoryUpdateDTO;
import com.returdev.nexflow.dto.request.update.UserUpdateDTO;

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

}
