# SE1020 Project Report Template

## 1. Introduction
- Project title: Online Grocery Ordering Management System
- Brief background and problem statement.

## 2. Objectives
- Build a Java web application for grocery ordering.
- Apply OOP concepts clearly.
- Implement file handling as persistence without database.

## 3. Technologies Used
- Java 17
- Spring Boot
- Thymeleaf
- HTML/CSS/JavaScript
- Bootstrap 5 CDN
- Maven
- Text file storage (`.txt`)

## 4. OOP Concepts Explanation
### 4.1 Encapsulation
- Explain private fields and getters/setters in model classes.

### 4.2 Inheritance
- `PremiumCustomer extends Customer`
- `PerishableProduct extends Product`
- `ExpressDelivery extends Delivery`
- `OnlinePayment extends Payment`

### 4.3 Polymorphism
- Overridden methods:
  - `calculateDiscount()`
  - `isValidForSale()`
  - `calculateDeliveryFee()`
  - `generateReceipt()`

### 4.4 Abstraction
- Abstract classes (`Customer`, `Product`, `Cart`, `Delivery`, `Payment`).

## 5. File Handling Explanation
- Describe each data file and record format:
  - `id|field1|field2|...`
- Explain read-parse-update-write approach.
- Explain auto file creation and missing-file handling.

## 6. CRUD Operations
- Customer CRUD
- Product CRUD
- Cart operations
- Order operations
- Delivery and Payment updates

## 7. Screenshots
- [Insert home page screenshot]
- [Insert login/register screenshot]
- [Insert product catalog screenshot]
- [Insert cart/checkout screenshot]
- [Insert admin dashboard screenshot]

## 8. GitHub Commit History
- [Paste commit history screenshots or summary]

## 9. Individual Contribution
- [Name]
- [Modules handled]
- [Contribution details]

## 10. Conclusion
- Summarize learning outcomes in OOP, file handling, and layered architecture.
