package com.returdev.nexflow.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Represents the operational state of a recurring plan.
 */
@Schema(enumAsRef = true)
public enum PlanStatus {

    ACTIVE,
    INACTIVE,
    ENDED

}
