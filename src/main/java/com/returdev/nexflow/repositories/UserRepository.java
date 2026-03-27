package com.returdev.nexflow.repositories;

import com.returdev.nexflow.model.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing {@link UserEntity} persistence.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    /**
     * Finds a user entity based on their unique email address.
     *
     * @param email the email string to search for.
     * @return an {@link Optional} containing the found {@link UserEntity},
     * or {@link Optional#empty()} if no user exists with the given email.
     */
    Optional<UserEntity> findByEmail(String email);

}