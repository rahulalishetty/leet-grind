# 2168. Unique Substrings With Equal Digit Frequency

## Problem Statement

Given a digit string `s`, return the **number of unique substrings** of `s` where **every digit appears the same number of times**.

A substring is a contiguous sequence of characters within a string.

Only **unique substrings** should be counted, even if the same substring appears multiple times in different positions.

---

## Example 1

**Input:**
`s = "1212"`

**Output:**
`5`

**Explanation:**
The substrings that satisfy the condition are:

- `"1"`
- `"2"`
- `"12"`
- `"21"`
- `"1212"`

Note that although `"12"` appears twice in the string, it is counted **only once** because we count **unique substrings**.

---

## Example 2

**Input:**
`s = "12321"`

**Output:**
`9`

**Explanation:**
The substrings that satisfy the condition are:

- `"1"`
- `"2"`
- `"3"`
- `"12"`
- `"23"`
- `"32"`
- `"21"`
- `"123"`
- `"321"`

---

## Constraints

- `1 <= s.length <= 1000`
- `s` consists only of **digits (`0-9`)**
