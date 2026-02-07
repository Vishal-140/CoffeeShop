# Coffee Shop Project

This is a full-stack Coffee Shop simulation application built with Spring Boot (Backend) and React (Frontend).

## Tech Stack

### Backend
- **Language:** Java 21
- **Framework:** Spring Boot 4.0.2
- **Database:** MySQL 8.0
- **Build Tool:** Maven

### Frontend
- **Framework:** React 19
- **Build Tool:** Vite
- **Styling:** TailwindCSS
- **Routing:** React Router 7
- **HTTP Client:** Axios

## Prerequisites

- **Docker** and **Docker Compose** installed on your machine.

## How to Run

The easiest way to run the entire application is using Docker Compose.

1.  **Clone the repository** (if not already done).
2.  **Navigate to the project root directory**.
3.  **Run the following command:**

    ```bash
    docker-compose up --build
    ```

This command will start:
- **MySQL Database** on port `3306`
- **Backend API** on port `8080`
- **Frontend Application** on port `3000`

Access the frontend at: `http://localhost:3000`
Access the backend API at: `http://localhost:8080`

## Project Structure

```
CoffeeShop/
├── backend/            # Spring Boot Application
│   ├── src/            # Source code
│   ├── pom.xml         # Maven dependencies
│   └── ...
├── frontend/           # React Application
│   ├── src/            # Source code
│   ├── package.json    # Node dependencies
│   └── ...
└── docker-compose.yml  # Container orchestration
```

## API Documentation

### Barista Endpoints
**Base URL:** `/baristas`

- **Get All Baristas**
  - `GET /baristas`
  - Returns a list of all baristas.

### Order Endpoints
**Base URL:** `/orders`

- **Create Order**
  - `POST /orders`
  - **Body:**
    ```json
    {
      "drinkType": "string",  // e.g., "Latte", "Espresso"
      "customerType": "string" // e.g., "Regular", "VIP"
    }
    ```
  - Creates a new order.

- **Get Waiting Orders**
  - `GET /orders/waiting`
  - Returns orders currently in the waiting queue.

- **Get In-Progress Orders**
  - `GET /orders/in-progress`
  - Returns orders currently being prepared.

- **Get Completed Orders**
  - `GET /orders/done`
  - Returns orders that have been completed.

- **Get Abandoned Orders**
  - `GET /orders/abandoned`
  - Returns orders that were abandoned by customers.

### Simulation Endpoints
**Base URL:** `/simulation`

- **Run Simulation**
  - `GET /simulation/{seed}`
  - Runs a simulation with the provided random seed.
  - **Path Variable:** `seed` (Long) - Seed for random number generation.
