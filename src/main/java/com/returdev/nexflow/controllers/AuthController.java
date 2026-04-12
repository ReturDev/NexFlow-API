package com.returdev.nexflow.controllers;

import com.returdev.nexflow.dto.request.AuthRequestDTO;
import com.returdev.nexflow.dto.request.TokenRequestDTO;
import com.returdev.nexflow.dto.request.UserRequestDTO;
import com.returdev.nexflow.dto.response.AuthResponseDTO;
import com.returdev.nexflow.services.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDTO> signup(
            @Valid @RequestBody UserRequestDTO userRequestDTO,
            HttpServletRequest request) {

        String deviceInfo = request.getHeader(HttpHeaders.USER_AGENT);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(userRequestDTO, deviceInfo));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @Valid @RequestBody AuthRequestDTO authRequestDTO,
            HttpServletRequest request) {

        String deviceInfo = request.getHeader(HttpHeaders.USER_AGENT);
        return ResponseEntity.ok(authService.logIn(authRequestDTO, deviceInfo));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(@RequestBody @Valid TokenRequestDTO request) {
        return ResponseEntity.ok(authService.refresh(request.refreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody @Valid TokenRequestDTO request) {
        authService.invalidateSession(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN') or #email == authentication.name")
    @DeleteMapping("/{email}/sessions")
    public ResponseEntity<Void> invalidateAllSessions(@PathVariable String email) {
        authService.invalidateAllSessions(email);
        return ResponseEntity.noContent().build();
    }
}