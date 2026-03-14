# 3841. Palindromic Path Queries in a Tree

## Problem Description

You are given an **undirected tree** with `n` nodes labeled from `0` to `n-1`.

The tree is represented by:

```
edges[i] = [ui, vi]
```

which indicates an undirected edge between nodes `ui` and `vi`.

Each node also has a **character assigned** from the string:

```
s
```

where:

```
s[i] = character at node i
```

You are also given an array of queries. Each query is one of two types:

### 1. Update Query

```
"update ui c"
```

Update the character at node `ui`:

```
s[ui] = c
```

### 2. Path Query

```
"query ui vi"
```

Check whether the string formed by characters on the **unique path from node `ui` to node `vi`** can be **rearranged to form a palindrome**.

---

# Output

Return a boolean array `answer`.

```
answer[j] = true  → if the j-th query of type "query" can form a palindrome
answer[j] = false → otherwise
```

Note that **only query operations produce output**.

---

# Key Observations

A string can be rearranged into a palindrome **if and only if**:

```
At most one character has an odd frequency.
```

For example:

```
"aab"  → palindrome possible ("aba")
"abc"  → impossible
```

---

# Example 1

## Input

```
n = 3
edges = [[0,1],[1,2]]
s = "aac"

queries =
["query 0 2",
 "update 1 b",
 "query 0 2"]
```

## Output

```
[true, false]
```

## Explanation

**Query 1**

Path:

```
0 → 1 → 2
```

Characters:

```
"aac"
```

This can be rearranged into:

```
"aca"
```

So:

```
true
```

---

**Update**

```
update 1 b
```

New string:

```
s = "abc"
```

---

**Query 2**

Path:

```
0 → 1 → 2
```

Characters:

```
"abc"
```

Cannot form a palindrome.

Result:

```
false
```

---

# Example 2

## Input

```
n = 4
edges = [[0,1],[0,2],[0,3]]
s = "abca"

queries =
["query 1 2",
 "update 0 b",
 "query 2 3",
 "update 3 a",
 "query 1 3"]
```

## Output

```
[false, false, true]
```

---

## Explanation

### Query 1

Path:

```
1 → 0 → 2
```

String:

```
"bac"
```

Cannot form palindrome → **false**

---

### Update

```
update 0 b
```

New string:

```
s = "bbca"
```

---

### Query 2

Path:

```
2 → 0 → 3
```

String:

```
"cba"
```

Cannot form palindrome → **false**

---

### Update

```
update 3 a
```

String remains:

```
"bbca"
```

---

### Query 3

Path:

```
1 → 0 → 3
```

Characters:

```
"bba"
```

Can form:

```
"bab"
```

Result:

```
true
```

---

# Constraints

```
1 <= n == s.length <= 5 * 10^4

edges.length == n - 1

edges[i] = [ui, vi]

0 <= ui, vi < n

s consists of lowercase English letters

The graph forms a valid tree.

1 <= queries.length <= 5 * 10^4

queries[i] =
    "update ui c"
    or
    "query ui vi"

c is a lowercase English letter.
```

---

# Important Characteristics

1. The graph is a **tree**, so there is exactly **one unique path** between any two nodes.
2. Queries include both **updates and path checks**.
3. The problem requires **efficient handling of up to 50,000 operations**.
4. Palindrome checking reduces to **counting characters with odd frequency**.
