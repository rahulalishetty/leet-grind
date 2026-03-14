# 464. Can I Win

## Problem Description

In the classic **100 game**, two players take turns adding a number from **1 to 10** to a running total.
The player who first causes the total to **reach or exceed 100** wins.

Now consider a variation of the game:

- Players can choose numbers from **1 to maxChoosableInteger**
- **Numbers cannot be reused**
- Players take turns selecting numbers
- Each chosen number is added to a running total
- The player whose move causes the total to **reach or exceed desiredTotal wins**

Both players play **optimally**.

Your task is to determine:

> Can the **first player** force a win?

Return:

- **true** → if the first player can guarantee a win
- **false** → otherwise

---

# Function Signature

```
boolean canIWin(int maxChoosableInteger, int desiredTotal)
```

---

# Example 1

### Input

```
maxChoosableInteger = 10
desiredTotal = 11
```

### Output

```
false
```

### Explanation

No matter what number the first player chooses, the second player can always respond with a number that reaches or exceeds 11.

Example:

If the first player chooses **1**:

```
Current total = 1
```

Second player chooses **10**:

```
1 + 10 = 11
```

Second player wins.

This logic applies to every possible first move.

Therefore:

```
First player cannot force a win.
```

---

# Example 2

### Input

```
maxChoosableInteger = 10
desiredTotal = 0
```

### Output

```
true
```

### Explanation

The target is already reached because:

```
desiredTotal = 0
```

The first player automatically wins.

---

# Example 3

### Input

```
maxChoosableInteger = 10
desiredTotal = 1
```

### Output

```
true
```

### Explanation

The first player can immediately choose **1**, reaching the target.

```
Total = 1 >= desiredTotal
```

So the first player wins instantly.

---

# Constraints

```
1 <= maxChoosableInteger <= 20
0 <= desiredTotal <= 300
```

---
