# Resource Status Management API Guide

This guide explains how to use the Resource Status Management API for updating and tracking resource statuses in your inventory system.

## Overview

The Resource Status Management API provides comprehensive functionality for:
- Updating resource statuses (Available, Assigned, InRepair, Lost, Damaged)
- Tracking status change history
- Bulk status updates
- Status validation and business rules
- Audit trail for all status changes

## Available Statuses

The system supports the following resource statuses:

1. **Available** - Resource is available for assignment
2. **Assigned** - Resource is currently assigned to an employee
3. **InRepair** - Resource is under repair or maintenance
4. **Lost** - Resource has been lost
5. **Damaged** - Resource is damaged and needs repair

## API Endpoints

### 1. Update Resource Status

**PUT** `/api/resource-status/update`

Updates the status of a specific resource with validation and history tracking.

**Request Body:**
```json
{
  "resourceId": 1,
  "statusName": "InRepair",
  "statusChangeReason": "Hardware malfunction",
  "notes": "Laptop screen not working properly"
}
```

**Response:**
```json
{
  "message": "Successfully Updated Data",
  "success": true,
  "data": {
    "resourceId": 1,
    "resourceCode": "LAP001",
    "brand": "Dell",
    "model": "Latitude 5520",
    "specification": "Intel i7, 16GB RAM, 512GB SSD",
    "purchaseDate": "2023-01-15",
    "warrantyExpiry": "2026-01-15",
    "resourceType": "Laptop",
    "resourceClass": "Computer Equipment",
    "resourceStatus": "InRepair",
    "batchCode": "BATCH001",
    "unitPrice": 1200.0,
    "serialNumber": "DL123456789",
    "remarks": "Development laptop",
    "createdAt": "2023-01-15 10:30:00",
    "updatedAt": "2024-01-20 14:30:00"
  }
}
```

### 2. Bulk Update Resource Statuses

**POST** `/api/resource-status/bulk-update`

Updates multiple resource statuses in a single operation.

**Request Body:**
```json
[
  {
    "resourceId": 1,
    "statusName": "InRepair",
    "statusChangeReason": "Hardware malfunction",
    "notes": "Laptop screen not working"
  },
  {
    "resourceId": 2,
    "statusName": "Lost",
    "statusChangeReason": "Employee reported missing",
    "notes": "Last seen in office on Friday"
  }
]
```

### 3. Get Status History for Resource

**GET** `/api/resource-status/history/resource/{resourceId}`

Retrieves the complete status change history for a specific resource.

**Response:**
```json
{
  "message": "Successfully Fetched Data",
  "success": true,
  "data": [
    {
      "historyId": 1,
      "resourceId": 1,
      "resourceCode": "LAP001",
      "oldStatus": "Available",
      "newStatus": "Assigned",
      "changeReason": "Employee assignment",
      "notes": "Assigned to John Doe",
      "changedBy": "admin",
      "changedAt": "2024-01-15 10:30:00"
    },
    {
      "historyId": 2,
      "resourceId": 1,
      "resourceCode": "LAP001",
      "oldStatus": "Assigned",
      "newStatus": "InRepair",
      "changeReason": "Hardware malfunction",
      "notes": "Laptop screen not working properly",
      "changedBy": "admin",
      "changedAt": "2024-01-20 14:30:00"
    }
  ]
}
```

### 4. Get Status History by Resource Code

**GET** `/api/resource-status/history/resource-code/{resourceCode}`

Retrieves status history using resource code instead of ID.

### 5. Get Resources by Status

**GET** `/api/resource-status/resources/status/{statusName}`

Retrieves all resources with a specific status.

**Response:**
```json
{
  "message": "Successfully Fetched Data",
  "success": true,
  "data": [
    {
      "resourceId": 1,
      "resourceCode": "LAP001",
      "brand": "Dell",
      "model": "Latitude 5520",
      "specification": "Intel i7, 16GB RAM, 512GB SSD",
      "purchaseDate": "2023-01-15",
      "warrantyExpiry": "2026-01-15",
      "resourceType": "Laptop",
      "resourceClass": "Computer Equipment",
      "resourceStatus": "InRepair",
      "batchCode": "BATCH001",
      "unitPrice": 1200.0,
      "serialNumber": "DL123456789",
      "remarks": "Development laptop",
      "createdAt": "2023-01-15 10:30:00",
      "updatedAt": "2024-01-20 14:30:00"
    }
  ]
}
```

### 6. Get Status History by Status

**GET** `/api/resource-status/history/status/{statusName}`

Retrieves all status change history for a specific status.

### 7. Get Status History by User

**GET** `/api/resource-status/history/user/{username}`

Retrieves all status changes made by a specific user.

### 8. Get Available Statuses

**GET** `/api/resource-status/available-statuses`

