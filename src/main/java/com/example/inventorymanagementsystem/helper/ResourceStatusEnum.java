package com.example.inventorymanagementsystem.helper;

public enum ResourceStatusEnum {
    AVAILABLE("Available"),
    ASSIGNED("Assigned"),
    IN_REPAIR("InRepair"),
    LOST("Lost"),
    DAMAGED("Damaged");

    private final String displayName;

    ResourceStatusEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ResourceStatusEnum fromDisplayName(String displayName) {
        for (ResourceStatusEnum status : values()) {
            if (status.displayName.equalsIgnoreCase(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + displayName);
    }
} 