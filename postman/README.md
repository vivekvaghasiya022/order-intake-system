# Postman Collection Summary

## 📦 What's Included

Complete Postman testing suite for Order Intake System REST API with 24+ automated test scenarios.

## 📁 Files in `/postman` Directory

| File | Description                               | Size      |
|------|-------------------------------------------|-----------|
| `Order_Intake_System_API_Collection.json` | Complete test collection with 17 requests | ~27KB      |
| `Order_Intake_System_API_Environment.json` | Environment configuration                 | ~1KB      |
| `POSTMAN_GUIDE.md` | Comprehensive usage guide                 | Full docs |
| `QUICK_REFERENCE.md` | Quick start reference card                | 1-page    |

## ✅ Test Coverage

### Test Categories

1. **Positive Scenarios (6 tests)** ✅
    - Create order with valid data
    - Get all orders
    - Get order by existing ID
    - Get all notifications
    - Get notifications by existing ID
    - Get notifications by existing Order ID

2. **Validation Scenarios - Validation Errors (3 tests)** ❌
    - Invalid email format
    - Invalid quantity (0 or negative)
    - Malformed JSON

3. **Negative Scenarios - Not Found (2 tests)** 🔍
    - Get order by Non-existing ID (999)
    - Get notification by Non-existing ID (999)

4. **Edge Cases (2 tests)** 🎯
    - Very long product code (200+ characters)
    - Special characters in product code (boundary case)

5. **Performance Tests (4 tests)** ⚡
    - Get all orders response time (<500ms)
    - Get order by ID response time (<300ms)
    - Get all notifications response time (<500ms)
    - Get notification by ID response time (<300ms)

## 🔬 Automated Test Assertions

Each request includes multiple automated tests:

- **Status Code Verification**: Checks correct HTTP status (200, 201, 400, 404)
- **Response Structure**: Validates JSON structure and required fields
- **Data Validation**: Verifies response data matches request
- **Error Handling**: Confirms proper error messages and formats
- **Performance**: Measures response times
- **Environment Variables**: Automatically captures and reuses data

## 🚀 Quick Import

### Step 1: Import Files
1. Open Postman
2. Click **Import** button
3. Drag & drop both JSON files or click **Choose Files**
4. Select:
    - `Order_Intake_System_API_Collection.json`
    - `Order_Intake_System_API_Environment.json`
5. Click **Import**

### Step 2: Select Environment
1. Top-right dropdown menu
2. Select **Order Intake System Env**
3. You're ready to test!

### Step 3: Run Tests
```
Option A: Run entire collection
  → Click collection name → Run

Option B: Run specific folder
  → Hover over folder → Click ▶ icon

Option C: Run individual request
  → Click request → Send
```

## 📊 Expected Results

When running against a fresh database:

| Category | Expected Pass Rate |
|----------|--------------------|
| Positive Scenarios | 100% (6/6)         |
| Validation Errors | 100% (3/3)         |
| Not Found Errors | 100% (2/2)         |
| Edge Cases | 100% (2/2)         |
| Performance Tests | 100% (4/4)         |
| **Overall** | **100%**           |

## 🎨 Features

✅ **Automated Testing** - No manual verification needed  
✅ **Environment Variables** - Dynamic data management  
✅ **Pre/Post Scripts** - Advanced test logic  
✅ **Response Validation** - Structure and data checks  
✅ **Performance Monitoring** - Response time tracking  
✅ **Error Scenarios** - Comprehensive negative testing  
✅ **Documentation** - Detailed guides included  
✅ **CI/CD Ready** - Newman CLI compatible  

## 🔧 Configuration

### Environment Variables

| Variable               | Default               | Description                   |
|------------------------|-----------------------|-------------------------------|
| base_url_orders        | http://localhost:8081 | Order API base URL            |
| base_url_notifications | http://localhost:8082 | Notification API base URL     |
| orderId                | (dynamic)             | Auto-set from create response |
| notificationId         | (dynamic)             | Auto-set from create response |

### Customization

Change environment for different setups:

```
Local:      http://localhost:8081
Custom:     http://localhost:8083
Remote:     https://api.yourserver.com
```

## 💻 CLI Usage (Newman)

Run tests from command line:

```bash
# Install Newman
npm install -g newman

# Run collection
newman run Order_Intake_System_API_Collection.json \
  -e Order_Intake_System_API_Collection.json

# Generate HTML report
newman run Order_Intake_System_API_Environment.json \
  -e Order_Intake_System_API_Environment.json \
  -r html

# Run specific folder
newman run Order_Intake_System_API_Collection.json \
  --folder "Positive Scenarios"
```

## 📈 Test Execution Order

Recommended sequence:

1. **Positive Scenarios** (creates test data)
2. **Get All Records** (verifies creation)
3. **Get by ID** (tests retrieval)
4. **Negative - Validation** (tests error handling)
5. **Negative - Not Found** (tests 404 errors)
6. **Edge Cases** (boundary testing)
7. **Performance** (speed checks)

## 🐛 Troubleshooting

### Common Issues

| Issue | Solution |
|-------|----------|
| Connection refused | Start API: `docker compose up` |
| Tests failing | Verify environment is selected |
| Wrong baseUrl | Edit environment variable |

## 📖 Documentation

- **Complete Guide**: See `POSTMAN_GUIDE.md`
- **Quick Start**: See `QUICK_REFERENCE.md`
- **API Docs**: See main `README.md`
- **cURL Examples**: See `docs/curl-examples.md`

## 🎯 Use Cases

### Development Testing
- Quick validation of changes
- Regression testing
- API exploration

### Integration Testing
- End-to-end workflows
- Error handling verification
- Performance validation

### CI/CD Pipeline
- Automated testing with Newman
- Build validation
- Deployment verification

### Documentation
- Living API documentation
- Example requests/responses
- Client integration reference

## 🔄 Version History

**v1.0 (Current)**
- 17+ comprehensive test scenarios
- Automated assertions
- Environment configuration
- Complete documentation
- Newman CLI support

## 📞 Support

For help:
1. Review `POSTMAN_GUIDE.md`
2. Check `QUICK_REFERENCE.md`
3. See main API documentation
4. Review test assertions in collection

## ✨ Benefits

- **Time Savings**: Automated vs manual testing
- **Consistency**: Same tests every time
- **Coverage**: All scenarios included
- **Documentation**: Self-documenting API
- **Integration**: Easy CI/CD setup
- **Reliability**: Catch regressions early

---

**Ready to test?** Import the collection and start exploring! 🚀

See `POSTMAN_GUIDE.md` for detailed instructions.