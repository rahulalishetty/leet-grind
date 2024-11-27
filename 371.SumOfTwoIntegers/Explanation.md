# Please don't use multiplication to manage negative numbers and make a clean bitwise solution.

Once you start to manage negative numbers using bit manipulation, your solution becomes language-specific.

Different languages represent negative numbers differently.

## Java

For example, a Java integer is a number of 32 bits. 31 bits are used for the value. The first bit is used for the sign: if it's equal to 1, the number is negative, if it's equal to 0, the number is positive.

1 = (0 00...01)
-1 = (1 00...01)

No!

For the representation of a negative number Java uses the so-called "two's complement":

-1 = (1 1111...11)

The idea is simple:

(-1 + 1) & (11...1) 32 1 bits = 0
(-x + x) & (11...1) 32 1 bits = 0

The main goal of "two's complement" is to decrease the complexity of bit manipulations. How does Java compute "two's complement" and manage 32-bits limit? Here is how:

- After each operation we have an invisible & mask, where mask = 0xFFFFFFFF, i.e. bitmask of 32 1-bits.
- The overflow, i.e. the situation of x > 0x7FFFFFFF (a bitmask of 31 1-bits), is managed as x --> ~(x ^ 0xFFFFFFFF).

At this point, we could come back to approach 1 and, surprisingly, all management of negative numbers, signs, and subtractions Java already does for us. That simplifies the solution to the computation of a sum of two positive integers. That's how the magic of "two's complement" works!

## Python

Now let's go back to real life. Python has no 32-bit limit, and hence its representation of negative integers is entirely different.

There is no Java magic by default, and if you need a magic - just do it:

- After each operation we have an invisible & mask, where mask = 0xFFFFFFFF, i.e. bitmask of 32 1-bits.
- The overflow, i.e. the situation of x > 0x7FFFFFFF (a bitmask of 31 1-bits), is managed as x --> ~(x ^ 0xFFFFFFFF).

```python
class Solution:
  def getSum(self, a: int, b: int) -> int:
    mask = 0xFFFFFFFF

    while b != 0:
      a, b = (a ^ b) & mask, ((a & b) << 1) & mask

      max_int = 0x7FFFFFFF
      return a if a < max_int else ~(a ^ mask)
```
