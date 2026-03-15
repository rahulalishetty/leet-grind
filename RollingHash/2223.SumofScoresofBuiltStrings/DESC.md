# 2223. Sum of Scores of Built Strings

## Problem Statement

You are building a string `s` of length `n` one character at a time, **prepending** each new character to the front of the string.
The strings are labeled from `1` to `n`, where the string with length `i` is labeled `s_i`.

For example, for:

```
s = "abaca"
```

The built strings are:

- `s1 = "a"`
- `s2 = "ca"`
- `s3 = "aca"`
- `s4 = "baca"`
- `s5 = "abaca"`

The **score** of `s_i` is defined as the **length of the longest common prefix** between `s_i` and `s_n` (the final string `s`).

Your task is to **return the sum of the scores of every `s_i`**.

---

## Example 1

**Input**

```
s = "babab"
```

**Output**

```
9
```

**Explanation**

| String         | Longest Common Prefix with `s` | Score |
| -------------- | ------------------------------ | ----- |
| `s1 = "b"`     | `"b"`                          | 1     |
| `s2 = "ab"`    | `""`                           | 0     |
| `s3 = "bab"`   | `"bab"`                        | 3     |
| `s4 = "abab"`  | `""`                           | 0     |
| `s5 = "babab"` | `"babab"`                      | 5     |

Sum of scores:

```
1 + 0 + 3 + 0 + 5 = 9
```

---

## Example 2

**Input**

```
s = "azbazbzaz"
```

**Output**

```
14
```

**Explanation**

Some strings that contribute to the score:

| String             | Longest Common Prefix | Score |
| ------------------ | --------------------- | ----- |
| `s2 = "az"`        | `"az"`                | 2     |
| `s6 = "azbzaz"`    | `"azb"`               | 3     |
| `s9 = "azbazbzaz"` | `"azbazbzaz"`         | 9     |

All other strings have score `0`.

Total:

```
2 + 3 + 9 = 14
```

---

## Constraints

- `1 ≤ s.length ≤ 10^5`
- `s` consists of **lowercase English letters**
