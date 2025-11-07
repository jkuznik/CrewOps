package pl.crewops.enums;
/**
 * Enumeration representing different types of HSE (Health, Safety & Environment) records.
 * <p>
 * Each record type includes:
 * <ul>
 *   <li>A unique identifier (UUID)</li>
 *   <li>A human-readable description</li>
 *   <li>Three boolean flags indicating classification:</li>
 *   <ul>
 *     <li><b>incidentRelated</b> – true if the entry is an accident or injury event</li>
 *     <li><b>hazardRelated</b> – true if the entry is a near miss, unsafe act, or hazard observation</li>
 *     <li><b>policyRelated</b> – true if the entry represents a policy, compliance, or behavioral issue</li>
 *   </ul>
 * </ul>
 */
public enum InitialSRTValues {

    /** Minor injury incident that required only first aid. */
    MINOR_ACCIDENT("6a209e07-41e6-45e0-9518-1d27c2132b0d", "Minor accident", true, false, false),

    /** Serious or major injury incident requiring medical treatment or hospitalization. */
    SERIOUS_ACCIDENT("c9f79e3f-8677-4e19-a05a-2dea7c7b78b3", "Serious accident", true, false, false),

    /** Accident involving multiple injured persons. */
    COLLECTIVE_ACCIDENT("10224f33-8575-4d8b-819d-4021517f1f8c", "Collective accident", true, false, false),

    /** Accident that resulted in a fatality. */
    FATAL_ACCIDENT("3ae9efc4-bf25-4675-8697-0c55ff29a03b", "Fatal accident", true, false, false),

    /** Dangerous event that did not result in injury or damage, but had the potential to. */
    NEAR_MISS("199c4e4e-a892-46d9-88cf-3c3a7d60d0a5", "Near miss", false, true, false),

    /** Unsafe employees behavior or action observed that could lead to an incident. */
    UNSAFE_ACT("bb313b50-8e95-4cd1-911f-90b484cbe635", "Unsafe act", false, true, false),

    /** Hazard observation (HAZOB) reported before any incident occurred. */
    HAZOB("4425d58f-4c52-4bb0-b872-2af058ccd176", "HAZOB", false, true, false),

    /** Deliberate or negligent breach of safety rules or procedures. */
    VIOLATION("9ddf7291-93e9-4f88-a65a-7446bf9ab7df", "Violation", false, false, true),

    /** Failure to comply with company policy or regulatory requirements. */
    NON_COMPLIANCE("ccf267a8-3a0f-4e1c-bbc9-5d7275720aed", "Non-compliance", false, false, true),

    /** Formal non-conformance to a management system or standard (e.g. ISO 45001). */
    NON_CONFORMANCE("ee712492-123a-4eb5-ae17-4a1fd488c235", "Non-conformance", false, false, true);

    private final String id;
    private final String description;
    private final boolean incidentRelated;
    private final boolean hazardRelated;
    private final boolean policyRelated;

    InitialSRTValues(
            String id, String description, boolean incidentRelated, boolean hazardRelated, boolean policyRelated) {
        this.id = id;
        this.description = description;
        this.incidentRelated = incidentRelated;
        this.hazardRelated = hazardRelated;
        this.policyRelated = policyRelated;
    }

    /** @return Unique UUID of this record type. */
    public String getId() {
        return id;
    }

    /** @return Human-readable description of this record type. */
    public String getDescription() {
        return description;
    }

    /** @return True if this record represents an accident or incident. */
    public boolean isIncidentRelated() {
        return incidentRelated;
    }

    /** @return True if this record represents a hazard, near miss, or unsafe act. */
    public boolean isHazardRelated() {
        return hazardRelated;
    }

    /** @return True if this record represents a policy, compliance, or behavioral issue. */
    public boolean isPolicyRelated() {
        return policyRelated;
    }
}
