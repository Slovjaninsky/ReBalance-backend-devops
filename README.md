## How to run the application:

Required variables to run the application:
- DATABASE_PASS
- security_key
- STORAGE_ACCOUNT_CONNECTION_STRING

For LOCAL profile mysql DB with database `rebalance_database` is required.

Goto [http://localhost:8080/swagger-ui/index.html]() to see the documentation

## How to run tests:

Add file **app_test.cfg** to /resources folder containing:
- Azure storage connection string
- security key