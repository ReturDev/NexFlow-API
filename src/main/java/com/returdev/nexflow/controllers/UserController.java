package com.returdev.nexflow.controllers;

import com.returdev.nexflow.dto.request.UserRequestDTO;
import com.returdev.nexflow.dto.request.update.PasswordUpdateDTO;
import com.returdev.nexflow.dto.request.update.UserUpdateDTO;
import com.returdev.nexflow.dto.response.UserResponseDTO;
import com.returdev.nexflow.dto.response.wrapper.ContentWrapperResponseDTO;
import com.returdev.nexflow.services.user.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ContentWrapperResponseDTO<UserResponseDTO>> getUserById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                ContentWrapperResponseDTO.of(userService.getUserById(id))
        );
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ContentWrapperResponseDTO<UserResponseDTO>> getUserByEmail(
            @PathVariable @Valid @Email String email
    ) {
        return ResponseEntity.ok(
                ContentWrapperResponseDTO.of(userService.getUserByEmail(email))
        );
    }

    @PostMapping()
    public ResponseEntity<ContentWrapperResponseDTO<UserResponseDTO>> saveUser(
            @RequestBody @Valid UserRequestDTO userRequestDTO
    ) {

        UserResponseDTO response = userService.saveUser(userRequestDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/email/{email}")
                .buildAndExpand(response.email())
                .toUri();

        return ResponseEntity.created(location)
                .body(ContentWrapperResponseDTO.of(response));

    }

    @PatchMapping("/{id}")
    public ResponseEntity<ContentWrapperResponseDTO<UserResponseDTO>> updateUser(
            @PathVariable UUID id,
            @RequestBody @Valid UserUpdateDTO updateDTO
    ) {
        return ResponseEntity.ok(
                ContentWrapperResponseDTO.of(userService.updateUser(id, updateDTO))
        );
    }

    @PatchMapping("/password/{id}")
    public ResponseEntity<Void> updateUserPassword(
            @PathVariable UUID id,
            @RequestBody PasswordUpdateDTO passwordUpdateDTO
    ) {

        userService.updateUserPassword(id, passwordUpdateDTO);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable UUID id
    ) {


        userService.deleteUser(id);

        return ResponseEntity.noContent().build();

    }


}
