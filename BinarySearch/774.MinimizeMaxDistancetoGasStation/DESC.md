# 774. Minimize Max Distance to Gas Station

You are given an integer array `stations` that represents the positions of gas stations on the **x-axis**. You are also given an integer `k`.

You must **add `k` new gas stations**. The new stations can be placed **anywhere on the x-axis**, and **do not need to be at integer positions**.

Define:

```
penalty() = maximum distance between adjacent gas stations
```

after adding the `k` new stations.

Your task is to return the **smallest possible value of `penalty()`**.

Answers within:

```
10^-6
```

of the correct answer will be accepted.

---

## Example 1

**Input**

```
stations = [1,2,3,4,5,6,7,8,9,10]
k = 9
```

**Output**

```
0.50000
```

---

## Example 2

**Input**

```
stations = [23,24,36,39,46,56,57,65,84,98]
k = 1
```

**Output**

```
14.00000
```

---

## Constraints

- `10 <= stations.length <= 2000`
- `0 <= stations[i] <= 10^8`
- `stations` is sorted in **strictly increasing order**
- `1 <= k <= 10^6`
