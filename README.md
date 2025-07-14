# Product API Service

This service manages **products** and **categories** for the e-commerce system. It exposes RESTful endpoints to perform CRUD operations, search, filter, and paginate products, as well as manage categories.

---

## 📁 Base URLs

- **Product APIs:** `/product`
- **Category APIs:** `/category`

---

## 🚀 Product Endpoints

| Method   | Endpoint        | Description                                              |
| -------- | --------------- | -------------------------------------------------------- |
| `POST`   | `/product`      | **Add a new product** <br> Request Body: `ProductDto`    |
| `GET`    | `/product/{id}` | **Get product by ID**                                    |
| `GET`    | `/product`      | **Get all products**                                     |
| `PUT`    | `/product/{id}` | **Update product by ID** <br> Request Body: `ProductDto` |
| `DELETE` | `/product/{id}` | **Delete product by ID**                                 |

### 🔍 Product Search & Filtering

| Method | Endpoint                                            | Description                                                          |
| ------ | ----------------------------------------------      | ---------------------------------------------------------------      |
| `GET`  | `/product/category?categoryName={name}`             | **Get products by category name**                                    |
| `GET`  | `/product/price?minPrice={min}&maxPrice={max}`      | **Get products within a price range**                                |
| `GET`  | `/product/search?keyword={keyword}`                 | **Search products by keyword**                                       |
| `GET`  | `/product/suggestions?partial={partialWord}`        | **Get search suggestions for auto-complete**                         |
| `GET`  | `/product/page?page={page}&size={size}`             | **Get products with pagination** <br> Defaults: page=0, size=10      |
| `GET`  | `/product/sort?sortBy={field}&direction={asc|desc}` | **Sort products by field** <br> Defaults: sortBy=name, direction=asc |


---

## 📂 Category Endpoints

| Method | Endpoint    | Description                                                |
| ------ | ----------- | ---------------------------------------------------------- |
| `POST` | `/category` | **Create a new category** <br> Request Body: `CategoryDto` |
| `GET`  | `/category` | **Get all categories**                                     |

---

## 📦 Data Transfer Objects (DTOs)

### 🛒 ProductDto

- `UUID id`
- `String name`
- `String description`
- `double price`
- `String categoryName`
- _(Add any other fields implemented in your model)_

### 📑 CategoryDto

- `Long id`
- `String name`

---

## ⚙️ Setup and Run

1. Clone the repository.
2. Configure your database connection in `application.properties`.
3. Build the project:

```bash
./mvnw clean install
```

4. Start the application:

```bash
./mvnw spring-boot:run
```

5. Access the API at `http://localhost:8081/product` for products and `http://localhost:8081/category` for categories.

---

## 📝 Notes

All endpoints return JSON responses.

Uses Spring Validation for request body validation.

Make sure required categories exist before assigning them to products.

Pagination and sorting endpoints are useful for large datasets.

## 🛠️ Technologies Used

- **Spring Boot** for building the RESTful service.
- **Spring Data JPA** for database interactions.
- **Lombok** for reducing boilerplate code in DTOs and entities.
- **Liquibase** for database migrations.
