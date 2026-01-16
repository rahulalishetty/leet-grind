# 1115. Print FooBar Alternately

## Problem Description

You are given the following code:

```java
class FooBar {
  public void foo() {
    for (int i = 0; i < n; i++) {
      print("foo");
    }
  }

  public void bar() {
    for (int i = 0; i < n; i++) {
      print("bar");
    }
  }
}
```

The same instance of `FooBar` will be passed to two different threads:

- Thread A will call `foo()`.
- Thread B will call `bar()`.

Modify the given program to output `"foobar"` `n` times.

---

## Examples

### Example 1:

**Input:**
`n = 1`
**Output:**
`"foobar"`
**Explanation:**
Two threads are fired asynchronously. One calls `foo()`, and the other calls `bar()`. `"foobar"` is output 1 time.

### Example 2:

**Input:**
`n = 2`
**Output:**
`"foobarfoobar"`
**Explanation:**
`"foobar"` is output 2 times.

---

## Constraints

- `1 <= n <= 1000`
- The program must ensure that the output alternates between `"foo"` and `"bar"`.
