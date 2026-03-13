# 1126. Active Businesses

## Table: Events

| Column Name | Type    |
| ----------- | ------- |
| business_id | int     |
| event_type  | varchar |
| occurrences | int     |

- `(business_id, event_type)` is the **primary key** (unique combination).
- Each row records how many times a specific **event type** occurred for a business.
- `occurrences` indicates the number of times that event happened.

---

# Problem

The **average activity** for an `event_type` is defined as:

```
average occurrences of that event_type across all businesses that have that event
```

A business is considered **active** if:

- it has **more than one event type**
- where the **occurrences for that event** are **strictly greater than the average occurrences** of that event type across all businesses.

Your task is to:

Return all **active businesses**.

---

# Output

Return a table containing:

| Column      |
| ----------- |
| business_id |

The result can be returned **in any order**.

---

# Example

## Input

### Events Table

| business_id | event_type | occurrences |
| ----------- | ---------- | ----------- |
| 1           | reviews    | 7           |
| 3           | reviews    | 3           |
| 1           | ads        | 11          |
| 2           | ads        | 7           |
| 3           | ads        | 6           |
| 1           | page views | 3           |
| 2           | page views | 12          |

---

# Average Activity Calculation

### reviews

```
(7 + 3) / 2 = 5
```

### ads

```
(11 + 7 + 6) / 3 = 8
```

### page views

```
(3 + 12) / 2 = 7.5
```

---

# Evaluate Each Business

### Business 1

| event_type | occurrences | avg | condition   |
| ---------- | ----------- | --- | ----------- |
| reviews    | 7           | 5   | greater     |
| ads        | 11          | 8   | greater     |
| page views | 3           | 7.5 | not greater |

Business **1 exceeds the average for two event types**, so it is **active**.

---

### Business 2

| event_type | occurrences | avg | condition   |
| ---------- | ----------- | --- | ----------- |
| ads        | 7           | 8   | not greater |
| page views | 12          | 7.5 | greater     |

Only **one event type exceeds the average**, so **not active**.

---

### Business 3

| event_type | occurrences | avg | condition   |
| ---------- | ----------- | --- | ----------- |
| reviews    | 3           | 5   | not greater |
| ads        | 6           | 8   | not greater |

No event types exceed the average, so **not active**.

---

# Output

| business_id |
| ----------- |
| 1           |

---

# Key Idea

A business is active if it satisfies:

```
COUNT(event_type where occurrences > average_occurrences(event_type)) > 1
```
