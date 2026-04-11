package com.returdev.nexflow.model.entities;

import com.returdev.nexflow.model.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

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
@Builder
@Getter
@Setter
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID id;

    @Column(name = "name", length = 50, nullable = false)
    @Size(max = 50, message = "{validation.max_size.message}")
    @NotBlank(message = "{validation.not_blank.message}")
    private String name;

    @Column(name = "surnames", length = 100, nullable = false)
    @Size(max = 100, message = "{validation.max_size.message}")
    @NotNull(message = "{validation.not_null.message}")
    private String surnames;

    @Column(name = "email", length = 100, nullable = false)
    @Email()
    @Size(max = 100, message = "{validation.max_size.message}")
    @NotNull(message = "{validation.not_null.message}")
    private String email;

    @Size(min = 8, message = "{validation.password.too_short}")
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
    @Builder.Default
    private List<WalletEntity> wallets = new ArrayList<>();

    @OneToMany(
            mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true
    )
    @Builder.Default
    private List<UserSessionEntity> sessions = new ArrayList<>();

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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("Role_"+ role.name()));
    }

    @Override
    @NonNull
    public String getUsername() {
        return email;
    }

}
