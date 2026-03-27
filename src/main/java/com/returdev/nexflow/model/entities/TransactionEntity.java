package com.returdev.nexflow.model.entities;

import com.returdev.nexflow.model.enums.TransactionStatus;
import com.returdev.nexflow.model.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "transactions"
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 50)
    @NotBlank(message = "{validation.not_blank.message}")
    private String title;

    @Column(name = "description", nullable = false, length = 200)
    @NotNull(message = "{validation.not_null.message}")
    private String description;

    @Column(name = "balance_in_cents", nullable = false)
    @Min(value = 0, message = "validation.min_value.message")
    private Long balanceInCents;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "{validation.not_null.message}")
    private TransactionType type;

    @Column(name = "date", nullable = false)
    @NotNull(message = "{validation.not_null.message}")
    private LocalDateTime date;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "{validation.not_null.message}")
    private TransactionStatus  status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "{validation.not_null.message}")
    private CategoryEntity category;

    @ManyToOne(optional = false)
    @JoinColumn(name = "wallet_id", nullable = false)
    @NotNull(message = "{validation.not_null.message}")
    private WalletEntity wallet;

    @ManyToOne()
    @JoinColumn(name = "plan_id")
    private RecurringPlanEntity plan;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
