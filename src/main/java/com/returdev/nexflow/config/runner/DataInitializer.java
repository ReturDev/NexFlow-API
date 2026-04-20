package com.returdev.nexflow.config.runner;

import com.returdev.nexflow.dto.request.UserRequestDTO;
import com.returdev.nexflow.dto.response.UserResponseDTO;
import com.returdev.nexflow.model.exceptions.FieldAlreadyExistException;
import com.returdev.nexflow.services.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserService userServiceImpl;

    @Value("${user.initial.name}")
    private String userName;

    @Value("${user.initial.surnames}")
    private String userSurnames;

    @Value("${user.initial.email}")
    private String userEmail;

    @Value("${user.initial.password}")
    private String userPassword;

    @Override
    public void run(String... args) {

        UserRequestDTO user = new UserRequestDTO(
                userName,
                userSurnames,
                userEmail,
                userPassword
        );

        try {
            UserResponseDTO savedUser = userServiceImpl.saveAdminUser(user);
            log.info("Saved initial user: {}", savedUser);
        } catch (FieldAlreadyExistException e) {
            log.info("Initial user already exists");
        } catch (Exception e) {
            log.info("Error during data initialization: {}", e.getMessage());
        }

    }
}
