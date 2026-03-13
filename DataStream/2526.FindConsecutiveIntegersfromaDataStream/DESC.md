# 2526. Find Consecutive Integers from a Data Stream

For a stream of integers, implement a data structure that checks if the last **k integers** parsed in the stream are equal to **value**.

---

# Problem Description

Implement the **DataStream** class.

## Constructor

```
DataStream(int value, int k)
```

Initializes the object with:

- an empty integer stream
- the integer `value`
- the integer `k`

---

## Method

### consec

```
boolean consec(int num)
```

Adds `num` to the stream of integers.

Returns:

- **true** → if the last `k` integers are equal to `value`
- **false** → otherwise

If fewer than `k` integers have been parsed, the condition cannot be satisfied, so return **false**.

---

# Example

## Input

```
["DataStream", "consec", "consec", "consec", "consec"]
[[4, 3], [4], [4], [4], [3]]
```

## Output

```
[null, false, false, true, false]
```

---

# Explanation

```
DataStream dataStream = new DataStream(4, 3);
```

value = 4
k = 3

---

```
dataStream.consec(4)
```

Stream:

```
[4]
```

Only 1 integer → less than k → **false**

---

```
dataStream.consec(4)
```

Stream:

```
[4, 4]
```

Only 2 integers → less than k → **false**

---

```
dataStream.consec(4)
```

Stream:

```
[4, 4, 4]
```

Last 3 integers equal to value → **true**

---

```
dataStream.consec(3)
```

Stream:

```
[4, 4, 4, 3]
```

Last 3 integers:

```
[4, 4, 3]
```

Not all equal to value → **false**

---

# Constraints

```
1 <= value, num <= 10^9
1 <= k <= 10^5
At most 10^5 calls will be made to consec
```
