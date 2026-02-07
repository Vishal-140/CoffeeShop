# Coffee Shop Project

This is a full-stack Coffee Shop application built with Spring Boot (Backend) and React (Frontend).

## 🐳 Docker Images

The application is containerized and available on Docker Hub:

*   **Backend Image:** [`vkc140/coffeeshop-backend`](https://hub.docker.com/r/vkc140/coffeeshop-backend)
*   **Frontend Image:** [`vkc140/coffeeshop-frontend`](https://hub.docker.com/repository/docker/vkc140/coffeeshop-frontend)

## Project Overview

### Problem Statement: The Coffee Shop Barista Dilemma

**Domain:** Food Service Operations

**Business Story:**
Coffee Shop receives 200-300 customers during morning rush (7-10 AM). They have 3 baristas and a variety of drinks with different preparation times. Customers get frustrated if they wait more than 8 minutes. Currently, baristas work first-come-first-served (FIFO), but this means someone ordering a simple cold brew might wait behind 3 people ordering complex specialty drinks.

**The Challenge:**
Create a smart order queuing system that:
*   Assigns orders to baristas to minimize average customer wait time.
*   Ensures no customer waits more than 10 minutes (hard constraint).
*   Balances workload among baristas.
*   Handles customer psychology: people who ordered first shouldn’t see too many later arrivals served first.

### Detailed Requirements

**Menu & Preparation Times:**
| Drink Type | Prep Time | Frequency | Price |
| :--- | :--- | :--- | :--- |
| Cold Brew | 1 min | 25% | ₹120 |
| Espresso | 2 min | 20% | ₹150 |
| Americano | 2 min | 15% | ₹140 |
| Cappuccino | 4 min | 20% | ₹180 |
| Latte | 4 min | 12% | ₹200 |
| Specialty (Mocha) | 6 min | 8% | ₹250 |

**Operating Parameters:**
*   **Operating Hours:** 7:00 AM - 10:00 AM (peak rush)
*   **Staff:** 3 baristas (uniform skill level)
*   **Customer Volume:** 200-300 customers (avg 250)
*   **Arrival Pattern:** Poisson distribution (λ = 1.4 customers/minute)

**Customer Psychology Factors:**
*   Customers tolerate 1-2 people who arrived later being served first if those orders are quick.
*   Regular customers wait 10 min, new customers abandon after 8 min.
*   Customers can see who’s being served (transparency matters).

**Constraints:**
*   **Hard:** No customer waits > 10 minutes.
*   **Hard:** Orders cannot be split (same barista makes all drinks in one order).
*   **Soft:** Minimize average wait time.
*   **Soft:** Balance barista workload.

---

## Solution Approach

### Algorithm: Dynamic Priority Queue with Predictive Scheduling

The system uses a real-time decision-making engine that creates a priority-based queue with look-ahead to balance fairness and efficiency.

#### Priority Scoring Function
A priority score (0-100+) is calculated for each waiting order based on:

1.  **Wait Time (Max 40):** Longer wait = higher priority. Formula: `min(40, waitMinutes * 4)`
2.  **Order Complexity (Max 25):** Shorter orders get a bonus for throughput.
    *   1 min (Cold Brew) -> 25 points
    *   2 min (Espresso) -> 18 points
    *   4 min (Latte) -> 10 points
    *   6 min (Specialty) -> 5 points
3.  **Loyalty Status (Max 20):**
    *   VIP -> 20 points
    *   PREMIUM -> 15 points
    *   GOLD -> 10 points
    *   REGULAR -> 5 points
4.  **Urgency (Max 25):** Approaching timeout gets a significant boost.
    *   Wait >= 8 min -> +25 points
    *   Wait >= 6 min -> +15 points

**Boosters:**
*   **Emergency Boost:** If wait time > 8 min, add **+50 points**.
*   **Fairness Boost:** If an order has been skipped > 3 times, add **+30 points**.

### Key Implementation Steps

#### 1. Real-time Order Assignment
*   Incoming orders enter a priority queue.
*   Scores are recalculated dynamically based on the current simulation time.
*   Available baristas pick the highest-priority compatible order.

#### 2. Workload Balancing
*   The system calculates the average workload across all baristas.
*   **Overloaded Baristas (> 1.2x average):** Prefer short orders (<= 2 min prep time) to catch up.
*   **Underutilized Baristas:** Can take complex orders.
*   **Emergency Override:** If a customer has been waiting >= 10 minutes, the next available barista takes the order regardless of workload.

#### 3. Fairness Enforcement
*   The system tracks how many times an order has been "skipped" in the queue.
*   If an order is skipped more than 3 times, it receives a significant priority boost to ensure it gets served next.

#### 4. Emergency Handling
*   **Abandonment:** New customers waiting >= 8 minutes will abandon the queue.
*   **Hard Constraint:** The system prioritizes orders approaching the 10-minute mark above all else.

### Expected Performance (vs FIFO)
*   **Average Wait Time:** Reduced (~4.8 min vs ~6.2 min)
*   **Timeout Rate:** Significantly lower (~2.3% vs ~8.5%)
*   **Workload Balance:** Improved distribution among baristas.

---

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
│   ├── src/main/java/com/example/CoffeeShop/
│   │   ├── model/          # Entities (Order, Barista)
│   │   ├── service/        # Business Logic (QueueService, PriorityService)
│   │   ├── util/           # Helpers (PriorityCalculator)
│   │   └── ...
│   └── ...
├── frontend/           # React Application
│   ├── src/            # Source code
│   ├── package.json    # Node dependencies
│   └── ...
└── docker-compose.yml  # Container orchestration
```

## API Documentation

### 1. Barista Controller
**Base URL:** `/baristas`

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/` | Returns a list of all baristas with their current status and workload. |

### 2. Order Controller
**Base URL:** `/orders`

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/` | **(Simulation Internal)** Creates a new order. Payload: `{ "drinkType": "...", "customerType": "..." }` |
| `GET` | `/waiting` | Returns a list of orders currently in the **Priority Queue** (Status: `WAITING`). |
| `GET` | `/in-progress` | Returns a list of orders currently being prepared by baristas (Status: `IN_PROGRESS`). |
| `GET` | `/done` | Returns a history of completed orders (Status: `DONE`). |
| `GET` | `/abandoned` | Returns a history of orders abandoned by customers due to long wait times (Status: `ABANDONED`). |

### 3. Simulation Controller
**Base URL:** `/simulation`

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/{seed}` | Trigger a full simulation run with a specific random seed. Returns detailed metrics, order history, and barista stats. |

## Frontend Integration

The frontend application communicates with the backend via REST APIs using **Axios**. The integration is centralized in `frontend/src/services/api.js`.

### How It Works:

1.  **Dashboard & Real-time Monitoring:**
    *   The frontend polls the `/orders/waiting`, `/orders/in-progress`, and `/baristas` endpoints to update the live dashboard.
    *   **Waiting Queue:** Displays priority scores, customer types, and wait times, sorted by the backend's priority logic.
    *   **Barista Status:** Shows which barista is working on which order and their current workload.

2.  **Order History:**
    *   Fetches data from `/orders/done` and `/orders/abandoned` to display historical performance metrics and charts.

3.  **Simulation Control:**
    *   The "Run Simulation" button triggers the `GET /simulation/{seed}` endpoint.
    *   The backend runs the Monte Carlo simulation (3-hour rush period) in memory and returns the results.
    *   The frontend then visualizes these results (Average Wait Time, Abandoned Count, Barista Workload Distribution).
