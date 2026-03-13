# 2102. Sequentially Ordinal Rank Tracker

A scenic location is represented by its **name** and **attractiveness score**, where:

- `name` is a unique string among all locations
- `score` is an integer

Locations are ranked **from best to worst** using:

1. Higher **score** is better.
2. If scores are equal, the **lexicographically smaller name** is better.

---

# System Requirements

You are building a system that:

1. **Adds scenic locations** one at a time.
2. **Queries the ith best location**, where `i` equals the number of times `get()` has been called so far.

Example rule:

- 1st call → return **1st best location**
- 2nd call → return **2nd best location**
- 3rd call → return **3rd best location**

The problem guarantees:

number of get() calls <= number of add() calls

---

# Implement the `SORTracker` Class

## Constructor

SORTracker()

Initializes the tracker system.

---

## Methods

### add(string name, int score)

Adds a scenic location.

name → location name
score → attractiveness score

---

### get()

Returns the **ith best location**, where:

i = number of times get() has been called

---

# Example

Input

["SORTracker","add","add","get","add","get","add","get","add","get","add","get","get"]

[[],["bradford",2],["branford",3],[],["alps",2],[],["orland",2],[],["orlando",3],[],["alpine",2],[],[]]

Output

[null,null,null,"branford",null,"alps",null,"bradford",null,"bradford",null,"bradford","orland"]

---

# Constraints

name consists of lowercase English letters
1 <= name.length <= 10
1 <= score <= 10^5

Total calls to add() and get() ≤ 4 \* 10^4
