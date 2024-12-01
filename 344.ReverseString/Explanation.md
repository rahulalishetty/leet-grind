# Principle of Recursion

You might wonder how we can implement a function that calls itself. The trick is that each time a recursive function calls itself, it reduces the given problem into subproblems. The recursion call continues until it reaches a point where the subproblem can be solved without further recursion.

A recursive function should have the following properties so that it does not result in an infinite loop:

- A simple base case (or cases) — a terminating scenario that does not use recursion to produce an answer.
- A set of rules, also known as recurrence relation that reduces all other cases towards the base case.

## Recurrence Relation

There are two important things that one needs to figure out before implementing a recursive function:

- recurrence relation: the relationship between the result of a problem and the result of its subproblems.
- base case: the case where one can compute the answer directly without any further recursion calls. Sometimes, the base cases are also called bottom cases, since they are often the cases where the problem has been reduced to the minimal scale, i.e. the bottom, if we consider that dividing the problem into subproblems is in a top-down manner.

Let's start with the recurrence relation within the Pascal's Triangle.

First of all, we define a function f(i,j) which returns the number in the Pascal's Triangle in the i-th row and j-th column.

We then can represent the recurrence relation with the following formula:

```note
f(i,j)=f(i−1,j−1)+f(i−1,j)
```

As one can see, the leftmost and rightmost numbers of each row are the base cases in this problem, which are always equal to 1.

As a result, we can define the base case as follows:

```note
f(i,j)=1 where j=1 or j=i
```

In the above example, you might have noticed that the recursive solution can incur some duplicate calculations, i.e. we compute the same intermediate numbers repeatedly in order to obtain numbers in the last row. For instance, in order to obtain the result for the number f(5,3), we calculate the number f(3,2) twice both in the calls of f(4,2) and f(4,3).

## Time Complexity - Recursion

Given a recursion algorithm, its time complexity O(T) is typically the product of the number of recursion invocations (denoted as R R) and the time complexity of calculation (denoted as O(s)) that incurs along with each recursion call:

```note
O ( T ) = R * O ( s )
```
