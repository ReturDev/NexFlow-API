package com.returdev.nexflow.model.entities;

import com.returdev.nexflow.model.enums.Frequency;
import com.returdev.nexflow.model.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "recurring_plans"
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RecurringPlanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 50)
    @Size(max = 50, message = "{validation.max_size.message}")
    @NotBlank(message = "{validation.not_blank.message}")
    private String title;

    @Column(name = "description", nullable = false, length = 200)
    @Size(max = 200, message = "{validation.max_size.message}")
    @NotNull(message = "{validation.not_null.message}")
    private String description;

    @Column(name = "balance_in_cents", nullable = false)
    @Min(value = 0, message = "{validation.min_value.message}")
    @NotNull(message = "{validation.not_null.message}")
    private Long balanceInCents;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "{validation.not_null.message}")
    private TransactionType type;

    @Column(name = "start_date", nullable = false)
    @NotNull(message = "{validation.not_null.message}")
    private LocalDateTime startDate;

    @Column(name = "frequency", nullable = false)
    @NotNull(message = "{validation.not_null.message}")
    private Frequency frequency;

    @Column(name = "interval", nullable = false)
    @Min(value = 1, message = "{validation.min_value.message}")
    @NotNull(message = "{validation.not_null.message}")
    private Integer interval;

    @Column(name = "next_execution_date", nullable = false)
    @NotNull(message = "{validation.not_null.message}")
    private LocalDateTime nextExecutionDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @ManyToOne()
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "{validation.not_null.message}")
    private CategoryEntity category;

    @ManyToOne()
    @JoinColumn(name = "wallet_id", nullable = false)
    @NotNull(message = "{validation.not_null.message}")
    private WalletEntity wallet;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RecurringPlanEntity that = (RecurringPlanEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
