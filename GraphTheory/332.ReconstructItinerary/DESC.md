# 332. Reconstruct Itinerary

## Problem

You are given a list of airline tickets where `tickets[i] = [from_i, to_i]` represent the departure and arrival airports of one flight.

Reconstruct the itinerary in order and return it.

### Rules

- All of the tickets belong to a man who departs from `"JFK"`.
- The itinerary must begin with `"JFK"`.
- If there are multiple valid itineraries, return the itinerary with the **smallest lexical order** when read as a single string.
- You must use **all tickets exactly once**.

### Example

`["JFK", "LGA"]` has a smaller lexical order than `["JFK", "LGB"]`.

You may assume all tickets form at least one valid itinerary.

---

## Example 1

![alt text](image.png)

### Input

```text
tickets = [["MUC","LHR"],["JFK","MUC"],["SFO","SJC"],["LHR","SFO"]]
```

### Output

```text
["JFK","MUC","LHR","SFO","SJC"]
```

---

## Example 2

![alt text](image-1.png)

### Input

```text
tickets = [["JFK","SFO"],["JFK","ATL"],["SFO","ATL"],["ATL","JFK"],["ATL","SFO"]]
```

### Output

```text
["JFK","ATL","JFK","SFO","ATL","SFO"]
```

### Explanation

Another possible reconstruction is:

```text
["JFK","SFO","ATL","JFK","ATL","SFO"]
```

However, it is lexicographically larger than the expected answer.

---

## Constraints

```text
1 <= tickets.length <= 300
tickets[i].length == 2
from_i.length == 3
to_i.length == 3
from_i and to_i consist of uppercase English letters.
from_i != to_i
```
