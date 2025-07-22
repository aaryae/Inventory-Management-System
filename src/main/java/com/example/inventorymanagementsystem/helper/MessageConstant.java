package com.example.inventorymanagementsystem.helper;


public final class MessageConstant {
    private MessageConstant(){
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }


    public static final String SUCCESSFULLY_FETCHED = "Successfully Fetched Data";
    public static final String SUCCESSFULLY_DELETED = "Successfully Deleted Data";
    public static final String SUCCESSFULLY_UPDATED = "Successfully Updated Data";
    public static final String SUCCESSFULLY_ADDED = "Successfully Added Data";
    public static final String RESOURCE = "Resource";
    public static final String BATCH = "Batch";
    public static final String RESOURCE_TYPE = "ResourceType";
    public static final String RESOURCE_CLASS = "ResourceClass";
    public static final String RESOURCE_STATUS = "ResourceStatus";
    public static final String BARCODE_GENERATION_FAILED = "Barcode Generation Failed";
}
