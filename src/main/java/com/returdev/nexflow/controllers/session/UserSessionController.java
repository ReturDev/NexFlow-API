package com.returdev.nexflow.controllers.session;


import com.returdev.nexflow.dto.response.UserSessionResponseDTO;
import com.returdev.nexflow.dto.response.wrapper.PaginationWrapperResponseDTO;
import com.returdev.nexflow.model.entities.UserEntity;
import com.returdev.nexflow.model.facade.AuthenticationFacade;
import com.returdev.nexflow.services.session.UserSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/sessions")
public class UserSessionController implements UserSessionApi {

    private final UserSessionService sessionService;
    private final AuthenticationFacade authFacade;

    @DeleteMapping("/all")
    @Override
    public ResponseEntity<Void> invalidateAllSessions(@RequestParam() UUID userId) {
        sessionService.invalidateAllSessions(userId, authFacade.getAuthenticateUser());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> invalidateSession(@PathVariable("id") Long sessionId) {
        sessionService.invalidateSessionById(sessionId, authFacade.getAuthenticateUser());
        return ResponseEntity.noContent().build();
    }

    @GetMapping(params = "userId")
    @Override
    public ResponseEntity<PaginationWrapperResponseDTO<UserSessionResponseDTO>> getUserSessions(
            @RequestParam() UUID userId, Pageable pageable) {
        UserEntity requester = authFacade.getAuthenticateUser();
        return ResponseEntity.ok(
                PaginationWrapperResponseDTO.fromPage(
                        sessionService.getSessionsByUserId(
                                userId,
                                requester,
                                pageable
                        )
                )
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ResponseEntity<PaginationWrapperResponseDTO<UserSessionResponseDTO>> getSessions(Pageable pageable) {
        return ResponseEntity.ok(
                PaginationWrapperResponseDTO.fromPage(
                        sessionService.getSessions(pageable)
                )
        );
    }


}
