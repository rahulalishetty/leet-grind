# 753. Cracking the Safe

A safe is protected by a password, which is a sequence of `n` digits where each digit ranges from `[0, k - 1]`.

The safe checks the most recent `n` digits entered each time you type a digit. For example, if the correct password is `"345"` and you enter `"012345"`:

- After typing `0`, the most recent 3 digits are `"0"` (incorrect).
- After typing `1`, the most recent 3 digits are `"01"` (incorrect).
- After typing `2`, the most recent 3 digits are `"012"` (incorrect).
- After typing `3`, the most recent 3 digits are `"123"` (incorrect).
- After typing `4`, the most recent 3 digits are `"234"` (incorrect).
- After typing `5`, the most recent 3 digits are `"345"` (correct, and the safe unlocks).

Your task is to return any string of minimum length that will unlock the safe at some point during the sequence.

## Examples

### Example 1:

**Input:**
`n = 1, k = 2`
**Output:**
`"10"`
**Explanation:**
The password is a single digit, so entering each digit will unlock the safe. `"01"` would also work.

### Example 2:

**Input:**
`n = 2, k = 2`
**Output:**
`"01100"`
**Explanation:**
For each possible password:

- `"00"` is typed starting from the 4th digit.
- `"01"` is typed starting from the 1st digit.
- `"10"` is typed starting from the 3rd digit.
- `"11"` is typed starting from the 2nd digit.

Thus, `"01100"` will unlock the safe. Other valid sequences include `"10011"` and `"11001"`.

## Constraints

- `1 <= n <= 4`
- `1 <= k <= 10`
- `1 <= k^n <= 4096`
