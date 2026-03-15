# 351. Android Unlock Patterns

Android devices feature a lock screen with a 3 x 3 grid of dots. Users can create an "unlock pattern" by connecting the dots in a specific sequence. This sequence forms a series of joined line segments, where each segment connects two consecutive dots. A sequence of `k` dots is considered a valid unlock pattern if it satisfies the following conditions:

1. **Distinct Dots**: All dots in the sequence must be unique.
2. **No Invalid Jumps**: If the line segment connecting two consecutive dots passes through the center of another dot, that dot must have already appeared earlier in the sequence.

### Examples of Valid and Invalid Patterns

- Connecting dots `2` and `9` without dots `5` or `6` appearing beforehand is **valid** because the line does not pass through the center of either dot `5` or `6`.
- Connecting dots `1` and `3` without dot `2` appearing beforehand is **invalid** because the line passes through the center of dot `2`.

#### Example Patterns:

1. `[4,1,3,6]` - **Invalid**: The line connecting dots `1` and `3` passes through dot `2`, which is not in the sequence.
2. `[4,1,9,2]` - **Invalid**: The line connecting dots `1` and `9` passes through dot `5`, which is not in the sequence.
3. `[2,4,1,3,6]` - **Valid**: The line connecting dots `1` and `3` is valid because dot `2` appears earlier in the sequence.
4. `[6,5,4,1,9,2]` - **Valid**: The line connecting dots `1` and `9` is valid because dot `5` appears earlier in the sequence.

### Problem Statement

Given two integers `m` and `n`, return the number of unique and valid unlock patterns on the Android grid lock screen that consist of at least `m` keys and at most `n` keys.

Two unlock patterns are considered unique if:

- They differ in at least one dot, or
- The order of the dots in the sequence is different.

### Examples

#### Example 1:

**Input**: `m = 1, n = 1`  
**Output**: `9`

#### Example 2:

**Input**: `m = 1, n = 2`  
**Output**: `65`

### Constraints

- `1 <= m, n <= 9`
- The grid is always a 3 x 3 matrix.
- Patterns must adhere to the rules outlined above.
- The sequence length must be between `m` and `n` inclusive.
