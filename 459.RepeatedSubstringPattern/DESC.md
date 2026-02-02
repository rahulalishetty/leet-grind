# 459. Repeated Substring Pattern

## Problem Statement

Given a string `s`, determine if it can be constructed by taking a substring of it and appending multiple copies of the substring together.

## Examples

### Example 1:

- **Input:** `s = "abab"`
- **Output:** `true`
- **Explanation:** The string is formed by repeating the substring `"ab"` twice.

### Example 2:

- **Input:** `s = "aba"`
- **Output:** `false`

### Example 3:

- **Input:** `s = "abcabcabcabc"`
- **Output:** `true`
- **Explanation:** The string is formed by repeating the substring `"abc"` four times or the substring `"abcabc"` twice.

## Constraints

- `1 <= s.length <= 10^4`
- `s` consists of lowercase English letters.
