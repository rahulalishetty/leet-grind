# 2153. The Number of Passengers in Each Bus II

## Table: Buses

| Column Name  | Type |
| ------------ | ---- |
| bus_id       | int  |
| arrival_time | int  |
| capacity     | int  |

- `bus_id` contains **unique values**.
- Each row represents a **bus arriving at the LeetCode station**.
- `arrival_time` indicates when the bus arrives.
- `capacity` represents the **number of passengers the bus can take**.
- No two buses arrive at the same time.
- All capacities are **positive integers**.

---

## Table: Passengers

| Column Name  | Type |
| ------------ | ---- |
| passenger_id | int  |
| arrival_time | int  |

- `passenger_id` contains **unique values**.
- Each row represents a **passenger arriving at the station**.
- `arrival_time` indicates when the passenger arrives.

---

## Problem

Buses and passengers arrive at the **LeetCode station**.

A passenger can take a bus if:

```
passenger_arrival_time <= bus_arrival_time
```

and the passenger **has not already boarded another bus**.

Additionally:

- Each bus has a **limited capacity**.
- If more passengers are waiting than the bus capacity, only the **first `capacity` passengers** will board the bus.

### Goal

Write a SQL query to report the **number of passengers that boarded each bus**.

### Output Requirements

- Return a table with:
  - `bus_id`
  - `passengers_cnt`
- Order the result by **bus_id in ascending order**.

---

# Example

## Input

### Buses

| bus_id | arrival_time | capacity |
| ------ | ------------ | -------- |
| 1      | 2            | 1        |
| 2      | 4            | 10       |
| 3      | 7            | 2        |

### Passengers

| passenger_id | arrival_time |
| ------------ | ------------ |
| 11           | 1            |
| 12           | 1            |
| 13           | 5            |
| 14           | 6            |
| 15           | 7            |

---

## Output

| bus_id | passengers_cnt |
| ------ | -------------- |
| 1      | 1              |
| 2      | 1              |
| 3      | 2              |

---

## Explanation

### Passenger Arrivals

```
Passenger 11 -> time 1
Passenger 12 -> time 1
Passenger 13 -> time 5
Passenger 14 -> time 6
Passenger 15 -> time 7
```

---

### Bus 1

```
arrival_time = 2
capacity = 1
```

Passengers waiting:

```
11, 12
```

Bus can take **1 passenger**, so:

```
Passenger 11 boards Bus 1
```

Remaining waiting:

```
12
```

---

### Bus 2

```
arrival_time = 4
capacity = 10
```

Passengers waiting:

```
12
```

Bus takes passenger **12**.

Remaining waiting:

```
none
```

---

### New Passenger Arrivals

```
Passenger 13 -> time 5
Passenger 14 -> time 6
Passenger 15 -> time 7
```

Passengers waiting before Bus 3:

```
13, 14, 15
```

---

### Bus 3

```
arrival_time = 7
capacity = 2
```

Passengers waiting:

```
13, 14, 15
```

Bus takes the **first 2 passengers**:

```
13 and 14
```

Passenger **15** remains unserved.

---

## Final Boarding Counts

| bus_id | passengers_cnt |
| ------ | -------------- |
| 1      | 1              |
| 2      | 1              |
| 3      | 2              |
