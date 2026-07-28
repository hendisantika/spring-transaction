# Spring Transaction Demo

A Spring Boot web application that demonstrates **declarative transaction management** using `@Transactional` annotations with different propagation behaviors. The project simulates a simple bank money transfer system where users can view account balances and send money between accounts — all protected by Spring's transaction framework to ensure data consistency.

## ✨ Features

- **Declarative Transaction Management** using Spring's `@Transactional` annotation
- **Transaction Propagation** strategies: `MANDATORY` and `REQUIRES_NEW`
- **Rollback on Exception** — ensures atomic money transfers (either both sides succeed or both fail)
- **Multi-Database Support** — MySQL, PostgreSQL, SQL Server, and Oracle
- **Thymeleaf Web UI** for viewing accounts and performing transfers
- **Spring Data JPA** with Hibernate ORM
- **Maven** build with wrapper included

## 🛠 Tech Stack

| Category | Technology | Version |
|----------|-----------|---------|
| Language | Java | 21 |
| Framework | Spring Boot | 4.1.0 |
| ORM | Spring Data JPA + Hibernate | — |
| Template Engine | Thymeleaf | — |
| Database | MySQL (default), PostgreSQL, MSSQL, Oracle | — |
| Build Tool | Maven | — |

## 📁 Project Structure

```
spring-transaction/
├── src/main/java/com/hendisantika/springtransaction/
│   ├── controller/
│   │   └── MainController.java          # Web controller handling page routing and form submission
│   ├── dao/
│   │   └── BankAccountDAO.java          # Data access layer with @Transactional methods
│   ├── entity/
│   │   ├── BankAccount.java             # JPA entity mapped to Bank_Account table
│   │   └── BankAccountInfo.java         # Projection DTO for listing account info
│   ├── exception/
│   │   └── BankTransactionException.java # Custom exception thrown on transaction failures
│   ├── form/
│   │   └── SendMoneyForm.java           # Form backing object for money transfer
│   └── SpringTransactionApplication.java # Main application entry point
├── src/main/resources/
│   ├── templates/
│   │   ├── _menu.html                   # Shared navigation menu fragment
│   │   ├── accountsPage.html            # Account listing page
│   │   └── sendMoneyPage.html           # Money transfer form page
│   └── application.properties           # Application configuration (datasource, JPA)
├── src/test/java/
│   └── SpringTransactionApplicationTests.java # Basic context load test
└── pom.xml                              # Maven build configuration
```

## ⚙️ Prerequisites

- **Java 21** or later
- **Maven 3.8+** (or use the included Maven wrapper `mvnw`)
- **MySQL** database running locally (or configure a different database)

## 🗄 Database Setup

### 1. Create the database

```sql
CREATE DATABASE IF NOT EXISTS `spring-transaction`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

### 2. Create the `Bank_Account` table

```sql
CREATE TABLE IF NOT EXISTS `Bank_Account` (
  `id`         BIGINT       NOT NULL AUTO_INCREMENT,
  `Full_Name`  VARCHAR(128) NOT NULL,
  `Balance`    DOUBLE       NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 3. Insert sample data

```sql
INSERT INTO `Bank_Account` (`Full_Name`, `Balance`) VALUES
  ('Tom',    1000.00),
  ('Jerry',  500.00),
  ('Donald', 200.00);
```

### 4. Configure database connection

Edit `src/main/resources/application.properties` to match your database credentials:

```properties
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/spring-transaction?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=root
```

> **Note:** Hibernate's `ddl-auto` is set to `none`, so the table must already exist in the database.

## 🚀 How to Run

### Build the project

```bash
# Using Maven
mvn clean package -DskipTests

# Or using the Maven wrapper
./mvnw clean package -DskipTests
```

### Run the application

```bash
# Using Maven
mvn spring-boot:run

# Or run the built JAR directly
java -jar target/spring-transaction-0.0.1-SNAPSHOT.jar
```

The application starts on port **8080** by default.

## 📖 Usage

### Available Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | View all bank accounts and balances |
| GET | `/sendMoney` | Show the money transfer form |
| POST | `/sendMoney` | Process a money transfer between accounts |

### Web UI

1. **Account Listing Page** (`/`) — Displays all accounts with ID, full name, and current balance.

2. **Money Transfer Page** (`/sendMoney`) — A form to transfer money:
   - Enter the **From Account ID** (e.g., `1` for Tom)
   - Enter the **To Account ID** (e.g., `2` for Jerry)
   - Enter the **Amount** (e.g., `700`)
   - Click **Send** to execute the transfer

If the source account has insufficient funds, an error message is displayed and no data is changed.

## 🔄 Transaction Management Explained

This project demonstrates two key **Spring Transaction Propagation** strategies working together to guarantee data consistency during money transfers.

### `addAmount()` — Propagation: `MANDATORY`

```java
@Transactional(propagation = Propagation.MANDATORY)
public void addAmount(Long id, double amount) throws BankTransactionException {
    // ... validate account and balance, then update
}
```

- **`MANDATORY`** means a transaction **must already exist** when this method is called.
- If no transaction is active, Spring throws an `IllegalTransactionStateException`.
- This method is called twice from `sendMoney()` — once to credit the recipient (`+amount`) and once to debit the sender (`-amount`).

### `sendMoney()` — Propagation: `REQUIRES_NEW`

```java
@Transactional(propagation = Propagation.REQUIRES_NEW,
        rollbackFor = BankTransactionException.class)
public void sendMoney(Long fromAccountId, Long toAccountId, double amount)
        throws BankTransactionException {
    addAmount(toAccountId, amount);
    addAmount(fromAccountId, -amount);
}
```

- **`REQUIRES_NEW`** creates a **new, independent transaction** for the entire money transfer operation.
- **`rollbackFor = BankTransactionException.class`** ensures that when a `BankTransactionException` is thrown (e.g., insufficient funds), the transaction is rolled back even though `BankTransactionException` is a checked exception.
- Because both `addAmount()` calls participate in the **same outer transaction** (due to `MANDATORY`), if **either** transfer fails, **both** are rolled back — preventing inconsistent data.

### How It Works Together

```
sendMoney()  [REQUIRES_NEW — starts new transaction]
  │
  ├── addAmount(toId, +amount)   [MANDATORY — joins the outer transaction]
  │     └── If account not found → throw BankTransactionException
  │
  └── addAmount(fromId, -amount) [MANDATORY — joins the outer transaction]
        └── If insufficient funds → throw BankTransactionException
                                        → entire transaction rolls back
                                        → no partial state is persisted
```

**Key takeaway:** The combination of `REQUIRES_NEW` (on the outer `sendMoney`) and `MANDATORY` (on the inner `addAmount`) ensures that both credit and debit operations are **atomic** — either both succeed or both fail, never leaving the system in an inconsistent state.

## 🧪 Running Tests

```bash
mvn test
```

The project includes a basic Spring Boot context load test in `SpringTransactionApplicationTests.java`.

## 📦 Multi-Database Support

Drivers for the following databases are included in `pom.xml`:

| Database | Driver | Scope |
|----------|--------|-------|
| MySQL | `mysql-connector-j` | runtime |
| PostgreSQL | `postgresql` | runtime |
| SQL Server | `mssql-jdbc` | runtime |
| Oracle | `ojdbc6` | compile |

To switch databases, update the `spring.datasource.*` properties in `application.properties` and set the appropriate Hibernate dialect:

```properties
# PostgreSQL example
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://localhost:5432/spring-transaction
spring.datasource.username=your_user
spring.datasource.password=your_password
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

