package pl.crewops.enums;

import java.util.UUID;

public enum AuthUserOptions {
    /**
     *
     * This UUID values are mirrored with database for each option record - do not modify manually those without
     * update database.
     *
     * */
    AGREE_RECEIVE_SMS_NOTIFICATION(UUID.fromString("f8bd1c2a-9e8f-4bca-9c56-37604be09e12"));

    private final UUID id;

    AuthUserOptions(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
