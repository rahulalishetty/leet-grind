# 871. Minimum Number of Refueling Stops

A car travels from a starting position to a destination which is **target miles east** of the starting position.

There are gas stations along the way. The gas stations are represented as an array:

```
stations[i] = [position_i, fuel_i]
```

Where:

- `position_i` is the position of the gas station in miles east of the starting position.
- `fuel_i` is the amount of fuel available at that gas station.

The car starts with an **infinite tank capacity**, which initially contains `startFuel` liters of fuel.

Fuel usage rules:

- The car uses **1 liter of fuel per 1 mile** traveled.
- When the car reaches a gas station, it **may stop and refuel**, transferring **all the gas from that station** into the car.

Return the **minimum number of refueling stops** required for the car to reach the destination.

If it is **impossible to reach the destination**, return **-1**.

---

## Important Notes

- If the car reaches a gas station with **0 fuel left**, it can still refuel there.
- If the car reaches the **destination with 0 fuel left**, it is still considered to have arrived.

---

# Examples

## Example 1

**Input**

```
target = 1
startFuel = 1
stations = []
```

**Output**

```
0
```

**Explanation**

We can reach the target without refueling.

---

## Example 2

**Input**

```
target = 100
startFuel = 1
stations = [[10,100]]
```

**Output**

```
-1
```

**Explanation**

We cannot reach the first gas station, so reaching the target is impossible.

---

## Example 3

**Input**

```
target = 100
startFuel = 10
stations = [[10,60],[20,30],[30,30],[60,40]]
```

**Output**

```
2
```

**Explanation**

1. Start with **10 liters** of fuel.
2. Drive to position **10**, using **10 liters** → fuel becomes **0**.
3. Refuel **60 liters** → fuel becomes **60**.
4. Drive from **10 → 60**, using **50 liters** → fuel becomes **10**.
5. Refuel **40 liters** → fuel becomes **50**.
6. Drive to the **target (100)**.

Total refueling stops = **2**.

---

# Constraints

```
1 <= target, startFuel <= 10^9
0 <= stations.length <= 500
1 <= position_i < position_(i+1) < target
1 <= fuel_i < 10^9
```
