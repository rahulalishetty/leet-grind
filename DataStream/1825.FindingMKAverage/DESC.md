# 1825. Finding MK Average

You are given two integers **m** and **k**, and a **stream of integers**. You must implement a data structure that calculates the **MKAverage** for the stream.

---

# Definition of MKAverage

The **MKAverage** is calculated as follows:

1. If the number of elements in the stream is **less than m**, the MKAverage is **-1**.
2. Otherwise:
   - Copy the **last m elements** of the stream into a container.
   - Remove the **smallest k elements** and the **largest k elements**.
   - Compute the **average of the remaining elements**.
3. The result should be **rounded down to the nearest integer**.

---

# Implement the `MKAverage` Class

## Constructor

```
MKAverage(int m, int k)
```

Initializes the MKAverage object with:

- an empty stream
- integers **m** and **k**

---

## Methods

### `void addElement(int num)`

Adds a new element `num` into the stream.

---

### `int calculateMKAverage()`

Returns the **MKAverage** for the current stream.

Rules:

- If the number of elements in the stream is **less than m**, return:

```
-1
```

- Otherwise compute the MKAverage using the defined process.

---

# Example

## Input

```
["MKAverage", "addElement", "addElement", "calculateMKAverage",
 "addElement", "calculateMKAverage",
 "addElement", "addElement", "addElement", "calculateMKAverage"]

[[3,1], [3], [1], [], [10], [], [5], [5], [5], []]
```

---

## Output

```
[null, null, null, -1, null, 3, null, null, null, 5]
```

---

# Explanation

```
MKAverage obj = new MKAverage(3, 1);
```

```
obj.addElement(3);
```

Current stream:

```
[3]
```

---

```
obj.addElement(1);
```

Current stream:

```
[3, 1]
```

---

```
obj.calculateMKAverage();
```

Since **m = 3** and only **2 elements exist**, return:

```
-1
```

---

```
obj.addElement(10);
```

Current stream:

```
[3, 1, 10]
```

---

```
obj.calculateMKAverage();
```

Take the **last 3 elements**:

```
[3, 1, 10]
```

Remove:

- smallest **1 element**
- largest **1 element**

Remaining:

```
[3]
```

Average:

```
3 / 1 = 3
```

Return:

```
3
```

---

```
obj.addElement(5);
obj.addElement(5);
obj.addElement(5);
```

Current stream:

```
[3, 1, 10, 5, 5, 5]
```

---

```
obj.calculateMKAverage();
```

Take the **last 3 elements**:

```
[5, 5, 5]
```

Remove:

- smallest **1 element**
- largest **1 element**

Remaining:

```
[5]
```

Average:

```
5 / 1 = 5
```

Return:

```
5
```

---

# Constraints

```
3 <= m <= 10^5
1 < 2k < m
1 <= num <= 10^5
```

Maximum calls:

```
10^5
```

for:

```
addElement
calculateMKAverage
```
