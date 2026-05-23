<<<<<<< HEAD
# Online Grocery Ordering Management System

SE1020 Object-Oriented Programming project built as a Java web-based application using Spring Boot and file handling (`.txt` files only).

## Project Overview
This system supports customer and admin flows for:
- Customer registration/login/profile management
- Product catalog and product management
- Cart, checkout, and order placement
- Delivery scheduling and updates
- Payment records and payment status management

No database is used. All persistence is file-based under the `data/` folder.

## Tech Stack
- Java 17
- Spring Boot
- Thymeleaf
- HTML, CSS, JavaScript
- Bootstrap 5 (CDN)
- Maven

## How To Run
1. Open the project in IntelliJ IDEA.
2. Make sure Java 17 is selected.
3. Run:
   ```bash
   mvn spring-boot:run
   ```
4. Open: `http://localhost:8080`

## Default Admin Login
- Username: `admin`
- Password: `admin123`
- Role: `ADMIN`

## File Storage (No Database)
Data files are stored in project root:
- `data/customers.txt`
- `data/products.txt`
- `data/cart.txt`
- `data/orders.txt`
- `data/deliveries.txt`
- `data/payments.txt`
- `data/admins.txt`

App automatically creates the `data/` folder and required files if missing.

## OOP Concepts Used
- Encapsulation:
  - Private fields + getters/setters in model classes.
- Inheritance:
  - `RegularCustomer` and `PremiumCustomer` extend `Customer`.
  - `PerishableProduct` and `NonPerishableProduct` extend `Product`.
  - `StandardDelivery` and `ExpressDelivery` extend `Delivery`.
  - `CashOnDelivery` and `OnlinePayment` extend `Payment`.
- Polymorphism:
  - Overridden methods such as `calculateDiscount`, `isValidForSale`, `calculateDeliveryFee`, `generateReceipt`, `verifyPayment`.
- Abstraction:
  - Abstract classes: `Customer`, `Product`, `Cart`, `Delivery`, `Payment`.

## CRUD Operations Implemented
- Customer: create, read/search, update, delete
- Product: create, read/search, update, delete
- Cart: add/read/update/remove/clear
- Order: create/read/track/update status/cancel
- Delivery: create/read/update
- Payment: create/read/update status

## Screens / Pages
- `index.html`
- `login.html`
- `register.html`
- `dashboard.html`
- `admin-dashboard.html`
- `customers.html`
- `customer-form.html`
- `products.html`
- `product-form.html`
- `product-catalog.html`
- `cart.html`
- `checkout.html`
- `orders.html`
- `order-details.html`
- `deliveries.html`
- `payments.html`
- `error.html`

## Notes
- Project is intentionally beginner-friendly and viva-friendly.
- Uses clear layered architecture: controller, service, repository, model.
=======
# Project-OOP
>>>>>>> 87e0471b5cc06c4b2d2f344282c47bd52e615a81
