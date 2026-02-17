# Order Intake System API - Postman Collection Guide

## Overview

This Postman collection provides comprehensive testing for the Order Intake System REST API, including all positive and negative scenarios.

## Files Included

1. **Order_Intake_System_API_Collection.json** - Complete test collection
2. **Order_Intake_System_API_Environment.json** - Environment configuration

## Collection Statistics

- **Total Requests**: 17+
- **Positive Scenarios**: 6 requests
- **Negative Scenarios**: 5 requests
- **Edge Cases**: 2 requests
- **Performance Tests**: 4 requests

## Import Instructions

### Method 1: Import Collection File

1. Open Postman
2. Click **Import** button (top left)
3. Select **Choose Files**
4. Navigate to and select:
    - `Order_Intake_System_API_Collection.json`
    - `Order_Intake_System_API_Environment.json`
5. Click **Import**

### Method 2: Import via URL (if hosted)

1. Open Postman
2. Click **Import** button
3. Select **Link** tab
4. Paste the collection URL
5. Click **Continue** → **Import**

## Environment Setup

After importing:

1. Click on **Environments** (left sidebar)
2. Select **Order Intake System Env**
3. Verify variables:
    - `base_url_orders`: `http://localhost:8081`
    - `base_url_notifications`: `http://localhost:8082`
    - `orderId`: (empty, will be set automatically)
    - `notificationId`: (empty, will be set automatically)
4. Click **Save**

### Using the Environment

1. Select the environment from the dropdown (top right)
2. Environment is now active for all requests

## Collection Structure

```
📁 Order Intake System API - Complete Test Suite
│
├── 📁 Positive Scenarios (6 requests)
│   ├── Create Order - Valid
│   ├── Get All Orders
│   ├── Get Order ID - Existing
│   ├── Get All Notifications
│   ├── Get Notification by ID - Existing
│   └── Get Notification by Order ID - Existing
│
├── 📁 Negative Scenarios - Validation Errors (3 requests)
│   ├── Create Order - Invalid Email
│   ├── Create Order - Quantity Zero
│   └── Create Order - Malformed JSON
│
├── 📁 Negative Scenarios - Not Found (2 requests)
│   ├── Get Order by ID - Non-Existing
│   └── Get Notification by ID - Non-Existing
│
├── 📁 Edge Cases (2 requests)
│   ├── Create Order - Very Long Product code
│   └── Create Order - Special Characters in Product code
│
└── 📁 Performance Tests (4 requests)
    ├── Get All Orders - Response Time Check
    ├── Get Order by Id - Response Time Check
    ├── Get All Notifications - Response Time Check
    └── Get Notification by ID - Response Time Check
```

## Running Tests

### Option 1: Run Individual Requests

1. Expand a folder (e.g., "Positive Scenarios")
2. Click on a request
3. Click **Send** button
4. View response in the bottom panel

### Option 2: Run Entire Folder

1. Hover over any folder name
2. Click the **▶** (Run) icon
3. Select **Run** in the popup
4. View results in Collection Runner

### Option 3: Run Entire Collection

1. Click on collection name
2. Click **Run** button
3. Configure run options:
    - Iterations: 1 (or more for load testing)
    - Delay: 2000ms (to complete schedule tasks)
    - Data file: None needed
4. Click **Run Order Intake System API**
5. View test results

## Test Scenarios Explained

### 1. Positive Scenarios ✅

**Purpose**: Verify that valid requests work correctly

| Request                                 | Description                    | Expected Result |
|-----------------------------------------|--------------------------------|-----------------|
| Valid Order                             | Create Order - Valid           | 201 Created |
| Get All Orders                          | Retrieve all                   | 200 OK |
| Get Order by ID (Existing)              | Get order ID 1                 | 200 OK |
| Get All Notifications                   | Retrieve all                   | 200 OK |
| Get Notification by ID (Existing)       | Get notification ID 1          | 200 OK |
| Get Notification by Order ID (Existing) | Get notification by Order ID 1 | 200 OK |

### 2. Negative Scenarios - Validation Errors ❌

**Purpose**: Verify proper validation and error handling

| Request        | Issue          | Expected Result |
|----------------|----------------|-----------------|
| Invalid Email  | Wrong format   | 400 Bad Request |
| Quantity Zero  | 0 quantity     | 400 or 503 |
| Malformed JSON | Invalid syntax | 400 Bad Request |
### 3. Negative Scenarios - Not Found ❌

**Purpose**: Verify 404 error handling

| Request                   | ID Used | Expected Result |
|---------------------------|---------|-----------------|
| Non-Existing Order        | 999 | 404 Not Found |
| Non-Existing Notification | 999 | 404 Not Found |

