package com.returdev.nexflow.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "user_sessions")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserSessionEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "refresh_token", nullable = false)
    @NotBlank(message = "{validation.not_blank.message}")
    private String refreshToken;

    @Column(name = "device_info", nullable = false)
    @NotBlank(message = "{validation.not_blank.message}")
    private String deviceInfo;

    @Column(name = "last_active", nullable = false)
    @NotNull(message = "{validation.not_null.message}")
    private LocalDateTime lastActive;

    @ManyToOne()
    @JoinColumn(
            name = "user_id", nullable = false
    )
    @NotNull(message = "{validation.not_null.message}")
    private UserEntity user;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserSessionEntity that = (UserSessionEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
