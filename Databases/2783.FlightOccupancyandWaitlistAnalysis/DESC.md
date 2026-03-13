# 2783. Flight Occupancy and Waitlist Analysis

## Table: Flights

| Column Name | Type |
| ----------- | ---- |
| flight_id   | int  |
| capacity    | int  |

**Notes**

- `flight_id` contains **unique values**.
- Each row represents a **flight** and the number of **available seats (capacity)** on that flight.

---

## Table: Passengers

| Column Name  | Type |
| ------------ | ---- |
| passenger_id | int  |
| flight_id    | int  |

**Notes**

- `passenger_id` contains **unique values**.
- Each row represents a passenger who **booked a ticket for a specific flight**.

---

# Problem

Passengers book tickets for flights in advance.

The booking logic works as follows:

- If there are **available seats** on the flight, the passenger **gets a confirmed seat**.
- If the flight has already reached its **capacity**, the passenger is placed on a **waitlist**.

---

# Task

For **each flight**, report:

| Column       | Description                                         |
| ------------ | --------------------------------------------------- |
| flight_id    | ID of the flight                                    |
| booked_cnt   | Number of passengers who **successfully got seats** |
| waitlist_cnt | Number of passengers who are **on the waitlist**    |

---

# Requirements

- The result must be **ordered by `flight_id` in ascending order**.

---

# Example

## Input

### Flights Table

| flight_id | capacity |
| --------- | -------- |
| 1         | 2        |
| 2         | 2        |
| 3         | 1        |

---

### Passengers Table

| passenger_id | flight_id |
| ------------ | --------- |
| 101          | 1         |
| 102          | 1         |
| 103          | 1         |
| 104          | 2         |
| 105          | 2         |
| 106          | 3         |
| 107          | 3         |

---

# Output

| flight_id | booked_cnt | waitlist_cnt |
| --------- | ---------- | ------------ |
| 1         | 2          | 1            |
| 2         | 2          | 0            |
| 3         | 1          | 1            |

---

# Explanation

## Flight 1

Capacity:

```
2
```

Passengers who booked:

```
101, 102, 103
```

Total bookings:

```
3
```

Since only **2 seats are available**:

- **2 passengers get seats**
- **1 passenger goes to the waitlist**

Result:

```
booked_cnt = 2
waitlist_cnt = 1
```

---

## Flight 2

Capacity:

```
2
```

Passengers:

```
104, 105
```

Total bookings:

```
2
```

Because bookings equal capacity:

- **All passengers get seats**
- **No one is waitlisted**

Result:

```
booked_cnt = 2
waitlist_cnt = 0
```

---

## Flight 3

Capacity:

```
1
```

Passengers:

```
106, 107
```

Total bookings:

```
2
```

Since the flight only has **1 seat**:

- **1 passenger gets a seat**
- **1 passenger is waitlisted**

Result:

```
booked_cnt = 1
waitlist_cnt = 1
```

---

# Summary

For each flight:

1. Count the **total passengers who booked the flight**.
2. Compare it with the **flight capacity**.
3. Compute:

```
booked_cnt = min(total_passengers, capacity)
waitlist_cnt = max(total_passengers - capacity, 0)
```