### 4. Edge Cases 🔍

**Purpose**: Test boundary conditions

| Request                | Test Case       | Expected Behavior |
|------------------------|-----------------|-------------------|
| Very Long Product code | 200+ characters | Accept or reject gracefully |
| Special Characters     | PR001$          | Accept and preserve |

### 5. Performance Tests ⚡

**Purpose**: Verify response times

| Request                | Max Time | Expected |
|------------------------|----------|----------|
| Get All Orders         | 500ms | Pass |
| Get Order by ID        | 300ms | Pass |
| Get All Notifications  | 500ms | Pass |
| Get Notification by ID | 300ms | Pass |

## Automated Tests

Each request includes automated tests that verify:

### Status Code Tests
```javascript
pm.test("Status code is 201", function () {
    pm.response.to.have.status(201);
});
```

### Response Structure Tests
```javascript
pm.test("Response has id", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.data).to.have.property('id');
});
```

### Response Data Tests
```javascript
pm.test("Response has correct product code", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.data.productCode).to.eql("PR1001");
});
```

### Performance Tests
```javascript
pm.test("Response time is less than 500ms", function () {
    pm.expect(pm.response.responseTime).to.be.below(500);
});
```

## Environment Variables

### Pre-configured Variables

| Variable               | Value                 | Purpose |
|------------------------|-----------------------|---------|
| base_url_orders        | http://localhost:8081 | API base URL |
| base_url_notifications | http://localhost:8082 | API base URL |
| orderId                | (empty)               | Stores created record ID |
| notificationId         | (empty)               | Stores created record ID |

### Dynamic Variables

The collection automatically saves the `orderId` from created records:

```javascript
pm.environment.set("orderId", jsonData.data.id);
```

This allows subsequent requests to use `{{orderId}}` in the URL.

## Running in Different Environments

### Local Development (Default)
```
baseUrl: http://localhost:8081
```

### Docker with Custom Port
```
baseUrl: http://localhost:8083
```

### Remote Server
```
baseUrl: https://api.yourserver.com
```

To change environment:
1. Click on environment name
2. Edit `baseUrl` value
3. Save changes

## Test Execution Order

For best results, run in this order:

1. **Positive Scenarios** (creates test data)
2. **Get All Records** (verifies data created)
3. **Get by ID** (tests retrieval)
4. **Negative Scenarios** (tests error handling)
5. **Edge Cases** (tests boundaries)
6. **Performance Tests** (measures speed)

## Expected Test Results

When running the full collection against a fresh database:

- ✅ Positive Scenarios: **6 passed** (100%)
- ⚠️ Negative - Validation: **3 passed** (100%)
- ⚠️ Negative - Not Found: **2 passed** (100%)
- ✅ Edge Cases: **2 passed** (100%, depends on validation)
- ✅ Performance: **4 passed** (100%, depends on system)

**Total**: ~16-17 tests passed

## Troubleshooting

### Issue: Connection Refused

**Problem**: Cannot reach API
**Solution**:
- Ensure API is running: `docker compose  ps`
- Check baseUrl in environment matches actual URL
- Verify port 8081 and 8082 is not blocked

### Issue: All Tests Fail

**Problem**: Wrong environment selected
**Solution**:
- Select correct environment from dropdown (top right)
- Verify environment variables are set

### Issue: 404 on Create

**Problem**: Wrong endpoint
**Solution**:
- Verify baseUrl doesn't have trailing slash
- Check endpoint path is `/api/v1/orders`

### Exporting Test Results

1. Run collection in Collection Runner
2. Click **Export Results**
3. Select format (JSON/HTML)
4. Save file

## Integration with CI/CD

### Using Newman (Postman CLI)

Install Newman:
```bash
npm install -g newman
```

Run collection:
```bash
newman run Order_Intake_System_API_Collection.json \
  -e Order_Intake_System_API_Environment.json
```

Generate HTML report:
```bash
newman run Order_Intake_System_API_Collection.json \
  -e Order_Intake_System_API_Environment.json \
  -r html
```

## Best Practices

1. **Always select the environment** before running tests
2. **Run positive scenarios first** to create test data
3. **Clear database** between full test runs for consistency
4. **Monitor response times** - slow responses may indicate issues
5. **Review failed tests** - check both request and response tabs

## Support

For issues or questions:
- Review the API documentation in `README.md`
- Check `docs/curl-examples.md` for endpoint details
- Examine request/response in Postman's console

## Version History

- **v1.0** - Initial release with 25+ test scenarios
    - Positive scenarios (6)
    - Negative scenarios (12)
    - Edge cases (4)
    - Performance tests (2)

---

**Happy Testing!** 🚀