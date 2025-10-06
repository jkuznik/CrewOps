package pl.crewops.enums;

/**
 * Defines the status of a DailyEntry record, managing the workflow from draft
 * through automatic and manual approval, including modifications made by a manager.
 */
public enum DailyEntryStatus {

    /**
     * Draft status. The entry is fully editable by the employee and has not been submitted for approval.
     */
    DRAFT,

    /**
     * Pending status. The entry has been submitted for approval and is waiting for manager review.
     */
    PENDING,

    /**
     * Automatically approved status. The entry was approved by the system based on predefined rules.
     */
    AUTOAPPROVED,

    /**
     * Manually approved status. The entry was approved by a manager without any modifications.
     */
    APPROVED,

    /**
     * Modified and approved status. The entry was edited by a manager after the initial approval.
     */
    MODIFIED_APPROVED
}
