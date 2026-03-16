import pypandoc, textwrap, pathlib

text = """

# 818. Race Car

## Problem Description

Your car starts at **position 0** with **speed +1** on an infinite number line.
The car can move into **negative positions** as well.

The car follows a sequence of instructions consisting of:

- **'A' (Accelerate)**
- **'R' (Reverse)**

---

## Instruction Rules

### Accelerate ('A')

When the instruction **'A'** is executed:

```
position += speed
speed *= 2
```

This means:

- The car moves forward (or backward depending on speed).
- The speed doubles after the move.

---

### Reverse ('R')

When the instruction **'R'** is executed:

```
If speed > 0 → speed = -1
Else → speed = 1
```

Key points:

- The **position does not change**.
- The **direction flips**.
- The speed resets to **±1** depending on direction.

---

## Example Walkthrough

For instructions:

```
"AAR"
```

Starting state:

```
position = 0
speed = 1
```

Step-by-step:

| Instruction | Position | Speed |
| ----------- | -------- | ----- |
| Start       | 0        | 1     |
| A           | 1        | 2     |
| A           | 3        | 4     |
| R           | 3        | -1    |

---

## Goal

Given a **target position**, return the **length of the shortest sequence of instructions** that moves the car from position `0` to the `target`.

---

## Example 1

### Input

```
target = 3
```

### Output

```
2
```

### Explanation

Shortest instruction sequence:

```
AA
```

Movement:

```
0 → 1 → 3
```

---

## Example 2

### Input

```
target = 6
```

### Output

```
5
```

### Explanation

Shortest instruction sequence:

```
AAARA
```

Movement:

```
0 → 1 → 3 → 7 → 7 → 6
```

Detailed steps:

| Instruction | Position | Speed |
| ----------- | -------- | ----- |
| Start       | 0        | 1     |
| A           | 1        | 2     |
| A           | 3        | 4     |
| A           | 7        | 8     |
| R           | 7        | -1    |
| A           | 6        | -2    |

---

## Constraints

```
1 <= target <= 10^4
```

---
