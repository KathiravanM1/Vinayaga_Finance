# Construction Finance App — API Contract v1

Base URL: `/api/v1`  
Content-Type: `application/json`

---

## Table of Contents

1. [Create Project](#1-create-project)
2. [Get All Projects](#2-get-all-projects)
3. [Get Project Details](#3-get-project-details)
4. [Create Transaction](#4-create-transaction)
5. [Get Transactions By Project](#5-get-transactions-by-project)
6. [Dashboard](#6-dashboard)
7. [Report By Date Range](#7-report-by-date-range)
8. [Get Categories](#8-get-categories)

---

## 1. Create Project

**POST** `/api/v1/projects`

### Request Body

```json
{
  "projectCode": "PROJ-001",
  "projectName": "Residential Villa - Anna Nagar",
  "clientName": "Ramesh Kumar",
  "location": "Chennai",
  "contractValue": 4500000.00,
  "startDate": "2025-01-15",
  "expectedEndDate": "2025-12-31",
  "projectStatus": "ACTIVE",
  "notes": "G+2 structure, client requires weekly updates"
}
```

### Response — `201 Created`

```json
{
  "projectId": 1,
  "projectCode": "PROJ-001",
  "projectName": "Residential Villa - Anna Nagar",
  "clientName": "Ramesh Kumar",
  "location": "Chennai",
  "contractValue": 4500000.00,
  "startDate": "2025-01-15",
  "expectedEndDate": "2025-12-31",
  "projectStatus": "ACTIVE",
  "notes": "G+2 structure, client requires weekly updates",
  "createdAt": "2025-07-10T10:30:00",
  "updatedAt": "2025-07-10T10:30:00"
}
```

### Validation Rules

| Field            | Rule                                                                 |
|------------------|----------------------------------------------------------------------|
| `projectCode`    | Required. Max 50 chars. Must be unique across all projects.          |
| `projectName`    | Required. Max 255 chars.                                             |
| `clientName`     | Required. Max 255 chars.                                             |
| `location`       | Optional. Max 255 chars.                                             |
| `contractValue`  | Optional. Must be > 0 if provided. Max 13 digits, 2 decimal places. |
| `startDate`      | Required. Format: `YYYY-MM-DD`. Cannot be a future date by > 1 year. |
| `expectedEndDate`| Optional. Must be after `startDate` if provided.                    |
| `projectStatus`  | Required. Allowed values: `ACTIVE`, `COMPLETED`, `ON_HOLD`, `CANCELLED`. |
| `notes`          | Optional. No length restriction.                                     |

---

## 2. Get All Projects

**GET** `/api/v1/projects`

### Query Parameters

| Parameter | Type   | Required | Description                                              |
|-----------|--------|----------|----------------------------------------------------------|
| `status`  | String | No       | Filter by status: `ACTIVE`, `COMPLETED`, `ON_HOLD`, `CANCELLED` |
| `page`    | int    | No       | Page number, default `0`                                 |
| `size`    | int    | No       | Page size, default `10`                                  |

### Response — `200 OK`

```json
{
  "content": [
    {
      "projectId": 1,
      "projectCode": "PROJ-001",
      "projectName": "Residential Villa - Anna Nagar",
      "clientName": "Ramesh Kumar",
      "location": "Chennai",
      "contractValue": 4500000.00,
      "startDate": "2025-01-15",
      "expectedEndDate": "2025-12-31",
      "projectStatus": "ACTIVE",
      "createdAt": "2025-07-10T10:30:00"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "pageNumber": 0,
  "pageSize": 10
}
```

### Validation Rules

| Parameter | Rule                                                                 |
|-----------|----------------------------------------------------------------------|
| `status`  | If provided, must be one of the 4 allowed enum values.               |
| `page`    | Must be >= 0.                                                        |
| `size`    | Must be between 1 and 100.                                           |

---

## 3. Get Project Details

**GET** `/api/v1/projects/{projectId}`

### Path Parameter

| Parameter   | Type | Description          |
|-------------|------|----------------------|
| `projectId` | Long | ID of the project    |

### Response — `200 OK`

```json
{
  "projectId": 1,
  "projectCode": "PROJ-001",
  "projectName": "Residential Villa - Anna Nagar",
  "clientName": "Ramesh Kumar",
  "location": "Chennai",
  "contractValue": 4500000.00,
  "startDate": "2025-01-15",
  "expectedEndDate": "2025-12-31",
  "projectStatus": "ACTIVE",
  "notes": "G+2 structure, client requires weekly updates",
  "totalIncome": 1800000.00,
  "totalExpense": 950000.00,
  "balance": 850000.00,
  "createdAt": "2025-07-10T10:30:00",
  "updatedAt": "2025-07-10T10:30:00"
}
```

### Response — `404 Not Found`

```json
{
  "status": 404,
  "error": "Project not found",
  "message": "No project exists with id: 99"
}
```

### Validation Rules

| Parameter   | Rule                              |
|-------------|-----------------------------------|
| `projectId` | Must be a positive Long. Required.|

---

## 4. Create Transaction

**POST** `/api/v1/projects/{projectId}/transactions`

### Path Parameter

| Parameter   | Type | Description                       |
|-------------|------|-----------------------------------|
| `projectId` | Long | Project under which to record the transaction |

### Request Body

```json
{
  "categoryId": 5,
  "amount": 75000.00,
  "description": "Cement purchase - 200 bags",
  "paymentMode": "BANK_TRANSFER",
  "transactionDate": "2025-07-08",
  "remarks": "Paid to Ramco Cements supplier",
  "attachmentUrl": "https://storage.example.com/receipts/receipt-001.pdf"
}
```

### Response — `201 Created`

```json
{
  "transactionId": 101,
  "projectId": 1,
  "projectName": "Residential Villa - Anna Nagar",
  "categoryId": 5,
  "categoryName": "CEMENT",
  "categoryType": "EXPENSE",
  "amount": 75000.00,
  "description": "Cement purchase - 200 bags",
  "paymentMode": "BANK_TRANSFER",
  "transactionDate": "2025-07-08",
  "remarks": "Paid to Ramco Cements supplier",
  "attachmentUrl": "https://storage.example.com/receipts/receipt-001.pdf",
  "createdAt": "2025-07-10T11:00:00"
}
```

### Validation Rules

| Field             | Rule                                                                        |
|-------------------|-----------------------------------------------------------------------------|
| `projectId`       | Must exist in the database. Returns `404` if not found.                     |
| `categoryId`      | Required. Must exist in the categories table. Returns `404` if not found.   |
| `amount`          | Required. Must be > 0. Max 13 digits, 2 decimal places.                     |
| `description`     | Optional. No length restriction.                                            |
| `paymentMode`     | Optional. Allowed values: `CASH`, `UPI`, `BANK_TRANSFER`, `CHEQUE`.         |
| `transactionDate` | Required. Format: `YYYY-MM-DD`. Cannot be a future date.                    |
| `remarks`         | Optional. No length restriction.                                            |
| `attachmentUrl`   | Optional. Must be a valid URL if provided. Max 500 chars.                   |

---

## 5. Get Transactions By Project

**GET** `/api/v1/projects/{projectId}/transactions`

### Path Parameter

| Parameter   | Type | Description       |
|-------------|------|-------------------|
| `projectId` | Long | ID of the project |

### Query Parameters

| Parameter     | Type   | Required | Description                                         |
|---------------|--------|----------|-----------------------------------------------------|
| `categoryType`| String | No       | Filter by type: `INCOME` or `EXPENSE`               |
| `categoryId`  | Long   | No       | Filter by specific category                         |
| `fromDate`    | String | No       | Start of date range. Format: `YYYY-MM-DD`           |
| `toDate`      | String | No       | End of date range. Format: `YYYY-MM-DD`             |
| `page`        | int    | No       | Page number, default `0`                            |
| `size`        | int    | No       | Page size, default `20`                             |

### Response — `200 OK`

```json
{
  "content": [
    {
      "transactionId": 101,
      "categoryId": 5,
      "categoryName": "CEMENT",
      "categoryType": "EXPENSE",
      "amount": 75000.00,
      "description": "Cement purchase - 200 bags",
      "paymentMode": "BANK_TRANSFER",
      "transactionDate": "2025-07-08",
      "remarks": "Paid to Ramco Cements supplier",
      "attachmentUrl": "https://storage.example.com/receipts/receipt-001.pdf",
      "createdAt": "2025-07-10T11:00:00"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "pageNumber": 0,
  "pageSize": 20
}
```

### Validation Rules

| Parameter      | Rule                                                              |
|----------------|-------------------------------------------------------------------|
| `projectId`    | Must exist. Returns `404` if not found.                           |
| `categoryType` | If provided, must be `INCOME` or `EXPENSE`.                       |
| `fromDate`     | Format: `YYYY-MM-DD`. Must not be after `toDate`.                 |
| `toDate`       | Format: `YYYY-MM-DD`. Must not be before `fromDate`.              |
| `page`         | Must be >= 0.                                                     |
| `size`         | Must be between 1 and 100.                                        |

---

## 6. Dashboard

**GET** `/api/v1/dashboard`

### Query Parameters

| Parameter   | Type | Required | Description                            |
|-------------|------|----------|----------------------------------------|
| `projectId` | Long | No       | If provided, scopes dashboard to one project. If absent, returns across all projects. |

### Response — `200 OK`

```json
{
  "totalProjects": 5,
  "activeProjects": 3,
  "completedProjects": 1,
  "onHoldProjects": 1,
  "cancelledProjects": 0,
  "totalContractValue": 22500000.00,
  "totalIncome": 9800000.00,
  "totalExpense": 6400000.00,
  "netBalance": 3400000.00,
  "expenseBreakdown": [
    {
      "categoryName": "CEMENT",
      "totalAmount": 1200000.00
    },
    {
      "categoryName": "LABOUR",
      "totalAmount": 2100000.00
    },
    {
      "categoryName": "STEEL",
      "totalAmount": 950000.00
    }
  ],
  "incomeBreakdown": [
    {
      "categoryName": "ADVANCE_PAYMENT",
      "totalAmount": 4500000.00
    },
    {
      "categoryName": "STAGE_PAYMENT",
      "totalAmount": 5300000.00
    }
  ]
}
```

### Validation Rules

| Parameter   | Rule                                          |
|-------------|-----------------------------------------------|
| `projectId` | If provided, must be a positive Long and must exist. Returns `404` if not found. |

---

## 7. Report By Date Range

**GET** `/api/v1/reports`

### Query Parameters

| Parameter   | Type   | Required | Description                                                    |
|-------------|--------|----------|----------------------------------------------------------------|
| `fromDate`  | String | Yes      | Start date. Format: `YYYY-MM-DD`                               |
| `toDate`    | String | Yes      | End date. Format: `YYYY-MM-DD`                                 |
| `projectId` | Long   | No       | If provided, scopes report to one project                      |
| `type`      | String | No       | Filter by `INCOME` or `EXPENSE`. If absent, returns both.      |

### Response — `200 OK`

```json
{
  "fromDate": "2025-06-01",
  "toDate": "2025-06-30",
  "totalIncome": 500000.00,
  "totalExpense": 310000.00,
  "netBalance": 190000.00,
  "transactions": [
    {
      "transactionId": 101,
      "projectId": 1,
      "projectName": "Residential Villa - Anna Nagar",
      "categoryName": "CEMENT",
      "categoryType": "EXPENSE",
      "amount": 75000.00,
      "paymentMode": "BANK_TRANSFER",
      "transactionDate": "2025-06-08",
      "description": "Cement purchase - 200 bags"
    }
  ]
}
```

### Validation Rules

| Parameter   | Rule                                                                      |
|-------------|---------------------------------------------------------------------------|
| `fromDate`  | Required. Format: `YYYY-MM-DD`.                                           |
| `toDate`    | Required. Format: `YYYY-MM-DD`. Must be equal to or after `fromDate`.     |
| `projectId` | If provided, must exist. Returns `404` if not found.                      |
| `type`      | If provided, must be `INCOME` or `EXPENSE`.                               |
| Date range  | Maximum range: 366 days. Returns `400` if exceeded.                       |

---

## 8. Get Categories

**GET** `/api/v1/categories`

### Query Parameters

| Parameter | Type   | Required | Description                              |
|-----------|--------|----------|------------------------------------------|
| `type`    | String | No       | Filter by `INCOME` or `EXPENSE`          |

### Response — `200 OK`

```json
[
  {
    "categoryId": 1,
    "categoryName": "ADVANCE_PAYMENT",
    "categoryType": "INCOME"
  },
  {
    "categoryId": 2,
    "categoryName": "STAGE_PAYMENT",
    "categoryType": "INCOME"
  },
  {
    "categoryId": 3,
    "categoryName": "FINAL_PAYMENT",
    "categoryType": "INCOME"
  },
  {
    "categoryId": 4,
    "categoryName": "OTHER_INCOME",
    "categoryType": "INCOME"
  },
  {
    "categoryId": 5,
    "categoryName": "CEMENT",
    "categoryType": "EXPENSE"
  },
  {
    "categoryId": 6,
    "categoryName": "STEEL",
    "categoryType": "EXPENSE"
  },
  {
    "categoryId": 7,
    "categoryName": "SAND",
    "categoryType": "EXPENSE"
  },
  {
    "categoryId": 8,
    "categoryName": "BRICKS",
    "categoryType": "EXPENSE"
  },
  {
    "categoryId": 9,
    "categoryName": "LABOUR",
    "categoryType": "EXPENSE"
  },
  {
    "categoryId": 10,
    "categoryName": "PLUMBING",
    "categoryType": "EXPENSE"
  },
  {
    "categoryId": 11,
    "categoryName": "ELECTRICAL",
    "categoryType": "EXPENSE"
  },
  {
    "categoryId": 12,
    "categoryName": "TRANSPORT",
    "categoryType": "EXPENSE"
  },
  {
    "categoryId": 13,
    "categoryName": "MACHINERY",
    "categoryType": "EXPENSE"
  },
  {
    "categoryId": 14,
    "categoryName": "OTHER_EXPENSE",
    "categoryType": "EXPENSE"
  }
]
```

### Validation Rules

| Parameter | Rule                                            |
|-----------|-------------------------------------------------|
| `type`    | If provided, must be `INCOME` or `EXPENSE`.     |

---

## Common Error Responses

All endpoints return errors in this shape:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "transactionDate must not be a future date"
}
```

| HTTP Status | When                                                    |
|-------------|---------------------------------------------------------|
| `400`       | Request validation failed                               |
| `404`       | Resource not found (project, transaction, category)     |
| `409`       | Conflict — e.g., duplicate `projectCode`                |
| `500`       | Unexpected server error                                 |

---

## Enum Reference

### Project Status
| Value       | Meaning                     |
|-------------|-----------------------------|
| `ACTIVE`    | Project is ongoing          |
| `COMPLETED` | Project is finished         |
| `ON_HOLD`   | Project is paused           |
| `CANCELLED` | Project is cancelled        |

### Payment Mode
| Value           | Meaning           |
|-----------------|-------------------|
| `CASH`          | Cash payment      |
| `UPI`           | UPI transfer      |
| `BANK_TRANSFER` | Bank wire         |
| `CHEQUE`        | Cheque payment    |

### Category Type
| Value     | Meaning                       |
|-----------|-------------------------------|
| `INCOME`  | Money received for the project |
| `EXPENSE` | Money spent on the project     |
