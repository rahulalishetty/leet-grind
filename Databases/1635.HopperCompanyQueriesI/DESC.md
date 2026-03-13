# 1635. Hopper Company Queries I

## Tables

### Drivers

| Column Name | Type |
| ----------- | ---- |
| driver_id   | int  |
| join_date   | date |

- `driver_id` is the **primary key**.
- Each row represents a **driver who joined the Hopper company** and the date they joined.

---

### Rides

| Column Name  | Type |
| ------------ | ---- |
| ride_id      | int  |
| user_id      | int  |
| requested_at | date |

- `ride_id` is the **primary key**.
- Each row represents a **ride request made by a user**.
- Some rides may **not be accepted**.

---

### AcceptedRides

| Column Name   | Type |
| ------------- | ---- |
| ride_id       | int  |
| driver_id     | int  |
| ride_distance | int  |
| ride_duration | int  |

- `ride_id` is the **primary key**.
- Each row contains information about a **ride that was accepted by a driver**.
- It is guaranteed that every `ride_id` here exists in the **Rides** table.

---

## Problem

For **each month of 2020**, report the following statistics:

1. **active_drivers** – the number of drivers who had joined the Hopper company **by the end of that month**.
2. **accepted_rides** – the number of rides **accepted during that month**.

The output should:

- Include all **12 months of 2020**
- Be **ordered by month in ascending order**
- Represent months as numbers (`1 = January`, `2 = February`, etc.)

---

## Example

### Input

#### Drivers

| driver_id | join_date  |
| --------- | ---------- |
| 10        | 2019-12-10 |
| 8         | 2020-01-13 |
| 5         | 2020-02-16 |
| 7         | 2020-03-08 |
| 4         | 2020-05-17 |
| 1         | 2020-10-24 |
| 6         | 2021-01-05 |

---

#### Rides

| ride_id | user_id | requested_at |
| ------- | ------- | ------------ |
| 6       | 75      | 2019-12-09   |
| 1       | 54      | 2020-02-09   |
| 10      | 63      | 2020-03-04   |
| 19      | 39      | 2020-04-06   |
| 3       | 41      | 2020-06-03   |
| 13      | 52      | 2020-06-22   |
| 7       | 69      | 2020-07-16   |
| 17      | 70      | 2020-08-25   |
| 20      | 81      | 2020-11-02   |
| 5       | 57      | 2020-11-09   |
| 2       | 42      | 2020-12-09   |
| 11      | 68      | 2021-01-11   |
| 15      | 32      | 2021-01-17   |
| 12      | 11      | 2021-01-19   |
| 14      | 18      | 2021-01-27   |

---

#### AcceptedRides

| ride_id | driver_id | ride_distance | ride_duration |
| ------- | --------- | ------------- | ------------- |
| 10      | 10        | 63            | 38            |
| 13      | 10        | 73            | 96            |
| 7       | 8         | 100           | 28            |
| 17      | 7         | 119           | 68            |
| 20      | 1         | 121           | 92            |
| 5       | 7         | 42            | 101           |
| 2       | 4         | 6             | 38            |
| 11      | 8         | 37            | 43            |
| 15      | 8         | 108           | 82            |
| 12      | 8         | 38            | 34            |
| 14      | 1         | 90            | 74            |

---

### Output

| month | active_drivers | accepted_rides |
| ----- | -------------- | -------------- |
| 1     | 2              | 0              |
| 2     | 3              | 0              |
| 3     | 4              | 1              |
| 4     | 4              | 0              |
| 5     | 5              | 0              |
| 6     | 5              | 1              |
| 7     | 5              | 1              |
| 8     | 5              | 1              |
| 9     | 5              | 0              |
| 10    | 6              | 0              |
| 11    | 6              | 2              |
| 12    | 6              | 1              |

---

## Explanation

- **January 2020**
  - Drivers joined by end of month: `10, 8` → **2 drivers**
  - Accepted rides: **0**

- **February 2020**
  - Drivers joined: `10, 8, 5` → **3 drivers**
  - Accepted rides: **0**

- **March 2020**
  - Drivers joined: `10, 8, 5, 7` → **4 drivers**
  - Accepted rides: **1 ride (ride_id 10)**

- **April 2020**
  - Active drivers remain **4**
  - Accepted rides: **0**

- **May 2020**
  - Driver `4` joins → **5 drivers**
  - Accepted rides: **0**

- **June 2020**
  - Active drivers remain **5**
  - Accepted rides: **1 ride (ride_id 13)**

- **July 2020**
  - Accepted rides: **1 ride (ride_id 7)**

- **August 2020**
  - Accepted rides: **1 ride (ride_id 17)**

- **September 2020**
  - Accepted rides: **0**

- **October 2020**
  - Driver `1` joins → **6 drivers**
  - Accepted rides: **0**

- **November 2020**
  - Accepted rides: **2 rides (20, 5)**

- **December 2020**
  - Accepted rides: **1 ride (2)**

Drivers joining in **2021** are ignored because the report is restricted to **2020**.

---
