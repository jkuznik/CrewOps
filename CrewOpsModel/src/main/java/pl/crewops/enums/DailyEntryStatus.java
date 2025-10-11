package pl.crewops.enums;

/**
 * Defines the status of a DailyEntry record, managing the workflow from draft
 * through automatic and manual approval, including modifications made by a manager.
 */
public enum DailyEntryStatus {
    EMPTY,

    DRAFT,

    PENDING,

    AUTO_GENERATED,

    MANUAL_EDITED,

    APPROVED
}
