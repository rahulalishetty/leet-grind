# 1661. Average Time of Process per Machine

## Table: Activity

| Column Name   | Type  |
| ------------- | ----- |
| machine_id    | int   |
| process_id    | int   |
| activity_type | enum  |
| timestamp     | float |

### Notes

- `(machine_id, process_id, activity_type)` is the **primary key**.
- `machine_id` represents the **machine identifier**.
- `process_id` represents the **process running on a machine**.
- `activity_type` can be:
  - `'start'` → process started
  - `'end'` → process finished
- `timestamp` represents the **time in seconds**.

Important guarantees:

- Each `(machine_id, process_id)` pair has **exactly one `start` and one `end` record**.
- The `start` timestamp is always **before** the `end` timestamp.

---

# Problem

There is a factory website where several machines run processes.

For each machine, compute the **average time required to complete a process**.

The processing time of a process is:

```
processing_time = end_timestamp - start_timestamp
```

The **average processing time** for a machine is:

```
(total time of all processes on that machine) / (number of processes)
```

### Output Requirements

Return a table containing:

| Column          | Description                                                |
| --------------- | ---------------------------------------------------------- |
| machine_id      | ID of the machine                                          |
| processing_time | Average time per process (rounded to **3 decimal places**) |

The result table may be returned **in any order**.

---

# Example

## Input

### Activity

| machine_id | process_id | activity_type | timestamp |
| ---------- | ---------- | ------------- | --------- |
| 0          | 0          | start         | 0.712     |
| 0          | 0          | end           | 1.520     |
| 0          | 1          | start         | 3.140     |
| 0          | 1          | end           | 4.120     |
| 1          | 0          | start         | 0.550     |
| 1          | 0          | end           | 1.550     |
| 1          | 1          | start         | 0.430     |
| 1          | 1          | end           | 1.420     |
| 2          | 0          | start         | 4.100     |
| 2          | 0          | end           | 4.512     |
| 2          | 1          | start         | 2.500     |
| 2          | 1          | end           | 5.000     |

---

# Output

| machine_id | processing_time |
| ---------- | --------------- |
| 0          | 0.894           |
| 1          | 0.995           |
| 2          | 1.456           |

---

# Explanation

There are **3 machines**, each running **2 processes**.

### Machine 0

```
((1.520 - 0.712) + (4.120 - 3.140)) / 2
= 0.894
```

### Machine 1

```
((1.550 - 0.550) + (1.420 - 0.430)) / 2
= 0.995
```

### Machine 2

```
((4.512 - 4.100) + (5.000 - 2.500)) / 2
= 1.456
```
