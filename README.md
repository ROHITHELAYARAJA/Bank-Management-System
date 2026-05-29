# Bank-Management-System
# 🏦 Bank Management System

> A console-based banking application built with **Core Java · JDBC · MySQL · BCrypt** — following a clean layered backend architecture with DAO and Service design patterns.

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![JDBC](https://img.shields.io/badge/JDBC-007396?style=for-the-badge&logo=java&logoColor=white)
![BCrypt](https://img.shields.io/badge/BCrypt-Security-green?style=for-the-badge)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

---

## 📌 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Architecture](#-architecture)
- [Project Structure](#-project-structure)
- [Database Schema](#-database-schema)
- [Technologies Used](#-technologies-used)
- [Setup Instructions](#-setup-instructions)
- [Sample Console Flow](#-sample-console-flow)
- [Backend Concepts](#-backend-concepts-used)
- [Future Improvements](#-future-improvements)
- [Author](#-author)

---

## 🧭 Overview

This project simulates real-world banking operations through a structured Java backend. It demonstrates secure authentication, transactional fund management, and clean separation of concerns across UI, Service, and DAO layers — concepts directly applicable to enterprise backend development.

---

## 🚀 Features

### 🔐 Security
- Password hashing with **BCrypt** (salted, one-way — never stored as plain text)
- **PreparedStatement** throughout — prevents SQL Injection
- Login verification using `BCrypt.checkpw()` — password never passes through SQL query

### 💳 Banking Operations
| Operation | Description |
|---|---|
| Create Account | Generates unique 6-digit account number automatically |
| Login | BCrypt-verified secure authentication |
| Deposit Money | Adds funds with transaction log entry |
| Withdraw Money | Validates balance limit before deducting |
| Transfer Money | Atomic transfer with full rollback safety |
| View Balance | Fetches live data from DB on demand |
| Delete Account | Credential-verified account removal |

### 🏦 Account Types
| Type | Rule |
|---|---|
| **Savings** | Cannot go below ₹0 |
| **Current** | Supports overdraft up to -₹1000 |

---

## 🧱 Architecture

```
┌─────────────────────────────────┐
│        BankApp.java             │  ← UI Layer (console menus, input/output)
└────────────────┬────────────────┘
                 │
┌────────────────▼────────────────┐
│       BankService.java          │  ← Business Logic Layer (rules, validation)
└────────────────┬────────────────┘
                 │
┌────────────────▼────────────────┐
│        BankDAO.java             │  ← Data Access Layer (all SQL queries)
└────────────────┬────────────────┘
                 │
┌────────────────▼────────────────┐
│     MySQL Database (bank)       │  ← Persistence Layer
└─────────────────────────────────┘
```

Each layer has **one responsibility only** — UI knows nothing about SQL, Service knows nothing about console input.

---

## 📂 Project Structure

```
BankManagementSystem/
│
├── src/
│   └── banking/
│       ├── Account.java           ← Model class (data shape)
│       ├── BankApp.java           ← Entry point, menus, user input
│       ├── BankDAO.java           ← All SQL queries live here
│       ├── BankService.java       ← Business logic, BCrypt, validation
│       └── DatabaseConnection.java ← Single DB connection factory
│
├── pom.xml                        ← Maven dependencies
└── README.md
```

---

## 🗄️ Database Schema

### `customer` table
```sql
CREATE TABLE customer (
    acno      INT           NOT NULL AUTO_INCREMENT,
    cname     VARCHAR(45)   UNIQUE NOT NULL,
    balance   DECIMAL(15,2) DEFAULT 0.00,
    pass_code VARCHAR(60)   NOT NULL,
    actype    VARCHAR(20)   DEFAULT 'Savings',
    PRIMARY KEY (acno)
);
```

### `transactions` table
```sql
CREATE TABLE transactions (
    txn_id      INT AUTO_INCREMENT PRIMARY KEY,
    sender_ac   INT NOT NULL,
    receiver_ac INT,
    amount      INT NOT NULL,
    type        ENUM('DEBIT','CREDIT','DEPOSIT','WITHDRAW') NOT NULL,
    timestamp   DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_ac) REFERENCES customer(acno)
);
```

---

## 🛠️ Technologies Used

| Technology | Version | Purpose |
|---|---|---|
| Java | JDK 17+ | Core backend logic |
| JDBC | — | Database connectivity |
| MySQL | 8.0+ | Relational database |
| BCrypt (jBCrypt) | 0.4 | Secure password hashing |
| Maven | 3.x | Dependency management |
| Git & GitHub | — | Version control |

---

## ⚙️ Setup Instructions

### 1. Clone the Repository
```bash
git clone https://github.com/ROHITHELAYARAJA/BankManagementSystem.git
cd BankManagementSystem
```

### 2. Set Up MySQL Database
```sql
CREATE DATABASE bank;
USE bank;
```
Then run the schema SQL provided above for both tables.

### 3. Configure Database Credentials

Edit `DatabaseConnection.java`:
```java
private static final String URL      = "jdbc:mysql://localhost:3306/bank";
private static final String USER     = "root";
private static final String PASSWORD = "your_password"; // ← update this
```

### 4. Install Maven Dependencies
```bash
mvn clean install
```

Or manually add these jars if not using Maven:
- `mysql-connector-j-8.0.33.jar`
- `jbcrypt-0.4.jar`

### 5. Run the Application
```bash
# Via Maven
mvn exec:java -Dexec.mainClass="banking.BankApp"

# Or directly in IntelliJ — run BankApp.java
```

---

## 📸 Sample Console Flow

```
===============================
 WELCOME TO THE BANK
===============================
1) CREATE ACCOUNT
2) LOGIN ACCOUNT
3) DELETE ACCOUNT
4) EXIT
ENTER YOUR CHOICE: 1

ENTER UNIQUE USERNAME: rohith
ENTER PASSWORD: ••••••••
ENTER ACCOUNT TYPE (Savings / Current): Savings

✅ ACCOUNT CREATED! YOUR ACCOUNT NUMBER: 483921

----------------------------------------------
ENTER YOUR CHOICE: 2
ENTER USERNAME: rohith
ENTER PASSWORD: ••••••••

WELCOME, ROHITH [Savings]!

-- ACCOUNT MENU --
1) TRANSFER MONEY
2) VIEW BALANCE
3) DEPOSIT MONEY
4) WITHDRAW MONEY
5) LOGOUT
```

---

## 🧠 Backend Concepts Used

- **Layered Architecture** — UI / Service / DAO separation
- **DAO Pattern** — all SQL isolated in one class
- **OOP Principles** — encapsulation, single responsibility
- **JDBC** — PreparedStatement, ResultSet, connection management
- **SQL Transactions** — `setAutoCommit(false)`, `commit()`, `rollback()`
- **BCrypt Hashing** — salted password storage, `checkpw()` verification
- **Exception Handling** — graceful error recovery at every layer
- **Random Account Number Generation** — collision-safe 6-digit acno

---

## 🔥 Future Improvements

- [ ] Spring Boot REST API conversion
- [ ] JWT Authentication
- [ ] Transaction History viewer (last 5 transactions on login)
- [ ] Docker containerization
- [ ] JUnit test coverage
- [ ] Swagger API documentation
- [ ] Redis caching for balance queries
- [ ] Admin dashboard

---

## 📌 Learning Outcomes

This project demonstrates understanding of:
- Real-world backend architecture design
- Secure authentication implementation
- SQL transaction management
- JDBC database operations
- Business logic separation from data access
- Financial application development patterns

---

## 👨‍💻 Author

**Rohith E**
B.E. Artificial Intelligence & Data Science — VSB Engineering College, Karur

[![GitHub](https://img.shields.io/badge/GitHub-ROHITHELAYARAJA-181717?style=for-the-badge&logo=github)](https://github.com/ROHITHELAYARAJA)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-Rohith_E-0A66C2?style=for-the-badge&logo=linkedin)](https://linkedin.com/in/rohith-e-600452331)

---

> *"Built to demonstrate real backend thinking — not just CRUD, but architecture, security, and data integrity."*
