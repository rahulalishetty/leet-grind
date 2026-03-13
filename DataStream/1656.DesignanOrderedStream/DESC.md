# 1656. Design an Ordered Stream

There is a stream of **n (idKey, value)** pairs arriving in an arbitrary order.

- `idKey` is an integer between **1 and n**
- `value` is a string
- No two pairs have the same `id`

We must design a stream that **returns values in increasing order of their IDs** by returning a **chunk (list) of values** after each insertion.

The **concatenation of all returned chunks** must result in the **sorted order of values by ID**.

---

# Class to Implement

## `OrderedStream`

### Constructor

```
OrderedStream(int n)
```

Constructs the stream to take **n values**.

---

### Method

```
String[] insert(int idKey, String value)
```

- Inserts the pair `(idKey, value)` into the stream.
- Returns the **largest possible chunk of values** that appear **next in order**.

---

# Example

### Input

```
["OrderedStream", "insert", "insert", "insert", "insert", "insert"]
[[5], [3, "ccccc"], [1, "aaaaa"], [2, "bbbbb"], [5, "eeeee"], [4, "ddddd"]]
```

### Output

```
[null, [], ["aaaaa"], ["bbbbb", "ccccc"], [], ["ddddd", "eeeee"]]
```

---

# Explanation

The values ordered by ID are:

```
["aaaaa", "bbbbb", "ccccc", "ddddd", "eeeee"]
```

### Step-by-step

```
OrderedStream os = new OrderedStream(5);
```

```
os.insert(3, "ccccc");
```

Inserted `(3, "ccccc")`.

Returned:

```
[]
```

---

```
os.insert(1, "aaaaa");
```

Returned:

```
["aaaaa"]
```

---

```
os.insert(2, "bbbbb");
```

Returned:

```
["bbbbb", "ccccc"]
```

---

```
os.insert(5, "eeeee");
```

Returned:

```
[]
```

---

```
os.insert(4, "ddddd");
```

Returned:

```
["ddddd", "eeeee"]
```

---

# Final Concatenation

```
[]
+ ["aaaaa"]
+ ["bbbbb", "ccccc"]
+ []
+ ["ddddd", "eeeee"]
```

Result:

```
["aaaaa", "bbbbb", "ccccc", "ddddd", "eeeee"]
```

This matches the sorted order of IDs.

---

# Constraints

```
1 <= n <= 1000
1 <= id <= n
value.length == 5
value consists only of lowercase letters
Each call to insert will have a unique id
Exactly n calls will be made to insert
```
