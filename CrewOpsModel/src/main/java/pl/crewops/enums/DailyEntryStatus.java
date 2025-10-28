package pl.crewops.enums;

import java.io.Serializable;

/**
 * Defines the status of a DailyEntry record, managing the workflow from draft
 * through automatic and manual approval, including modifications made by a manager.
 */
public enum DailyEntryStatus implements Serializable {
    EMPTY,

    DRAFT,

    PENDING,

    AUTO_GENERATED,

    MANUAL_EDITED,

    APPROVED
}
