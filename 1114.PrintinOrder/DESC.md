# 1114. Print in Order

## Problem Description

You are given a class:

```java
public class Foo {
  public void first() { print("first"); }
  public void second() { print("second"); }
  public void third() { print("third"); }
}
```

The same instance of `Foo` will be passed to three different threads. Thread A will call `first()`, thread B will call `second()`, and thread C will call `third()`. Design a mechanism to ensure that:

1. `second()` is executed after `first()`.
2. `third()` is executed after `second()`.

### Notes

- The thread scheduling in the operating system is unknown, even though the input numbers imply an ordering.
- The input format ensures the comprehensiveness of the tests.

## Examples

### Example 1

**Input:** `nums = [1,2,3]`
**Output:** `"firstsecondthird"`

**Explanation:**
Three threads are fired asynchronously. The input `[1,2,3]` means:

- Thread A calls `first()`.
- Thread B calls `second()`.
- Thread C calls `third()`.

The correct output is `"firstsecondthird"`.

### Example 2

**Input:** `nums = [1,3,2]`
**Output:** `"firstsecondthird"`

**Explanation:**
The input `[1,3,2]` means:

- Thread A calls `first()`.
- Thread B calls `third()`.
- Thread C calls `second()`.

The correct output remains `"firstsecondthird"`.

## Constraints

- `nums` is a permutation of `[1, 2, 3]`.
- The solution must handle asynchronous thread execution.
