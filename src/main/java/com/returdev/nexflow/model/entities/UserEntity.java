package com.returdev.nexflow.model.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.returdev.nexflow.model.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"email"}
                )
        }
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID id;

    @Column(name = "name", length = 50, nullable = false)
    @NotBlank(message = "{validation.not_blank.message}")
    private String name;

    @Column(name = "surnames", length = 100, nullable = false)
    @NotNull(message = "{validation.not_null.message}")
    private String surnames;

    @Column(name = "email", length = 100, nullable = false)
    @Email()
    @NotNull(message = "{validation.not_null.message}")
    private String email;

    @Column(name = "password", nullable = false)
    @NotBlank(message = "{validation.not_blank.message}")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @NotNull(message = "{validation.not_null.message}")
    private Role role;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(
            mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true
    )
    private List<WalletEntity> wallets = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
