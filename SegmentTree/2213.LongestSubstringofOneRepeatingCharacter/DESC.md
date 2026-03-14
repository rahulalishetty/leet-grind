# 2213. Longest Substring of One Repeating Character

## Problem Description

You are given:

- A **0-indexed string** `s`
- A **0-indexed string** `queryCharacters`
- A **0-indexed integer array** `queryIndices`

Both `queryCharacters` and `queryIndices` have length `k`, representing **k queries**.

Each query performs the following operation:

```
s[queryIndices[i]] = queryCharacters[i]
```

After each query update, you must determine:

> The **length of the longest substring consisting of only one repeating character** in the updated string.

Return an integer array:

```
lengths
```

Where:

```
lengths[i] = longest repeating-character substring length after the ith query
```

---

# Example 1

## Input

```
s = "babacc"
queryCharacters = "bcb"
queryIndices = [1,3,3]
```

## Output

```
[3,3,4]
```

## Explanation

### Query 1

Update:

```
s[1] = 'b'
```

New string:

```
bbbacc
```

Longest repeating substring:

```
"bbb"
```

Length:

```
3
```

---

### Query 2

Update:

```
s[3] = 'c'
```

New string:

```
bbbccc
```

Longest repeating substrings:

```
"bbb"
"ccc"
```

Length:

```
3
```

---

### Query 3

Update:

```
s[3] = 'b'
```

New string:

```
bbbbcc
```

Longest repeating substring:

```
"bbbb"
```

Length:

```
4
```

Result:

```
[3,3,4]
```

---

# Example 2

## Input

```
s = "abyzz"
queryCharacters = "aa"
queryIndices = [2,1]
```

## Output

```
[2,3]
```

## Explanation

### Query 1

Update:

```
s[2] = 'a'
```

New string:

```
abazz
```

Longest repeating substring:

```
"zz"
```

Length:

```
2
```

---

### Query 2

Update:

```
s[1] = 'a'
```

New string:

```
aaazz
```

Longest repeating substring:

```
"aaa"
```

Length:

```
3
```

Result:

```
[2,3]
```

---

# Constraints

```
1 <= s.length <= 10^5
s consists of lowercase English letters

k == queryCharacters.length == queryIndices.length
1 <= k <= 10^5

queryCharacters consists of lowercase English letters
0 <= queryIndices[i] < s.length
```