Retrieves all available status options.

**Response:**
```json
{
  "message": "Successfully Fetched Data",
  "success": true,
  "data": [
    "Available",
    "Assigned",
    "InRepair",
    "Lost",
    "Damaged"
  ]
}
```

## Business Rules and Status Transitions

### Valid Status Transitions

The system enforces the following business rules for status changes:

1. **Available** → **Assigned** ✅ (When resource is assigned to employee)
2. **Available** → **InRepair** ✅ (When resource needs maintenance)
3. **Available** → **Lost** ✅ (When resource is reported lost)
4. **Available** → **Damaged** ✅ (When resource is damaged)

5. **Assigned** → **Available** ❌ (Must return resource first)
6. **Assigned** → **InRepair** ✅ (When assigned resource needs repair)
7. **Assigned** → **Lost** ✅ (When assigned resource is lost)
8. **Assigned** → **Damaged** ✅ (When assigned resource is damaged)

9. **InRepair** → **Available** ✅ (When repair is completed)
10. **InRepair** → **Damaged** ✅ (When repair reveals more damage)

11. **Lost** → **Available** ✅ (When lost resource is found)
12. **Lost** → **Damaged** ❌ (Invalid transition)

13. **Damaged** → **InRepair** ✅ (When damaged resource is sent for repair)
14. **Damaged** → **Available** ❌ (Invalid transition)

### Status-Specific Rules

- **Assigned resources** cannot be directly changed to Available - they must be returned first
- **Lost resources** can only be changed to Available (when found)
- **Damaged resources** can only be changed to InRepair

## Error Handling

### Common Error Responses

**Invalid Status Transition:**
```json
{
  "message": "Cannot change status from Assigned to Available. Return the resource first.",
  "success": false,
  "data": null,
  "status": "CONFLICT"
}
```

**Status Not Found:**
```json
{
  "message": "Status not found: InvalidStatus",
  "success": false,
  "data": null,
  "status": "NOT_FOUND"
}
```

**Resource Not Found:**
```json
{
  "message": "Resource not found with id: 999",
  "success": false,
  "data": null,
  "status": "NOT_FOUND"
}
```

## Usage Examples

### Example 1: Mark Resource as In Repair

```bash
curl -X PUT http://localhost:8080/api/resource-status/update \
  -H "Content-Type: application/json" \
  -d '{
    "resourceId": 1,
    "statusName": "InRepair",
    "statusChangeReason": "Hardware malfunction",
    "notes": "Laptop screen not working properly"
  }'
```

### Example 2: Mark Resource as Lost

```bash
curl -X PUT http://localhost:8080/api/resource-status/update \
  -H "Content-Type: application/json" \
  -d '{
    "resourceId": 2,
    "statusName": "Lost",
    "statusChangeReason": "Employee reported missing",
    "notes": "Last seen in office on Friday"
  }'
```

### Example 3: Get All Resources in Repair

```bash
curl -X GET http://localhost:8080/api/resource-status/resources/status/InRepair
```

### Example 4: Get Status History for Resource

```bash
curl -X GET http://localhost:8080/api/resource-status/history/resource/1
```

### Example 5: Bulk Update Multiple Resources

```bash
curl -X POST http://localhost:8080/api/resource-status/bulk-update \
  -H "Content-Type: application/json" \
  -d '[
    {
      "resourceId": 1,
      "statusName": "InRepair",
      "statusChangeReason": "Hardware malfunction",
      "notes": "Laptop screen not working"
    },
    {
      "resourceId": 2,
      "statusName": "Lost",
      "statusChangeReason": "Employee reported missing",
      "notes": "Last seen in office on Friday"
    }
  ]'
```

## Status Management Best Practices

### 1. Always Provide Reason
When updating status, always provide a clear reason for the change to maintain proper audit trail.

### 2. Use Appropriate Status
- **Available**: Resources ready for assignment
- **Assigned**: Resources currently in use by employees
- **InRepair**: Resources under maintenance or repair
- **Lost**: Resources that cannot be located
- **Damaged**: Resources that need repair but are not yet in repair

### 3. Regular Status Reviews
- Monitor resources in "InRepair" status regularly
- Follow up on "Lost" resources
- Ensure "Damaged" resources are sent for repair

### 4. Audit Trail
The system automatically maintains a complete audit trail of all status changes, including:
- Who made the change
- When the change was made
- Reason for the change
- Previous and new status

## Integration with Assignment System

The status management system integrates with the assignment system:

- When a resource is assigned to an employee, its status automatically becomes "Assigned"
- When a resource is returned from an employee, its status becomes "Available"
- The assignment system validates that only "Available" resources can be assigned

This comprehensive status management system ensures proper resource tracking while maintaining data integrity and providing full audit capabilities. 