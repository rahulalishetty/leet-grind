# 362. Design Hit Counter

Design a hit counter which counts the number of hits received in the past **5 minutes (300 seconds)**.

The system accepts a timestamp parameter (in seconds granularity). You may assume:

- Calls are made **in chronological order**.
- `timestamp` is **monotonically increasing**.
- Several hits may arrive at the **same timestamp**.

---

# Class Specification

Implement the `HitCounter` class.

## Constructor

```
HitCounter()
```

Initializes the hit counter system.

---

## Method

```
void hit(int timestamp)
```

Records a hit that happened at the given timestamp.

Multiple hits may happen at the same timestamp.

---

## Method

```
int getHits(int timestamp)
```

Returns the number of hits in the **past 5 minutes (300 seconds)** from the given timestamp.

The time window considered is:

```
(timestamp - 300, timestamp]
```

---

# Example

## Input

```
["HitCounter", "hit", "hit", "hit", "getHits", "hit", "getHits", "getHits"]

[[], [1], [2], [3], [4], [300], [300], [301]]
```

## Output

```
[null, null, null, null, 3, null, 4, 3]
```

---

# Explanation

```
HitCounter hitCounter = new HitCounter();
```

### Step 1

```
hit(1)
```

A hit occurs at timestamp 1.

---

### Step 2

```
hit(2)
```

A hit occurs at timestamp 2.

---

### Step 3

```
hit(3)
```

A hit occurs at timestamp 3.

---

### Step 4

```
getHits(4)
```

Hits within the past 300 seconds:

```
[1,2,3]
```

Result:

```
3
```

---

### Step 5

```
hit(300)
```

A hit occurs at timestamp 300.

---

### Step 6

```
getHits(300)
```

Valid timestamps:

```
[1,2,3,300]
```

Result:

```
4
```

---

### Step 7

```
getHits(301)
```

Valid timestamps:

```
[2,3,300]
```

Timestamp 1 falls outside the 300 second window.

Result:

```
3
```

---

# Constraints

```
1 <= timestamp <= 2 * 10^9
```

- Calls are **monotonically increasing**
- At most **300 total calls** to `hit` and `getHits`

---

# Follow Up

What if:

- The number of hits **per second becomes extremely large**?

How would you redesign the system so that it:

- **Scales efficiently**
- Uses **bounded memory**
- Handles **high throughput traffic**
