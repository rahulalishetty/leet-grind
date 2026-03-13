# 1661. Average Time of Process per Machine — Approaches

## Approach 1: Transform Values with CASE WHEN and then Calculate

### Algorithm

To compute the time required for each process, we need the difference between the **end timestamp** and the **start timestamp** for every `(machine_id, process_id)` pair.

Instead of pairing rows explicitly, we can transform the timestamps:

- Convert `start` timestamps to **negative values**
- Keep `end` timestamps **positive**

This works because:

```
(-start) + end = end - start
```

So if we aggregate timestamps using `SUM()`, we directly obtain the processing time.

### Transform Start Timestamp

```sql
SUM(
  CASE
    WHEN activity_type = 'start' THEN timestamp * -1
    ELSE timestamp
  END
)
```

This produces the **total processing time for each process**.

---

### Calculate Average Processing Time

Since machines may run multiple processes, we divide the total processing time by the **number of distinct processes**.

```sql
SUM(
  CASE
    WHEN activity_type = 'start' THEN timestamp * -1
    ELSE timestamp
  END
) * 1.0 / (SELECT COUNT(DISTINCT process_id))
```

---

### Round the Result

The result must be rounded to **3 decimal places**:

```sql
ROUND(..., 3) AS processing_time
```

---

### Implementation

```sql
SELECT
    machine_id,
    ROUND(
        SUM(
            CASE
                WHEN activity_type = 'start'
                THEN timestamp * -1
                ELSE timestamp
            END
        ) * 1.0 / (SELECT COUNT(DISTINCT process_id)),
        3
    ) AS processing_time
FROM Activity
GROUP BY machine_id;
```

---

# Approach 2: Self Join the Table

## Algorithm

In this method we **join the Activity table with itself**:

- One instance stores **start timestamps**
- Another instance stores **end timestamps**

### Step 1: Join Start and End Records

```sql
SELECT *
FROM Activity a, Activity b
WHERE
    a.machine_id = b.machine_id
AND a.process_id = b.process_id
AND a.activity_type = 'start'
AND b.activity_type = 'end';
```

This produces rows containing:

| machine_id | process_id | start_timestamp | end_timestamp |

Example output:

| machine_id | process_id | start | end   |
| ---------- | ---------- | ----- | ----- |
| 0          | 0          | 0.712 | 1.520 |
| 0          | 1          | 3.140 | 4.120 |
| 1          | 0          | 0.550 | 1.550 |
| 1          | 1          | 0.430 | 1.420 |
| 2          | 0          | 4.100 | 4.512 |
| 2          | 1          | 2.500 | 5.000 |

---

### Step 2: Calculate Processing Time

Processing time per process:

```sql
(b.timestamp - a.timestamp)
```

---

### Step 3: Compute Average per Machine

We compute the average processing time per machine:

```sql
AVG(b.timestamp - a.timestamp)
```

Then round it to **3 decimal places**.

---

### Implementation

```sql
SELECT
    a.machine_id,
    ROUND(AVG(b.timestamp - a.timestamp), 3) AS processing_time
FROM Activity a, Activity b
WHERE
    a.machine_id = b.machine_id
AND a.process_id = b.process_id
AND a.activity_type = 'start'
AND b.activity_type = 'end'
GROUP BY machine_id;
```

---

# Key SQL Concepts

- **CASE WHEN** for conditional transformation
- **Self Join** for pairing start/end rows
- **SUM / AVG** for aggregation
- **ROUND()** for formatting numeric output
