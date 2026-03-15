# 2430. Maximum Deletions on a String

## Problem Statement

You are given a string `s` consisting only of lowercase English letters.

In one operation, you can:

1. **Delete the entire string `s`**, or
2. **Delete the first `i` letters of `s`** if the first `i` letters are equal to the following `i` letters in `s`, where:

```
1 <= i <= s.length / 2
```

For example:

```
s = "ababc"
```

You can delete the first two letters `"ab"` because the first two letters and the following two letters are both `"ab"`.
After deletion, the string becomes:

```
"abc"
```

Your task is to **return the maximum number of operations required to delete the entire string**.

---

## Example 1

**Input**

```
s = "abcabcdabc"
```

**Output**

```
2
```

**Explanation**

- Delete the first 3 letters `"abc"` because the next 3 letters are also `"abc"`
  Now `s = "abcdabc"`
- Delete the entire remaining string

Total operations = **2**

It can be proven that `2` is the maximum possible.

Note: In the second operation we cannot delete `"abc"` again because the next `"abc"` does not appear immediately after the first three letters.

---

## Example 2

**Input**

```
s = "aaabaab"
```

**Output**

```
4
```

**Explanation**

Operations:

1. Delete `"a"` → `"aabaab"`
2. Delete `"aab"` → `"aab"`
3. Delete `"a"` → `"ab"`
4. Delete the remaining string

Total operations = **4**

---

## Example 3

**Input**

```
s = "aaaaa"
```

**Output**

```
5
```

**Explanation**

We can repeatedly delete the first character `"a"` because the next character is always equal.

Operations:

```
aaaaa → aaaa → aaa → aa → a → ""
```

Total operations = **5**

---

## Constraints

- `1 <= s.length <= 4000`
- `s` consists only of **lowercase English letters**
