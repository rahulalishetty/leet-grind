# 933. Number of Recent Calls

You have a `RecentCounter` class which counts the number of recent requests within a certain time frame.

---

# Problem Description

Implement the `RecentCounter` class.

## Constructor

```
RecentCounter()
```

Initializes the counter with **zero recent requests**.

---

## Method

```
int ping(int t)
```

Adds a new request at time `t` (in milliseconds) and returns the number of requests that occurred in the **past 3000 milliseconds**.

The range considered is:

```
[t - 3000, t]
```

This range **includes the new request**.

---

# Important Guarantee

Every call to `ping` uses a **strictly larger value of `t`** than the previous call.

This means timestamps are **monotonically increasing**.

---

# Example

## Input

```
["RecentCounter", "ping", "ping", "ping", "ping"]

[[], [1], [100], [3001], [3002]]
```

## Output

```
[null, 1, 2, 3, 3]
```

---

# Explanation

```
RecentCounter recentCounter = new RecentCounter();
```

### Step 1

```
ping(1)
```

Requests:

```
[1]
```

Valid range:

```
[-2999, 1]
```

Result:

```
1
```

---

### Step 2

```
ping(100)
```

Requests:

```
[1, 100]
```

Valid range:

```
[-2900, 100]
```

Result:

```
2
```

---

### Step 3

```
ping(3001)
```

Requests:

```
[1, 100, 3001]
```

Valid range:

```
[1, 3001]
```

Result:

```
3
```

---

### Step 4

```
ping(3002)
```

Requests:

```
[1, 100, 3001, 3002]
```

Valid range:

```
[2, 3002]
```

Request `1` falls outside the range.

Result:

```
3
```

---

# Constraints

```
1 <= t <= 10^9
```

Additional guarantees:

- `t` values are **strictly increasing**
- At most **10^4 calls** will be made to `ping`
