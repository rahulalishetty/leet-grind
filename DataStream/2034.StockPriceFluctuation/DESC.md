# 2034. Stock Price Fluctuation

You are given a **stream of records** about a particular stock.
Each record contains:

- a **timestamp**
- the **price of the stock** at that timestamp.

The records **may not arrive in order**, and sometimes a **record may correct a previous record** with the same timestamp.

Your task is to design a data structure that supports efficient updates and queries.

---

# Required Operations

Implement the `StockPrice` class.

## Constructor

```
StockPrice()
```

Initializes the object with **no price records**.

---

## Methods

### `void update(int timestamp, int price)`

Updates the price of the stock at the given timestamp.

If the timestamp already exists, the **previous price should be corrected**.

---

### `int current()`

Returns the **latest price of the stock**.

The latest price is the price corresponding to the **largest timestamp** recorded so far.

---

### `int maximum()`

Returns the **maximum price** of the stock among all recorded prices.

---

### `int minimum()`

Returns the **minimum price** of the stock among all recorded prices.

---

# Example

![alt text](image.png)

## Input

```
["StockPrice", "update", "update", "current", "maximum", "update", "maximum", "update", "minimum"]
[[], [1, 10], [2, 5], [], [], [1, 3], [], [4, 2], []]
```

---

## Output

```
[null, null, null, 5, 10, null, 5, null, 2]
```

---

# Explanation

```
StockPrice stockPrice = new StockPrice();
```

### Step 1

```
stockPrice.update(1, 10);
```

Current records:

```
timestamp: [1]
price:     [10]
```

---

### Step 2

```
stockPrice.update(2, 5);
```

Current records:

```
timestamp: [1,2]
price:     [10,5]
```

---

### Step 3

```
stockPrice.current();
```

Latest timestamp = `2`

```
return 5
```

---

### Step 4

```
stockPrice.maximum();
```

Maximum price:

```
10
```

---

### Step 5

```
stockPrice.update(1, 3);
```

Timestamp `1` had an incorrect price.

Updated records:

```
timestamp: [1,2]
price:     [3,5]
```

---

### Step 6

```
stockPrice.maximum();
```

Maximum price now:

```
5
```

---

### Step 7

```
stockPrice.update(4, 2);
```

Current records:

```
timestamp: [1,2,4]
price:     [3,5,2]
```

---

### Step 8

```
stockPrice.minimum();
```

Minimum price:

```
2
```

---

# Constraints

```
1 <= timestamp, price <= 10^9
```

Maximum number of operations:

```
10^5
```

Operations include:

```
update
current
maximum
minimum
```

Note:

- `current`, `maximum`, and `minimum` are called **only after at least one update**.
